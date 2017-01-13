package com.zierfisch;

import static org.lwjgl.opengl.GL11.GL_CULL_FACE;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

import java.io.IOException;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.zierfisch.app.Application;
import com.zierfisch.app.ApplicationListener;
import com.zierfisch.cam.Camera;
import com.zierfisch.cam.CameraSystem;
import com.zierfisch.flocking.Boid;
import com.zierfisch.flocking.FlockingSystem;
import com.zierfisch.gui.TweakingSystem;
import com.zierfisch.maker.Maker;
import com.zierfisch.render.Gestalt;
import com.zierfisch.render.Light;
import com.zierfisch.render.Pose;
import com.zierfisch.render.RenderSystem;
import com.zierfisch.tex.TextureLoader;
import com.zierfisch.util.GLErrors;
import com.zierfisch.util.ObjImporter;

import xyz.krachzack.gfx.mesh.Mesh;
import xyz.krachzack.gfx.mesh.MeshBuilder;
import xyz.krachzack.gfx.mesh.Primitive;
import xyz.krachzack.gfx.mesh.SegmentedMeshBuilder;

public class Zierfisch implements ApplicationListener {

	private Application app;
	private Engine engine;
	private Matrix4f scale = new Matrix4f();
	
	@Override
	public void enter(Application app) {
		this.app = app;
		
		Entity enviroment = new Entity();
		enviroment.add(RenderSystem.makeEnviromentGestalt());
		enviroment.add(new Pose());
		
		initEngine();
		
		engine.addEntity(enviroment);
		addMainCamera();
		createFishflock(7, 5, 7, 0.4f);
		//createFishflock(3, 5, 3, 0.5f);
		
		glEnable(GL_DEPTH_TEST);
		glEnable(GL_CULL_FACE);
	}

	private void addMainCamera() {
		Pose pose = new Pose();
		pose.position.set(0, 0, 1f);
		pose.orientation.rotateY((float) Math.PI);
		//pose.setFocus(new Vector3f());
		
		Entity cam = new Entity();
		cam.add(new Camera());
		cam.add(pose);
		cam.add(new Light());
		
		engine.addEntity(cam);
	}

	private void initEngine() {
		 engine = new Engine();
		 
		 engine.addSystem(new CameraSystem());
		 engine.addSystem(new RenderSystem(app.getPhysicalSurface()));

		 //engine.addSystem(new TweakingSystem());
		 engine.addSystem(new FlockingSystem());
	}

	@Override
	public void exit(Application app) {
	}
	
	static long startTime = System.currentTimeMillis();

	@Override
	public void update(float dt) {
		float secs = (System.currentTimeMillis() - startTime) / 1000.0f;
		float sine = (float) Math.sin(secs);
		float rotation = (float) (sine * Math.PI);
		
		//scale.rotateY(rotation);
		scale.identity();
		scale.scale((sine + 1) / 2.0f);
		
		GL11.glClearColor(0.0f, 0.2f, 0.3f, 1.0f);
		
		engine.update(dt);
	}
	
	
	public void createFishflock(int nrX, int nrY, int nrZ, float margin){
		Boid protoypeBoid = new Boid();
		protoypeBoid.influence = 0.3f;
		
		Maker maker = new Maker().setMesh("assets/models/zierfisch.obj")
		                         .setTexture(0, "assets/textures/fish-diffuse.png")
		                         .setTexture(4, "assets/textures/fog-gradient-03.png")
		                         .setScale(0.5f)
		                         .add(protoypeBoid);
		
		
		Vector3f center = new Vector3f(0,0,0);
		
		for(int x = 0; x < nrX; x++){
			for(int y = 0; y < nrY; y++){
				for(int z = 0; z < nrZ; z++){
					
					Entity fish = maker.setPosition(
						x*margin - (nrX-1)*margin/2 + center.x,
						y*margin - (nrY-1)*margin/2 + center.y,
						z*margin - (nrZ-1)*margin/2 + center.z
					).build();
					
					engine.addEntity(fish);
				}
			}
		}
	}
}
