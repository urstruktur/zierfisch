package com.zierfisch.gfx.tex;

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
import java.util.Objects;

import javax.imageio.ImageIO;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL13;

import com.badlogic.gdx.utils.BufferUtils;
import com.zierfisch.ResourceException;
import com.zierfisch.gfx.surf.Surface;

import slim.texture.io.PNGDecoder;
import slim.texture.io.PNGDecoder.Format;

public class TextureBuilder {

	private int width = -1;
	private int height = -1;
	private int wrapping = Texture.WRAPPING_REPEAT;
	private int filtering = Texture.FILTERING_LINEAR;
	private TextureUsage usage = TextureUsage.COLOR;
	private int bitsPerComponent = -1;
	private int componentCount = -1;
	private ByteBuffer contents;
	private PNGDecoder pngDecoder;

	/**
	 * Initializes the given texture to the properties previously specified
	 * with the builder setters.
	 * 
	 * @param binding Texture to write into, or null to generate a new one
	 * @return the called texture builder, easy going method chaining
	 */
	public Texture build(Texture tex) {
		
		if(tex == null) {
			tex = new Texture();
		}
		
		tex.bind();
		tex.setWrapping(wrapping);
		tex.setFiltering(filtering);
		
		if(pngDecoder != null && contents != null) {
			throw new RuntimeException("You say you want to the contents directly, then you say " +
				"you want to load them from a PNG file, which one is it now?");
		} else if(pngDecoder != null) {
			setDimensionsFromPNG(pngDecoder);
			contents = loadTexture(pngDecoder);
		}
		
		int bitsPerComponent = chooseBitsPerComponent();
		int componentCount = chooseComponentCount();
		
		if(width == -1 || height == -1) {
			throw new RuntimeException("Seems you forget to set texture dimensions or forgot to set a PNG file: W=" + width + "/H=" + height);
		}
		
		tex.allocate(usage, width, height, bitsPerComponent, componentCount, contents);
		
		if(pngDecoder != null) {
			// Discard the current contents so the builder will properly re-generate the texture,
			// in case parameters where changed in the meantime
			contents = null;
		}

		return tex;
	}
	
	public Texture build() {
		return build(null);
	}

	/**
	 * 
	 * @param usage
	 * @return the called texture builder, easy going method chaining
	 */
	public TextureBuilder setUsage(TextureUsage usage) {
		Objects.requireNonNull(usage);
		this.usage = usage;
		return this;
	}

	/**
	 * Sets the size of the built texture. This is only necessary if you want
	 * to create an empty texture. A texture loaded from an image automatically
	 * has the same size as the image and width and height will be ignored.
	 * 
	 * @param width
	 * @param height
	 * 
	 * @return the called texture builder, easy going method chaining
	 */
	public TextureBuilder setSize(int width, int height) {
		this.width = width;
		this.height = height;
		return this;
	}

	/**
	 * <p>
	 * Sets the precision of one component in the texture, e.g. the precision of
	 * one color channel in an RGBA texture, or the precision of one vector
	 * component in a vector texture.
	 * </p>
	 * 
	 * <p>
	 * Never calling this method or setting precision purposely to -1 instead
	 * lets the texture builder pick a sensible default precision depending on
	 * texture usage. This is typically a tradeoff between performance and
	 * quality that should be sufficient for most circumstances. You are free to
	 * choose your own precision to optimize for one of performance or quality,
	 * though.
	 * </p>
	 * 
	 * <p>
	 * Precision is specified by the amount of bits used for a single component,
	 * not all of them. With some exceptions, generally only power of two
	 * precisions are allowed, and not all texture usages support all precisions
	 * due to OpenGL limitations. Stick to typical power-of-two values like
	 * <code>8</code>, <code>16</code> or <code>32</code> and you should be
	 * fine. More exotic ones like <code>
	 * 22</code> only work in special circumstances like for depth textures.
	 * </p>
	 * 
	 * @param bitsPerComponent
	 *            Bits to use for each component
	 * 
	 * @return the called texture builder, easy going method chaining
	 */
	public TextureBuilder setPrecision(int bitsPerComponent) {
		this.bitsPerComponent = bitsPerComponent;
		return this;
	}

