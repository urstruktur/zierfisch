package com.zierfisch.render;

import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;

/**
 * <p>
 * Represents an end point for OpenGL drawing operations that can be bound at
 * any time to override the current surface.
 * </p>
 * 
 * <p>
 * The default is the physical surface <code>{@link SurfaceBuilder}.physical()</code>,
 * which draws into a back buffer of the swap chain used for presentation on the
 * window surface.
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
	/**
	 * Represents the color buffer, holding the primary image result of
	 * rendering.
	 */
	public static final int COLOR = GL_COLOR_BUFFER_BIT;
	/**
	 * Represents the z buffer or depth buffer, holding Z values of rendered
	 * stuff.
	 */
	public static final int DEPTH = GL_DEPTH_BUFFER_BIT;

	/**
	 * Represents both the color buffer and the depth buffer.
	 */
	public static final int ALL = COLOR | DEPTH;

	/**
	 * <p>
	 * Returns the OpenGL name associated with the framebuffer object used by this
	 * surface.
	 * </p>
	 * 
	 * <p>
	 * This will always be zeo for the physical surface and always be non-zero for
	 * offscreen surfaces.
	 * </p>
	 * 
	 * @return the fbo name
	 */
	public int getName();

	public int getWidth();

	public int getHeight();

	/**
	 * <p>
	 * Clears all attachments of the surface, including color buffer and depth
	 * buffer.
	 * </p>
	 * 
	 * <p>
	 * Take care that the surface is bound when calling this, otherwise the call
	 * may have unintended side effects.
	 * </p>
	 */
	public void clear();

	public void clear(int which);

	public void bind();

	/**
	 * <p>
	 * <strong>Note:</strong>Only call this when the surface is currently bound.
	 * </p>
	 * 
	 * @return
	 */
	public boolean isComplete();
}
