package com.zierfisch;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

import com.zierfisch.app.ApplicationListener;
import com.zierfisch.shader.Shader;
import com.zierfisch.shader.ShaderBuilder;

import xyz.krachzack.gfx.assets.CuboidMaker;
import xyz.krachzack.gfx.mesh.Mesh;
import xyz.krachzack.gfx.mesh.MeshBuilder;
import xyz.krachzack.gfx.mesh.Primitive;
import xyz.krachzack.gfx.mesh.SegmentedMeshBuilder;

public class Zierfisch implements ApplicationListener {

	private static Shader shader;
	private static Mesh cuboid;
	private static Mesh objMesh;
	private Matrix4f scale = new Matrix4f();
	
	@Override
	public void enter() {
		int vao = glGenVertexArrays();
		glBindVertexArray(vao);
		
		shader = new ShaderBuilder().setVertexShader("assets/shaders/cc/cc.vert.glsl")
                                    .setFragmentShader("assets/shaders/cc/cc.frag.glsl")
                                    .build();

		MeshBuilder builder = new SegmentedMeshBuilder(Primitive.TRIANGLES);
		CuboidMaker cuboidMaker = new CuboidMaker();
		cuboid = cuboidMaker.make(builder, 0.5);
		
		ObjImporter importer = new ObjImporter();
		try {
			importer.load("assets/models/pascal.obj");
		} catch (IOException e) {
			e.printStackTrace();
		}
		MeshBuilder objBuilder = new SegmentedMeshBuilder(Primitive.TRIANGLES);
		objMesh = importer.make(objBuilder);
		
		glEnable(GL_DEPTH_TEST);
		glEnable(GL_CULL_FACE);
	}

	@Override
	public void exit() {
	}
	
	static long startTime = System.currentTimeMillis();

	@Override
	public void update() {
		float secs = (System.currentTimeMillis() - startTime) / 1000.0f;
		float sine = (float) Math.sin(secs);
		float rotation = (float) (sine * Math.PI);
		
		System.out.println(rotation);
		
		//scale.rotateY(rotation);
		scale.scale((sine + 1) / 2.0f);
	}

	@Override
	public void render() {
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		
		shader.bind();
		
		shader.setUniform("u_model", scale);
		
		shader.render(cuboid);
		shader.render(objMesh);
	}
}
