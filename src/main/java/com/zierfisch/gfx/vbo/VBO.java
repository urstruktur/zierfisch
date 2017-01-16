package com.zierfisch.gfx.vbo;

import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_ELEMENT_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glGenBuffers;

import com.zierfisch.gfx.util.GLErrors;

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

	/**
	 * Sets the contents of this buffer to the given float array.
	 * 
	 * Note that this leaves this VBO bound to GL_ARRAY_BUFFER as a side effect.
	 * 
	 * @param data
	 */
	public void setContents(float[] data) {
		name = glGenBuffers();
		target = GL_ARRAY_BUFFER;
		glBindBuffer(target, name);
        glBufferData(target, data, GL_STATIC_DRAW);
        GLErrors.check();
	}
	
	/**
	 * Sets the contents of this buffer to the given short array.
	 * 
	 * Note that this leaves this VBO bound to GL_ELEMENT_ARRAY_BUFFER as a side effect.
	 * 
	 * @param data
	 */
	public void setContents(short[] data) {
		name = glGenBuffers();
		target = GL_ELEMENT_ARRAY_BUFFER;
		glBindBuffer(target, name);
        glBufferData(target, data, GL_STATIC_DRAW);
        GLErrors.check();
	}
	
	/**
	 * Sets the contents of this buffer to the given short array.
	 * 
	 * Note that this leaves this VBO bound to GL_ELEMENT_ARRAY_BUFFER as a side effect.
	 * 
	 * @param data
	 */
	public void setContents(int[] data) {
		name = glGenBuffers();
		target = GL_ELEMENT_ARRAY_BUFFER;
		glBindBuffer(target, name);
        glBufferData(target, data, GL_STATIC_DRAW);
        GLErrors.check();
	}

}
