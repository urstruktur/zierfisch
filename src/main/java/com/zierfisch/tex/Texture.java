package com.zierfisch.tex;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL14.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL21.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL31.*;
import static org.lwjgl.opengl.GL32.*;
import static org.lwjgl.opengl.GL33.*;
import static org.lwjgl.opengl.GL40.*;
import static org.lwjgl.opengl.GL41.*;

import java.nio.ByteBuffer;

import org.lwjgl.opengl.GL11;

import com.zierfisch.util.GLErrors;

public class Texture {
	/**
	 * When sampling the texture outside the 0..1 range, this setting will clamp
	 * to either 0 or 1, whichever is nearer.
	 */
	public static final int WRAPPING_CLAMP = GL_CLAMP;
	/**
	 * When sampling the texture outside the 0..1 range, the integer part will
	 * be ignored using this setting, essentially making the texture repeat
	 * itself. This is the default OpenGL setting.
	 */
	public static final int WRAPPING_REPEAT = GL_REPEAT;
	/**
	 * When sampling the texture outside the 0..1 range, the texture will either
	 * be sampled mirrored or just repeated, depending on whether hte integer part
	 * is odd or even.
	 */
	public static final int WRAPPING_MIRROR = GL_MIRRORED_REPEAT;

	public static final int FILTERING_NEAREST = GL_NEAREST;
	/**
	 * Default setting.
	 */
	public static final int FILTERING_LINEAR = GL_LINEAR;
	
	private int name;

	public Texture(int name) {
		this.name = name;
	}

	public Texture() {
		name = glGenTextures(); // Generate texture ID
	}

	public void bind() {
		glBindTexture(GL_TEXTURE_2D, name);
	}

	/**
	 * 
	 * <p>
	 * <strong>Note:</strong> Only call this method when the texture is
	 * currently bound.
	 * </p>
	 * 
	 * @param flags
	 */
	public void setWrapping(int wrapping) {
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, wrapping);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, wrapping);
	}
	
	public void setFiltering(int filtering) {
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, filtering);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, filtering);
	}
	
	public int getName() {
		return name;
	}
	
	public void allocate(TextureUsage usage, int width, int height) {
		switch(usage) {
		case COLOR:
			allocate(usage, width, height, 8);
			break;
		case DEPTH:
			allocate(usage, width, height, 32);
			break;
		case VECTOR:
			allocate(usage, width, height, 32);
			break;
		default:
			throw new UnsupportedOperationException();
		}
	}
	
	public void allocate(TextureUsage usage, int width, int height, int componentBits) {
		switch(usage) {
		case COLOR:
			allocate(usage, width, height, componentBits, 4, null);
			break;
		case DEPTH:
			allocate(usage, width, height, componentBits, 1, null);
			break;
		case VECTOR:
			allocate(usage, width, height, componentBits, 3, null);
			break;
		default:
			throw new UnsupportedOperationException();
		}
	}
	
	public void allocate(TextureUsage usage, int width, int height, int componentBits, int componentCount, ByteBuffer contents) {
		int target = GL_TEXTURE_2D;
		int level = 0; // no mipmapping, always 0
		int internalFormat = internalFormat(usage, componentBits, componentCount);
		int border = 0; // must always be 0 per documentation
		int format = format(usage, componentBits, componentCount);
		int type = type(usage, componentBits, componentCount);
		
		System.out.println(format);
		
		glTexImage2D(target, level, internalFormat, width, height, border, format, type, contents);
		GLErrors.check();
	}
	
	private int internalFormat(TextureUsage usage, int componentBits, int componentCount) {
		switch(usage) {
		case COLOR:
			switch (componentCount) {
			case 1:
				switch (componentBits) {
				case 8:
					return GL_R8;
				case 16:
					return GL_R16;
				default:
					throw new UnsupportedOperationException("No suitable storage formats for " + componentCount
							+ " components with a depth of " + componentBits + " bits");
				}
			case 3:
				switch (componentBits) {
				case 8:
					return GL_RGB8;
				default:
					throw new UnsupportedOperationException("No suitable storage formats for " + componentCount
							+ " components with a depth of " + componentBits + " bits");
				}
		
			case 4:
				switch (componentBits) {
				case 8:
					return GL_RGBA8;
				case 16:
					return GL_RGBA16;
				default:
					throw new UnsupportedOperationException("No suitable storage formats for " + componentCount
							+ " components with a depth of " + componentBits + " bits");
				}
		
			default:
				throw new UnsupportedOperationException("Unsupported component count: " + componentCount);
			}
			
		case DEPTH:
			switch(componentBits) {
			case 16:
				return GL_DEPTH_COMPONENT16;
			case 24:
				return GL_DEPTH_COMPONENT24;
			case 32:
				return GL_DEPTH_COMPONENT32;
			default:
				throw new UnsupportedOperationException("Unsupported bit depth " + componentBits + " for DEPTH usage texture");
			}

		case VECTOR:
			switch(componentCount) {
			case 4:
				switch(componentBits) {
				case 16:
					return GL_RGBA16F;
				case 32:
					return GL_RGBA32F;
				default:
					throw new UnsupportedOperationException();
				}
			default:
				throw new UnsupportedOperationException("Unsupported component count " + componentCount + " for a VECTOR usage texture");
			}
			
		default:
			throw new UnsupportedOperationException();
		}
	}

	private int format(TextureUsage usage, int bitsPerComponent, int componentCount) {
		switch(usage) {
		case DEPTH:
			return GL_DEPTH_COMPONENT;

		case COLOR:
		case VECTOR:
			switch(componentCount) {
			case 1:
				return GL_RED;
			case 3:
				return GL_RGB;
			case 4:
				return GL_RGBA;
			default:
				throw new UnsupportedOperationException();
			}
			
		default:
			throw new UnsupportedOperationException();
		}
	}
	
	private int type(TextureUsage usage, int bitsPerComponent, int componentCount) {
		switch(usage) {
		case DEPTH:
			return GL_DEPTH_COMPONENT;

		case COLOR:
			switch(bitsPerComponent) {
			case 8:
				return GL_UNSIGNED_BYTE;
			case 16:
				return GL_UNSIGNED_SHORT;
			case 32:
				return GL_UNSIGNED_INT;
			default:
				throw new UnsupportedOperationException();
			}
			
		case VECTOR:
			return GL_FLOAT;
			
		default:
			throw new UnsupportedOperationException();
		}
	}
}
