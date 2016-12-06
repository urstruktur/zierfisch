package com.zierfisch.shader;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL32.*;

public class ShaderBuilder {
	
	private int vertexShader = Integer.MIN_VALUE;
	private int fragmentShader = Integer.MIN_VALUE;
	private int geometryShader = Integer.MIN_VALUE;
	
	ShaderBuilder setVertexShader(String src) {
		vertexShader = compile(src, GL_VERTEX_SHADER);
		return this;
	}
	
	ShaderBuilder setFragmentShader(String src) {
		fragmentShader = compile(src, GL_FRAGMENT_SHADER);
		return this;
	}
	
	ShaderBuilder setGeometryShader(String src) {
		geometryShader = compile(src, GL_GEOMETRY_SHADER);
		return this;
	}
	
	Shader build() {
		if(vertexShader == Integer.MIN_VALUE || fragmentShader == Integer.MIN_VALUE) {
			throw new RuntimeException("At least vertex and fragment shader are required");
		}
		
		int shaderProgram = glCreateProgram();
        glAttachShader(shaderProgram, vertexShader);
        glAttachShader(shaderProgram, fragmentShader);
        
        if(geometryShader != Integer.MIN_VALUE) {
        	glAttachShader(shaderProgram, geometryShader);
        }

        glLinkProgram(shaderProgram);

        int[] isLinked = { GL_FALSE };
        glGetProgramiv(shaderProgram, GL_LINK_STATUS, isLinked);
        
        if(isLinked[0] == GL_FALSE) {
        	String log = glGetProgramInfoLog(shaderProgram);
        	
            glDeleteProgram(shaderProgram);
            //Don't leak shaders either.
            glDeleteShader(vertexShader);
            glDeleteShader(fragmentShader);
            if(geometryShader != Integer.MIN_VALUE) {
                glDeleteShader(geometryShader);
            }

            throw new RuntimeException("Shader program could not be linked:\n"  + log);
        } else {
        	//Always detach shaders after a successful link.
            glDetachShader(shaderProgram, vertexShader);
            glDetachShader(shaderProgram, vertexShader);
            if(geometryShader != Integer.MIN_VALUE) {
                glDetachShader(shaderProgram, geometryShader);
            }

            return new Shader(shaderProgram);
        }
	}
	
	int compile(String shaderSource, int shaderType) {
        int shader = glCreateShader(shaderType);
        glShaderSource(shader, shaderSource);
        glCompileShader(shader);

        int[] isCompiled = new int[1];
        isCompiled[0] = GL_FALSE;
        glGetShaderiv(shader, GL_COMPILE_STATUS, isCompiled);
        
        if(isCompiled[0] == GL_FALSE) {
        	glDeleteShader(shader);
        	throw new RuntimeException("Error occurred compiling shader:\n" + glGetShaderInfoLog(shader));
        }
        
        return shader;
	}
}
