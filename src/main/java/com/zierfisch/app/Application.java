package com.zierfisch.app;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.GLFW_CONTEXT_VERSION_MAJOR;
import static org.lwjgl.glfw.GLFW.GLFW_CONTEXT_VERSION_MINOR;
import static org.lwjgl.glfw.GLFW.GLFW_CURSOR;
import static org.lwjgl.glfw.GLFW.GLFW_CURSOR_HIDDEN;
import static org.lwjgl.glfw.GLFW.GLFW_CURSOR_NORMAL;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_CORE_PROFILE;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_FORWARD_COMPAT;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_PROFILE;
import static org.lwjgl.glfw.GLFW.GLFW_RESIZABLE;
import static org.lwjgl.glfw.GLFW.GLFW_SAMPLES;
import static org.lwjgl.glfw.GLFW.glfwCreateWindow;
import static org.lwjgl.glfw.GLFW.glfwDestroyWindow;
import static org.lwjgl.glfw.GLFW.glfwGetFramebufferSize;
import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwSetCursorPosCallback;
import static org.lwjgl.glfw.GLFW.glfwSetFramebufferSizeCallback;
import static org.lwjgl.glfw.GLFW.glfwSetInputMode;
import static org.lwjgl.glfw.GLFW.glfwSetKeyCallback;
import static org.lwjgl.glfw.GLFW.glfwSetMouseButtonCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowFocusCallback;
import static org.lwjgl.glfw.GLFW.glfwShowWindow;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.glfw.GLFW.glfwSwapInterval;
import static org.lwjgl.glfw.GLFW.glfwWindowHint;
import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;
import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL11.GL_TRUE;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.concurrent.TimeUnit;

import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWFramebufferSizeCallback;
import org.lwjgl.glfw.GLFWWindowFocusCallback;
import org.lwjgl.opengl.GL;

import com.zierfisch.render.PhysicalSurface;
import com.zierfisch.render.Surface;
import com.zierfisch.time.DeltaTimer;
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
	private PhysicalSurface physicalSurface;
	private DeltaTimer timer;
	private float deltaTime;
	private NumberFormat secondsFormat = new DecimalFormat("###.###");
	
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
	
	/**
	 * Gets the delta time since the last iteration of the main loop.
	 * 
	 * @return the delta time
	 */
	public float getDeltaTime() {
		return deltaTime;
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
		// int fbo = glGetInteger(GL_FRAMEBUFFER_BINDING);
		physicalSurface = new PhysicalSurface();
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
		
		glfwSetFramebufferSizeCallback(window, new GLFWFramebufferSizeCallback() {
			@Override
			public void invoke(long window, int width, int height) {
				physicalSurface.resize(width, height);
			}
		});
		int[] width = new int[1];
		int[] height = new int[1];
		glfwGetFramebufferSize(window, width, height);
		physicalSurface.resize(width[0], height[0]);
		
		listener.enter(this);
		timer = new DeltaTimer();
		while(!glfwWindowShouldClose(window) && !exitScheduled) {
			deltaTime = timer.getDelta();
			timer.setReference();
			
			if(deltaTime > (1/50f)) {
				System.err.println("SHEESH that last frame was insanely long: " + secondsFormat.format(deltaTime) + "s");
			}
			
			glfwPollEvents();
			listener.update(deltaTime * 1000);
			
			glfwSwapBuffers(window);
		}
		listener.exit(this);
		
		glfwFreeCallbacks(window);
		glfwDestroyWindow(window);
		
		System.exit(0);
	}
}
