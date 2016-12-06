package xyz.krachzack.gfx.mesh;

import java.util.Iterator;

import xyz.krachzack.gfx.render.Primitive;

public class PatchVertexAttributeIterator implements Iterator<float[][]> {

	/**
	 * Each second-level element in the primitive array contains a
	 * two-dimensional float array that is managed by one iterator in this
	 * array.
	 */
	private VertexAttribute attribute;
	private Primitive type;
	private float[][] primitive;
	private int primitiveCount;
	private int primitiveIdx;
	private float[] vertices;
	private int[] indexes;

	public PatchVertexAttributeIterator(VertexAttribute attribute, Primitive type, float[] vertices, int[] indexes) {
		this.type = type;
		this.vertices = vertices;
		this.indexes = indexes;
		this.attribute = attribute;

		primitive = new float[type.width][];
		primitiveCount = indexes.length / type.increment;

		for (int primitiveIdx = 0; primitiveIdx < primitive.length; ++primitiveIdx) {
			primitive[primitiveIdx] = new float[attribute.width];
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
	public float[][] next() {
		++primitiveIdx;

		for (int primitiveOffset = 0; primitiveOffset < type.width; ++primitiveOffset) {
			attribute.fetch(vertices, indexes[primitiveIdx * type.increment + primitiveOffset],
					primitive[primitiveOffset]);
		}

		return primitive;
	}
}
