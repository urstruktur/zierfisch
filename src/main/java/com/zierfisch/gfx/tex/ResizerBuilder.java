package com.zierfisch.gfx.tex;

import com.zierfisch.gfx.surf.Surface;
import com.zierfisch.gfx.surf.SurfaceBuilder;

public class ResizerBuilder {

	private int iterations = 1;

	private int sourceWidth = -1;
	private int sourceHeight = -1;

	private int targetWidth = -1;
	private int targetHeight = -1;

	public ResizerBuilder setFrom(int width, int height) {
		sourceWidth = width;
		sourceHeight = height;
		return this;
	}

	public ResizerBuilder setTo(int width, int height) {
		targetWidth = width;
		targetHeight = height;
		return this;
	}
	
	/**
	 * <p>
	 * Sets the maximum amount of downscaling operations that will be performed
	 * to resize the texture. The implementation may choose a lower amount of
	 * iterations if equal or superior quality can be achieved with a lower
	 * amount of iterations.
	 * </p>
	 * 
	 * <p>
	 * The default is <code>1</code>, so the source texture will be directly
	 * resized from source to target with no intermediary textures. This usually
	 * leads to a single blit operation being performed for resizing, which is
	 * relatively fast on most graphics cards.
	 * </p>
	 * 
	 * <p>
	 * A number higher than one will perform multiple blits into continously
	 * smaller, resp. larger textures often leading to smoother resizing
	 * results. Also, this may be used to accurately scale to a single pixel and
	 * get relatively accurate average color values.
	 * </p>
	 * 
	 * @param iterations
	 * @return
	 */
	public ResizerBuilder setIterations(int iterations) {
		if (iterations < 1) {
			throw new IllegalArgumentException("Iteration count should be >= 1, but was: " + iterations);
		}

		this.iterations = iterations;
		return this;
	}

	/**
	 * 
	 * @param sourceTexture
	 * @param targetTexture
	 * @param usage has to be the same for both source and target
	 * @return
	 */
	public Resizer build(Texture sourceTexture, Texture targetTexture, TextureUsage usage) {
		selectDimensions();
		
		if(iterations != 1) {
			throw new UnsupportedOperationException("Sorry, only one-off resizing is supported atm");
		}
		
		SurfaceBuilder surfBuilder = new SurfaceBuilder().attach(usage);
		
		Surface sourceSurf = surfBuilder.setSize(sourceWidth, sourceHeight).build(sourceTexture);
		Surface targetSurf = surfBuilder.setSize(targetWidth, targetHeight).build(targetTexture);
		
		return new BlitResizer(sourceTexture, targetTexture, sourceSurf, targetSurf);
	}

	/**
	 * <p>
	 * Checks if all dimensions have been specified, taking corrective action if
	 * possible.
	 * </p>
	 * 
	 * <p>
	 * If one of the target dimensions was set to -1 or omitted, it will be set
	 * to have same aspect ratio or the closest possible approximation to it.
	 * </p>
	 */
	private void selectDimensions() {
		if (sourceWidth == -1 || sourceHeight == -1) {
			throw new IllegalStateException(
					"The source width and height must both be specified when building Resizer, currently: "
							+ sourceWidth + "/" + sourceHeight);
		}

		if (targetWidth == -1 || targetHeight == -1) {
			if (targetWidth == -1 && targetHeight == -1) {
				throw new IllegalStateException("Neither target width nor height were specified, need at least one");
			} else if (targetWidth == -1) {
				// Target width was omitted but height was provided
				double ratio = (double) sourceWidth / (double) sourceHeight;
				targetWidth = (int) Math.round(targetHeight * ratio);
			} else {
				// Target height was omitted but width was provided
				double ratio = (double) sourceHeight / (double) sourceWidth;
				targetHeight = (int) Math.round(targetWidth * ratio);
			}
		}
	}
}
