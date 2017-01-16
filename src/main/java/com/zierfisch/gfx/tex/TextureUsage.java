package com.zierfisch.gfx.tex;

public enum TextureUsage {
	/**
	 * <p>
	 * Texture contains colors, rendered results are clamped to the interval 0 ≤
	 * x ≤ 1, where x is an element of the color channels r, g, b or a. The clamping
	 * is applied to the fragment shader outputs in hardware.
	 * </p>
	 * 
	 * <p>
	 * The colors are generally stored as integers in the texture, representing
	 * values in linear color space.
	 * </p>
	 */
	COLOR,
	
	/**
	 * <p>
	 * Texture contains unrestrained floating point values as components. This may be used
	 * for storing positions, orientations, HDR colors or other arbitrary vector data.
	 * </p>
	 * 
	 * <p>
	 * Results are never clamped when written to texture.
	 * </p>
	 */
	VECTOR,
	
	/**
	 * Texture will be used to render Z values to. Using this as a color output is unsupported,
	 * use it for depth attachments that you also want to read from, e.g. for implementing
	 * shadow mapping.
	 */
	DEPTH
}
