package com.zierfisch.render;

import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glDeleteVertexArrays;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL31.*;
import static org.lwjgl.opengl.GL32.*;
import static org.lwjgl.opengl.GL33.*;
import static org.lwjgl.opengl.GL40.*;
import static org.lwjgl.opengl.GL41.*;

import java.io.IOException;

import org.joml.Matrix4f;
import org.joml.Vector4f;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.zierfisch.cam.Camera;
import com.zierfisch.cam.CameraSystem;
import com.zierfisch.shader.Shader;
import com.zierfisch.shader.ShaderBuilder;
import com.zierfisch.tex.LuminosityAverager;
import com.zierfisch.tex.Texture;
import com.zierfisch.tex.TextureLoader;
import com.zierfisch.util.GLErrors;
import com.zierfisch.util.ObjImporter;

import xyz.krachzack.gfx.assets.QuadMaker;
import xyz.krachzack.gfx.mesh.Mesh;
import xyz.krachzack.gfx.mesh.MeshBuilder;
import xyz.krachzack.gfx.mesh.Primitive;
import xyz.krachzack.gfx.mesh.SegmentedMeshBuilder;

public class RenderSystem extends EntitySystem {

	public static final int MAX_LIGHTS = 6;
	
	private ImmutableArray<Entity> entities;
	private ImmutableArray<Entity> lights;

	private ComponentMapper<Pose> pm = ComponentMapper.getFor(Pose.class);
	private ComponentMapper<Gestalt> gm = ComponentMapper.getFor(Gestalt.class);
	private ComponentMapper<Light> lm = ComponentMapper.getFor(Light.class);

	/**
	 * Holds the name of a single vertex array object that is used for all
	 * shaders. If uninitialized, holds -1.
	 */
	private int vao = -1;
	private Shader lastShader;
	private Shader defaultShader;
	private CameraSystem camSys;
	/**
	 * The surface that the final image is presented to.
	 */
	private Surface surface;
	/**
	 * Used for presenting textures to screen
	 */
	private Mesh fullscreenQuad;
	/**
	 * Used for presenting textures to screen
	 */
	private Shader presentShader;
	/**
	 * Offscreen surface that the content is rendered to first before presenting.
	 */
	private Surface offscreen;
	private Texture offscreenColor;
	private Texture offscreenDepth;
	private Vector4f offscreenAvgColor;
	
	private LuminosityAverager averager;
	
	public RenderSystem(Surface surface) {
		this.surface = surface;
	}
	
	public Surface getSurface() {
		return surface;
	}
	
	@Override
	public void addedToEngine(Engine engine) {
		super.addedToEngine(engine);
		initialize(engine);
	}

	public void initialize(Engine engine) {
		vao = glGenVertexArrays();
		glBindVertexArray(vao);

		entities = engine.getEntitiesFor(Family.all(Pose.class, Gestalt.class).get());
		lights = engine.getEntitiesFor(Family.all(Pose.class, Light.class).get());

		initDefaultShader();

		camSys = engine.getSystem(CameraSystem.class);
		
		initFullscreenQuad();
	}

	private void initFullscreenQuad() {
		fullscreenQuad = new QuadMaker().make(new SegmentedMeshBuilder());
		presentShader = new ShaderBuilder()
				              .setVertexShader("assets/shaders/present/present.vert.glsl")
				              .setFragmentShader("assets/shaders/present/present.frag.glsl")
				              .build();
				
		offscreenColor = new Texture();
		offscreenDepth = new Texture();
		offscreen = Surfaces.createOffscreen(surface.getWidth(), surface.getHeight(), offscreenColor, offscreenDepth);
		
		averager = new LuminosityAverager(offscreen);
		offscreenAvgColor = averager.getAverageColor();
	}
	
	private void present(Texture texture) {
		presentShader.bind();
		
		int loc = presentShader.getUniformLocation("content");
		presentShader.setUniform(loc, 0);
		GLErrors.check();
		glActiveTexture(GL_TEXTURE0);
		texture.bind();
		
		presentShader.render(fullscreenQuad);
		lastShader = presentShader;
	}

	private void initDefaultShader() {
		defaultShader = new ShaderBuilder()
				.setVertexShader("assets/shaders/cc/depth.vert.glsl")
				.setFragmentShader("assets/shaders/cc/depth.frag.glsl")
				.build();
	}

	@Override
	public void removedFromEngine(Engine engine) {
		super.removedFromEngine(engine);

		glDeleteVertexArrays(vao);
		vao = -1;
	}

