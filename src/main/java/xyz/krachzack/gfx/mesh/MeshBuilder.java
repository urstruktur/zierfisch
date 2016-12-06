package xyz.krachzack.gfx.mesh;

import at.fhooe.mtd.sgl.math.Vector2d;
import at.fhooe.mtd.sgl.math.Vector3d;

public interface MeshBuilder {

	/**
	 * 
	 * Returns an integer that stores the attribute mask of position in its most
	 * significant byte and the attribute index in the least significant three
	 * bytes. This makes it possible to pass the attribute index to addVertex in
	 * any order.
	 * 
	 * @param attributeValue
	 * @return
	 */
	int addPosition(Vector2d attributeValue);

	int addPosition(Vector3d attributeValue);

	int addPosition(double... attributeValue);

	int addPosition(double[] attributeValue, int offset, int length);

	int addNormal(Vector3d attributeValue);

	int addNormal(double... attributeValue);

	int addNormal(double[] attributeValue, int offset, int length);

	int addTexCoords(Vector2d attributeValue);

	int addTexCoords(Vector3d attributeValue);

	int addTexCoords(double... attributeValue);

	int addTexCoords(double[] attributeValue, int offset, int length);

	int addColor(Vector3d attributeValue);

	int addColor(double... attributeValue);

	int addColor(double[] attributeValue, int offset, int length);

	int addTangent(Vector3d attributeValue);

	int addTangent(double... attributeValue);

	int addTangent(double[] attributeValue, int offset, int length);

	int addBitangent(Vector3d attributeValue);

	int addBitangent(double... attributeValue);

	int addBitangent(double[] attributeValue, int offset, int length);

	/**
	 * <p>
	 * Creates a new vertex with the given indexes. Note that this method
	 * ignores the most significant byte of the given indexes, so it is safe to
	 * pass the packed integers returned from the attribute creation methods that
	 * use this byte to store the attribute mask.
	 * </p>
	 * 
	 * <p>
	 * When manually calculating or counting indexes, it can be assumed that
	 * the indexes start at 0 with the first add operation and then monotonically rise
	 * by 1 with each add operation.
	 * </p>
	 * 
	 * <p>
	 * If some attributes are not available, e.g. because the model does not contain
	 * tangents, you can pass <code>-1</code> to explicitly mark an attribute as
	 * unavailable.
	 * </p>
	 * 
	 * <p>
	 * Returns the index of the newly added vertex, which can be used to form
	 * faces.
	 * </p>
	 * 
	 * @param positionIdx
	 *            Index of position attribute, or <code>-1</code>, if
	 *            inaccessible
	 * @param normalIdx
	 *            Index of normal vector attribute, or <code>-1</code>, if
	 *            inaccessible
	 * @param texCoordsIdx
	 *            Index of texture coordinates attribute, or <code>-1</code>, if
	 *            inaccessible
	 * @param colorIdx
	 *            Index of vertex color attribute, or <code>-1</code>, if
	 *            inaccessible
	 * @param tangentIdx
	 *            Index of tangent attribute, or <code>-1</code>, if
	 *            inaccessible
	 * @param bitangentIdx
	 *            Index of bitangent attribute, or <code>-1</code>, if
	 *            inaccessible
	 * @return index of the newly added vertex
	 */
	int addVertexWithAttributeIndexes(int positionIdx, int normalIdx, int texCoordsIdx, int colorIdx, int tangentIdx,
			int bitangentIdx);

	/**
	 * <p>
	 * Creates a new vertex using the packed attribute masks and attribute
	 * indexes returned from the attribute creation methods such as
	 * <code>addPosition</code>
	 * </p>
	 * 
	 * <p>
	 * Returns the index of the newly added vertex, which can be used to form
	 * faces.
	 * </p>
	 * 
	 * @param packedAttributeMasksAndIndexes
	 *            Attribute masks and indexes as returned from addPosition and
	 *            friends
	 * @return index of the newly added vertex
	 */
	int addVertex(int... packedAttributeMasksAndIndexes);

	/**
	 * <p>
	 * Adds a new face with the vertices that have the given set of vertex
	 * indexes. Note that attributes (e.g. positions) are not yet a vertex, but
	 * must be made into a vertex with <code>addVertex(…)</code>.
	 * </p>
	 * 
	 * 
	 * @param vertexIndexes
	 *            Vertex indexes to make into a vertex, must have at least one
	 *            element
	 * @return Index of the newly created face
	 */
	int addFace(int... vertexIndexes);

	/**
	 * Constructs a new mesh that contains all added faces.
	 * 
	 * @return
	 */
	Mesh create();
}
