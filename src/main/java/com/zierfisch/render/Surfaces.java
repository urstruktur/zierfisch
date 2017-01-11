package com.zierfisch.render;

import com.zierfisch.tex.Texture;
import com.zierfisch.tex.TextureUsage;

public final class Surfaces {
	public static final int DEFAULT_BITS_PER_COLOR_COMPONENT = 8;
	public static final int DEFAULT_BITS_PER_DEPTH_COMPONENT = 16;
	
	/**
	 * Creates a new offscreen surface with a color and a depth attachment.
	 * There is no stencil attachment.
	 * 
	 * @param width Horizontal resolution of the offscreen texture
	 * @param height
	 * @return
	 */
	public static Surface createOffscreen(int width, int height, boolean unclamped) {
		Texture color = new Texture();
		color.bind();
		color.allocate(unclamped ? TextureUsage.VECTOR : TextureUsage.COLOR, width, height);
		
		Texture depth = new Texture();
		depth.bind();
		depth.allocate(TextureUsage.DEPTH, width, height);
		
		return null;
	}
	
	public static Surface createOffscreen(int width, int height) {
		return createOffscreen(width, height, false);
	}
	
}
