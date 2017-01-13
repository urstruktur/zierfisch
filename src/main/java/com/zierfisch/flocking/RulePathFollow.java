package com.zierfisch.flocking;
import org.joml.Vector3f;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.math.Bezier;
import com.badlogic.gdx.math.Path;
import com.badlogic.gdx.math.Vector3;
import com.zierfisch.render.Pose;


/**
 * The follow path rule calculates a velocity based prediction of the future position and projects it to the nearest point on the path.
 * 
 * @see http://www.red3d.com/cwr/steer/gdc99/ path following
 */
public class RulePathFollow implements Rule {
	
	private float weight;
	//private float pathRadius;
	//private float offset;
	Path<Vector3> curve;
	
	public RulePathFollow(Path<Vector3> curve, float weight){
		this.curve = curve;
		this.weight = weight;
	}
	
	public RulePathFollow(Bezier<Vector3> curve){
		this(curve, 1.0f);
	}
	
	public void setWeight(float weight){
		this.weight = weight;
	}

	@Override
	public Vector3f calcForce(Entity target, ImmutableArray<Entity> neighbours) {
		Pose p = target.getComponent(Pose.class);
		
		Vector3f approximatePos = new Vector3f(p.position);
		approximatePos.add(p.velocity);	// add velocity to position to estimate future position
		
		float nearestCurveValue = curve.approximate(new Vector3(approximatePos.x,approximatePos.y,approximatePos.z));

		Vector3f desired_velocity = new Vector3f();
		if(!Float.isNaN(nearestCurveValue)){
			Vector3 point = new Vector3();
			curve.valueAt(point, nearestCurveValue);
			Vector3f nearestCurvePoint = new Vector3f(point.x, point.y, point.z);
			desired_velocity = nearestCurvePoint.sub(p.position).normalize().mul(Boid.maxSpeed);
		}
		
		return desired_velocity.mul(weight);
	}

	public float getWeight() {
		return weight;
	}
}
