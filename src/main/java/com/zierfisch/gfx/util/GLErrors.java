package com.zierfisch.gfx.util;

import static org.lwjgl.opengl.GL11.*;

public class GLErrors {
	
	private static final boolean ERROR_CHECKING_ENABLED = true;

	public static void check() {
		if(ERROR_CHECKING_ENABLED) {
			int err = glGetError();
			if(err != GL_NO_ERROR) {
				String errStr = errorEnumToString(err);
				throw new RuntimeException("OpenGL signalled an error: " + errStr);
			}
		}
	}
	
	public static void check(String contextInfo) {
		if(ERROR_CHECKING_ENABLED) {
			int err = glGetError();
			if(err != GL_NO_ERROR) {
				String errStr = errorEnumToString(err);
				throw new RuntimeException("OpenGL signalled an error: " + errStr + "\n" + contextInfo);
			}
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
				
			case GL_STACK_OVERFLOW:
				return "GL_STACK_OVERFLOW";
				
			case GL_STACK_UNDERFLOW:
				return "GL_STACK_UNDERFLOW";
				
			case 0x0506:
				return "GL_INVALID_FRAMEBUFFER_OPERATION";
				
			default:
				return "UNKNOWN ERROR ENUM: " + glEnum;
		}
	}
		
}
