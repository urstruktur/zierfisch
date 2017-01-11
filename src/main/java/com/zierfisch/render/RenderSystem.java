package com.zierfisch.render;

import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glDeleteVertexArrays;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

import java.io.IOException;

import org.joml.Matrix4f;

import xyz.krachzack.gfx.mesh.Mesh;
import xyz.krachzack.gfx.mesh.MeshBuilder;
import xyz.krachzack.gfx.mesh.Primitive;
import xyz.krachzack.gfx.mesh.SegmentedMeshBuilder;

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
import com.zierfisch.tex.Texture;
import com.zierfisch.tex.TextureLoader;
import com.zierfisch.util.GLErrors;
import com.zierfisch.util.ObjImporter;

public class RenderSystem extends EntitySystem {

	private ImmutableArray<Entity> entities;

	private ComponentMapper<Pose> pm = ComponentMapper.getFor(Pose.class);
	private ComponentMapper<Gestalt> gm = ComponentMapper.getFor(Gestalt.class);

	/**
	 * Holds the name of a single vertex array object that is used for all
	 * shaders. If uninitialized, holds -1.
	 */
	private int vao = -1;

	private Shader lastShader;

	private Shader defaultShader;
	
	private CameraSystem camSys;
	
	private Surface surface;
	
	public RenderSystem(Surface surface) {
		this.surface = surface;
	}
	
	public Surface getSurface() {
		return surface;
	}
	
	public void setSurface(Surface surface) {
		this.surface = surface;
	}

	@Override
	public void addedToEngine(Engine engine) {
		super.addedToEngine(engine);
		
		vao = glGenVertexArrays();
		glBindVertexArray(vao);

		entities = engine.getEntitiesFor(Family.all(Pose.class, Gestalt.class).get());

		initDefaultShader();

		//addTestEntities();
		
		camSys = engine.getSystem(CameraSystem.class);
	}

	private void initDefaultShader() {
		defaultShader = new ShaderBuilder()
				.setVertexShader("assets/shaders/cc/depth.vert.glsl")
				.setFragmentShader("assets/shaders/cc/depth.frag.glsl")
				.build();
	}

	private void addTestEntities() {
		Entity fish = makeFishEntity();
		getEngine().addEntity(fish);
	}

	public Entity makeFishEntity() {
		Entity ent = new Entity();
		ent.add(new Pose());
		ent.getComponent(Pose.class).setScale(1.0f);
		ent.add(makeDefaultGestalt());
		return ent;
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
		
		GLErrors.check("Before binding physical surface");
		surface.bind();
		GLErrors.check("After binding physical surface");
		surface.clear();
		GLErrors.check();
		
		glEnable(GL_DEPTH_TEST);
		
		//glClear(GL_COLOR_BUFFER_BIT);
		
		for (int i = 0; i < entities.size(); ++i) {
			Entity entity = entities.get(i);
			Pose pose = pm.get(entity);
			Gestalt gestalt = gm.get(entity);

			render(pose, gestalt);
		}
	}

	private void render(Pose pose, Gestalt gestalt) {
		Shader shader = selectShader(gestalt);

		if (shader != lastShader) {
			shader.bind();
		}

		setTextureUniforms(shader, gestalt);
		setMatrixUniforms(shader, pose);
		setTimeUniform(shader);
		shader.render(gestalt.mesh);

		lastShader = shader;
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
	
	public static Component makeDefaultGestalt() {
		ObjImporter importer = new ObjImporter();
		try {
			importer.load("assets/models/zierfisch.obj");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		MeshBuilder objBuilder = new SegmentedMeshBuilder(Primitive.TRIANGLES);
		Mesh mesh = importer.make(objBuilder);
		
		//MeshBuilder builder = new SegmentedMeshBuilder(Primitive.TRIANGLES);
		//CuboidMaker cuboidMaker = new CuboidMaker();
		//Mesh cuboid = cuboidMaker.make(builder, 0.5);

		Gestalt gestalt = new Gestalt();

		gestalt.mesh = mesh;
		gestalt.shader = null;
		gestalt.texture0 = new TextureLoader().load("assets/textures/fish-diffuse.png");
		gestalt.texture4 = new TextureLoader().load("assets/textures/fog-gradient-03.png");

		return gestalt;
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
