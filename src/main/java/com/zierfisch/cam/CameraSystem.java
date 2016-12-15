package com.zierfisch.cam;

import org.joml.Vector3f;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.zierfisch.Main;
import com.zierfisch.render.Pose;


public class CameraSystem extends IteratingSystem {

	private static final Family CAM_FAMILY = Family.all(Camera.class, Pose.class).get();
	private ComponentMapper<Camera> cm = ComponentMapper.getFor(Camera.class);
	private ComponentMapper<Pose> pm = ComponentMapper.getFor(Pose.class);
	
	private Camera mainCam;
	private Vector3f forward = new Vector3f();
	private Vector3f up = new Vector3f();
	private Vector3f focus = new Vector3f();
	/**
	 * Sets the camera whose entity was first added to the engine as the main
	 * camera. If that same entity is later removed, mainCam will be set to null
	 * again. When a new camera is added, it will become the new main camera.
	 */
	private EntityListener mainCamSetter = new EntityListener() {
		@Override
		public void entityAdded(Entity entity) {
			if(mainCam == null) {
				mainCam = cm.get(entity);
			}
		}
		
		@Override
		public void entityRemoved(Entity entity) {
			if(mainCam == cm.get(entity)) {
				mainCam = null;
			}
		}
		
	};
	
	public CameraSystem() {
		super(CAM_FAMILY);
	}
	
	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		Camera cam = cm.get(entity);
		Pose pose = pm.get(entity);
		updateCam(cam, pose);
	}
	
	private void updateCam(Camera cam, Pose pose) {
		recalculateView(cam, pose);
		recalculateProjection(cam);
	}
	
	public void recalculateView(Camera cam, Pose pose) {
		focus.set(0, 0, 1);
		pose.orientation.transform(focus);
		focus.add(pose.position);
		
		up.set(0, 1, 0);
		pose.orientation.transform(up);
		
		cam.view.setLookAt(pose.position, focus, up);
	}

	public void recalculateProjection(Camera cam) {
		cam.projection.setPerspective(cam.fovy, Main.WINDOW_ASPECT, cam.nearPlaneDist, cam.farPlaneDist);
	}
	
	/**
	 * Gets the first camera that was added to the engine.
	 * 
	 * @return the main camera
	 */
	public Camera getMainCamera() {
		return mainCam;
	}
	
	@Override
	public void addedToEngine(Engine engine) {
		super.addedToEngine(engine);
		engine.addEntityListener(CAM_FAMILY, mainCamSetter);
	}
	
	@Override
	public void removedFromEngine(Engine engine) {
		super.removedFromEngine(engine);
		engine.removeEntityListener(mainCamSetter);
	}
	
}
