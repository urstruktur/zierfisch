package com.zierfisch.tex;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBindTexture;

public class Texture {
	private int name;

	public Texture(int name) {
		super();
		this.name = name;
	}
	
	public void bind() {
		glBindTexture(GL_TEXTURE_2D, name);
	}
}
