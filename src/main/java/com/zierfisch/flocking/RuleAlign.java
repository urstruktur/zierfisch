package com.zierfisch.flocking;

import org.joml.Vector3f;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.utils.ImmutableArray;
import com.zierfisch.render.Pose;

/**
 * The alignment rule calculates the velocity direction mean of all adjecent boids.
 */
public class RuleAlign implements Rule {
	
	private float weight;
	private float influenceDist;
	
	public RuleAlign(){
		weight = 1.4f;
		influenceDist = 1f;
	}

	@Override
	public Vector3f calcForce(Entity target, ImmutableArray<Entity> neighbours) {
		int countOfInfluenced = 0;
		Pose targetPose = target.getComponent(Pose.class);
		Vector3f velSum = new Vector3f();
		for (Entity boid : neighbours) {
			Vector3f neighborVel = boid.getComponent(Pose.class).velocity;
			float dist = targetPose.position.distance(neighborVel);
			if (dist > 0 && dist < influenceDist * target.getComponent(Boid.class).influence) {
				velSum.add(neighborVel);
				countOfInfluenced++;
			}
		}

		if (countOfInfluenced > 0) {
			velSum.div(countOfInfluenced);
			velSum.normalize();
			velSum.mul(Boid.maxSpeed);
			
			Vector3f steer = velSum.sub(target.getComponent(Pose.class).velocity); // Steering = Desired - Velocity
			
			// clamp to maxForce
			if(steer.lengthSquared() > Boid.maxForce * Boid.maxForce){	
				steer.normalize();
				steer.mul(Boid.maxForce);
			}
			
			return steer.mul(weight);
		} else {
			return new Vector3f();
		}
	}

	public void setWeight(float weight){
		this.weight = weight;
	}

	public float getWeight() {
		return weight;
	}
	
	public float getDist(){
		return influenceDist;
	}

	public void setDist(float val) {
		this.influenceDist = val;
	}
}
