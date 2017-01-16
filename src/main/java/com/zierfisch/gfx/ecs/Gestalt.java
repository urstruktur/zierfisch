package com.zierfisch.gfx.ecs;

import com.badlogic.ashley.core.Component;
import com.zierfisch.gfx.mesh.Mesh;
import com.zierfisch.gfx.shader.Shader;
import com.zierfisch.gfx.tex.Texture;

public class Gestalt implements Component {

	public Gestalt() {
		// Leave everything at null
	}
	
	public Gestalt(Gestalt other) {
		shader = other.shader;
		mesh = other.mesh;
		texture0 = other.texture0;
		texture1 = other.texture1;
		texture2 = other.texture2;
		texture3 = other.texture3;
		texture4 = other.texture4;
		uvscale = other.uvscale;
	}

	/**
	 * Holds the shader that is bound when rendering.
	 */
	public Shader shader;
	
	/**
	 * Holds the mesh that is passed to the shader for rendering.
	 */
	public Mesh mesh;

	/**
	 * <p>
	 * Holds a texture that needs to be bound when rendering the entity, or <code>null</code>
	 * if no texture is required.
	 * </p>
	 * 
	 * <p>
	 * If set, it will be bound to a sampler named <code>texture0</code> in the shader.
	 * </p>
	 * 
	 * <p>
	 * When set to <code>null</code>, it is assumed that the shader does not use
	 * a texture and leaves the last bound texture when rendering.
	 * </p>
	 */
	public Texture texture0;
	
	/**
	 * <p>
	 * Holds a texture that needs to be bound when rendering the entity, or <code>null</code>
	 * if no texture is required.
	 * </p>
	 * 
	 * <p>
	 * If set, it will be bound to a sampler named <code>texture1</code> in the shader.
	 * </p>
	 * 
	 * <p>
	 * When set to <code>null</code>, it is assumed that the shader does not use
	 * a texture and leaves the last bound texture when rendering.
	 * </p>
	 */
	public Texture texture1;
	
	/**
	 * <p>
	 * Holds a texture that needs to be bound when rendering the entity, or <code>null</code>
	 * if no texture is required.
	 * </p>
	 * 
	 * <p>
	 * If set, it will be bound to a sampler named <code>texture2</code> in the shader.
	 * </p>
	 * 
	 * <p>
	 * When set to <code>null</code>, it is assumed that the shader does not use
	 * a texture and leaves the last bound texture when rendering.
	 * </p>
	 */
	public Texture texture2;
	
	/**
	 * <p>
	 * Holds a texture that needs to be bound when rendering the entity, or <code>null</code>
	 * if no texture is required.
	 * </p>
	 * 
	 * <p>
	 * If set, it will be bound to a sampler named <code>texture3</code> in the shader.
	 * </p>
	 * 
	 * <p>
	 * When set to <code>null</code>, it is assumed that the shader does not use
	 * a texture and leaves the last bound texture when rendering.
	 * </p>
	 */
	public Texture texture3;
	
	/**
	 * <p>
	 * Holds a texture that needs to be bound when rendering the entity, or <code>null</code>
	 * if no texture is required.
	 * </p>
	 * 
	 * <p>
	 * If set, it will be bound to a sampler named <code>texture4</code> in the shader.
	 * </p>
	 * 
	 * <p>
	 * When set to <code>null</code>, it is assumed that the shader does not use
	 * a texture and leaves the last bound texture when rendering.
	 * </p>
	 */
	public Texture texture4;

	/**
	 * Determines if the textures will be scaled. Is 1.0 when left alone.
	 */
	public float uvscale;
}
