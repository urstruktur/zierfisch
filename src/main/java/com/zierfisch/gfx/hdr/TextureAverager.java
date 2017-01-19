package com.zierfisch.gfx.hdr;

import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_LINEAR;
import static org.lwjgl.opengl.GL11.GL_RGBA;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glGetInteger;
import static org.lwjgl.opengl.GL11.glGetTexImage;
import static org.lwjgl.opengl.GL30.GL_DRAW_FRAMEBUFFER;
import static org.lwjgl.opengl.GL30.GL_DRAW_FRAMEBUFFER_BINDING;
import static org.lwjgl.opengl.GL30.GL_READ_FRAMEBUFFER;
import static org.lwjgl.opengl.GL30.GL_READ_FRAMEBUFFER_BINDING;
import static org.lwjgl.opengl.GL30.glBindFramebuffer;
import static org.lwjgl.opengl.GL30.glBlitFramebuffer;

import java.nio.FloatBuffer;

import org.joml.Vector4d;
import org.joml.Vector4f;
import org.lwjgl.BufferUtils;

import com.zierfisch.gfx.surf.Surface;
import com.zierfisch.gfx.surf.SurfaceBuilder;
import com.zierfisch.gfx.tex.Resizer;
import com.zierfisch.gfx.tex.ResizerBuilder;
import com.zierfisch.gfx.tex.Texture;
import com.zierfisch.gfx.tex.TextureUsage;
import com.zierfisch.gfx.util.GLErrors;

/**
 * Calculates the average luminosity of any texture using an OpenGL blit
 * operation.
 * 
 * @author phil
 */
public class TextureAverager {

	// can hold 4 colors as four-byte floating point numbers
	private Vector4f averageColor = new Vector4f();
	private Texture averageColorTexture;
	private FloatBuffer averageColorHistory;
	private int averageColorHistoryInsertionIdx = 0;
	private FloatBuffer pixelBuf;
	/**
	 * Used for temporary results in <code>calculateAverageColor()</code>.
	 * Double for extra precision.
	 */
	private Vector4d colorSum = new Vector4d();
	/**
	 * Used for temporary results in <code>calculateAverageColor()</code>.
	 */
	private Vector4f aColor = new Vector4f();
	private Vector4f rollingAverageColor = new Vector4f();
	
	private Resizer resizer;
	
	public TextureAverager(Texture sourceTexture, int sourceWidth, int sourceHeight) {
		this(sourceTexture, sourceWidth, sourceHeight, 64);
	}

	public TextureAverager(Texture sourceTexture, int sourceWidth, int sourceHeight, int rollingAverageColorCount) {
		int targetW = 2;
		int targetH = 2;
		this.averageColorTexture = new Texture();
		
		this.resizer = buildResizer(sourceTexture, sourceWidth, sourceHeight, targetW, targetH);
		
		averageColorHistory = BufferUtils.createFloatBuffer(rollingAverageColorCount * 4);
		pixelBuf = BufferUtils.createFloatBuffer(targetW * targetH * 4);
	}

	private Resizer buildResizer(Texture sourceTexture, int sourceWidth, int sourceHeight, int targetW, int targetH) {
		ResizerBuilder resizerBuilder = new ResizerBuilder().setIterations(6);
		
		resizerBuilder.setFrom(sourceWidth, sourceHeight)
		              .setTo(targetH, targetW);

		return resizerBuilder.build(sourceTexture, averageColorTexture, TextureUsage.VECTOR);
	}

	/**
	 * <p>
	 * Calculates the average of the current content of the source surface.
	 * </p>
	 * 
	 * <p>
	 * <strong>A FAIR WARNING</strong>: this seems to work for a while but
	 * randomly crashes the app. Directly sample the 1x1 pixel texture instead.
	 * </p>
	 * 
	 * @return
	 */
	public void update() {
		long start = System.currentTimeMillis();
		resizer.resize();
		GLErrors.check();
		//System.out.println("Resizing took " + (System.currentTimeMillis() - start));
		
		start = System.currentTimeMillis();
		//downloadPixelBuf();
		long downloadFinishTime = System.currentTimeMillis();
		//System.out.println("Downloading the resized image took " + (downloadFinishTime - start) + "ms");
		calculateAverageColor(pixelBuf, averageColor);
		pushAverageColorToHistory();
		calculateAverageColor(averageColorHistory, rollingAverageColor);
		//System.out.println("Average color calculatations took" + (System.currentTimeMillis() - downloadFinishTime));

		GLErrors.check();
		// averageColor.set(pixelBuf);
		pixelBuf.clear();
	}

	public void downloadPixelBuf() {
		averageColorTexture.bind();
		glGetTexImage(GL_TEXTURE_2D, 0, GL_RGBA, GL_FLOAT, pixelBuf);
		GLErrors.check();
	}

	private void calculateAverageColor(FloatBuffer buf, Vector4f result) {
		int floatCount = buf.limit() / 4;
		int colorCount = floatCount / 4;

		colorSum.zero();

		for (int i = 0; i < buf.limit(); i += 4 * 4) {
			aColor.set(i, buf);
			colorSum.add(aColor);
		}

		result.set(colorSum.div(colorCount));
	}

	private void pushAverageColorToHistory() {
		int position = averageColorHistoryInsertionIdx % averageColorHistory.capacity();

		averageColorHistoryInsertionIdx += 4 * 4;
		int limit = (averageColorHistoryInsertionIdx > averageColorHistory.capacity()) ? averageColorHistory.capacity()
				: averageColorHistoryInsertionIdx;

		averageColorHistory.limit(limit);
		averageColor.get(position, averageColorHistory);
	}

	private static final float luminosity(Vector4f color) {
		return color.w * (0.2126f * color.x + 0.7152f * color.y + 0.0722f * color.z);
	}

	public float getAverageLuminosity() {
		return luminosity(averageColor);
	}

	public float getRollingAverageLuminosity() {
		return luminosity(rollingAverageColor);
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

	public Vector4f getRollingAverageColor() {
		return rollingAverageColor;
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
