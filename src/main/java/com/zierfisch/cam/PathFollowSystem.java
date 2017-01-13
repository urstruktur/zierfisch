package com.zierfisch.cam;

import org.joml.Vector3f;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.CatmullRomSpline;
import com.badlogic.gdx.math.Vector3;
import com.zierfisch.render.Pose;

public class PathFollowSystem extends IteratingSystem{
	
	CatmullRomSpline<Vector3> spline;
	
	public PathFollowSystem(){
		super(Family.all(PathFollower.class, Pose.class).get());
		Vector3[] controlPoints = new Vector3[]{
												new Vector3(0,0,1),
												new Vector3(2,-2.6f,-7.6f),
												new Vector3(3.1f,-1.8f,-15.4f),
												new Vector3(4.2f,-1.3f,-23.8f),
												new Vector3(4.6f,-0.4f,-29.8f),
												new Vector3(4.7f,-0.4f,-31f)};
		spline = new CatmullRomSpline<Vector3>(controlPoints, false);
	}

	
	public PathFollowSystem(CatmullRomSpline<Vector3> spline){
		super(Family.all(PathFollower.class, Pose.class).get());
		this.spline = spline;
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		// move along spline with speed*deltaTime
		Pose p = entity.getComponent(Pose.class);
		PathFollower f = entity.getComponent(PathFollower.class);
		
		if(f.position <= 1){
			Vector3 newPos = spline.valueAt(new Vector3(), f.position);
			p.position = convert(newPos);
			p.smut();
			
			f.position += deltaTime * f.speed * 0.001;
		}
	}
	
	/**
	 * converts a libgdx vector3 to a joml vector3f 
	 */
	private Vector3f convert(Vector3 v) {
		return new Vector3f(v.x,v.y,v.z);
	}

	/**
	 * converts a joml vector3f to a libgdx vector3
	 */
	private Vector3 convert(Vector3f v){
		return new Vector3(v.x,v.y,v.z);
	}
}
