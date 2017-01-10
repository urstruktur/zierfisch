package com.zierfisch.render;

import com.zierfisch.tex.RenderSink;

/**
 * <p>
 * Represents an end point for OpenGL drawing operations that can be bound at
 * any time to override the current render sink.
 * </p>
 * 
 * <p>
 * The default is the physical render sink
 * <code>{@link RenderSinks}.physical()</code>, which draws into a back buffer of
 * the swap chain used for presentation on the window surface.
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
public interface Surface {
	public int getWidth();

	public int getHeight();

	public void clear();
	
	public void bind();
}
