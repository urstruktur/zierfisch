package com.zierfisch.render;

import com.badlogic.ashley.core.Component;
import com.zierfisch.shader.Shader;
import com.zierfisch.tex.Texture;

import xyz.krachzack.gfx.mesh.Mesh;

public class Gestalt implements Component {

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

}
