package com.zierfisch.render;

import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glViewport;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER;
import static org.lwjgl.opengl.GL30.glBindFramebuffer;

public abstract class AbstractSurface implements Surface {

	int fbo;
	
	public AbstractSurface(int fbo) {
		this.fbo = fbo;
	}
	
	@Override
	public void bind() {
		glBindFramebuffer(GL_FRAMEBUFFER, fbo);
		glViewport(0, 0, getWidth(), getHeight());
	}
	
	public void clear(int which) {
		glClear(which);
	}
	
	@Override
	public void clear() {
		clear(COLOR | DEPTH);
	}
}
