package com.zierfisch.flocking;
import org.joml.Vector3f;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.utils.ImmutableArray;
import com.zierfisch.render.Pose;


/**
 * The seperation rule calculates the sum of the weighted vectors pointing away from adjecant boids.
 */
public class RuleOrbit implements Rule {
	
	private float weight;
	Vector3f center;
	
	public RuleOrbit(){
		weight = 1f;
		center = new Vector3f();
	}
	
	public void setWeight(float weight){
		this.weight = weight;
	}
	
	
	public RuleOrbit(float weight){
		this.weight = weight;
	}

	@Override
	public Vector3f calcForce(Entity target, ImmutableArray<Entity> neighbours) {
		Vector3f steer = new Vector3f();

		Pose p = target.getComponent(Pose.class);
		
		Vector3f tangent = p.position;
		tangent.sub(center); // calc vector from center to position
		tangent.normalize();
		//tangent.rotate
		
		
		return steer.mul(weight);
	}

	public float getWeight() {
		return weight;
	}
}
