package com.zierfisch.tex;

import static org.lwjgl.opengl.GL11.GL_LINEAR;
import static org.lwjgl.opengl.GL11.GL_RGBA;
import static org.lwjgl.opengl.GL11.GL_RGBA8;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_S;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_T;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glGenTextures;
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL11.glTexParameteri;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.imageio.ImageIO;

import org.lwjgl.opengl.GL12;

import com.badlogic.gdx.utils.BufferUtils;
import com.zierfisch.ResourceException;

import slim.texture.io.PNGDecoder;
import slim.texture.io.PNGDecoder.Format;

public class TextureLoader {

	private static final int BYTES_PER_PIXEL = 4;// 3 for RGB, 4 for RGBA

	private int minFilter = GL_LINEAR;
	private int magFilter = GL_LINEAR;
	
	public Texture load(String filename) {
		PNGDecoder img = loadImage(filename);
		return new Texture(createTexture(img));
	}
	
	/**
	 * 
	 * 
	 * 
	 * @param loc
	 * @return
	 * @see http://stackoverflow.com/a/10872080
	 */
	public static PNGDecoder loadImage(String loc) {
		try {
			byte[] imgBytes = Files.readAllBytes(Paths.get(loc));
			InputStream imgStream = new ByteArrayInputStream(imgBytes);
			return new PNGDecoder(imgStream);
		} catch (Exception e) {
			throw new Error(e);
		}
	}
	
	/**
	 * 
	 * @param img
	 * @return
	 * @see http://stackoverflow.com/a/10872080
	 */
	private int createTexture(PNGDecoder image) {
		int width = image.getWidth();
		int height = image.getHeight();
		ByteBuffer buffer = loadTexture(image);
		
		
		// You now have a ByteBuffer filled with the color data of each pixel.
		// Now just create a texture ID and bind it. Then you can load it using
		// whatever OpenGL method you want, for example:

		int name = glGenTextures(); // Generate texture ID
		glBindTexture(GL_TEXTURE_2D, name); // Bind texture ID

		// Setup wrap mode
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);

		// Setup texture scaling filtering
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, minFilter);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, magFilter);

		// Send texel data to OpenGL
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE,
				buffer);

		// Return the texture ID so we can bind it later again
		return name;
	}
	
	/**
	 * 
	 * @param image
	 * @return
	 * @see http://stackoverflow.com/a/10872080
	 */
	private ByteBuffer loadTexture(PNGDecoder image) {
		ByteBuffer buffer = BufferUtils.newByteBuffer(image.getWidth() * image.getHeight() * BYTES_PER_PIXEL);
		try {
			image.decode(buffer, image.getWidth()*4, Format.RGBA);
		} catch (IOException e) {
			throw new ResourceException(e);
		}
		buffer.flip(); // FOR THE LOVE OF GOD DO NOT FORGET THIS
		return buffer;
	}

}