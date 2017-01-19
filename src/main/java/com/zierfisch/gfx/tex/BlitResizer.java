package com.zierfisch.gfx.tex;

import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_LINEAR;
import static org.lwjgl.opengl.GL30.glBlitFramebuffer;

import com.zierfisch.gfx.surf.Surface;
import com.zierfisch.gfx.util.GLErrors;

public class BlitResizer extends FboResizer {

	public BlitResizer(Texture sourceTexture, Texture targetTexture, Surface sourceSurface, Surface targetSurface) {
		super(sourceTexture, targetTexture, sourceSurface, targetSurface);
	}

	@Override
	protected void resizeFBOStyle() {
		glBlitFramebuffer(
				// source framebuffer dimensions
				0, 0, sourceSurface.getWidth(), sourceSurface.getHeight(),
				// target framebuffer dimensions, will be downscaled
				0, 0, targetSurface.getWidth(), targetSurface.getHeight(),
				GL_COLOR_BUFFER_BIT, GL_LINEAR);
		
		GLErrors.check();
	}
	
}
