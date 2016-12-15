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

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.zierfisch.app.ApplicationListener;
import com.zierfisch.cam.Camera;
import com.zierfisch.cam.CameraSystem;
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
		
		glEnable(GL_DEPTH_TEST);
		glEnable(GL_CULL_FACE);
	}

	private void addMainCamera() {
		Pose pose = new Pose();
		pose.position.set(0, 0, 1.1f);
		pose.orientation.rotateY((float) Math.PI);
		//pose.setFocus(new Vector3f());
		
		Entity cam = new Entity();
		cam.add(new Camera());
		cam.add(pose);
		
		engine.addEntity(cam);
	}

	private void initEngine() {
		 engine = new Engine();
		 
		 engine.addSystem(new CameraSystem());
		 engine.addSystem(new RenderSystem());
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
}
