package xyz.krachzack.gfx.mesh;

import java.util.Iterator;

/**
 * Enumerates the value of one attribute of each vertex inside an indexed vertex
 * array.
 * 
 * The data layout of the underlying vertex array is specified with a
 * VertexAttribute instance passed to the constructor.
 * 
 * @author phil
 */
class VertexAttributeIterator implements Iterator<float[]> {
	/**
	 * Index of the index of the attribute last returned by next(). Will be
	 * <code>-1</code> after a rewind, so the first next() returns the attribute
	 * value at index zero.
	 */
	private int currentIndexesIdx;
	/** Specifies data layout of  */
	private VertexAttribute attr;
	private float[] vertices;
	private int[] indexes;
	private float[] vertex;

	public VertexAttributeIterator(VertexAttribute attr, float[] vertices, int[] indexes) {
		this.attr = attr;
		this.vertices = vertices;
		this.indexes = indexes;

		vertex = new float[attr.width];
	}

	/**
	 * Gets the float array that will be internally reused and returned on every
	 * call to next.
	 */
	public float[] getInternalVertexArray() {
		return vertex;
	}

	/** Must be called before calling hasNext or next */
	public void rewind() {
		currentIndexesIdx = -1;
	}

	public void rewind(int toIdx) {
		currentIndexesIdx = toIdx;
	}

	@Override
	public boolean hasNext() {
		return (currentIndexesIdx + 1) < indexes.length;
	}

	public boolean hasAtLeastNext(int count) {
		return (currentIndexesIdx + count) < indexes.length;
	}

	@Override
	public float[] next() {
		if (!hasNext()) {
			throw new IndexOutOfBoundsException("Calling next but there are no more vertices");
		}

		++currentIndexesIdx;
		
		attr.fetch(vertices, indexes, currentIndexesIdx, vertex);

		return vertex;
	}

	public void skip(int count) {
		currentIndexesIdx += count;
	}
}