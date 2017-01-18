package com.zierfisch;

import static org.lwjgl.opengl.GL11.GL_CULL_FACE;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.glEnable;

import org.lwjgl.opengl.GL11;

import com.badlogic.ashley.core.Engine;
import com.zierfisch.app.Application;
import com.zierfisch.app.ApplicationListener;

public class Zierfisch implements ApplicationListener {

	private Application app;
	private Engine engine;
	
	static long startTime = System.currentTimeMillis();
	
	@Override
	public void enter(Application app) {
		this.app = app;
		
		initEngine();
		initWorld();
		
		glEnable(GL_DEPTH_TEST);
		glEnable(GL_CULL_FACE);
	}

	private void initEngine() {
		engine = new Engine();
		Systems.add(app, engine);
	}

	private void initWorld() {
		World.add(engine);
	}

	@Override
	public void exit(Application app) {
	}
	
	@Override
	public void update(float dt) {
		GL11.glClearColor(0.0f, 0.17f, 0.27f, 1.0f);
		engine.update(dt);
	}
}
