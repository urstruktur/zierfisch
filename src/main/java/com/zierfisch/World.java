package com.zierfisch;

import org.joml.Vector3f;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.utils.ImmutableArray;
import com.zierfisch.assets.Maker;
import com.zierfisch.assets.geom.CuboidMaker;
import com.zierfisch.assets.geom.SkyboxMaker;
import com.zierfisch.cam.Camera;
import com.zierfisch.cam.PathFollower;
import com.zierfisch.flocking.Boid;
import com.zierfisch.gfx.ecs.Gestalt;
import com.zierfisch.gfx.ecs.Light;
import com.zierfisch.gfx.ecs.Pose;
import com.zierfisch.gfx.mesh.Mesh;
import com.zierfisch.gfx.mesh.MeshBuilder;
import com.zierfisch.gfx.mesh.Primitive;
import com.zierfisch.gfx.mesh.SegmentedMeshBuilder;
import com.zierfisch.gfx.shader.ShaderBuilder;
import com.zierfisch.gfx.tex.TextureBuilder;

public final class World {

	public static void add(Engine engine) {
		// build cave
		Maker maker = new Maker().setMesh("assets/models/cave_old.obj")
				.setShader("assets/shaders/cc/depth.vert.glsl", "assets/shaders/cc/depth.frag.glsl")
                .setTexture(0, "assets/textures/RockPerforated0029_1_seamless_S.png")
                .setTexture(4, "assets/textures/fog-gradient-03.png")
                .setTextureScale(12f);
		engine.addEntity(maker.build());
		
		// build seafloor
		maker.setMesh("assets/models/seafloor.obj")
			 .setShader("assets/shaders/cc/caustic.vert.glsl", "assets/shaders/cc/caustic.frag.glsl");
		engine.addEntity(maker.build());
	
		
		addMainCamera(engine);
		addAuxiliaryQuantumLightConvolutionAcceleratorBuffer();
		addFishflock(engine, 7, 7, 7, 0.4f);
		addSkybox(engine);
		//addFishflock(engine, 3, 5, 3, 0.5f);
	}
	
	private static void addAuxiliaryQuantumLightConvolutionAcceleratorBuffer() {
		/*Light light = new Light();
		
		Maker maker = new Maker().add(light);
		
		light.color.set(1.0f, 0.1f, 0.14f);
		Entity light1 = maker.setPosition(0.0f, 0.5f, -0.4f).build();*/
		
		// Changing the protoype affects the next built light, but not the already built one
		//light.color.set(0.0f, 1.1f, 0.14f);
		//Entity light2 = maker.setPosition(10.0f, 10.5f, 10.4f).add(light).build();
		
		//engine.addEntity(light2);
	}

	private static void addMainCamera(Engine engine) {
		Pose pose = new Pose();
		pose.position.set(0, 0, 1f);
		pose.orientation.rotateY((float) Math.PI);
		//pose.setFocus(new Vector3f());
		
		Entity cam = new Entity();
		cam.add(new Camera());
		cam.add(new PathFollower(0.017f));
		cam.add(pose);
		
		engine.addEntity(cam);
	}
	
	private static void addSkybox(Engine engine){
		Maker maker = new Maker().setMesh(new SkyboxMaker().make(new SegmentedMeshBuilder(Primitive.TRIANGLES),115))
				 .setShader("assets/shaders/cc/skybox.vert.glsl", "assets/shaders/cc/skybox.frag.glsl")
                .setTexture(4, "assets/textures/fog-gradient-03.png");

		engine.addEntity(maker.build());
	}
	
	private static void addFishflock(Engine engine, int nrX, int nrY, int nrZ, float margin){
		Boid protoypeBoid = new Boid();
		protoypeBoid.influence = 0.3f;
		
		Maker maker = new Maker().setMesh("assets/models/zierfisch.obj")
								 .setShader("assets/shaders/cc/fish.vert.glsl", "assets/shaders/cc/fish.frag.glsl")
		                         .setTexture(0, "assets/textures/fish-diffuse.png")
		                         .setTexture(1, "assets/textures/fish-emission.png")
		                         .setTexture(4, "assets/textures/fog-gradient-03.png")
		                         .setScale(0.5f)
		                         .add(protoypeBoid);
		
		
		Vector3f center = new Vector3f(0,0,-5);
		
		for(int x = 0; x < nrX; x++){
			for(int y = 0; y < nrY; y++){
				for(int z = 0; z < nrZ; z++){
					
					Entity fish = maker.setPosition(
						x*margin - (nrX-1)*margin/2 + center.x,
						y*margin - (nrY-1)*margin/2 + center.y,
						z*margin - (nrZ-1)*margin/2 + center.z
					).build();
					
					engine.addEntity(fish);
				}
			}
		}
		
		ImmutableArray<Entity> allEnts = engine.getEntities();
		
		Entity randomEnt1 = allEnts.get(allEnts.size() - 1);
		Light light1 = new Light();
		light1.color.set(.3f, 0.9f, 1f);
		light1.intensity = 0.7f;
		randomEnt1.add(light1);
		
		
		/*Entity startLight = new Entity();
		startLight.add(new Light());
		startLight.add(new Pose());
		startLight.getComponent(Light.class).color.set(1.0f, 0.7f, 0.94f);
		startLight.getComponent(Light.class).intensity = 0.01f;
		startLight.getComponent(Pose.class).position.y = 1.0f;
		engine.addEntity(startLight);*/
		
		/*
		
		Entity randomEnt2 = allEnts.get((int) (Math.random() * allEnts.size()));
		Light light2 = new Light();
		light2.color.set(.6f, 1f, 1f);
		light2.intensity = 1.0f;
		randomEnt2.add(light2);
		
		Entity randomEnt3 = allEnts.get((int) (Math.random() * allEnts.size()));
		Light light3 = new Light();
		light3.color.set(.7f, 1f, 1f);
		light3.intensity = 10.0f;
		randomEnt3.add(light3);*/
	}
	
}
