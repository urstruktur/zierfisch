package xyz.krachzack.gfx.mesh;

import org.joml.Vector2f;
import org.joml.Vector3f;

public abstract class AbstractMeshBuilder implements MeshBuilder {

	public static final int ATTR_OFFSET_POSITIONS = 0;
	public static final int ATTR_OFFSET_NORMALS = ATTR_OFFSET_POSITIONS + 1;
	public static final int ATTR_OFFSET_TEX_COORDS = ATTR_OFFSET_NORMALS + 1;
	public static final int ATTR_OFFSET_COLORS = ATTR_OFFSET_TEX_COORDS + 1;
	public static final int ATTR_OFFSET_TANGENTS = ATTR_OFFSET_COLORS + 1;
	public static final int ATTR_OFFSET_BITANGENTS = ATTR_OFFSET_TANGENTS + 1;
	public static final int ATTR_OFFSET_MAX = ATTR_OFFSET_BITANGENTS;
	
	public static final int ATTR_POSITIONS = 1 << ATTR_OFFSET_POSITIONS;
	public static final int ATTR_NORMALS = 1 << ATTR_OFFSET_NORMALS;
	public static final int ATTR_TEX_COORDS = 1 << ATTR_OFFSET_TEX_COORDS;
	public static final int ATTR_COLORS = 1 << ATTR_OFFSET_COLORS;
	public static final int ATTR_TANGENTS = 1 << ATTR_OFFSET_TANGENTS;
	public static final int ATTR_BITANGENTS = 1 << ATTR_OFFSET_BITANGENTS;
	public static final int ATTR_ALL = ~0;

	protected static final int[] ATTRS = {
		ATTR_POSITIONS,
		ATTR_NORMALS,
		ATTR_TEX_COORDS,
		ATTR_COLORS,
		ATTR_TANGENTS,
		ATTR_BITANGENTS
	};
	
	protected static final String[] ATTR_NAMES = {
		"position",
		"normal",
		"texCoords",
		"color",
		"tangent",
		"bitangent"
	};
	
	private int attributeMask = ATTR_ALL;
	private double[] doAddBuf = new double[3];
	private Primitive type;
	
	public AbstractMeshBuilder(Primitive type) {
		this.type = type;
	}
	
	public Primitive getType() {
		return type;
	}

	public abstract int addRelevantAttribute(int attributeMask, double[] attributeValue, int offset, int length);
	
	public boolean isIrrelevant(int attributeMask) {
		return (this.attributeMask & attributeMask) == 0;
	}
	
	public boolean isRelevant(int attributeMask) {
		return (this.attributeMask & attributeMask) != 0;
	}
	
	public int pack(int attributeMask, int idx) {
		// Ignore already stored attributeMask in idx
		idx = idx & 0x00ffffff;
		
		return (attributeMask << (3*8)) | (idx & 0xffffff);
	}
	
	public int unpackAttributeMask(int packed) {
		return packed >> (3*8);
	}
	
	public int unpackIndex(int packed) {
		return packed & 0xffffff;
	}
	
	public int getAttributeMask() {
		return attributeMask;
	}
	
	public void setAttributeMask(int attributeMask) {
		this.attributeMask = attributeMask;
	}

	@Override
	public int addPosition(Vector2f attributeValue) {
		return addAttribute(ATTR_POSITIONS, attributeValue);
	}
	
	@Override
	public int addPosition(Vector3f attributeValue) {
		return addAttribute(ATTR_POSITIONS, attributeValue);
	}
	
	@Override
	public int addPosition(double... attributeValue) {
		return addAttribute(ATTR_POSITIONS, attributeValue);
	}
	
	@Override
	public int addPosition(double[] attributeValue, int offset, int length) {
		return addAttribute(ATTR_POSITIONS, attributeValue, offset, length);
	}
	
	@Override
	public int addNormal(Vector3f attributeValue) {
		return addAttribute(ATTR_NORMALS, attributeValue);
	}
	
	@Override
	public int addNormal(double... attributeValue) {
		return addAttribute(ATTR_NORMALS, attributeValue);
	}
	
	@Override
	public int addNormal(double[] attributeValue, int offset, int length) {
		return addAttribute(ATTR_NORMALS, attributeValue, offset, length);
	}
	
	@Override
	public int addTexCoords(Vector2f attributeValue) {
		return addAttribute(ATTR_TEX_COORDS, attributeValue);
	}
	
	@Override
	public int addTexCoords(Vector3f attributeValue) {
		return addAttribute(ATTR_TEX_COORDS, attributeValue);
	}
	
