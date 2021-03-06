package com.zierfisch.flocking;

import org.joml.Vector3f;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.utils.ImmutableArray;
import com.zierfisch.gfx.ecs.Pose;

/**
 * The cohesion rule calculates the mean position of all adjacent boids and subsequently calculates the force vector towards that position.
 */
public class RuleCohesion implements Rule {

	private float weight;
	private float influenceDist;
	
	public RuleCohesion(){
		weight = 1f;
		influenceDist = 8f;
	}
	
	@Override
	public Vector3f calcForce(Entity target, ImmutableArray<Entity> neighbours) {
		
		int countOfInfluenced = 0;

		Vector3f targetPos = target.getComponent(Pose.class).position;
		Vector3f sum = new Vector3f();
		for (Entity boid : neighbours) {
			Vector3f neighborPos = boid.getComponent(Pose.class).position;
			float dist = targetPos.distance(neighborPos);
			if (dist > 0 && dist < influenceDist * target.getComponent(Boid.class).influence) {
				sum.add(neighborPos);
				countOfInfluenced++;
			}
		}

		if (countOfInfluenced > 0) {
			sum.div(countOfInfluenced);	// mean position of adjecent boids
			Vector3f steer = new Vector3f();

			sum.sub(targetPos, steer); // TODO: check if direction is right
			steer.normalize();
			steer.mul(Boid.maxSpeed);
			
			steer.sub(target.getComponent(Pose.class).velocity); // Steering = Desired - Velocity
			
			// clamp to maxForce
			if(steer.lengthSquared() > Boid.maxForce*Boid.maxForce){	
				steer.normalize();
				steer.mul(Boid.maxForce);
			}

			return steer.mul(weight);
		} else {
			return new Vector3f();
		}
	}
	
	public float getWeight(){
		return weight;
	}

	public void setWeight(float weight){
		this.weight = weight;
	}
	
	public float getDist(){
		return influenceDist;
	}

	public void setDist(float val) {
		this.influenceDist = val;
	}
}
