package com.zierfisch.gfx.surf;

import java.util.ArrayList;
import java.util.List;

import com.zierfisch.gfx.tex.Texture;
import com.zierfisch.gfx.tex.TextureBuilder;
import com.zierfisch.gfx.tex.TextureUsage;

public class SurfaceBuilder {
	
	private int width = -1;
	private int height = -1;
	private List<TextureBuilder> colorAttachmentBuilders = new ArrayList<>(4);
	private TextureBuilder depthAttachmentBuilder;
	
	public SurfaceBuilder() {
		// Add a default depth texture, so this is not always required
		attach(TextureUsage.DEPTH, -1, -1);
	}
	
	public Surface build(Texture[] colorTexes, Texture depthTex) {
		if(width == -1 || height == -1) {
			throw new RuntimeException("When building, size of the surface needs to be fully set");
		}
		
		Texture depth = depthAttachmentBuilder.setSize(width, height).build(depthTex);
		
		int colorAtachmentCount = colorAttachmentBuilders.size();
		if(colorAtachmentCount == 0) {
			attach(TextureUsage.COLOR, -1, -1);
			colorAtachmentCount = 1;
		}
		
		Texture[] colors;
		if(colorTexes == null) {
			colors = new Texture[colorAtachmentCount];
		} else {
			if(colorTexes.length < colorAttachmentBuilders.size()) {
				throw new RuntimeException("When specifying colorTexes array, it must have a texture for each of the " + colorAtachmentCount + " attachments");
			}
			colors = colorTexes;
		}
		
		
		for(int i = 0; i < colorAtachmentCount; ++i) {
			colors[i] = colorAttachmentBuilders.get(i).setSize(width, height).build(colors[i]);
		}
		
		return new OffscreenSurface(width, height, colors, depth);
	}
	
	public Surface build(Texture colorTex, Texture depthTex) {
		return build(new Texture[] { colorTex }, depthTex);
	}
	
	public Surface build(Texture colorTex) {
		return build(new Texture[] { colorTex }, null);
	}
	
	public Surface build() {
		return build((Texture[]) null, (Texture) null);
	}

	public SurfaceBuilder setSize(int width, int height) {
		this.width = width;
		this.height = height;
		return this;
	}
	
	/**
	 * Sets the size of the built surface to be equal to the size of the given surface.
	 * 
	 * @param likeThis Surface from which to use the size as reference
	 * @return the called surface builder, easy going method chaining
	 */
	public SurfaceBuilder setSize(Surface likeThis) {
		return setSize(likeThis.getWidth(), likeThis.getHeight());
	}
	
	/**
	 * Adds the given texture to the texture. Color attachments will be ordered in the order
	 * you called attach. The last texture with usage DEPTH will serve as the depth texture.
	 * 
	 * @param tex
	 * @param usage
	 * @param bitsPerComponent
	 * @param componentCount
	 * @return
	 */
	public SurfaceBuilder attach(TextureUsage usage, int bitsPerComponent, int componentCount) {
		TextureBuilder builder = new TextureBuilder();
		builder.setUsage(usage);
		builder.setPrecision(bitsPerComponent);
		builder.setComponentCount(componentCount);
		
		if(usage == TextureUsage.DEPTH) {
			depthAttachmentBuilder = builder;
		} else {
			colorAttachmentBuilders.add(builder);
		}
		
		return this;
	}
	
	public SurfaceBuilder attach(TextureUsage usage, int bitsPerComponent) {
		return attach(usage, bitsPerComponent, -1);
	}
	
	public SurfaceBuilder attach(TextureUsage usage) {
		return attach(usage, -1, -1);
	}
	
}
