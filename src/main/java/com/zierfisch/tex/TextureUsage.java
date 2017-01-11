package com.zierfisch.tex;

public enum TextureUsage {
	/**
	 * Texture contains colors, rendered results are clamped to 1.
	 */
	COLOR,
	/**
	 * Framebuffer output is unrestraint float vector.
	 */
	VECTOR,
	/**
	 * Texture will be used to render Z values to.
	 */
	DEPTH
}
