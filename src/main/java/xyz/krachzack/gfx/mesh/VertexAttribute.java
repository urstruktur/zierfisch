package xyz.krachzack.gfx.mesh;

/**
 * Specifies the data layout of a vertex attribute inside a vertex array and
 * provides methods to fetch the attribute from vertex arrays.
 * 
 * @author phil
 */
public final class VertexAttribute {
	/**
	 * Name of the attribute, e.g. "position".
	 */
	public final String name;
	/**
	 * Size of the attribute in float addresses. A vec3 in GLSL will be 3 float
	 * addresses long in this value.
	 */
	public final int width;
	/**
	 * Offset of the first occurrence of the attribute in a vertex array. E.g.
	 * if the buffer contains a sequence of positions and normals, the offset of
	 * the position will be zero and the offset of the normal will be equal to
	 * the size of the position.
	 */
	public final int offset;
	/**
	 * Offset between occurrences of the attribute in the array in float
	 * addresses. For tightly packed arrays, this will be 0, for interleaved
	 * arrays it will be the size of the other attributes together.
	 */
	public final int stride;

	/**
	 * Initializes a new vertex attribute with the given values.
	 * 
	 * @param name
	 *            Name of the attribute, e.g. "position".
	 * @param width
	 *            Size of the attribute in float addresses. A vec3 in GLSL will
	 *            be 3 float addresses long in this value.
	 * @param offset
	 *            Offset of the first occurrence of the attribute in a vertex
	 *            array. E.g. if the buffer contains a sequence of positions and
	 *            normals, the offset of the position will be zero and the
	 *            offset of the normal will be equal to the size of the
	 *            position.
	 * @param stride
	 *            Offset between occurrences of the attribute in the array in
	 *            float addresses. For tightly packed arrays, this will be 0,
	 *            for interleaved arrays it will be the size of the other
	 *            attributes together.
	 */
	public VertexAttribute(String name, int width, int offset, int stride) {
		this.name = name;
		this.width = width;
		this.offset = offset;
		this.stride = stride;
	}

	/**
	 * <p>
	 * Fetches the nth occurrence of the vertex attribute inside the given
	 * indexed vertex array. The result is written into the given target array.
	 * The passed <code>indexIndex</code> is an index into the passed index
	 * array, which must contain valid vertex indexes.
	 * </p>
	 * 
	 * <p>
	 * The given vertex array must be laid out as specified in the called
	 * VertexAttribute.
	 * </p>
	 * 
	 * <p>
	 * Note that the indexes in the passed index array enumerate vertices and
	 * not individual floats in the vertex array.
	 * </p>
	 * 
	 * @param vertices
	 *            The vertex array, correctly laid out
	 * @param indexes
	 *            Vertex indexes into the given vertex array
	 * @param indexIndex
	 *            Index into the indexes array to fetch the index to fetch the
	 *            vertex
	 * @param target
	 *            Array to write the fetched vertex attribute value in
	 */
	public void fetch(float[] vertices, int[] indexes, int indexIndex, float[] target) {
		fetch(vertices, indexes[indexIndex], target);
	}

	/**
	 * <p>
	 * Fetches the nth occurrence of the vertex attribute inside the given
	 * vertex array. The result is written into the given target array.
	 * </p>
	 * 
	 * <p>
	 * The given vertex array must be laid out as specified in the called
	 * VertexAttribute.
	 * </p>
	 * 
	 * <p>
	 * Note that the index enumerates vertices and not individual floats in the
	 * vertex array.
	 * </p>
	 * 
	 * @param vertices
	 *            The vertex array, correctly laid out
	 * @param index
	 *            Index of the occurrence of the attribute to fetch, that is
	 *            vertex index
	 * @param target
	 *            Array to write the fetched vertex attribute value in
	 * 
	 */
	public void fetch(float[] vertices, int index, float[] target) {
		int startIdx = offset + index * (width + stride);
		System.arraycopy(vertices, startIdx, target, 0, width);
	}
	
	public void store(float[] vertices, int index, float[] values) {
		int startIdx = offset + index * (width + stride);
		System.arraycopy(values, 0, vertices, startIdx, width);
	}

	@Override
	public String toString() {
		return "VertexAttribute [name=" + name + ", width=" + width + ", offset=" + offset + ", stride=" + stride + "]";
	}
	
}