package com.zierfisch.util;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import static org.lwjgl.glfw.GLFW.GLFW_MOD_SHIFT;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_REPEAT;
import static org.lwjgl.glfw.GLFW.glfwDestroyWindow;

import org.lwjgl.glfw.GLFWKeyCallback;

public class Keyboard extends GLFWKeyCallback{

	private static boolean[] keys = new boolean[65535];
	private static boolean shift = false;

	@Override
	public void invoke(long window, int key, int scancode, int action, int mod) {
		keys[key] = action == GLFW_REPEAT || action == GLFW_PRESS;
		shift = mod == GLFW_MOD_SHIFT;

		if(key == GLFW_KEY_ESCAPE && action == GLFW_PRESS){
			glfwDestroyWindow(window);
			System.exit(0);
		}
	}
	
	// boolean method that returns true if a given key
	// is pressed.
	public static boolean isKeyDown(int keycode) {
		return keys[keycode];
	}
	
	public static boolean isShiftDown(){
		return shift;
	}
	
}
