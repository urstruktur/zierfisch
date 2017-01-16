package com.zierfisch.gfx.mesh;

import java.util.Iterator;

public class VertexAttributeIterable implements Iterable<float[]>{

	private final VertexAttributeIterator iterator;
	
	public VertexAttributeIterable(VertexAttribute attr, float[] vertices, int[] indexes) {
		iterator = new VertexAttributeIterator(attr, vertices, indexes);
	}
	
	@Override
	public Iterator<float[]> iterator() {
		iterator.rewind();
		return iterator;
	}

}
