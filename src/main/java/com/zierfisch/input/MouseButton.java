package com.zierfisch.input;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWMouseButtonCallback;

public class MouseButton extends GLFWMouseButtonCallback {
	
	private static boolean[] buttons = new boolean[65535];
	
	public static boolean windowActive = false;

	@Override
	public void invoke(long window, int button, int action, int mods) {
		buttons[button] = action == GLFW.GLFW_REPEAT || action == GLFW.GLFW_PRESS;
	}
	
	
}
