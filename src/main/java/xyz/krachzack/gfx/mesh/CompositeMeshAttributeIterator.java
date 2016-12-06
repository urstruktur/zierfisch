package xyz.krachzack.gfx.mesh;

import java.util.Iterator;

public class CompositeMeshAttributeIterator implements Iterator<float[][]> {

	private float[][] vertex;
	private VertexAttributeIterator[] iterators;
	
	public CompositeMeshAttributeIterator(VertexAttribute[] attributes, float[] vertices, int[] indexes) {
		if(attributes.length == 0) {
			throw new IndexOutOfBoundsException("Cannot iterate over zero attributes");
		}
		
		iterators = new VertexAttributeIterator[attributes.length];
		vertex = new float[attributes.length][];
		
		for(int i = 0; i < iterators.length; ++i) {
			iterators[i] = new VertexAttributeIterator(attributes[i], vertices, indexes);
			vertex[i] = iterators[i].getInternalVertexArray();
		}
	}
	
	/**
	 * Gets the two-dimensional float array that will be internally reused and returned on every
	 * call to next.
	 */
	public float[][] getInternalVertexArray() {
		return vertex;
	}

	public void rewind() {
		for(VertexAttributeIterator iter: iterators) {
			iter.rewind();
		}
	}
	
	public void rewind(int toIdx) {
		for(VertexAttributeIterator iter: iterators) {
			iter.rewind(toIdx);
		}
	}

	@Override
	public boolean hasNext() {
		return iterators[0].hasNext();
	}
	
	public boolean hasAtLeastNext(int count) {
		return iterators[0].hasAtLeastNext(count);
	}
	
	public void skip(int count) {
		for(VertexAttributeIterator iter: iterators) {
			iter.skip(count);
		}
	}

	@Override
	public float[][] next() {
		for(VertexAttributeIterator iter: iterators) {
			// This call updates the internal vertex arrays of the MeshAttributeIterator
			// Since vertex in this class holds references to the internal arrays of the iterators,
			// the internal change will also be reflected in the composite vertex array.
			iter.next();
		}
		
		return vertex;
	}
}
