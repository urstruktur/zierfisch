package com.zierfisch.tex;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

/**
 * <p>
 * Represents an end point for OpenGL drawing operations that can be bound at
 * any time to override the current render sink.
 * </p>
 * 
 * <p>
 * The default is the physical render sink <code>RenderSink.PHYSICAL</code>,
 * which draws into the back buffer of the double buffer that is presented on
 * the window.
 * </p>
 * 
 * <p>
 * Alternatively drawing can be performed into an offscreen buffer that can be
 * used as a texture for later drawing operations. This allows for multi-pass
 * rendering techniques, shadow maps and other advanced graphics techniques.
 * </p>
 * 
 * @author phil
 */
public abstract class RenderSink {

	public static final RenderSink PHYSICAL = new RenderSink();

	/**
	 * Configures OpenGL to render onto the screen rather than into a render
	 * texture.
	 */
	public static void bindPhysical() {
		PHYSICAL.bind();
	}

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

	public RenderSink(int width, int height) {
		this.width = width;
		this.height = height;

		fbo = glGenFramebuffers();
	}

	private RenderSink() {
		width = -1;
		height = -1;
		fbo = -1;
	}

	public abstract void bind() {
		if (fbo == -1) {
			bindGBuffer();
		} else {
			bindRenderTexture();
		}
	}

	private void bindRenderTexture() {
		// TODO Auto-generated method stub

	}

	private void bindGBuffer() {
		// TODO Auto-generated method stub

	}

}
