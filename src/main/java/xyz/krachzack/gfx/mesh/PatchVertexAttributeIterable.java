package xyz.krachzack.gfx.mesh;

import java.util.Iterator;

import xyz.krachzack.gfx.render.Primitive;

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
