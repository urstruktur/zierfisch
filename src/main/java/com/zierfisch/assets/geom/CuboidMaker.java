package com.zierfisch.assets.geom;

import com.zierfisch.gfx.mesh.Mesh;
import com.zierfisch.gfx.mesh.MeshBuilder;

public class CuboidMaker {

	public Mesh make(MeshBuilder builder) {
		return make(builder, 0f, 0f, 0f, 1f, 1f, 1f);
	}
	
	public Mesh make(MeshBuilder builder, double size) {
		return make(builder, 0f, 0f, 0f, size, size, size);
	}
	
	public Mesh make(MeshBuilder builder, double width, double height, double depth) {
		return make(builder, 0f, 0f, 0f, width, height, depth);
	}
	
	
	public Mesh make(MeshBuilder builder, double centerX, double centerY, double centerZ, double width, double height, double depth) {
		
		// front plane
		builder.addFace(
			// front bottom left
			builder.addVertex(
				builder.addPosition(centerX - width/2, centerY - height/2, centerZ - depth/2),
				builder.addNormal(0, 0, 1)
			),
			// front bottom right
			builder.addVertex(
				builder.addPosition(centerX + width/2, centerY - height/2, centerZ - depth/2),
				builder.addNormal(0, 0, 1)
			),
			// front top right
			builder.addVertex(
				builder.addPosition(centerX + width/2, centerY + height/2, centerZ - depth/2),
				builder.addNormal(0, 0, 1)
			)
		);
		builder.addFace(
			// front bottom left
			builder.addVertex(
				builder.addPosition(centerX - width/2, centerY - height/2, centerZ - depth/2),
				builder.addNormal(0, 0, 1)
			),
			// front top right
			builder.addVertex(
				builder.addPosition(centerX + width/2, centerY + height/2, centerZ - depth/2),
				builder.addNormal(0, 0, 1)
			),
			// front top left
			builder.addVertex(
				builder.addPosition(centerX - width/2, centerY + height/2, centerZ - depth/2),
				builder.addNormal(0, 0, 1)
			)
		);
		
		// back plane
		builder.addFace(
			// front bottom left
			builder.addVertex(
				builder.addPosition(centerX - width/2, centerY - height/2, centerZ + depth/2),
				builder.addNormal(0, 0, -1)
			),
			// front top right
			builder.addVertex(
				builder.addPosition(centerX + width/2, centerY + height/2, centerZ + depth/2),
				builder.addNormal(0, 0, -1)
			),
			// front bottom right
			builder.addVertex(
				builder.addPosition(centerX + width/2, centerY - height/2, centerZ + depth/2),
				builder.addNormal(0, 0, -1)
			)
		);
		builder.addFace(
			// front bottom left
			builder.addVertex(
				builder.addPosition(centerX - width/2, centerY - height/2, centerZ + depth/2),
				builder.addNormal(0, 0, -1)
			),
			// front top left
			builder.addVertex(
				builder.addPosition(centerX - width/2, centerY + height/2, centerZ + depth/2),
				builder.addNormal(0, 0, -1)
			),
			// front top right
			builder.addVertex(
				builder.addPosition(centerX + width/2, centerY + height/2, centerZ + depth/2),
				builder.addNormal(0, 0, -1)
			)
		);
		
		// top plane
		builder.addFace(
			// top bottom left
			builder.addVertex(
				builder.addPosition(centerX - width/2, centerY + height/2, centerZ + depth/2),
				builder.addNormal(0, 1, 0)
			),
			// top top right
			builder.addVertex(
				builder.addPosition(centerX + width/2, centerY + height/2, centerZ - depth/2),
				builder.addNormal(0, 1, 0)
			),
			// top bottom right
			builder.addVertex(
				builder.addPosition(centerX + width/2, centerY + height/2, centerZ + depth/2),
				builder.addNormal(0, 1, 0)
			)
		);
		builder.addFace(
			// top bottom left
			builder.addVertex(
				builder.addPosition(centerX - width/2, centerY + height/2, centerZ + depth/2),
				builder.addNormal(0, 1, 0)
			),
			// top top right
			builder.addVertex(
				builder.addPosition(centerX + width/2, centerY + height/2, centerZ - depth/2),
				builder.addNormal(0, 1, 0)
			),
			// top top left
			builder.addVertex(
				builder.addPosition(centerX - width/2, centerY + height/2, centerZ - depth/2),
				builder.addNormal(0, 1, 0)
			)
		);
		// bottom plane
		builder.addFace(
			// bottom bottom left
			builder.addVertex(
				builder.addPosition(centerX - width/2, centerY - height/2, centerZ + depth/2),
				builder.addNormal(0, -1, 0)
			),
			// bottom bottom right
			builder.addVertex(
				builder.addPosition(centerX + width/2, centerY - height/2, centerZ + depth/2),
				builder.addNormal(0, -1, 0)
			),
			// bottom top right
			builder.addVertex(
				builder.addPosition(centerX + width/2, centerY - height/2, centerZ - depth/2),
				builder.addNormal(0, -1, 0)
			)
		);
		builder.addFace(
			// bottom bottom left
			builder.addVertex(
				builder.addPosition(centerX - width/2, centerY - height/2, centerZ + depth/2),
				builder.addNormal(0, -1, 0)
			),
			// bottom top left
			builder.addVertex(
				builder.addPosition(centerX - width/2, centerY - height/2, centerZ - depth/2),
				builder.addNormal(0, -1, 0)
			),
			// bottom top right
			builder.addVertex(
				builder.addPosition(centerX + width/2, centerY - height/2, centerZ - depth/2),
				builder.addNormal(0, -1, 0)
			)
		);
		// left plane
		builder.addFace(
			// left bottom left
			builder.addVertex(
				builder.addPosition(centerX - width/2, centerY - height/2, centerZ - depth/2),
				builder.addNormal(-1, 0, 0)
			),
			// left bottom right
			builder.addVertex(
				builder.addPosition(centerX - width/2, centerY - height/2, centerZ + depth/2),
				builder.addNormal(-1, 0, 0)
			),
			// left top right
			builder.addVertex(
				builder.addPosition(centerX - width/2, centerY + height/2, centerZ + depth/2),
				builder.addNormal(-1, 0, 0)
			)
		);
		builder.addFace(
			// left bottom left
			builder.addVertex(
				builder.addPosition(centerX - width/2, centerY - height/2, centerZ - depth/2),
				builder.addNormal(-1, 0, 0)
			),
			// left top right
			builder.addVertex(
				builder.addPosition(centerX - width/2, centerY + height/2, centerZ + depth/2),
				builder.addNormal(-1, 0, 0)
			),
			// left top left
			builder.addVertex(
				builder.addPosition(centerX - width/2, centerY + height/2, centerZ - depth/2),
				builder.addNormal(-1, 0, 0)
			)
		);
		// right plane
		builder.addFace(
			// right bottom left
			builder.addVertex(
				builder.addPosition(centerX + width/2, centerY - height/2, centerZ - depth/2),
				builder.addNormal(1, 0, 0)
			),
			// right top right
			builder.addVertex(
				builder.addPosition(centerX + width/2, centerY + height/2, centerZ + depth/2),
				builder.addNormal(1, 0, 0)
			),
			// right bottom right
			builder.addVertex(
				builder.addPosition(centerX + width/2, centerY - height/2, centerZ + depth/2),
				builder.addNormal(1, 0, 0)
			)
		);
		builder.addFace(
			// right bottom left
			builder.addVertex(
				builder.addPosition(centerX + width/2, centerY - height/2, centerZ - depth/2),
				builder.addNormal(1, 0, 0)
			),
			// right top left
			builder.addVertex(
				builder.addPosition(centerX + width/2, centerY + height/2, centerZ - depth/2),
				builder.addNormal(1, 0, 0)
			),
			// right top right
			builder.addVertex(
				builder.addPosition(centerX + width/2, centerY + height/2, centerZ + depth/2),
				builder.addNormal(1, 0, 0)
			)
		);
		
		return builder.create();
	}

}
