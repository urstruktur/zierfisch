package com.zierfisch.render;

import com.zierfisch.tex.Texture;
import com.zierfisch.tex.TextureUsage;

public final class Surfaces {

	public static Surface createOffscreen(int width, int height, Texture colorTex, Texture depthTex) {
		return createOffscreen(width, height, colorTex, depthTex, false);
	}
	
	/**
	 * Creates a new offscreen surface with a color and a depth attachment.
	 * There is no stencil attachment.
	 * 
	 * @param width Horizontal resolution of the offscreen texture
	 * @param height
	 * @return
	 */
	public static Surface createOffscreen(int width, int height, Texture color, Texture depth, boolean unclamped) {
		return createOffscreen(width, height, new Texture[] { color }, depth, unclamped);
	}
	
	public static Surface createOffscreen(int width, int height, Texture[] colors, Texture depth, boolean unclamped) {
		for(int i = 0; i < colors.length; ++i) {
			colors[i] = prepareColorTexture(colors[i], width, height, unclamped);
		}
		
		depth = prepareDepthTexture(depth, width, height);
		OffscreenSurface offscreen = new OffscreenSurface(width, height, colors, depth);
		return offscreen;
	}

	public static Texture prepareColorTexture(Texture color, int width, int height, boolean unclamped) {
		if(color == null) {
			color = new Texture();
		}
		
		color.bind();
		color.allocate(unclamped ? TextureUsage.VECTOR : TextureUsage.COLOR, width, height);
		
		return color;
	}
	
	public static Texture prepareDepthTexture(Texture depth, int width, int height) {
		if(depth == null) {
			depth = new Texture();
		}
		
		depth.bind();
		depth.allocate(TextureUsage.DEPTH, width, height);
		
		return depth;
	}
}
