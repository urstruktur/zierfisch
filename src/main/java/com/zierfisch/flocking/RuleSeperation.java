package com.zierfisch.flocking;
import org.joml.Vector3f;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.utils.ImmutableArray;
import com.zierfisch.gfx.ecs.Pose;


/**
 * The seperation rule calculates the sum of the weighted vectors pointing away from adjecant boids.
 */
public class RuleSeperation implements Rule {
	
	private float weight;
	private float influenceDist;
	
	public RuleSeperation(){
		weight = 0.1f;
		influenceDist = 0.4f;
	}
	
	public void setWeight(float weight){
		this.weight = weight;
	}
	
	
	public RuleSeperation(float seperationDistance, float weight){
		this.influenceDist = seperationDistance;
		this.weight = weight;
	}

	@Override
	public Vector3f calcForce(Entity target, ImmutableArray<Entity> neighbours) {
		Vector3f steer = new Vector3f();
		int countOfInfluenced = 0;

		Vector3f targetPos = target.getComponent(Pose.class).position;
		for(Entity boid : neighbours){
			Vector3f neighborPos = boid.getComponent(Pose.class).position;
			float dist = targetPos.distance(neighborPos);
			
			if(dist > 0 && dist < influenceDist * target.getComponent(Boid.class).influence){
				Vector3f toTarget = new Vector3f();
				targetPos.sub(neighborPos, toTarget);
				toTarget.normalize();
				toTarget.mul(1/(dist*dist)); // weight by distance
				steer.add(toTarget);
				countOfInfluenced++;
			}
		}
		
		// averaging
		if(countOfInfluenced > 0){
			steer.div(countOfInfluenced);
		}
		
		return steer.mul(weight);
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
