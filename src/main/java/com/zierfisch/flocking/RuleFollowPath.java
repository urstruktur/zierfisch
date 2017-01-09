package com.zierfisch.flocking;
import org.joml.Vector3f;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.math.Bezier;
import com.badlogic.gdx.math.Vector3;
import com.zierfisch.render.Pose;


/**
 * The follow path rule calculates a velocity based prediction of the future position and projects it to the nearest point on the path.
 * 
 * @see http://www.red3d.com/cwr/steer/gdc99/ path following
 */
public class RuleFollowPath implements Rule {
	
	private float weight;
	private float pathRadius;
	private float offset;
	Bezier<Vector3> curve;
	
	public RuleFollowPath(Bezier<Vector3> curve, float weight){
		this.curve = curve;
		this.weight = weight;
	}
	
	public RuleFollowPath(Bezier<Vector3> curve){
		this(curve, 0.5f);
	}
	
	public void setWeight(float weight){
		this.weight = weight;
	}

	@Override
	public Vector3f calcForce(Entity target, ImmutableArray<Entity> neighbours) {
		Vector3f steer = new Vector3f();
		Pose p = target.getComponent(Pose.class);
		/*
		Vector3f approximatePos = new Vector3f().set(p.position);
		approximatePos.add(p.velocity);
		float nearestCurveValue = curve.approximate(new Vector3(p.position.x,p.position.y,p.position.z));
		Vector3 point = curve.valueAt(new Vector3(), nearestCurveValue);
		Vector3f nearestCurvePoint = new Vector3f(point.x, point.y, point.z);
		*/

		// seek:
		// desired_velocity = normalize (position - target) * max_speed
	    // steering = desired_velocity - velocity
		
		//return steer.mul(weight);
		
		Vector3f dir = new Vector3f(0.1f*weight,0,-weight);
		//Vector3f dir = new Vector3f(0,0,0);
		
		return dir;
	}

	public float getWeight() {
		return weight;
	}
}
