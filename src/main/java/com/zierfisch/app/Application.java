package com.zierfisch.app;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWWindowFocusCallback;
import org.lwjgl.opengl.GL;

import com.zierfisch.render.PhysicalSurface;
import com.zierfisch.render.Surface;
import com.zierfisch.util.Keyboard;
import com.zierfisch.util.MouseButton;
import com.zierfisch.util.MousePos;

public class Application {

	private static boolean glfwLoaded = false;
	
	private static void ensureGlfwLoaded() {
		if(!glfwLoaded) {
			GLFWErrorCallback.createPrint(System.err).set();
			if (!glfwInit()) {
	            throw new IllegalStateException("Error initializing GLFW");
	        }
			
			glfwLoaded = true;
		}
	}
	
	private long window;
	private boolean exitScheduled = false;
	private ApplicationListener listener;
	private String title = "Mysterious Window";
	/** The initial width of the window, may change later */
	private int windowWidth = 1000;
	/** The initial height of the window, may change later */
	private int windowHeight = 600;
	
	private int defaultVao;
	private Surface physicalSurface;
	
	public Application(ApplicationListener listener) {
		this.listener = listener;
	}
	
	public void setWindowSize(int width, int height) {
		this.windowWidth = width;
		this.windowHeight = height;
	}
	
	public Surface getPhysicalSurface() {
		return physicalSurface;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getTitle() {
		return title;
	}
	
	public boolean isExitScheduled() {
		return exitScheduled;
	}
	
	private void setWindowHints() {
		glfwWindowHint(GLFW_SAMPLES, 4);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
        glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GL_TRUE);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        glfwWindowHint(GLFW_RESIZABLE, GL_FALSE);
	}
	
	private void initWindow() {
		window = glfwCreateWindow(windowWidth, windowHeight, title, 0, 0);
        if (window == 0) {
        	throw new IllegalStateException("Error creating GLFW window");
        }
	}
	
	private void initContext() {
		glfwMakeContextCurrent(window);
        
        glfwSwapInterval(1);

		// This line is critical for LWJGL's interoperation with GLFW's
		// OpenGL context, or any context that is managed externally.
		// LWJGL detects the context that is current in the current thread,
		// creates the GLCapabilities instance and makes the OpenGL
		// bindings available for use.
		GL.createCapabilities();
	}
	
	private void initDefaultVAO() {
		defaultVao = glGenVertexArrays();
		glBindVertexArray(defaultVao);
	}
	
	/**
	 * Finds the name of the framebuffer object that is used for normal rendering.
	 * This will most likely but not always on all platforms be zero.
	 */
	private void initPhysicalSurface() {
		int fbo = glGetInteger(GL_FRAMEBUFFER_BINDING);
		physicalSurface = new PhysicalSurface(fbo);
	}

	/**
	 * Tells the application to shut down after the current thread.
	 */
	public void exit() {
		exitScheduled = true;
	}
	
	public void run() {
		ensureGlfwLoaded();
		setWindowHints();
		initWindow();
		glfwShowWindow(window);
		initContext();
		initDefaultVAO();
		initPhysicalSurface();
		glfwSetKeyCallback(window, new Keyboard());	
		MousePos m = new MousePos();
		glfwSetCursorPosCallback(window, m);
		glfwSetMouseButtonCallback(window, new MouseButton());
		glfwSetWindowFocusCallback(window, new GLFWWindowFocusCallback() {
			@Override
			public void invoke(long window, boolean focused) {
				m.focused = focused;
				if(focused){
					glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_HIDDEN);
				}else{
					glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_NORMAL);
				}
				
			}
		});
		
		listener.enter();
		while(!glfwWindowShouldClose(window) && !exitScheduled) {
			glfwPollEvents();
			
			listener.update(16.0f);
			
			glfwSwapBuffers(window);
		}
		listener.exit();
		
		glfwFreeCallbacks(window);
		glfwDestroyWindow(window);
		
		System.out.println("das ist das letzte");
		System.exit(0);
	}
}
