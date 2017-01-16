package com.zierfisch.gfx.mesh;

import java.util.Iterator;

public class PatchCompositeVertexAttributeIterator implements Iterator<float[][][]> {

	/**
	 * Each second-level element in the primitive array contains a
	 * two-dimensional float array that is managed by one iterator in this
	 * array.
	 */
	private VertexAttribute[] attributes;
	private Primitive type;
	private float[][][] primitive;
	private int primitiveCount;
	private int primitiveIdx;
	private float[] vertices;
	private int[] indexes;

	public PatchCompositeVertexAttributeIterator(VertexAttribute[] attributes, Primitive type, float[] vertices, int[] indexes) {
		this.type = type;
		this.vertices = vertices;
		this.indexes = indexes;
		this.attributes = attributes;

		primitive = new float[type.width][][];
		primitiveCount = indexes.length / type.increment;

		for (int primitiveIdx = 0; primitiveIdx < primitive.length; ++primitiveIdx) {
			primitive[primitiveIdx] = new float[attributes.length][];

			for (int attrIdx = 0; attrIdx < attributes.length; ++attrIdx) {
				primitive[primitiveIdx][attrIdx] = new float[attributes[attrIdx].width];
			}
		}
	}

	public void rewind() {
		primitiveIdx = -1;
	}

	@Override
	public boolean hasNext() {
		return (primitiveIdx + 1) < primitiveCount;
	}

	@Override
	public float[][][] next() {
		++primitiveIdx;

		for (int primitiveOffset = 0; primitiveOffset < type.width; ++primitiveOffset) {
			for (int attrIdx = 0; attrIdx < attributes.length; ++attrIdx) {
				attributes[attrIdx].fetch(vertices,
						indexes[primitiveIdx * type.increment + primitiveOffset],
						primitive[primitiveOffset][attrIdx]);
			}
		}

		return primitive;
	}

}
