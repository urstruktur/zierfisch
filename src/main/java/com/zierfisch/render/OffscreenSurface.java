package com.zierfisch.render;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

/**
 * <p>
 * Represents a render target that is not directly used for presentation.
 * Rather, rendering is performed into a texture that can be sampled later
 * to implement advanced graphics techniques.
 * </p>
 * 
 * <p>
 * This is implemented with OpenGL framebuffer objects.
 * </p>
 * 
 * @author phil
 */
public class OffscreenSurface implements Surface {

	/**
	 * Width of the render texture in pixels.
	 */
	private int width;
	/**
	 * Height of the render texture in pixels
	 */
	private int height;
	/**
	 * OpenGL name of the framebuffer object
	 */
	private int fbo;
	
	@Override
	public int getWidth() {
		return width;
	}

	@Override
	public int getHeight() {
		return height;
	}
	
	public OffscreenSurface(int width, int height) {
		this.width = width;
		this.height = height;

		fbo = glGenFramebuffers();
	}

	@Override
	public void bind() {
		// TODO Auto-generated method stub
		glBindFramebuffer(GL_FRAMEBUFFER, fbo);
	}

	@Override
	public void clear() {
		// TODO Auto-generated method stub
		
	}

}
