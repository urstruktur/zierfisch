package xyz.krachzack.gfx.assets;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import xyz.krachzack.gfx.mesh.Mesh;
import xyz.krachzack.gfx.mesh.MeshBuilder;

/**
 * <p>
 * Loads wavefront OBJ models from arbitrary input streams using a mesh builder.
 * </p>
 * 
 * <p>
 * Note that only a subset of OBJ is supported. The most notable restrictions are:
 * </p>
 * 
 * <ul>
 * 	<li>Only supports triangular faces
 *  <li>No material support
 *  <li>Any metadata like section names is thrown away
 *  <li>The file you are going to really need will fail because this class is largely untested
 * </ul>
 * 
 * @author phil
 */
public class ObjLoader {

	private static final Pattern vertexPattern = Pattern.compile("(?<position>\\d*)/(?<texCoords>\\d*)/(?<normal>\\d*)|(?<position2>\\d*)/(?<texCoords2>\\d*)");
	
	private MeshBuilder builder;

	public Mesh load(MeshBuilder builder, InputStream inputStream) throws IOException {
		this.builder = builder;
		
		BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
		String line;
		
		while((line = in.readLine()) != null) {
			parseLine(line.trim());
		}
		
		return builder.create();
	}
	
	private void parseLine(String line) {
		if(line.startsWith("#") || line.isEmpty()) {
			return;
		}
		
		String[] tokens = line.split("\\s+");
		
		switch(tokens[0]) {
		case "v":
			parsePosition(tokens);
			break;
			
		case "vt":
			parseTexCoords(tokens);
			break;
			
		case "vn":
			parseNormal(tokens);
			break;
			
		case "f":
			parseFace(tokens);
			break;
		
		case "g":
		case "usemtl":
		case "mtllib":
		case "s":
		case "o":
			// Unsupported by design
			break;
			
		default:
			throw new AssetFormatException("OBJ directive " + tokens[0] + " is unsupported");
		}
	}
	
	private void parsePosition(String[] line) {
		if(line.length < 4) {
			throw new AssetFormatException("v directive in obj could not be parsed: " + Arrays.toString(line));
		}
		
		builder.addPosition(Double.valueOf(line[1]), Double.valueOf(line[2]), Double.valueOf(line[3]));
	}
	
	private void parseTexCoords(String[] line) {
		if(line.length < 3) {
			throw new AssetFormatException("vt directive in obj could not be parsed: " + Arrays.toString(line));
		}
		
		builder.addTexCoords(Double.valueOf(line[1]), Double.valueOf(line[2]));
	}
	
	private void parseNormal(String[] line) {
		if(line.length < 4) {
			throw new AssetFormatException("vt directive in obj could not be parsed: " + Arrays.toString(line));
		}
		
		builder.addNormal(Double.valueOf(line[1]), Double.valueOf(line[2]), Double.valueOf(line[3]));
	}
	
	private void parseFace(String[] line) {
		// f v/vt/vn v/vt/vn v/vt/vn v/vt/vn
		if(line.length != 4) {
			throw new AssetFormatException("Only triangle faces are currently supported in OBJ: " + Arrays.toString(line));
		}
		
		int vertIdx1 = makeVertex(line[1]);
		int vertIdx2 = makeVertex(line[2]);
		int vertIdx3 = makeVertex(line[3]);
		
		builder.addFace(vertIdx1, vertIdx2, vertIdx3);
	}
	
	private int makeVertex(String faceIndexesStr) {
		Matcher matcher = vertexPattern.matcher(faceIndexesStr);
		matcher.matches(); // Named groups are only available after calling this

		int positionIndex = extractIndex(matcher, "position");
		int textureCoordIndex = extractIndex(matcher, "texCoords");
		int normalIndex = extractIndex(matcher, "normal");
		
		return builder.addVertexWithAttributeIndexes(positionIndex, normalIndex, textureCoordIndex, -1, -1, -1);
	}
	
	private int extractIndex(Matcher matcher, String groupName) {
		String idxString = matcher.group(groupName);
		
		if(idxString == null) {
			if(groupName == "position" || groupName == "texCoords") {
				idxString = matcher.group(groupName + "2");
			} else {
				idxString = "";
			}
		}
		
		if(idxString.isEmpty()) {
			return -1;
		}
		
		// -1 because indexes are 1-based in OBJ
		return Integer.parseInt(idxString) - 1;
	}

}
