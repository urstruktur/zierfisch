package com.zierfisch.maker;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.joml.Vector3f;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.zierfisch.render.Gestalt;
import com.zierfisch.render.Pose;
import com.zierfisch.shader.Shader;
import com.zierfisch.shader.ShaderBuilder;
import com.zierfisch.tex.Texture;
import com.zierfisch.tex.TextureLoader;
import com.zierfisch.util.ObjImporter;

import xyz.krachzack.gfx.mesh.Mesh;
import xyz.krachzack.gfx.mesh.MeshBuilder;
import xyz.krachzack.gfx.mesh.Primitive;
import xyz.krachzack.gfx.mesh.SegmentedMeshBuilder;

/**
 * <p>
 * Prepare to meet your baker!
 * </p>
 * 
 * <p>
 * For the glory of the almighty architect of everything that is, was and will be.
 * </p>
 * 
 * <p>
 * With a thick and tasty crust.
 * </p>
 * 
 * @author phil
 */
public class Maker {

	Pose pose;
	Gestalt gestalt;
	/**
	 * Holds components of which a clone will be added to each entity.
	 */
	List<Component> extraComponents;
	
	public Maker() {
		reset();
	}
	
	public void reset() {
		pose = new Pose();
		gestalt = new Gestalt();
		extraComponents = new ArrayList<>();
	}
	
	public Maker setPosition(float x, float y, float z) {
		pose.position.set(x, y, z);
		return this;
	}
	
	public Maker setPosition(Vector3f pos) {
		pose.position.set(pos);
		return this;
	}
	
	public Maker setScale(float scale) {
		pose.scale = scale;
		return this;
	}
	
	/**
	 * <p>Saves a component to later add a copy to each built entity.</p>
	 * 
	 * <p>The component is required to have a copy constructor</p>
	 * 
	 * @param comp
	 * @return
	 */
	public Maker add(Component comp) {
		extraComponents.add(comp);
		return this;
	}
	
	public Maker setShader(Shader shader) {
		gestalt.shader = shader;
		return this;
	}
	
	public Maker setShader(String vertexShaderPath, String fragmentShaderPath) {
		Shader shader = new ShaderBuilder().setVertexShader(vertexShaderPath)
		                                   .setFragmentShader(fragmentShaderPath)
		                                   .build();
		return setShader(shader);
	}
	
	public Maker setShader(String vertexShaderPath, String geometryShaderPath, String fragmentShaderPath) {
		Shader shader = new ShaderBuilder().setVertexShader(vertexShaderPath)
		                                   .setGeometryShader(geometryShaderPath)
		                                   .setFragmentShader(fragmentShaderPath)
		                                   .build();
		return setShader(shader);
	}
	
	public Maker setTextures(Texture... texes) {
		for(int i = 0; i < texes.length; ++i) {
			setTexture(i, texes[i]);
		}
		return this;
	}
	
	public Maker setTexture(int idx, Texture texture) {
		switch(idx) {
		case 0:
			gestalt.texture0 = texture;
			break;
			
		case 1:
			gestalt.texture1 = texture;
			break;
			
		case 2:
			gestalt.texture2 = texture;
			break;
			
		case 3:
			gestalt.texture3 = texture;
			break;
			
		case 4:
			gestalt.texture4 = texture;
			break;
			
		default:
			throw new RuntimeException("No texture slot in Gestalt: " + idx);
		}
		
		return this;
	}
	
	public Maker setTexture(int idx, String texturePath) {
		 Texture tex = new TextureLoader().load(texturePath);
		 return setTexture(idx, tex);
	}
	
	public Maker setTextures(String... texturePaths) {
		for(int i = 0; i < texturePaths.length; ++i) {
			setTexture(i, texturePaths[i]);
		}
		return this;
	}
	
	public Maker setMesh(String objFilePath) {
		gestalt.mesh = meshFromObjPath(objFilePath);
		return this;
	}
	
	public Maker setMesh(Mesh mesh) {
		gestalt.mesh = mesh;
		return this;
	}
	
	public Entity build() {
		Entity ent = new Entity().add(new Pose(pose))
		                         .add(new Gestalt(gestalt));
		
		for(Component comp: extraComponents) {
			try {
				Component clone = comp.getClass().getConstructor(comp.getClass()).newInstance(comp);
				ent.add(clone);
			} catch (NoSuchMethodException e) {
				throw new RuntimeException("No copy constructor available", e);
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | SecurityException e) {
				throw new RuntimeException(e);
			}
		}
		
		return ent;
	}

	private Mesh meshFromObjPath(String objFilePath) {
		ObjImporter importer = new ObjImporter();
		
		try {
			importer.load(objFilePath);
		} catch (IOException e) {
			throw new RuntimeException("Maker could not read OBJ file", e);
		}
		
		MeshBuilder objBuilder = new SegmentedMeshBuilder(Primitive.TRIANGLES);
		return importer.make(objBuilder);
	}
}
