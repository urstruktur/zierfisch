package xyz.krachzack.gfx.mesh;

import java.util.Iterator;

public class CompositeMeshAttributeIterable implements Iterable<float[][]> {

	private final CompositeMeshAttributeIterator iter;
	
	public CompositeMeshAttributeIterable(VertexAttribute[] attributes, float[] vertices, int[] indexes) {
		iter = new CompositeMeshAttributeIterator(attributes, vertices, indexes);
	}

	@Override
	public Iterator<float[][]> iterator() {
		iter.rewind();
		return iter;
	}

}
