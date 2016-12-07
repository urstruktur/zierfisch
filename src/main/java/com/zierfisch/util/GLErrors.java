package com.zierfisch.util;

import static org.lwjgl.opengl.GL11.*;

public class GLErrors {

	public static void check() {
		int err = glGetError();
		if(err != GL_NO_ERROR) {
			String errStr = errorEnumToString(err);
			throw new RuntimeException("OpenGL signalled an error: " + errStr);
		}
	}
	
	public static void check(String contextInfo) {
		int err = glGetError();
		if(err != GL_NO_ERROR) {
			String errStr = errorEnumToString(err);
			throw new RuntimeException("OpenGL signalled an error: " + errStr + "\n" + contextInfo);
		}
	}
	
	public static String errorEnumToString(int glEnum) {
		switch(glEnum) {
			case GL_INVALID_ENUM:
				return "GL_INVALID_ENUM";
				
			case GL_INVALID_VALUE:
				return "GL_INVALID_VALUE";
				
			case GL_INVALID_OPERATION:
				return "GL_INVALID_OPERATION";
				
			case GL_OUT_OF_MEMORY:
				return "GL_OUT_OF_MEMORY";
				
			default:
				return "UNKNOWN ERROR ENUM";
		}
	}
		
}
