package com.zierfisch.assets.geom;

import com.zierfisch.gfx.mesh.Mesh;
import com.zierfisch.gfx.mesh.MeshBuilder;

public class QuadMaker {

	/**
	 * Makes a fullscreen quad spanning normalized device coordinates in x and y direction
	 * with texture coordinates available.
	 * 
	 * @param builder
	 * @return
	 */
	public Mesh make(MeshBuilder builder) {
		int bottomLeft = builder.addVertex(
			builder.addPosition(-1.0, -1.0),
			builder.addTexCoords(0.0, 0.0)
		);
		
		int bottomRight = builder.addVertex(
			builder.addPosition(1.0, -1.0),
			builder.addTexCoords(1.0, 0.0)
		);
		
		int topRight = builder.addVertex(
			builder.addPosition(1.0, 1.0),
			builder.addTexCoords(1.0, 1.0)
		);
		
		int topLeft = builder.addVertex(
			builder.addPosition(-1.0, 1.0),
			builder.addTexCoords(0.0, 1.0)
		);
		
		builder.addFace(bottomLeft, bottomRight, topRight);
		builder.addFace(topLeft, bottomLeft, topRight);
		
		return builder.create();
	}
	
}
