package com.zierfisch.render;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL21.*;
import static org.lwjgl.opengl.GL30.*;

import org.joml.Matrix4f;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.zierfisch.shader.Shader;
import com.zierfisch.shader.ShaderBuilder;
import com.zierfisch.tex.Texture;
import com.zierfisch.tex.TextureLoader;
import com.zierfisch.util.GLErrors;

import xyz.krachzack.gfx.assets.CuboidMaker;
import xyz.krachzack.gfx.mesh.Mesh;
import xyz.krachzack.gfx.mesh.MeshBuilder;
import xyz.krachzack.gfx.mesh.Primitive;
import xyz.krachzack.gfx.mesh.SegmentedMeshBuilder;

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

	@Override
	public void addedToEngine(Engine engine) {
		super.addedToEngine(engine);
		
		vao = glGenVertexArrays();
		glBindVertexArray(vao);

		entities = engine.getEntitiesFor(Family.all(Pose.class, Gestalt.class).get());

		System.out.println("initing shader");
		initDefaultShader();

		System.out.println("adding test entities");
		addTestEntities();
		
		System.out.println("added test entities");
	}

	private void initDefaultShader() {
		defaultShader = new ShaderBuilder()
				.setVertexShader("assets/shaders/cc/cc.vert.glsl")
				.setFragmentShader("assets/shaders/cc/cc.frag.glsl")
				.build();
	}

	private void addTestEntities() {
		Entity cube = makeCubeEntity();
		getEngine().addEntity(cube);
	}

	public Entity makeCubeEntity() {
		Entity ent = new Entity();
		ent.add(new Pose());
		ent.add(makeDefaultGestalt());
		return ent;
	}

	private Component makeDefaultGestalt() {
		MeshBuilder builder = new SegmentedMeshBuilder(Primitive.TRIANGLES);
		CuboidMaker cuboidMaker = new CuboidMaker();
		Mesh cuboid = cuboidMaker.make(builder, 0.5);

		Gestalt gestalt = new Gestalt();

		gestalt.mesh = cuboid;
		gestalt.shader = null;
		gestalt.texture0 = new TextureLoader().load("assets/textures/fins.png");

		return gestalt;
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
		
		System.out.println("UPDAATE");

		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

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

		System.out.println("rendering");
		setTextureUniforms(shader, gestalt);
		setMatrixUniforms(shader, pose);
		shader.render(gestalt.mesh);

		lastShader = shader;
	}

	private void setTextureUniforms(Shader shader, Gestalt gestalt) {
		setTextureUniform(shader, gestalt.texture0, 0);
		setTextureUniform(shader, gestalt.texture1, 1);
		setTextureUniform(shader, gestalt.texture2, 2);
		setTextureUniform(shader, gestalt.texture3, 3);
		setTextureUniform(shader, gestalt.texture4, 4);
	}

	public void setTextureUniform(Shader shader, Texture tex, int offset) {
		if(tex != null) {
			int loc = shader.getUniformLocation("texture" + offset);
			if(loc != -1) {
				shader.setUniform(loc, offset);
				glActiveTexture(GL_TEXTURE0 + offset);
				tex.bind();
			}
		}
	}

	public void setMatrixUniforms(Shader shader, Pose pose) {
		Matrix4f model = pose.getModel();
		shader.setUniform("u_model", model);
	}

	public Shader selectShader(Gestalt gestalt) {
		Shader shader = gestalt.shader;

		if (shader == null) {
			shader = defaultShader;
		}

		return shader;
	}

}
