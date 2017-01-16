package com.zierfisch.assets.geom;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;

import com.owens.oobjloader.builder.Build;
import com.owens.oobjloader.builder.Face;
import com.owens.oobjloader.builder.FaceVertex;
import com.owens.oobjloader.parser.Parse;
import com.zierfisch.gfx.mesh.Mesh;
import com.zierfisch.gfx.mesh.MeshBuilder;

public class ObjImporter {
	Build objBuilder;
	public ObjImporter() {
		objBuilder = new Build();
	}

	/**
	 * @param path path to obj model 
	 */
	public void load(String path) throws FileNotFoundException, IOException {
		Parse parse = new Parse(objBuilder, path);
	}

	public Mesh make(MeshBuilder meshBuilder) {
		
		for(Face face : objBuilder.faces){
			FaceVertex faceVert0 = face.vertices.get(0);
			FaceVertex faceVert1 = face.vertices.get(1);
			FaceVertex faceVert2 = face.vertices.get(2);
			
			int pos0 = (faceVert0.v == null) ? -1 : meshBuilder.addPosition(faceVert0.v.x, faceVert0.v.y, faceVert0.v.z);
			int pos1 = (faceVert1.v == null) ? -1 : meshBuilder.addPosition(faceVert1.v.x, faceVert1.v.y, faceVert1.v.z);
			int pos2 = (faceVert2.v == null) ? -1 : meshBuilder.addPosition(faceVert2.v.x, faceVert2.v.y, faceVert2.v.z);
			
			int norm0 = (faceVert0.n == null) ? -1 : meshBuilder.addNormal(faceVert0.n.x, faceVert0.n.y, faceVert0.n.z);
			int norm1 = (faceVert1.n == null) ? -1 : meshBuilder.addNormal(faceVert1.n.x, faceVert1.n.y, faceVert1.n.z);
			int norm2 = (faceVert2.n == null) ? -1 : meshBuilder.addNormal(faceVert2.n.x, faceVert2.n.y, faceVert2.n.z);
			
			int tex0 = (faceVert0.t == null) ? -1 : meshBuilder.addTexCoords(faceVert0.t.u, faceVert0.t.v);
			int tex1 = (faceVert1.t == null) ? -1 : meshBuilder.addTexCoords(faceVert1.t.u, faceVert1.t.v);
			int tex2 = (faceVert2.t == null) ? -1 : meshBuilder.addTexCoords(faceVert2.t.u, faceVert2.t.v);
			
			int vert0 = meshBuilder.addVertexWithAttributeIndexes(pos0, norm0, tex0, -1, -1, -1);
			int vert1 = meshBuilder.addVertexWithAttributeIndexes(pos1, norm1, tex1, -1, -1, -1);
			int vert2 = meshBuilder.addVertexWithAttributeIndexes(pos2, norm2, tex2, -1, -1, -1);
			
			meshBuilder.addFace(vert0, vert1, vert2);
		}
		return meshBuilder.create();
	}

}
