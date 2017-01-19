package com.zierfisch.gfx.ecs;

import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER_SRGB;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glDeleteVertexArrays;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

import java.io.IOException;

import org.joml.Matrix4f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL11;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.zierfisch.assets.geom.ObjImporter;
import com.zierfisch.assets.geom.QuadMaker;
import com.zierfisch.cam.Camera;
import com.zierfisch.cam.CameraSystem;
import com.zierfisch.gfx.hdr.SurfaceAverager;
import com.zierfisch.gfx.mesh.Mesh;
import com.zierfisch.gfx.mesh.MeshBuilder;
import com.zierfisch.gfx.mesh.Primitive;
import com.zierfisch.gfx.mesh.SegmentedMeshBuilder;
import com.zierfisch.gfx.shader.Shader;
import com.zierfisch.gfx.shader.ShaderBuilder;
import com.zierfisch.gfx.surf.PingPong;
import com.zierfisch.gfx.surf.Surface;
import com.zierfisch.gfx.surf.SurfaceBuilder;
import com.zierfisch.gfx.tex.Texture;
import com.zierfisch.gfx.tex.TextureBuilder;
import com.zierfisch.gfx.tex.TextureUsage;
import com.zierfisch.gfx.util.GLErrors;

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
	
	private Shader postShader;
	private Shader blurShader;
	/**
	 * Offscreen surface that the content is rendered to first before presenting.
	 */
	private Surface offscreen;
	private Texture offscreenColor;
	private Texture offscreenBrightColor;
	private Texture offscreenDepth;
	
	private PingPong pingPong;
	
	private SurfaceAverager averager;
	private long startTime;
	
	public RenderSystem(Surface surface) {
		this.surface = surface;
	}
	
	public Surface getSurface() {
		return surface;
	}
	
	@Override
	public void addedToEngine(Engine engine) {
		super.addedToEngine(engine);
		startTime = System.currentTimeMillis();
		initialize(engine);
	}

	public void initialize(Engine engine) {
		vao = glGenVertexArrays();
		glBindVertexArray(vao);

		entities = engine.getEntitiesFor(Family.all(Pose.class, Gestalt.class).get());
		lights = engine.getEntitiesFor(Family.all(Pose.class, Light.class).get());

		initDefaultShader();
		initPostShaders();

		camSys = engine.getSystem(CameraSystem.class);
		
		initFullscreenQuad();
	}

	/**
	 * Intitializes the postprocessing shader used for hdr and bloom.
	 */
	private void initPostShaders() {
		postShader = new ShaderBuilder().setVertexShader("assets/shaders/post/post.vert.glsl")
		                                .setFragmentShader("assets/shaders/post/post.frag.glsl")
		                                .build();
		blurShader = new ShaderBuilder().setVertexShader("assets/shaders/post/blur.vert.glsl")
								        .setFragmentShader("assets/shaders/post/blur.frag.glsl")
								        .build();
	}

	private void initFullscreenQuad() {
		fullscreenQuad = new QuadMaker().make(new SegmentedMeshBuilder());
		presentShader = new ShaderBuilder()
				              .setVertexShader("assets/shaders/present/present.vert.glsl")
				              .setFragmentShader("assets/shaders/present/present.frag.glsl")
				              .build();
				
		offscreenColor = new Texture();
		offscreenBrightColor = new Texture();
		offscreenDepth = new Texture();
		offscreen = new SurfaceBuilder().setSize(surface)
		                                .attach(TextureUsage.VECTOR)
		                                .attach(TextureUsage.VECTOR)
		                                .build(new Texture[]{offscreenColor,offscreenBrightColor}, offscreenDepth);
		
		SurfaceBuilder blurSurfaceBuilder = new SurfaceBuilder().setSize(surface)
									      .attach(TextureUsage.VECTOR);
		pingPong = new PingPong(blurSurfaceBuilder);
		
		
		averager = new SurfaceAverager(offscreen);
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
	
	private void presentPostprocessed(Texture tex) {
		postShader.bind();
		
		postShader.setUniform("hdr", 0);
		GLErrors.check();
		glActiveTexture(GL_TEXTURE0);
		tex.bind();
		
		postShader.setUniform("avgLuminosity", averager.getRollingAverageLuminosity());
		postShader.setUniform("avgColor", averager.getAverageColor());
		postShader.setUniform("rollingAvgLuminosity", averager.getRollingAverageLuminosity()); 
		//System.out.println(averager.getRollingAverageLuminosity());
		postShader.setUniform("rollingAvgColor", averager.getRollingAverageColor());
		setTextureUniform(postShader, pingPong.getColorTex(), 1);
		
		// Apply gamma correction exactly once at this point.
		// Do not do this again in a shader!
		// GL_FRAMEBUFFER_SRGB knows what it is doing
		glEnable(GL_FRAMEBUFFER_SRGB);
		postShader.render(fullscreenQuad);
		glDisable(GL_FRAMEBUFFER_SRGB);
		
		lastShader = postShader;
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
		
		final float skyBrightness = 1.2f;
		GL11.glClearColor(0.0f, 0.2f*skyBrightness, 0.298f*skyBrightness, 1.0f);
		offscreen.clear();
		GLErrors.check("Cleared offscreen surface");
		
		glEnable(GL_DEPTH_TEST);
		
		for (int i = 0; i < entities.size(); ++i) {
			Entity entity = entities.get(i);
			Pose pose = pm.get(entity);
			Gestalt gestalt = gm.get(entity);

			render(pose, gestalt);
		}
		
		averager.update();
		
		glDisable(GL_DEPTH_TEST); // disable for two-pass gaussian blur shader
		
		
		// BLUR THE SHIT OF IT
		
		pingPong.bind();
		pingPong.clear();
		
		// here i want to scale pingpong down
		
		boolean first_iteration = true;
		boolean horizontal = true;
		for(int i = 0; i < 6; i++){
			// step 1: draw horizontal
			blurShader.bind();
			blurShader.setUniform("horizontal", horizontal?1:0);
			
			if(first_iteration){
				setTextureUniform(blurShader, offscreenBrightColor, 0);
				first_iteration = false;
			}else{
				setTextureUniform(blurShader, pingPong.getColorTex(), 0);
			}
			
			blurShader.render(fullscreenQuad);
			
			pingPong.flip();
			pingPong.bind();
			
			horizontal = !horizontal;
		}
		
		// here i want to scale pingpong up
		
		//pingPong.getColorTex()

		GLErrors.check("Before binding physical surface");
		surface.bind();
		GLErrors.check("After binding physical surface");
		surface.clear();
		GLErrors.check();
		
		
		//present(offscreenColor);
		presentPostprocessed(offscreenColor);
		
		//present(pingPong.getColorTex());
		
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
			shader.setUniform("averageLuminosity", averager.getRollingAverageLuminosity());
			
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
			color.x = light.color.x * light.intensity;
			color.y = light.color.y * light.intensity;
			color.z = light.color.z * light.intensity;
			color.w = 1.0f;
			
			shader.setUniform("lights["+i+"].position", pos);
			shader.setUniform("lights["+i+"].color", color);
		}
	}

	private void setTimeUniform(Shader shader){
		shader.setUniform("time", (float)((System.currentTimeMillis()-startTime)/1000f));
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
			importer.load("assets/models/cave.obj");
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
		g.texture0 = new TextureBuilder().setContents("assets/textures/RockPerforated0029_1_seamless_S.png").build();
		g.texture4 = new TextureBuilder().setContents("assets/textures/fog-gradient-03.png").build(); // texture4 is used for fog texture gradient
		g.uvscale = 12f;
		return g;
	}

}
