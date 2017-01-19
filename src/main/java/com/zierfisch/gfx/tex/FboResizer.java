package com.zierfisch.gfx.tex;

import static org.lwjgl.opengl.GL11.glGetInteger;
import static org.lwjgl.opengl.GL30.GL_DRAW_FRAMEBUFFER;
import static org.lwjgl.opengl.GL30.GL_DRAW_FRAMEBUFFER_BINDING;
import static org.lwjgl.opengl.GL30.GL_READ_FRAMEBUFFER;
import static org.lwjgl.opengl.GL30.GL_READ_FRAMEBUFFER_BINDING;
import static org.lwjgl.opengl.GL30.glBindFramebuffer;

import com.zierfisch.gfx.surf.Surface;
import com.zierfisch.gfx.util.GLErrors;

public abstract class FboResizer implements Resizer {

	protected Texture sourceTexture;
	protected Texture targetTexture;
	
	protected Surface sourceSurface;
	protected Surface targetSurface;
	
	public FboResizer(Texture sourceTexture, Texture targetTexture, Surface sourceSurface,
			Surface targetSurface) {
		
		
		this.sourceTexture = sourceTexture;
		this.targetTexture = targetTexture;
		this.sourceSurface = sourceSurface;
		this.targetSurface = targetSurface;
	}
	
	@Override
	public void resize() {
		// Save last bound FBOs
		int drawFboName = glGetInteger(GL_DRAW_FRAMEBUFFER_BINDING);
		int readFboName = glGetInteger(GL_READ_FRAMEBUFFER_BINDING);
		
		// Bin the ones for resizing
		bindFramebuffers();
		
		// Do it
		resizeFBOStyle();
		
		// And restore the original state
		bind(readFboName, drawFboName);
	}
	
	/**
	 * Resizes the from source texture to target texture knowing
	 * that the surfaces have been bound and the original state will
	 * be restored later.
	 */
	abstract protected void resizeFBOStyle();

	private void bindFramebuffers() {
		bind(sourceSurface, targetSurface);
	}

	private void bind(Surface source, Surface target) {
		bind(source.getName(), target.getName());
	}
	
	private void bind(int sourceFbo, int targetFbo) {
		glBindFramebuffer(GL_READ_FRAMEBUFFER, sourceFbo);
		GLErrors.check();
		glBindFramebuffer(GL_DRAW_FRAMEBUFFER, targetFbo);
		GLErrors.check();
	}

}
