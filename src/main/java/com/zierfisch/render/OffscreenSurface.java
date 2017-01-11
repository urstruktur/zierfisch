package com.zierfisch.render;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL21.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL31.*;
import static org.lwjgl.opengl.GL32.*;
import static org.lwjgl.opengl.GL33.*;
import static org.lwjgl.opengl.GL40.*;
import static org.lwjgl.opengl.GL41.*;

/**
 * <p>
 * Represents a render target that is not directly used for presentation.
 * Rather, rendering is performed into texture attachments. The attached
 * textures can be sampled later to implement advanced graphics techniques.
 * </p>
 * 
 * <p>
 * This is implemented with OpenGL framebuffer objects that have texture objects
 * as image attachments.
 * </p>
 * 
 * @author phil
 */
public class OffscreenSurface extends AbstractSurface {

	/**
	 * Width of the render texture in pixels.
	 */
	private int width;
	/**
	 * Height of the render texture in pixels
	 */
	private int height;

	public OffscreenSurface(int width, int height, int bitsPerColorComponent, int depthBits) {
		super(glGenFramebuffers());
		
		this.width = width;
		this.height = height;

		initColorAttachment(bitsPerColorComponent);
		initDepthAttachment(depthBits);
	}
	
	@Override
	public int getWidth() {
		return width;
	}

	@Override
	public int getHeight() {
		return height;
	}

	private void initDepthAttachment(int depthBits) {
		switch (depthBits) {
		case 0:
			return; // no attachment required

		case 8:
			// The depth buffer
//			GLuint depthrenderbuffer;
//			glGenRenderbuffers(1, &depthrenderbuffer);
//			glBindRenderbuffer(GL_RENDERBUFFER, depthrenderbuffer);
//			glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH_COMPONENT, 1024, 768);
//			glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_RENDERBUFFER, depthrenderbuffer);
			break;

		case 16:
			// TODO implement
			break;

		default:
			throw new UnsupportedOperationException("Cannot create a depth attachment with " + depthBits + " bits");
		}
	}

	private void initColorAttachment(int colorCompBits) {
		switch (colorCompBits) {
		case 0:
			return; // no attachment required

		case 8:
			int texture = createTexture(4, colorCompBits, GL_NEAREST, GL_NEAREST);

			// TODO implement
			break;

		case 16:
			// TODO implement
			break;

		default:
			throw new UnsupportedOperationException(
					"Cannot create a color attachment with " + colorCompBits + " bits per component");
		}
	}

	private int createTexture(int componentCount, int bitsPerComponent, int minFilter, int magFilter) {
		int tex = glGenTextures();
		glBindTexture(GL_TEXTURE_2D, tex);

//		int internalFormat = texInternalFormatFromComponentCount(componentCount);
//		int format = texFormatFromComponentCount(componentCount, bitsPerComponent);
//		int type = texTypeFromComponentCount(componentCount, bitsPerComponent);
//
//		// Give an empty image to OpenGL ( the last "0" )
//		glTexImage2D(GL_TEXTURE_2D,
//				0, // level, 0 is the base texture level
//				internalFormat,
//				width, height,
//				0, // this parameter is always 0 per documentation (border)
//				format,
//				type,
//				0 // no data, empty image
//		);
//
//		// Poor filtering. Needed !
//		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, magFilter);
//		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, minFilter);

		return tex;
	}

	

}
