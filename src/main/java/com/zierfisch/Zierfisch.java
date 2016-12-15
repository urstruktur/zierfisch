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
import com.zierfisch.app.ApplicationListener;
import com.zierfisch.cam.Camera;
import com.zierfisch.cam.CameraSystem;
import com.zierfisch.flocking.FlockingComponent;
import com.zierfisch.flocking.FlockingSystem;
import com.zierfisch.gui.TweakingSystem;
import com.zierfisch.render.Gestalt;
import com.zierfisch.render.MovementSystem;
import com.zierfisch.render.Pose;
import com.zierfisch.render.RenderSystem;
import com.zierfisch.shader.Shader;
import com.zierfisch.util.ObjImporter;

import xyz.krachzack.gfx.mesh.Mesh;
import xyz.krachzack.gfx.mesh.MeshBuilder;
import xyz.krachzack.gfx.mesh.Primitive;
import xyz.krachzack.gfx.mesh.SegmentedMeshBuilder;

public class Zierfisch implements ApplicationListener {

	private Engine engine;
	private static Shader shader;
	private static Mesh cuboid;
	private static Mesh objMesh;
	private Matrix4f scale = new Matrix4f();
	
	@Override
	public void enter() {
		int vao = glGenVertexArrays();
		glBindVertexArray(vao);
		
		ObjImporter importer = new ObjImporter();
		try {
			importer.load("assets/models/pascal.obj");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		MeshBuilder objBuilder = new SegmentedMeshBuilder(Primitive.TRIANGLES);
		objMesh = importer.make(objBuilder);
		
		initEngine();
		addMainCamera();
		createFishflock(5, 5, 5, 1);
		
		glEnable(GL_DEPTH_TEST);
		glEnable(GL_CULL_FACE);
	}

	private void addMainCamera() {
		Pose pose = new Pose();
		pose.position.set(0, 0, 9.9f);
		pose.orientation.rotateY((float) Math.PI);
		//pose.setFocus(new Vector3f());
		
		Entity cam = new Entity();
		cam.add(new Camera());
		cam.add(pose);
		
		engine.addEntity(cam);
	}

	private void initEngine() {
		 engine = new Engine();
		 
		 engine.addSystem(new MovementSystem());
		 engine.addSystem(new CameraSystem());
		 engine.addSystem(new RenderSystem());
		 engine.addSystem(new FlockingSystem());
		 //engine.addSystem(new TweakingSystem());
	}

	@Override
	public void exit() {
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
		
		GL11.glClearColor(0.5f, 0.5f, 0.6f, 1.0f);
		engine.update(dt);
	}
	
	public void createFishflock(int nrX, int nrY, int nrZ, float margin){
		Component gestalt = RenderSystem.makeDefaultGestalt();
		
		for(int x = 0; x < nrX; x++){
			for(int y = 0; y < nrY; y++){
				for(int z = 0; z < nrZ; z++){
					Entity fish = new Entity();
					Pose p = new Pose();
					p.scale = 0.5f;
					p.mass = 1f;
					p.position.x = x*margin - (nrX-1)*margin/2;
					p.position.y = y*margin - (nrY-1)*margin/2;
					p.position.z = z*margin - (nrZ-1)*margin/2;
					//p.acceleration = new Vector3f(0,0,-.01f);
					
					fish.add(new FlockingComponent());
					fish.getComponent(FlockingComponent.class).influence = 0.3f;
					fish.add(p);
					fish.add(gestalt);
					p.smut();
					engine.addEntity(fish);
				}
			}
		}
	}
}
