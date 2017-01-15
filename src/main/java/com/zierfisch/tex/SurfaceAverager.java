package com.zierfisch.tex;

import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_LINEAR;
import static org.lwjgl.opengl.GL11.GL_RGBA;
import static org.lwjgl.opengl.GL11.glGetInteger;
import static org.lwjgl.opengl.GL11.glReadPixels;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.GL_DRAW_FRAMEBUFFER;
import static org.lwjgl.opengl.GL30.GL_DRAW_FRAMEBUFFER_BINDING;
import static org.lwjgl.opengl.GL30.GL_READ_FRAMEBUFFER;
import static org.lwjgl.opengl.GL30.GL_READ_FRAMEBUFFER_BINDING;
import static org.lwjgl.opengl.GL30.glBindFramebuffer;
import static org.lwjgl.opengl.GL30.glBlitFramebuffer;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL14.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL21.*;

import org.joml.Vector4f;
import org.lwjgl.BufferUtils;

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
	private Vector4f averageColor = new Vector4f();
	private Texture averageColorTexture = new Texture();
	private float[] luminosityHistory = new float[32];
	private int luminosityHistoryInsertionIdx = 0;
	private int luminosityHistoryCount = 0;
	private float rollingAverageLuminosity;
	private FloatBuffer pixelBuf;

	public SurfaceAverager(Surface sourceSurface) {
		this.sourceSurface = sourceSurface;
		// 1x1 pixel floating point texture, do not expose depth texture
		int w = sourceSurface.getWidth()/4;
		int h = sourceSurface.getHeight()/4;
		this.targetSurface = Surfaces.createOffscreen(w, h, averageColorTexture, null, true);
		
		pixelBuf = BufferUtils.createFloatBuffer(w*h * 4);
	}
	
	private void pushLuminosity(float luminosity) {
		luminosityHistory[luminosityHistoryInsertionIdx++ % luminosityHistory.length] = luminosity;
		
		if(luminosityHistoryCount < luminosityHistory.length) {
			++luminosityHistoryCount;
		}
		
		double sum = 0.0;
		for(int i = 0; i < luminosityHistoryCount; ++i) {
			sum += luminosityHistory[i];
		}
		
		rollingAverageLuminosity = (float) (sum / luminosityHistoryCount);
	}

	/**
	 * <p>
	 * Calculates the average of the current content of the source surface.
	 * </p>
	 * 
	 * <p>
	 * <strong>A FAIR WARNING</strong>: this seems to work for a while but randomly crashes
	 * the app. Directly sample the 1x1 pixel texture instead.
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
				0, 0, targetSurface.getWidth(), targetSurface.getHeight(),
				GL_COLOR_BUFFER_BIT, GL_LINEAR);
		
		GLErrors.check();

		targetSurface.bind();

		averageColorTexture.bind();
		glGetTexImage(GL_TEXTURE_2D, 0, GL_RGBA, GL_FLOAT, pixelBuf);
		//pixelBuf.flip();
		
		float avgLuminosity = calculateAvgLuminosityInPixelBuf();
		
		//glReadPixels(0, 0, 1, 1, GL_RGBA, GL_FLOAT, pixelBuffer);
		GLErrors.check();
		//averageColor.set(pixelBuf);
		pixelBuf.clear();
		
		// Restore last bound FBO
		glBindFramebuffer(GL_DRAW_FRAMEBUFFER, drawFboName);
		glBindFramebuffer(GL_READ_FRAMEBUFFER, readFboName);
		
		pushLuminosity(avgLuminosity);
	}

	private float calculateAvgLuminosityInPixelBuf() {
		double[] factors = { 0.2126, 0.7152, 0.0722, 0.0 };
		int offset = 0;
		
		double combinedLuminosity = 0.0;
		
		while(offset < (targetSurface.getWidth() * targetSurface.getHeight() * 4)) {
			combinedLuminosity += pixelBuf.get(offset) * factors[offset % factors.length];
			++offset;
		}
		
		return (float) (combinedLuminosity / (pixelBuf.capacity() / 4));
	}

	public float getAverageLuminosity() {
		return 0.2126f * averageColor.x + 0.7152f * averageColor.y + 0.0722f * averageColor.z;
	}
	public float getRollingAverageLuminosity() {
		return rollingAverageLuminosity;
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
