package com.zierfisch.render;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL20.glDrawBuffers;
import static org.lwjgl.opengl.GL30.GL_COLOR_ATTACHMENT0;
import static org.lwjgl.opengl.GL30.GL_DEPTH_ATTACHMENT;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER;
import static org.lwjgl.opengl.GL30.glFramebufferTexture2D;
import static org.lwjgl.opengl.GL30.glGenFramebuffers;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL31.*;
import static org.lwjgl.opengl.GL32.*;
import static org.lwjgl.opengl.GL33.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL21.*;
import static org.lwjgl.opengl.GL12.*;
import static org.lwjgl.opengl.GL40.*;
import static org.lwjgl.opengl.GL41.*;

import com.zierfisch.tex.Texture;
import com.zierfisch.util.GLErrors;

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
	
	/**
	 * <p>Argument passed to glDrawBuffers on every bind.</p>
	 * 
	 * <p>This makes output framebuffer selectable with layout specifiers in the fragment shader:</p>
	 * 
	 * <pre>
	 * layout(location = 0) out vec4 color;
	 * layout(location = 1) out vec4 funOut;
	 * </pre>
	 */
	private int[] drawBuffers;

	public OffscreenSurface(int width, int height, Texture[] colorTexes, Texture depthTex) {
		super(glGenFramebuffers());
		
		this.width = width;
		this.height = height;

		initDrawBuffers(colorTexes.length);
		
		bind();
		initColorAttachments(colorTexes);
		initDepthAttachment(depthTex);
	}
	
	public OffscreenSurface(int width, int height, Texture colorTex, Texture depthTex) {
		this(width, height, new Texture[] { colorTex }, depthTex);
	}
	
	@Override
	public void bind() {
		super.bind();
		
		/**
		 * This makes output framebuffer selectable with layout specifiers in the fragment shader:
		 * 
		 * layout(location = 0) out vec4 color;
		 * layout(location = 1) out vec4 funOut;
		 */
		
		glDrawBuffers(drawBuffers);
	}
	
	@Override
	public int getWidth() {
		return width;
	}

	@Override
	public int getHeight() {
		return height;
	}
	
	private void initDrawBuffers(int colorAttachmentCount) {
		drawBuffers =  new int[colorAttachmentCount];
		
		for(int i = 0; i < colorAttachmentCount; ++i) {
			drawBuffers[i] = GL_COLOR_ATTACHMENT0 + i;
		}
	}

	private void initColorAttachments(Texture[] colorTexes) {
		int offset = 0;
		
		for(Texture tex: colorTexes) {
			int attachment = GL_COLOR_ATTACHMENT0 + offset;
			tex.bind();
			glFramebufferTexture2D(
				GL_FRAMEBUFFER,
				attachment,
				GL_TEXTURE_2D,
				tex.getName(),
				0
			);
			GLErrors.check();
			++offset;
		}
	}

	private void initDepthAttachment(Texture depthTex) {
		depthTex.bind();
		glFramebufferTexture2D(
			GL_FRAMEBUFFER,
			GL_DEPTH_ATTACHMENT,
			GL_TEXTURE_2D,
			depthTex.getName(),
			0
		);
		GLErrors.check();
	}
}
