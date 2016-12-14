package com.zierfisch;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

import java.io.IOException;

import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;

import com.badlogic.ashley.core.Engine;
import com.zierfisch.app.ApplicationListener;
import com.zierfisch.render.RenderSystem;
import com.zierfisch.shader.Shader;
import com.zierfisch.shader.ShaderBuilder;
import com.zierfisch.util.ObjImporter;

import xyz.krachzack.gfx.assets.CuboidMaker;
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
		
		glEnable(GL_DEPTH_TEST);
		glEnable(GL_CULL_FACE);
	}

	private void initEngine() {
		 engine = new Engine();
		 
		 RenderSystem sys = new RenderSystem();
		 engine.addSystem(sys);
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
