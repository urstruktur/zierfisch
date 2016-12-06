package xyz.krachzack.gfx.mesh;

import java.util.Iterator;

import xyz.krachzack.gfx.render.Primitive;

public class PatchCompositeVertexAttributeIterable implements Iterable<float[][][]> {

	private PatchCompositeVertexAttributeIterator iter;
	
	public PatchCompositeVertexAttributeIterable(VertexAttribute[] attributes, Primitive type, float[] vertices, int[] indexes) {
		iter = new PatchCompositeVertexAttributeIterator(attributes, type, vertices, indexes);
	}

	@Override
	public Iterator<float[][][]> iterator() {
		iter.rewind();
		return iter;
	}

}
