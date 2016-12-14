package com.zierfisch.util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;

import xyz.krachzack.gfx.mesh.Mesh;
import xyz.krachzack.gfx.mesh.MeshBuilder;

import com.owens.oobjloader.builder.Build;
import com.owens.oobjloader.builder.Face;
import com.owens.oobjloader.builder.FaceVertex;
import com.owens.oobjloader.parser.Parse;

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
		System.out.println("Loaded " + objBuilder.objectName);
	}

	public Mesh make(MeshBuilder meshBuilder) {
		
		for(Face face : objBuilder.faces){
			meshBuilder.addFace(
					meshBuilder.addVertex(
							meshBuilder.addPosition(face.vertices.get(0).v.x, face.vertices.get(0).v.y, face.vertices.get(0).v.z),
							meshBuilder.addNormal(face.vertices.get(0).n.x, face.vertices.get(0).n.y, face.vertices.get(0).n.z),
							meshBuilder.addTexCoords(face.vertices.get(0).t.u, face.vertices.get(0).t.v)
					),
					meshBuilder.addVertex(
							meshBuilder.addPosition(face.vertices.get(1).v.x, face.vertices.get(1).v.y, face.vertices.get(1).v.z),
							meshBuilder.addNormal(face.vertices.get(1).n.x, face.vertices.get(1).n.y, face.vertices.get(1).n.z),
							meshBuilder.addTexCoords(face.vertices.get(1).t.u, face.vertices.get(1).t.v)
							),
					meshBuilder.addVertex(
							meshBuilder.addPosition(face.vertices.get(2).v.x, face.vertices.get(2).v.y, face.vertices.get(2).v.z),
							meshBuilder.addNormal(face.vertices.get(2).n.x, face.vertices.get(2).n.y, face.vertices.get(2).n.z),
							meshBuilder.addTexCoords(face.vertices.get(2).t.u, face.vertices.get(2).t.v)
					)
			);
			
		}
		return meshBuilder.create();
	}

}
