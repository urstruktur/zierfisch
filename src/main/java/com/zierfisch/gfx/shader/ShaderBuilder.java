package com.zierfisch.gfx.shader;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL32.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import com.zierfisch.gfx.util.GLErrors;

public class ShaderBuilder {
	
	private static final int NO_SHADER = Integer.MIN_VALUE;
	
	private String vertexShaderSource;
	private String fragmentShaderSource;
	private String geometryShaderSource;
	
	private int vertexShader = NO_SHADER;
	private int fragmentShader = NO_SHADER;
	private int geometryShader = NO_SHADER;
	
	/**
	 * Caches the last built shader in case nothing changed.
	 * This way, multiple build calls return the same shader.
	 */
	private Shader shader;
	
	public ShaderBuilder setVertexShader(String srcFilePath) {
		vertexShaderSource = srcFilePath;
		vertexShader = NO_SHADER;
		shader = null;
		return this;
	}
	
	public ShaderBuilder setFragmentShader(String srcFilePath) {
		fragmentShaderSource = srcFilePath;
		fragmentShader = NO_SHADER;
		shader = null;
		return this;
	}
	
	public ShaderBuilder setGeometryShader(String srcFilePath) {
		geometryShaderSource = srcFilePath;
		geometryShader = NO_SHADER;
		shader = null;
		return this;
	}
	
	public Shader build() {
		// Be lazy if no build parameters changed since last the last build
		if(shader == null) {
			buildVertexShader();
			buildGeometryShader();
			buildFragmentShader();
			
			int shaderProgram = compileAndLinkProgram();
		    checkLinkStatus(shaderProgram);
		    
		    cleanupShaders(shaderProgram);
			
			shader =  new Shader(shaderProgram);
		}
		
	    return shader;
	}

	private void buildVertexShader() {
		if(vertexShaderSource == null) {
			throw new IllegalStateException("Trying to build Shader but no vertex shader source file was specified");
		}
		
		if(vertexShader == NO_SHADER) {
			vertexShader = compileShader(vertexShaderSource, GL_VERTEX_SHADER);
		}
	}
	
	private void buildFragmentShader() {
		if(fragmentShaderSource == null) {
			throw new IllegalStateException("Trying to build Shader but no fragment shader source file was specified");
		}
		
		if(fragmentShader == NO_SHADER) {
			fragmentShader = compileShader(fragmentShaderSource, GL_FRAGMENT_SHADER);
		}
	}
	
	private void buildGeometryShader() {
		if(geometryShaderSource != null && geometryShader == NO_SHADER) {
			geometryShader = compileShader(geometryShaderSource, GL_GEOMETRY_SHADER);
		}
	}
	
	private int compileAndLinkProgram() {
		int shaderProgram = glCreateProgram();
		
        glAttachShader(shaderProgram, vertexShader);
        glAttachShader(shaderProgram, fragmentShader);
        
        if(geometryShader != NO_SHADER) {
        	glAttachShader(shaderProgram, geometryShader);
        }
        
        glLinkProgram(shaderProgram);
        
		return shaderProgram;
	}
	
	private void checkLinkStatus(int shaderProgram) {
		int[] isLinked = { GL_FALSE };
	    glGetProgramiv(shaderProgram, GL_LINK_STATUS, isLinked);
	    
	    if(isLinked[0] == GL_FALSE) {
	    	String log = glGetProgramInfoLog(shaderProgram);
	    	
	        glDeleteProgram(shaderProgram);
	        
	        // Delete shaders when link whas unsuccessful
	        glDeleteShader(vertexShader);
	        vertexShader = NO_SHADER;
	        
	        glDeleteShader(fragmentShader);
	        fragmentShader = NO_SHADER;
	        
	        if(geometryShader != NO_SHADER) {
	            glDeleteShader(geometryShader);
	            geometryShader = NO_SHADER;
	        }
	
	        throw new RuntimeException("Shader program could not be linked:\n"  + log);
	    }
	}

	private void cleanupShaders(int shaderProgram) {
		glDetachShader(shaderProgram, vertexShader);
	    vertexShader = NO_SHADER;
	    
	    glDetachShader(shaderProgram, fragmentShader);
	    fragmentShader = NO_SHADER;
	    
	    if(geometryShader != NO_SHADER) {
	        glDetachShader(shaderProgram, geometryShader);
	        geometryShader = NO_SHADER;
	    }
	}

	private String load(String path) {
		try {
			return String.join("\n", Files.readAllLines(Paths.get(path)));
		} catch (IOException e) {
			throw new RuntimeException("Loading shader source failed", e);
		}
	}
	
	private int compileShader(String shaderSourceFilePath, int shaderType) {
		if(shaderSourceFilePath == null) {
			return NO_SHADER;
		}
		
		String shaderSource = load(shaderSourceFilePath);
		
        int shader = glCreateShader(shaderType);
        GLErrors.check();
        glShaderSource(shader, shaderSource);
        System.out.println(shader);
        GLErrors.check();
        
        glCompileShader(shader);
        GLErrors.check();

        int[] isCompiled = new int[1];
        isCompiled[0] = GL_FALSE;
        glGetShaderiv(shader, GL_COMPILE_STATUS, isCompiled);
        
        if(isCompiled[0] == GL_FALSE) {
        	String log = glGetShaderInfoLog(shader);
        	glDeleteShader(shader);
        	throw new RuntimeException(shaderSourceFilePath + "\nError occurred compiling shader:\n" + log);
        }
        
        return shader;
	}
}
