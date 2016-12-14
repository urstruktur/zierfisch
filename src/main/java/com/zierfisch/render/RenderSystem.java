package com.zierfisch.render;

import static org.lwjgl.opengl.GL30.*;

import org.joml.Matrix4f;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.zierfisch.shader.Shader;

public class RenderSystem extends EntitySystem {

	private ImmutableArray<Entity> entities;

	private ComponentMapper<Pose> pm = ComponentMapper.getFor(Pose.class);
	private ComponentMapper<Gestalt> gm = ComponentMapper.getFor(Gestalt.class);

	/**
	 * Holds the name of a single vertex array object that is used for all
	 * shaders. If uninitialized, holds -1.
	 */
	private int vao = -1;

	@Override
	public void addedToEngine(Engine engine) {
		super.addedToEngine(engine);

		vao = glGenVertexArrays();
		glBindVertexArray(vao);

		entities = engine.getEntitiesFor(Family.all(Pose.class, Gestalt.class).get());
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
		
		Shader lastShader = null;

		for (int i = 0; i < entities.size(); ++i) {
			Entity entity = entities.get(i);
			Pose pose = pm.get(entity);
			Gestalt gestalt = gm.get(entity);
			
			Shader shader = gestalt.shader;
			Matrix4f model = pose.getModel();
			
			if(shader != lastShader) {
				shader.bind();
			}
			
			shader.setUniform("u_model", model);
			shader.render(gestalt.mesh);
			
			lastShader = shader;
		}
	}

}
