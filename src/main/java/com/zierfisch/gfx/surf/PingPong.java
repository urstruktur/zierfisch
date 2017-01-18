package com.zierfisch.gfx.surf;

import static org.lwjgl.opengl.GL11.glViewport;
import static org.lwjgl.opengl.GL30.GL_DRAW_FRAMEBUFFER;
import static org.lwjgl.opengl.GL30.GL_READ_FRAMEBUFFER;
import static org.lwjgl.opengl.GL30.glBindFramebuffer;

import com.zierfisch.gfx.tex.Texture;
import com.zierfisch.gfx.util.GLErrors;

public class PingPong {

	private Surface s1;
	private Texture[] s1ColorTexes;
	private Texture s1DepthTex;
	
	private Surface s2;
	private Texture[] s2ColorTexes;
	private Texture s2DepthTex;
	
	private boolean isS1ForReading;
	
	/**
	 * Takes a builder configured to create two surfaces like you want them. 
	 * 
	 * @param builder
	 */
	public PingPong(SurfaceBuilder builder) {
		int colorAttachmentCount = builder.getColorAttachmentCount();

		s1ColorTexes = new Texture[colorAttachmentCount];
		for(int i = 0; i < s1ColorTexes.length; ++i) {
			s1ColorTexes[i] = new Texture();
		}
		s1DepthTex = new Texture();
		
		s2ColorTexes = new Texture[colorAttachmentCount];
		for(int i = 0; i < s2ColorTexes.length; ++i) {
			s2ColorTexes[i] = new Texture();
		}
		s1DepthTex = new Texture();
		
		this.s1 = builder.build(s1ColorTexes, s1DepthTex);
		this.s2 = builder.build(s2ColorTexes, s2DepthTex);
	}
	
	
	/**
	 * Marks the reading surface for writing and the writing surface for reading.
	 * Note that this will not take effect until you bind the ping pong again.
	 */
	public void flip() {
		isS1ForReading = !isS1ForReading;
	}
	
	/**
	 * <p>Gets the color textures that were written before the last call to flip.</p>
	 * 
	 * <p>
	 * Before the first flip, this contains uninitialized textur data.
	 * </p>
	 */
	public Texture[] getColorTexes() {
		return isS1ForReading ? s1ColorTexes : s2ColorTexes;
	}
	
	public Texture getColorTex() {
		return getColorTexes()[0];
	}
	
	public Texture getDepthTex() {
		return isS1ForReading ? s1DepthTex : s2DepthTex;
	}


	/**
	 * <p>Binds one surface for reading and one surface for writing.</p>
	 * 
	 * <p>Call <code>flip()</code> for reading and writing surfaces to change places.</p>.
	 * 
	 */
	public void bind() {
		glBindFramebuffer(GL_DRAW_FRAMEBUFFER, getDrawSurface().getName());
		glBindFramebuffer(GL_READ_FRAMEBUFFER, getReadSurface().getName());
		glViewport(0, 0, getDrawSurface().getWidth(), getDrawSurface().getHeight());
		GLErrors.check();
	}
	
	public void clear() {
		getDrawSurface().clear();
		GLErrors.check();
	}
	

	private Surface getDrawSurface() {
		boolean isS1ForDrawing = !isS1ForReading;
		return isS1ForDrawing ? s1 : s2;
	}
	
	private Surface getReadSurface() {
		return isS1ForReading ? s1 : s2;
	}
}