	/**
	 * <p>
	 * Sets the number of components each texel has. For colors, this is the
	 * number of color channels, e.g. 4 for RGBA textures and 3 for RGB
	 * textures.
	 * </p>
	 * 
	 * <p>
	 * Never calling this method or setting the component count to
	 * <code>-1</code> lets the texture builder pick a component count at the
	 * time of building the texture that is probably what you want by examining
	 * your choice of texture usage. For colors and vectors, it will pick 4
	 * channels, for depth textures it will pick 1.
	 * </p>
	 * 
	 * @param componentCount
	 *            Number of components per texel, e.g. 4 for RGBA
	 * 
	 * @return the called texture builder, easy going method chaining
	 */
	public TextureBuilder setComponentCount(int componentCount) {
		this.componentCount = componentCount;
		return this;
	}

	/**
	 * <p>
	 * Sets the contents of the texture with the specified bytebuffer. The data
	 * layout of the buffer should be in obvious order (<code>RGBARGBARGBA</code>,
	 * interleaved colors in native endian order, one color component after the other).
	 * </p>
	 * 
	 * <p>
	 * Never calling this method or setting the contents to <code>null</code>
	 * while still specifying a width and height lets the texture builder
	 * reserve space for the texture but not initialize its contents, useful for
	 * rendering into. Take care to clear the texture at some point however, as
	 * its contents are undefined after building and may be anything from
	 * negative infinity to zero to NaN.
	 * </p>
	 * 
	 * @param contents
	 *            Texel data, or <code>null</code>
	 * 
	 * @return the called texture builder, easy going method chaining
	 */
	public TextureBuilder setContents(ByteBuffer contents) {
		this.contents = contents;
		return this;
	}
	
	/**
	 * <p>
	 * Sets the contents of the built texture by loading and decoding
	 * the PNG data stored in the file at the given path.
	 * </p>
	 * 
	 * @param pngFilePath A path to a valid PNG file
	 * 
	 * @return the called texture builder, easy going method chaining
	 */
	public TextureBuilder setContents(String pngFilePath) {
		Objects.requireNonNull(pngFilePath);
		
		if(!pngFilePath.toLowerCase().endsWith(".png")) {
			throw new RuntimeException("Only PNG images supported at this point");
		}
		
		pngDecoder = makeDecoder(pngFilePath);
		return this;
	}

	private int chooseBitsPerComponent() {
		if(bitsPerComponent != -1) {
			return bitsPerComponent;
		} else {
			switch(usage) {
			case COLOR:
				return 8;
				
			case VECTOR:
				return 32;
				
			case DEPTH:
				return 24;
				
			default:
				throw new UnsupportedOperationException();
			}
		}
	}

	private int chooseComponentCount() {
		if(componentCount != -1) {
			return componentCount;
		} else {
			if(usage == TextureUsage.DEPTH) {
				return 1;
			} else {
				return 4;
			}
		}
	}

	/**
	 * 
	 * 
	 * 
	 * @param loc
	 * @return
	 * @see http://stackoverflow.com/a/10872080
	 */
	private static PNGDecoder makeDecoder(String loc) {
		try {
			byte[] imgBytes = Files.readAllBytes(Paths.get(loc));
			InputStream imgStream = new ByteArrayInputStream(imgBytes);
			return new PNGDecoder(imgStream);
		} catch (Exception e) {
			throw new Error(e);
		}
	}
	
	private void setDimensionsFromPNG(PNGDecoder png) {
		width = png.getWidth();
		height = png.getHeight();
	}

	/**
	 * 
	 * @param image
	 * @return
	 * @see http://stackoverflow.com/a/10872080
	 */
	private ByteBuffer loadTexture(PNGDecoder image) {
		int bytesPerComponent = chooseBitsPerComponent() / 8;
		int bytesPerPixel = chooseComponentCount() * bytesPerComponent;
		int imageBufSize = image.getWidth() * image.getHeight() * bytesPerPixel;
		ByteBuffer buffer = BufferUtils.newByteBuffer(imageBufSize);
		Format format = pngDecodingFormatFromComponentCount();
		
		try {
			image.decode(buffer, image.getWidth() * bytesPerPixel, format);
		} catch (IOException e) {
			throw new ResourceException(e);
		}
		
		buffer.flip(); // FOR THE LOVE OF GOD DO NOT FORGET THIS
		
		return buffer;
	}

	private Format pngDecodingFormatFromComponentCount() {
		int componentCount = chooseComponentCount();
		
		switch(componentCount) {
		case 1:
			return Format.ALPHA;
			
		case 3:
			return Format.RGB;
			
		case 4:
			return Format.RGBA;
			
		default:
			throw new RuntimeException("Cannot decode PNG texture to component count: " + componentCount);
		}
	}
}
