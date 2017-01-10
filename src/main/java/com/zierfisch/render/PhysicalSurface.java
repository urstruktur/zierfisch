package com.zierfisch.render;

import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

public class PhysicalSurface implements Surface {

	int fbo;
	
	public PhysicalSurface(int physicalFbo) {
		fbo = physicalFbo;
	}
	
	@Override
	public int getWidth() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getHeight() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void bind() {
		// Note that this is not guaranteed to work
		// Usually 0 is used for default rendering, but some platforms like iOS
		// are known to implement this differently.
		// An alternative aproach is to get the initially bound framebuffer, which
		// is actually already done in application but then never used.
		glBindFramebuffer(GL_FRAMEBUFFER, 0);
	}

	@Override
	public void clear() {
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
	}

}
