package xyz.krachzack.gfx.mesh;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.carrotsearch.hppc.DoubleArrayList;
import com.carrotsearch.hppc.IntArrayList;

/**
 * <p>
 * Builds meshes with the attributes stored tightly packed one attribute after
 * the other.
 * </p>
 * 
 * <p>
 * Example with three added three-component positions and normals, where each xyz
 * represents three consecutive floats in the vertex array of the resulting
 * created mesh:
 * 
 * <pre>
 * positions  normals
 *     |        |
 *     V        V
 * |       ||       |
 * xyzxyzxyzxyzxyzxyz
 * <pre>
 * </p>
 * 
 * @author phil
 */
public class SegmentedMeshBuilder extends AbstractMeshBuilder {

	/**
	 * Holds an array holding the attribute values of the attribute represented
	 * by the given index. E.g.
	 * <code>attributeBufs[ATTR_OFFSET_POSITIONS]</code> will hold all the
	 * position components. <code>null</code> is contained for ignored or
	 * inaccessible attributes.
	 */
	private DoubleArrayList[] attributeBufs = new DoubleArrayList[ATTR_OFFSET_MAX + 1];
	/**
	 * Zero for ignored or unavailable attributes, otherwise number of
	 * components in attribute
	 */
	private int[] attributeSizes = new int[ATTR_OFFSET_MAX + 1];
	
	/** Stores equally-sized arrays of attribute indexes, each forming one vertex */
	private List<int[]> vertices = new ArrayList<>();
	
	/** Stores equally-sized arrays of faces, each face containing the indexes of the vertices forming it */
	private List<int[]> faces = new ArrayList<>();
	
	private int attributeCount = -1;
	
	public SegmentedMeshBuilder(Primitive type) {
		super(type);
	}
	
	/**
	 * <p>
	 * Returns the base 2 logarithm for the given number.
	 * </p>
	 * 
	 * <p>
	 * For power-of-two numbers, this acts like a reverse left bitshift, e.g.
	 * <code>log2(1 << 4)</code> returns <code>4</code>.
	 * </p>
	 * 
	 * <p>
	 * For input zero, it will return 0, despite the result actually not being
	 * mathematically defined.
	 * </p>
	 * 
	 * @param bits
	 *            Integer to obtain the logarithm of
	 * @return The base 2 logarithm of the given number
	 */
	private static int log2(int bits) {
		return 31 - Integer.numberOfLeadingZeros(bits);
	}

	@Override
	public int addRelevantAttribute(int attributeMask, double[] attributeValue, int offset, int length) {
		int attributeOffset = log2(attributeMask);
		DoubleArrayList buf = attributeBufs[attributeOffset];

		if (buf == null) {
			buf = new DoubleArrayList();
			attributeBufs[attributeOffset] = buf;
			attributeSizes[attributeOffset] = length;
		}

		int lengthBeforeAdding = buf.size();

		buf.add(attributeValue, offset, length);

		return lengthBeforeAdding / length;
	}
	
	@Override
	public int addVertex(int... packedAttributeMasksAndIndexes) {
		int[] packeds = Arrays.copyOf(packedAttributeMasksAndIndexes, packedAttributeMasksAndIndexes.length);
		
		if(attributeCount == -1) {
			attributeCount = packeds.length;
		} else if(attributeCount != packeds.length) {
			throw new MeshLayoutException("Expected " + attributeCount + " attributes per vertex, but got " + packeds.length);
		}
		
		int lengthBefore = vertices.size();
		vertices.add(packeds);
		return lengthBefore;
	}

	@Override
	public int addFace(int... vertexIndexes) {
		if(vertexIndexes.length != getType().width) {
			throw new MeshLayoutException("Tried to add face with " + vertexIndexes.length + " edges, " +
					"but builder type is " + getType() + ", which implies " + getType().width + " vertices per face");
		}
		
		int[] idxs = Arrays.copyOf(vertexIndexes, vertexIndexes.length);
		int lengthBefore = faces.size();
		faces.add(idxs);
		return lengthBefore;
	}

	@Override
	public Mesh create() {
		List<VertexAttribute> attributes = new ArrayList<>();
		IntArrayList attributeOffsets = new IntArrayList();
		
		int lastAttributeEnd = 0;
		for(int attrOffset = 0; attrOffset < ATTRS.length; ++attrOffset) {
			if(attributeSizes[attrOffset] > 0) {
				String name = ATTR_NAMES[attrOffset];
				int size = attributeSizes[attrOffset];
				VertexAttribute attr = new VertexAttribute(name, size, lastAttributeEnd, 0);
				attributes.add(attr);
				attributeOffsets.add(attrOffset);
				lastAttributeEnd += vertices.size() * size;
			}
		}
		
		float[] meshVertices = new float[lastAttributeEnd];
		int vertIdx = 0;
		
		for(int i = 0; i < attributes.size(); ++i) {
			VertexAttribute attribute = attributes.get(i);
			int attributeOffset = attributeOffsets.get(i);
			
			for(int[] vertex: this.vertices) {
				for(int vertAttrPacked: vertex) {
					int mask = unpackAttributeMask(vertAttrPacked);
					int offset = log2(mask);
					int attrIdx = unpackIndex(vertAttrPacked);
					
					if(offset == attributeOffset) {
						DoubleArrayList attrBuf = attributeBufs[offset];
						int attrBufOffset = attrIdx * attribute.width;
						
						for(int j = attrBufOffset; j < (attrBufOffset + attribute.width); ++j) {
							meshVertices[vertIdx++] = (float) attrBuf.get(j);
						}
						
						break;
					}
				}
			}
		}
		
		int[] meshIndexes = new int[faces.size() * getType().width];
		int indexIdx = 0;
		for(int[] face: faces) {
			for(int vertexIdx: face) {
				meshIndexes[indexIdx++] = vertexIdx;
			}
		}
		
		return new Mesh(meshVertices, meshIndexes, getType(), attributes.toArray(new VertexAttribute[0]));
	}
}
