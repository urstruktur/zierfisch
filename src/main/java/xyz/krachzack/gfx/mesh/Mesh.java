package xyz.krachzack.gfx.mesh;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import xyz.krachzack.gfx.mesh.Primitive;

public class Mesh {
	private float[] vertices;
	private int[] indexes;
	private Primitive type;
	private VertexAttribute[] attributes;
	
	public Mesh(float[] vertices, int[] indexes, Primitive type, VertexAttribute... attributes) {
		this.vertices = vertices;
		this.indexes = indexes;
		this.type = type;
		this.attributes = attributes;
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
