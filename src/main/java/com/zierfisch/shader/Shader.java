package com.zierfisch.shader;

import xyz.krachzack.gfx.mesh.Mesh;
import xyz.krachzack.gfx.mesh.Primitive;
import xyz.krachzack.gfx.mesh.VertexAttribute;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;

import com.zierfisch.util.GLErrors;
import com.zierfisch.vbo.VBO;

/**
 * Represents a shader program.
 * 
 * @author phil
 */
public class Shader {

	private int name;
	
	public Shader(int name) {
		this.name = name;
	}
	
	public void bind() {
		glUseProgram(name);
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
