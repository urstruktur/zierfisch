package com.zierfisch;

import com.badlogic.ashley.core.Engine;
import com.zierfisch.app.Application;
import com.zierfisch.audio.MusicSystem;
import com.zierfisch.cam.CameraSystem;
import com.zierfisch.flocking.FlockingSystem;
import com.zierfisch.gfx.ecs.RenderSystem;

public final class Systems {

	/**
	 * Adds all required systems to the engine
	 * 
	 * @param engine
	 */
	public static void add(Application app, Engine engine) {
		engine.addSystem(new CameraSystem());
		engine.addSystem(new RenderSystem(app.getPhysicalSurface()));
		engine.addSystem(new MusicSystem());
		//engine.addSystem(new PathFollowSystem());

		//engine.addSystem(new TweakingSystem());
		engine.addSystem(new FlockingSystem());
	}
	
}
