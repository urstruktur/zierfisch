package com.zierfisch.input;

import org.lwjgl.glfw.GLFWCursorPosCallback;
import static org.lwjgl.glfw.GLFW.*;

import org.lwjgl.glfw.GLFW;

public class MousePos extends GLFWCursorPosCallback {
	
	private static double x = 0;
	private static double y = 0;
	
	private static double deltaX = 0;
	private static double deltaY = 0;
	
	public boolean focused = true;
	
    @Override
    public void invoke(long window, double xpos, double ypos) {
    	glfwSetInputMode(window, GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_DISABLED);
    	
    	if(focused){
        	deltaX = xpos - x;
        	deltaY = ypos - y;
    	}

    	x = xpos;
    	y = ypos;
    }
    
    public static double getXDelta() {
    	double temp = deltaX;
    	deltaX = 0;
    	return temp;
    }

    public static double getYDelta() {
    	double temp = deltaY;
    	deltaY = 0;
    	return temp;
    }
}
