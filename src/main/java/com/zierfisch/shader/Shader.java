package com.zierfisch.shader;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glGetAttribLocation;
import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL20.glUniform1f;
import static org.lwjgl.opengl.GL20.glUniform1i;
import static org.lwjgl.opengl.GL20.glUniform2f;
import static org.lwjgl.opengl.GL20.glUniform3f;
import static org.lwjgl.opengl.GL20.glUniform4f;
import static org.lwjgl.opengl.GL20.glUniformMatrix4fv;
import static org.lwjgl.opengl.GL20.glUseProgram;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;

import java.nio.FloatBuffer;

import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.BufferUtils;

import com.zierfisch.util.GLErrors;
import com.zierfisch.vbo.VBO;

import xyz.krachzack.gfx.mesh.Mesh;
import xyz.krachzack.gfx.mesh.Primitive;
import xyz.krachzack.gfx.mesh.VertexAttribute;

/**
 * Represents a shader program.
 * 
 * @author phil
 */
public class Shader {
	
	private static FloatBuffer tmp4x4Buf = BufferUtils.createFloatBuffer(4*4);

	private int name;
	
	public Shader(int name) {
		this.name = name;
	}
	
	public void bind() {
		glUseProgram(name);
	}
	
	public void setUniform(int location, int value) {
		glUniform1i(location, value);
	}
	
	public void setUniform(int location, float value) {
		glUniform1f(location, value);
	}
	
	public void setUniform(int location, Vector2f value) {
		glUniform2f(location, value.x, value.y);
	}
	
	public void setUniform(int location, Vector3f value) {
		glUniform3f(location, value.x, value.y, value.z);
	}
	
	public void setUniform(int location, Vector4f value) {
		glUniform4f(location, value.x, value.y, value.z, value.w);
	}
	
	public void setUniform(int location, Matrix4f value) {
		tmp4x4Buf.clear();
		glUniformMatrix4fv(location, false, value.get(tmp4x4Buf));
	}
	
	public void setUniform(int location, Matrix3f value) {
		tmp4x4Buf.clear();
		glUniformMatrix4fv(location, false, value.get(tmp4x4Buf));
	}
	
	public void setUniform(String uniformName, int value) {
		setUniform(getUniformLocation(uniformName), value);
	}
	
	public void setUniform(String uniformName, float value) {
		setUniform(getUniformLocation(uniformName), value);
	}
	
	public void setUniform(String uniformName, Vector2f value) {
		setUniform(getUniformLocation(uniformName), value);
	}
	
	public void setUniform(String uniformName, Vector3f value) {
		setUniform(getUniformLocation(uniformName), value);
	}
	
	public void setUniform(String uniformName, Vector4f value) {
		setUniform(getUniformLocation(uniformName), value);
	}
	
	public void setUniform(String uniformName, Matrix3f value) {
		setUniform(getUniformLocation(uniformName), value);
	}
	
	public void setUniform(String uniformName, Matrix4f value) {
		setUniform(getUniformLocation(uniformName), value);
	}
	
	public int getUniformLocation(String uniformName) {
		return glGetUniformLocation(name, uniformName);
	}
	
	public int getAttributeLocation(String attrName) {
		return glGetAttribLocation(name, attrName);
	}
	
	public void render(Mesh mesh) {
		bind();
		
		VBO vertexBuffer = mesh.getVertexBuffer();
		VBO indexBuffer = mesh.getIndexBuffer();
		
		vertexBuffer.bind();
		GLErrors.check("Binding vertex buffer during render");
		
		indexBuffer.bind();
		GLErrors.check("Binding index buffer during render");
		
		for(VertexAttribute attr: mesh.getAttributes()) {
			int loc = getAttributeLocation(attr.name);
			GLErrors.check("Getting attribute location");
			
			if(loc != -1) {
				glVertexAttribPointer(loc, attr.width, GL_FLOAT, false, attr.stride, 0);
				GLErrors.check("Setting vertex attrib pointers");
				
				glEnableVertexAttribArray(loc);
				GLErrors.check("Enabling vertex attrib array");
			}
		}
		
		if(mesh.getType() != Primitive.TRIANGLES) {
			throw new RuntimeException("Only triangles can be rendered right now");
		}
		
		glDrawElements(GL_TRIANGLES, mesh.getIndexes().length, GL_UNSIGNED_INT, 0);
		GLErrors.check("Indexed drawing");
	}

}
