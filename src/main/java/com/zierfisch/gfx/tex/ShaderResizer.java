package com.zierfisch.gfx.tex;

import com.zierfisch.assets.geom.QuadMaker;
import com.zierfisch.gfx.mesh.Mesh;
import com.zierfisch.gfx.mesh.SegmentedMeshBuilder;
import com.zierfisch.gfx.shader.Shader;
import com.zierfisch.gfx.surf.Surface;

public class ShaderResizer extends FboResizer {
	
	private Shader shader;
	private Mesh quad;
	
	public ShaderResizer(Shader shader, Texture sourceTexture, Texture targetTexture, Surface sourceSurface, Surface targetSurface) {
		super(sourceTexture, targetTexture, sourceSurface, targetSurface);
		
		this.shader = shader;
		this.quad = new QuadMaker().make(new SegmentedMeshBuilder());
	}

	@Override
	protected void resizeFBOStyle() {
		shader.render(quad);
	}
	
}
