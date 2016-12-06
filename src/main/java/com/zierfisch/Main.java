package com.zierfisch;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;

public class Main {

	private static long window;

	public static void main(String[] args) {
		try {
			GLFWErrorCallback.createPrint(System.err).set();
			if (!glfwInit()) {
	            throw new IllegalStateException("Error initializing GLFW");
	        }

	        // Window Hints for OpenGL context
	        glfwWindowHint(GLFW_SAMPLES, 4);
	        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
	        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
	        glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GL_TRUE);
	        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
	        glfwWindowHint(GLFW_RESIZABLE, GL_FALSE);
	        window = glfwCreateWindow(640, 480, "zierfisch", 0, 0);

	        if (window == 0) {
	            System.err.println("Error creating a window");
	            System.exit(1);
	        }

	        glfwMakeContextCurrent(window);
	        
	        glfwSwapInterval(1);

			// Make the window visible
			glfwShowWindow(window);

			loop();
	        
	        glfwFreeCallbacks(window);
			glfwDestroyWindow(window);
		} finally {
			glfwTerminate();
			//glfwSetErrorCallback(null).free();
		}
		
	}

	private static void loop() {
		// This line is critical for LWJGL's interoperation with GLFW's
		// OpenGL context, or any context that is managed externally.
		// LWJGL detects the context that is current in the current thread,
		// creates the GLCapabilities instance and makes the OpenGL
		// bindings available for use.
		GL.createCapabilities();

		// Set the clear color
		glClearColor(1.0f, 0.0f, 0.0f, 0.0f);

		// Run the rendering loop until the user has attempted to close
		// the window or has pressed the ESCAPE key.
		while ( !glfwWindowShouldClose(window) ) {
			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer

			glfwSwapBuffers(window); // swap the color buffers

			// Poll for window events. The key callback above will only be
			// invoked during this call.
			glfwPollEvents();
		}
	}

}
