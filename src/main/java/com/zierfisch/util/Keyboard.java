package com.zierfisch.util;

import org.lwjgl.glfw.GLFW;

import static org.lwjgl.glfw.GLFW.*;

import org.lwjgl.glfw.GLFWKeyCallback;

public class Keyboard extends GLFWKeyCallback{

	private static boolean[] keys = new boolean[65535];
	private static boolean shift = false;

	@Override
	public void invoke(long window, int key, int scancode, int action, int mod) {
		keys[key] = action == GLFW.GLFW_REPEAT || action == GLFW.GLFW_PRESS;
		shift = mod == GLFW_MOD_SHIFT;
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
