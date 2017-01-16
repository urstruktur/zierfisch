package com.zierfisch.gfx.mesh;

import java.util.Iterator;

public class PatchVertexAttributeIterable implements Iterable<float[][]> {
	private PatchVertexAttributeIterator iter;
	
	public PatchVertexAttributeIterable(VertexAttribute attribute, Primitive type, float[] vertices, int[] indexes) {
		iter = new PatchVertexAttributeIterator(attribute, type, vertices, indexes);
	}

	@Override
	public Iterator<float[][]> iterator() {
		iter.rewind();
		return iter;
	}

}
