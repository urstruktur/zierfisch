package com.zierfisch.gfx.surf;

import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glViewport;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL30.glBindFramebuffer;

import com.zierfisch.gfx.util.GLErrors;

abstract class AbstractSurface implements Surface {

	int fbo;
	
	public AbstractSurface(int fbo) {
		this.fbo = fbo;
	}
	
	@Override
	public int getName() {
		return fbo;
	}
	
	@Override
	public boolean isComplete() {
		return glCheckFramebufferStatus(GL_FRAMEBUFFER) == GL_FRAMEBUFFER_COMPLETE;
	}
	
	@Override
	public void bind() {
		glBindFramebuffer(GL_FRAMEBUFFER, fbo);
		GLErrors.check();
		glViewport(0, 0, getWidth(), getHeight());
		GLErrors.check();
	}
	
	@Override
	public void clear(int which) {
		glClear(which);
		GLErrors.check();
	}
	
	@Override
	public void clear() {
		clear(COLOR | DEPTH);
	}
}
