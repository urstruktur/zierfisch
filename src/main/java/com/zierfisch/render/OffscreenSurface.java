package com.zierfisch.render;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL21.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL31.*;
import static org.lwjgl.opengl.GL32.*;
import static org.lwjgl.opengl.GL33.*;
import static org.lwjgl.opengl.GL40.*;
import static org.lwjgl.opengl.GL41.*;

import com.zierfisch.tex.Texture;
import com.zierfisch.util.GLErrors;

/**
 * <p>
 * Represents a render target that is not directly used for presentation.
 * Rather, rendering is performed into texture attachments. The attached
 * textures can be sampled later to implement advanced graphics techniques.
 * </p>
 * 
 * <p>
 * This is implemented with OpenGL framebuffer objects that have texture objects
 * as image attachments.
 * </p>
 * 
 * @author phil
 */
public class OffscreenSurface extends AbstractSurface {

	/**
	 * Width of the render texture in pixels.
	 */
	private int width;
	/**
	 * Height of the render texture in pixels
	 */
	private int height;

	public OffscreenSurface(int width, int height, Texture colorTex, Texture depthTex) {
		super(glGenFramebuffers());
		
		this.width = width;
		this.height = height;

		bind();
		initColorAttachment(colorTex);
		initDepthAttachment(depthTex);
	}
	
	@Override
	public int getWidth() {
		return width;
	}

	@Override
	public int getHeight() {
		return height;
	}

	private void initColorAttachment(Texture colorTex) {
		colorTex.bind();
		glFramebufferTexture2D(
			GL_FRAMEBUFFER,
			GL_COLOR_ATTACHMENT0,
			GL_TEXTURE_2D,
			colorTex.getName(),
			0
		);
		GLErrors.check();
	}

	private void initDepthAttachment(Texture depthTex) {
		depthTex.bind();
		glFramebufferTexture2D(
			GL_FRAMEBUFFER,
			GL_DEPTH_ATTACHMENT,
			GL_TEXTURE_2D,
			depthTex.getName(),
			0
		);
		GLErrors.check();
	}
}