	@Override
	public int addTexCoords(double... attributeValue) {
		return addAttribute(ATTR_TEX_COORDS, attributeValue);
	}
	
	@Override
	public int addTexCoords(double[] attributeValue, int offset, int length) {
		return addAttribute(ATTR_TEX_COORDS, attributeValue, offset, length);
	}
	
	@Override
	public int addTangent(Vector3f attributeValue) {
		return addAttribute(ATTR_TANGENTS, attributeValue);
	}
	
	@Override
	public int addTangent(double... attributeValue) {
		return addAttribute(ATTR_TANGENTS, attributeValue);
	}
	
	@Override
	public int addTangent(double[] attributeValue, int offset, int length) {
		return addAttribute(ATTR_TANGENTS, attributeValue);
	}
	
	@Override
	public int addBitangent(Vector3f attributeValue) {
		return addAttribute(ATTR_BITANGENTS, attributeValue);
	}
	
	@Override
	public int addBitangent(double... attributeValue) {
		return addAttribute(ATTR_BITANGENTS, attributeValue);
	}
	
	@Override
	public int addBitangent(double[] attributeValue, int offset, int length) {
		return addAttribute(ATTR_BITANGENTS, attributeValue, offset, length);
	}
	
	@Override
	public int addColor(Vector3f attributeValue) {
		return addAttribute(ATTR_COLORS, attributeValue);
	}
	
	@Override
	public int addColor(double... attributeValue) {
		return addAttribute(ATTR_COLORS, attributeValue);
	}
	
	@Override
	public int addColor(double[] attributeValue, int offset, int length) {
		return addAttribute(ATTR_COLORS, attributeValue, offset, length);
	}
	
	/*private int addAttribute(int attributeMask, double attributeValue) {
		if(isIrrelevant(attributeMask)) {
			return -1;
		} else {
			doAddBuf[0] = attributeValue;
			return addAttribute(attributeMask, doAddBuf, 0, 1);
		}
	}*/

	private int addAttribute(int attributeMask, Vector2f attributeValue) {
		if(isIrrelevant(attributeMask)) {
			return -1;
		} else {
			doAddBuf[0] = attributeValue.x;
			doAddBuf[1] = attributeValue.y;
			return addAttribute(attributeMask, doAddBuf, 0, 2);
		}
	}

	private int addAttribute(int attributeMask, Vector3f attributeValue) {
		if(isIrrelevant(attributeMask)) {
			return -1;
		} else {
			doAddBuf[0] = attributeValue.x;
			doAddBuf[1] = attributeValue.y;
			doAddBuf[2] = attributeValue.z;
			return addAttribute(attributeMask, doAddBuf, 0, 3);
		}
	}

	private int addAttribute(int attributeMask, double[] attributeValue) {
		if(isIrrelevant(attributeMask)) {
			return -1;
		} else {
			return addAttribute(attributeMask, attributeValue, 0, attributeValue.length);
		}
	}

	private int addAttribute(int attributeMask, double[] attributeValue, int offset, int length) {
		if(isIrrelevant(attributeMask)) {
			return -1;
		} else {
			return pack(attributeMask, addRelevantAttribute(attributeMask, attributeValue, offset, length));
		}
	}
	
	@Override
	public int addVertexWithAttributeIndexes(int positionIdx, int normalIdx, int texCoordsIdx, int colorIdx,
			int tangentIdx, int bitangentIdx) {
		
		int attributeCount = 6;
		
		if(positionIdx == -1) --attributeCount;
		if(normalIdx == -1) --attributeCount;
		if(texCoordsIdx == -1) --attributeCount;
		if(colorIdx == -1) --attributeCount;
		if(tangentIdx == -1) --attributeCount;
		if(bitangentIdx == -1) --attributeCount;
		
		int[] packeds = new int[attributeCount];
		int packedIdx = 0;
		if(positionIdx != -1) {
			packeds[packedIdx++] = pack(ATTR_POSITIONS, positionIdx);
		}
		if(normalIdx != -1) {
			packeds[packedIdx++] = pack(ATTR_NORMALS, normalIdx);
		}
		if(texCoordsIdx != -1) {
			packeds[packedIdx++] = pack(ATTR_TEX_COORDS, texCoordsIdx);
		}
		if(colorIdx != -1) {
			packeds[packedIdx++] = pack(ATTR_COLORS, colorIdx);
		}
		if(tangentIdx != -1) {
			packeds[packedIdx++] = pack(ATTR_TANGENTS, tangentIdx);
		}
		if(bitangentIdx != -1) {
			packeds[packedIdx++] = pack(ATTR_BITANGENTS, bitangentIdx);
		}
		
		return addVertex(packeds);
	}
	
	
}