	@Override
	public void update(float deltaTime) {
		super.update(deltaTime);
		
		offscreen.bind();
		GLErrors.check("Bound offscreen surface");
		
		offscreen.clear();
		GLErrors.check("Cleared offscreen surface");
		
		glEnable(GL_DEPTH_TEST);
		for (int i = 0; i < entities.size(); ++i) {
			Entity entity = entities.get(i);
			Pose pose = pm.get(entity);
			Gestalt gestalt = gm.get(entity);

			render(pose, gestalt);
		}
		
		averager.calculateAverage();
		
		GLErrors.check("Before binding physical surface");
		surface.bind();
		GLErrors.check("After binding physical surface");
		surface.clear();
		GLErrors.check();
		
		present(offscreenColor);
		//present(offscreenDepth);
		//present(averager.getAverageColorTexture());
	}

	private void render(Pose pose, Gestalt gestalt) {
		if(gestalt.mesh != null) {
			Shader shader = selectShader(gestalt);
	
			if (shader != lastShader) {
				shader.bind();
			}
	
			setTextureUniforms(shader, gestalt);
			setMatrixUniforms(shader, pose);
			setTimeUniform(shader);
			setLightUniforms(shader);
			
			shader.render(gestalt.mesh);
	
			lastShader = shader;
		}
	}
	
	private void setLightUniforms(Shader shader) {
		
		int count = lights.size();
		
		if(count > MAX_LIGHTS) {
			throw new RuntimeException(count + " lights exceed maximum light count of " + MAX_LIGHTS);
		}
		
		for(int i = 0; i < count; ++i) {
			Entity ent = lights.get(i);
			
			Pose pose = pm.get(ent);
			Light light = lm.get(ent);
			
			Vector4f pos = new Vector4f();
			pos.x = pose.position.x;
			pos.y = pose.position.y;
			pos.z = pose.position.z;
			pos.w = 1.0f; // Point light, not directional, always
			
			Vector4f color = new Vector4f();
			color.x = light.color.x;
			color.y = light.color.y;
			color.z = light.color.z;
			color.w = light.intensity;
			
			shader.setUniform("lights["+i+"].position", pos);
			shader.setUniform("lights["+i+"].color", color);
		}
	}

	private void setTimeUniform(Shader shader){
		shader.setUniform("time", (float)(System.currentTimeMillis()/1000f));
	}

	private void setTextureUniforms(Shader shader, Gestalt gestalt) {
		setTextureUniform(shader, gestalt.texture0, 0);
		setTextureUniform(shader, gestalt.texture1, 1);
		setTextureUniform(shader, gestalt.texture2, 2);
		setTextureUniform(shader, gestalt.texture3, 3);
		setTextureUniform(shader, gestalt.texture4, 4);
		
		if(gestalt.uvscale > 0){
			shader.setUniform("uvscale", gestalt.uvscale);
		}else{
			shader.setUniform("uvscale", 1.0f);
		}
	}

	public void setTextureUniform(Shader shader, Texture tex, int offset) {
		if(tex != null) {
			int loc = shader.getUniformLocation("texture" + offset);
			if(loc != -1) {
				shader.setUniform(loc, offset);
				GLErrors.check();
				glActiveTexture(GL_TEXTURE0 + offset);
				tex.bind();
			}
		}
	}

	public void setMatrixUniforms(Shader shader, Pose pose) {
		Camera cam = camSys.getMainCamera();
		Matrix4f model = pose.getModel();
		Matrix4f view = cam.view;
		Matrix4f projection = cam.projection;
		
		shader.setUniform("u_model", model);
		shader.setUniform("u_view", view);
		shader.setUniform("u_projection", projection);
	}

	public Shader selectShader(Gestalt gestalt) {
		Shader shader = gestalt.shader;

		if (shader == null) {
			shader = defaultShader;
		}

		return shader;
	}
	
	public static Component makeEnviromentGestalt() {
		ObjImporter importer = new ObjImporter();
		try {
			importer.load("assets/models/enviroment.obj");
		} catch (IOException e) {
			e.printStackTrace();
		}
		MeshBuilder objBuilder = new SegmentedMeshBuilder(Primitive.TRIANGLES);
		Mesh enviromentMesh = importer.make(objBuilder);
		
		Gestalt g = new Gestalt();
		g.mesh = enviromentMesh;
		g.shader = new ShaderBuilder()
				.setVertexShader("assets/shaders/cc/depth.vert.glsl")
				.setFragmentShader("assets/shaders/cc/depth.frag.glsl")
				.build();
		g.texture0 = new TextureLoader().load("assets/textures/RockPerforated0029_1_seamless_S.png");
		g.texture4 = new TextureLoader().load("assets/textures/fog-gradient-03.png"); // texture4 is used for fog texture gradient
		g.uvscale = 12f;
		return g;
	}

}
