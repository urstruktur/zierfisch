package com.zierfisch.gfx.mesh;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.zierfisch.gfx.vbo.VBO;

public class Mesh {
	private float[] vertices;
	private int[] indexes;
	private Primitive type;
	private VertexAttribute[] attributes;
	private VBO vertexBuffer;
	private VBO indexBuffer;
	
	public Mesh(float[] vertices, int[] indexes, Primitive type, VertexAttribute... attributes) {
		this.vertices = vertices;
		this.indexes = indexes;
		this.type = type;
		this.attributes = attributes;
	}
	
	/**
	 * Uploads the contents of vertices and indexes into vertex bufer objects.
	 */
	public void upload() {
		vertexBuffer = new VBO();
		vertexBuffer.setContents(vertices);
		
		indexBuffer = new VBO();
		indexBuffer.setContents(indexes);
	}
	
	public VBO getVertexBuffer() {
		if(vertexBuffer == null) {
			upload();
		}
		
		return vertexBuffer;
	}
	
	public VBO getIndexBuffer() {
		if(indexBuffer == null) {
			upload();
		}
		
		return indexBuffer;
	}
	
	public List<VertexAttribute> getAttributes() {
		return Collections.unmodifiableList(Arrays.asList(attributes));
	}
	
	public Primitive getType() {
		return type;
	}
	
	public float[] getVertices() {
		return vertices;
	}
	
	public int[] getIndexes() {
		return indexes;
	}
	
	public VertexAttribute findAttribute(String attrName) {
		for(VertexAttribute attr: attributes) {
			if(attr.name.equals(attrName)) {
				return attr;
			}
		}
		
		return null;
	}
	
	public Iterable<float[][]> eachPatch(VertexAttribute attribute) {
		return new PatchVertexAttributeIterable(attribute, type, vertices, indexes);
	}
	
	public Iterable<float[][]> eachLine(VertexAttribute attribute) {
		if(type != Primitive.LINES) {
			throw new MeshLayoutException("eachLine requires that the mesh has LINES primitive type, instead has: " + type);
		}
		
		return eachPatch(attribute);
	}
	
	public Iterable<float[][]> eachTriangle(VertexAttribute attribute) {
		if(type != Primitive.TRIANGLES) {
			throw new MeshLayoutException("eachTriangle requires that the mesh has TRIANGLES primitive type, instead has: " + type);
		}
		
		return eachPatch(attribute);
	}
	
	public Iterable<float[][][]> eachPatch(VertexAttribute... attributes) {
		return new PatchCompositeVertexAttributeIterable(attributes, type, vertices, indexes);
	}
	
	public Iterable<float[][][]> eachLine(VertexAttribute... attributes) {
		if(type != Primitive.LINES) {
			throw new MeshLayoutException("eachLine requires that the mesh has LINES primitive type, instead has: " + type);
		}
		
		return eachPatch(attributes);
	}
	
	public Iterable<float[][][]> eachTriangle(VertexAttribute... attributes) {
		if(type != Primitive.TRIANGLES) {
			throw new MeshLayoutException("eachTriangle requires that the mesh has TRIANGLES primitive type, instead has: " + type);
		}
		
		return eachPatch(attributes);
	}
	
	public Iterable<float[]> eachVertex(VertexAttribute attribute) {
		return new VertexAttributeIterable(attribute, vertices, indexes);
	}
	
	public Iterable<float[][]> eachVertex(VertexAttribute... attributes) {
		return new CompositeMeshAttributeIterable(attributes, vertices, indexes);
	}
}
