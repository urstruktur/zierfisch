package com.zierfisch;

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
	}

	@Override
	public void exit() {
	}

	@Override
	public void update() {
	}

	@Override
	public void render() {
		shader.render(cuboid);
	}
}
