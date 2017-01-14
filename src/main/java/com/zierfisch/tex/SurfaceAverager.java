package com.zierfisch.tex;

import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_LINEAR;
import static org.lwjgl.opengl.GL11.GL_RGBA;
import static org.lwjgl.opengl.GL11.glGetInteger;
import static org.lwjgl.opengl.GL11.glReadPixels;
import static org.lwjgl.opengl.GL30.GL_DRAW_FRAMEBUFFER;
import static org.lwjgl.opengl.GL30.GL_DRAW_FRAMEBUFFER_BINDING;
import static org.lwjgl.opengl.GL30.GL_READ_FRAMEBUFFER;
import static org.lwjgl.opengl.GL30.GL_READ_FRAMEBUFFER_BINDING;
import static org.lwjgl.opengl.GL30.glBindFramebuffer;
import static org.lwjgl.opengl.GL30.glBlitFramebuffer;

import org.joml.Vector4f;

import com.zierfisch.render.Surface;
import com.zierfisch.render.Surfaces;
import com.zierfisch.util.GLErrors;

/**
 * Calculates the average luminosity of any texture using an OpenGL blit
 * operation.
 * 
 * @author phil
 */
public class SurfaceAverager {
	/**
	 * Contains the source framebuffer to calculate the average from.
	 */
	private Surface sourceSurface;
	private Surface targetSurface;
	// can hold 4 colors as four-byte floating point numbers
	private float[] pixelBuffer = new float[4];
	private Vector4f averageColor = new Vector4f();
	private Texture averageColorTexture = new Texture();

	public SurfaceAverager(Surface sourceSurface) {
		this.sourceSurface = sourceSurface;
		// 1x1 pixel floating point texture, do not expose depth texture
		this.targetSurface = Surfaces.createOffscreen(1, 1, averageColorTexture, null, true);
	}

	/**
	 * <p>
	 * Calculates the average of the current content of the source surface.
	 * </p>
	 * 
	 * @return
	 */
	public void update() {
		// Save last bound FBOs
		int drawFboName = glGetInteger(GL_DRAW_FRAMEBUFFER_BINDING);
		int readFboName = glGetInteger(GL_READ_FRAMEBUFFER_BINDING);

		glBindFramebuffer(GL_READ_FRAMEBUFFER, sourceSurface.getName());
		GLErrors.check();
		glBindFramebuffer(GL_DRAW_FRAMEBUFFER, targetSurface.getName());
		GLErrors.check();

		glBlitFramebuffer(
				// source framebuffer dimensions
				0, 0, sourceSurface.getWidth(), sourceSurface.getHeight(),
				// target framebuffer dimensions, will be downscaled
				0, 0, 1, 1, GL_COLOR_BUFFER_BIT, GL_LINEAR);
		GLErrors.check();

		targetSurface.bind();

		glReadPixels(0, 0, 1, 1, GL_RGBA, GL_FLOAT, pixelBuffer);
		GLErrors.check();
		averageColor.set(pixelBuffer[0], pixelBuffer[1], pixelBuffer[2], pixelBuffer[3]);

		// Restore last bound FBO
		glBindFramebuffer(GL_DRAW_FRAMEBUFFER, drawFboName);
		glBindFramebuffer(GL_READ_FRAMEBUFFER, readFboName);
	}

	public float getAverageLuminosity() {
		return 0.2126f * averageColor.x + 0.7152f * averageColor.y + 0.0722f * averageColor.z;
	}

	/**
	 * <p>
	 * Gets the average calculated on the last call to
	 * <code>calculateAverage()</code> or a zero vector, if it has never been
	 * called.
	 * </p>
	 * 
	 * <p>
	 * For the love of god, please do not modify the returned vector. You may
	 * keep a reference to it, however, it will be edited, in place when
	 * calculateAverage is called.
	 * </p>
	 * 
	 * @return Average color as RGBA
	 */
	public Vector4f getAverageColor() {
		return averageColor;
	}

	/**
	 * Returns a 1x1 pixel float texture holding the average color at the last
	 * call to <code>update()</code>.
	 * 
	 * @return texture name
	 */
	public Texture getAverageColorTexture() {
		return averageColorTexture;
	}

}
