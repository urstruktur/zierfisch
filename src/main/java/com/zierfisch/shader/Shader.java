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
		if(location != -1) {
			glUniform1i(location, value);
		}
	}
	
	public void setUniform(int location, float value) {
		if(location != -1) {
			glUniform1f(location, value);
		}
	}
	
	public void setUniform(int location, Vector2f value) {
		if(location != -1) {
			glUniform2f(location, value.x, value.y);
		}
	}
	
	public void setUniform(int location, Vector3f value) {
		if(location != -1) {
			glUniform3f(location, value.x, value.y, value.z);
		}
	}
	
	public void setUniform(int location, Vector4f value) {
		if(location != -1) {
			glUniform4f(location, value.x, value.y, value.z, value.w);
		}
	}
	
	public void setUniform(int location, Matrix4f value) {
		if(location != -1) {
			tmp4x4Buf.clear();
			glUniformMatrix4fv(location, false, value.get(tmp4x4Buf));
		}
	}
	
	public void setUniform(int location, Matrix3f value) {
		if(location != -1) {
			tmp4x4Buf.clear();
			glUniformMatrix4fv(location, false, value.get(tmp4x4Buf));
		}
	}
	
	public void setUniform(String uniformName, int value) {
		int loc = getUniformLocation(uniformName);
		
		if(loc != -1) {
			setUniform(getUniformLocation(uniformName), value);
		}
	}
	
	public void setUniform(String uniformName, float value) {
		int loc = getUniformLocation(uniformName);
		
		if(loc != -1) {
			setUniform(getUniformLocation(uniformName), value);
		}
	}
	
	public void setUniform(String uniformName, Vector2f value) {
		int loc = getUniformLocation(uniformName);
		
		if(loc != -1) {
			setUniform(getUniformLocation(uniformName), value);
		}
	}
	
	public void setUniform(String uniformName, Vector3f value) {
		int loc = getUniformLocation(uniformName);
		
		if(loc != -1) {
			setUniform(getUniformLocation(uniformName), value);
		}
	}
	
	public void setUniform(String uniformName, Vector4f value) {
		int loc = getUniformLocation(uniformName);
		
		if(loc != -1) {
			setUniform(getUniformLocation(uniformName), value);
		}
	}
	
	public void setUniform(String uniformName, Matrix3f value) {
		int loc = getUniformLocation(uniformName);
		
		if(loc != -1) {
			setUniform(getUniformLocation(uniformName), value);
		}
	}
	
	public void setUniform(String uniformName, Matrix4f value) {
		int loc = getUniformLocation(uniformName);
		
		if(loc != -1) {
			setUniform(getUniformLocation(uniformName), value);
		}
	}
	
	public int getUniformLocation(String uniformName) {
		return glGetUniformLocation(name, uniformName);
	}
	
	public int getAttributeLocation(String attrName) {
		return glGetAttribLocation(name, attrName);
	}
	
	public void render(Mesh mesh) {
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
				final int glFloatSize = 4;
				glVertexAttribPointer(loc, attr.width, GL_FLOAT, false, glFloatSize*attr.stride, glFloatSize*attr.offset);
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
