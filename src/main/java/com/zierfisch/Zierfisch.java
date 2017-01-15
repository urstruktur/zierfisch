package com.zierfisch;

import static org.lwjgl.opengl.GL11.GL_CULL_FACE;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.glEnable;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.utils.ImmutableArray;
import com.zierfisch.app.Application;
import com.zierfisch.app.ApplicationListener;
import com.zierfisch.cam.Camera;
import com.zierfisch.cam.CameraSystem;
import com.zierfisch.cam.PathFollowSystem;
import com.zierfisch.cam.PathFollower;
import com.zierfisch.flocking.Boid;
import com.zierfisch.flocking.FlockingSystem;
import com.zierfisch.maker.Maker;
import com.zierfisch.render.Light;
import com.zierfisch.render.Pose;
import com.zierfisch.render.RenderSystem;

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
		addAuxiliaryQuantumLightConvolutionAcceleratorBuffer();
		createFishflock(7, 5, 7, 0.4f);
		//createFishflock(3, 5, 3, 0.5f);
		
		glEnable(GL_DEPTH_TEST);
		glEnable(GL_CULL_FACE);
	}

	private void addAuxiliaryQuantumLightConvolutionAcceleratorBuffer() {
		Light light = new Light();
		
		Maker maker = new Maker().add(light);
		
		/*light.color.set(1.0f, 0.1f, 0.14f);
		Entity light1 = maker.setPosition(0.0f, 0.5f, -0.4f).build();*/
		
		// Changing the protoype affects the next built light, but not the already built one
		//light.color.set(0.0f, 1.1f, 0.14f);
		//Entity light2 = maker.setPosition(10.0f, 10.5f, 10.4f).add(light).build();
		
		//engine.addEntity(light2);
	}

	private void addMainCamera() {
		Pose pose = new Pose();
		pose.position.set(0, 0, 1f);
		pose.orientation.rotateY((float) Math.PI);
		//pose.setFocus(new Vector3f());
		
		Entity cam = new Entity();
		cam.add(new Camera());
		cam.add(new PathFollower());
		cam.add(pose);
		
		engine.addEntity(cam);
	}

	private void initEngine() {
		 engine = new Engine();
		 
		 engine.addSystem(new CameraSystem());
		 engine.addSystem(new RenderSystem(app.getPhysicalSurface()));
		 //engine.addSystem(new PathFollowSystem());

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
		
		GL11.glClearColor(0.0f, 0.17f, 0.27f, 1.0f);
		
		engine.update(dt);
	}
	
	
	public void createFishflock(int nrX, int nrY, int nrZ, float margin){
		Boid protoypeBoid = new Boid();
		protoypeBoid.influence = 0.3f;
		
		Maker maker = new Maker().setMesh("assets/models/zierfisch.obj")
								 .setShader("assets/shaders/cc/fish.vert.glsl", "assets/shaders/cc/fish.frag.glsl")
		                         .setTexture(0, "assets/textures/fish-diffuse.png")
		                         .setTexture(1, "assets/textures/fish-emission.png")
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
		
		ImmutableArray<Entity> allEnts = engine.getEntities();
		
		Entity randomEnt1 = allEnts.get(allEnts.size() - 1);
		Light light1 = new Light();
		light1.color.set(.8f, 1f, 1f);
		light1.intensity = 1.0f;
		randomEnt1.add(light1);
		
		
		/*Entity startLight = new Entity();
		startLight.add(new Light());
		startLight.add(new Pose());
		startLight.getComponent(Light.class).color.set(1.0f, 0.7f, 0.94f);
		startLight.getComponent(Light.class).intensity = 0.01f;
		startLight.getComponent(Pose.class).position.y = 1.0f;
		engine.addEntity(startLight);*/
		
		/*
		
		Entity randomEnt2 = allEnts.get((int) (Math.random() * allEnts.size()));
		Light light2 = new Light();
		light2.color.set(.6f, 1f, 1f);
		light2.intensity = 1.0f;
		randomEnt2.add(light2);
		
		Entity randomEnt3 = allEnts.get((int) (Math.random() * allEnts.size()));
		Light light3 = new Light();
		light3.color.set(.7f, 1f, 1f);
		light3.intensity = 10.0f;
		randomEnt3.add(light3);*/
	}
}
