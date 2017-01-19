package com.zierfisch.gfx.shader;

/**
 * Lazily builds and gets stock shaders that are used in multiple places.
 * 
 * @author phil
 */
public final class Shaders {

	/**
	 * Gets a shader that dumps a texture named content on a two-dimensional
	 * quad.
	 * 
	 * @return The stock present shader
	 */
	public static Shader present() {
		return CACHE.get("present");
	}
	
	private static ShaderCache CACHE = new ShaderCache();
	
	static {
		CACHE.define("present").setVertexShader("assets/shaders/present/present.vert.glsl")
	                           .setFragmentShader("assets/shaders/present/present.frag.glsl");
	}
}
