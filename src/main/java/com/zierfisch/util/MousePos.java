package com.zierfisch.util;

import org.lwjgl.glfw.GLFWCursorPosCallback;
import static org.lwjgl.glfw.GLFW.*;

public class MousePos extends GLFWCursorPosCallback{
	
	private static double x = 0;
	private static double y = 0;
	
	private static double deltaX = 0;
	private static double deltaY = 0;

    @Override
    public void invoke(long window, double xpos, double ypos) {
    	deltaX = xpos - x;
    	deltaY = ypos - y;
    	x = xpos;
    	y = ypos;
    	
    	glfwSetCursorPos(window, 200, 200);
    	glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_HIDDEN);
    }
    
    public static double getXDelta(){
    	double temp = deltaX;
    	deltaX = 0;
    	return temp;
    }

    public static double getYDelta(){
    	double temp = deltaY;
    	deltaY = 0;
    	return temp;
    }
}
