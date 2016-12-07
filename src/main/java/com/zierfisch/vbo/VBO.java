package com.zierfisch.vbo;

import static org.lwjgl.opengl.GL15.*;

public class VBO {

	private int name = Integer.MIN_VALUE;
	private int target;
	
	private void verifyHasContents() {
		if(name == Integer.MIN_VALUE) {
			throw new RuntimeException("Trying to use buffer that has no contents set");
		}
	}
	
	public int getName() {
		verifyHasContents();
		return name;
	}
	
	public void bind() {
		verifyHasContents();
		glBindBuffer(target, name);
	}
	
	public void setContents(float[] data) {
		name = glGenBuffers();
		target = GL_ARRAY_BUFFER;
		glBindBuffer(target, name);
        glBufferData(name, data, GL_STATIC_DRAW);
	}
	
	public void setContents(short[] data) {
		name = glGenBuffers();
		target = GL_ELEMENT_ARRAY_BUFFER;
		glBindBuffer(target, name);
        glBufferData(name, data, GL_STATIC_DRAW);
	}

}
