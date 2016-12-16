package com.zierfisch.render;

import org.joml.Vector3f;
import org.joml.Vector3fc;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.zierfisch.flocking.FlockingComponent;

/**
 * This system applies the acceleration and velocity onto the position taking the delta time into account.
 */
public class MovementSystem extends IteratingSystem {

	public MovementSystem() {
		super(Family.one(Pose.class).get());
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		Pose p = entity.getComponent(Pose.class);
		p.velocity.add(p.acceleration);
		
		// clamp to maxSpeed
		if(p.velocity.lengthSquared() > Pose.maxSpeed*Pose.maxSpeed){
			p.velocity.normalize();
			p.velocity.mul(Pose.maxSpeed);
		}
		
		p.position.add(p.velocity.mul(deltaTime, new Vector3f()));
		
		if(p.velocity.lengthSquared() > 0){
			p.orientation.identity();
			p.orientation.lookAlong(new Vector3f(p.velocity).normalize(), new Vector3f(0,1,0));
		}
		
		p.acceleration.zero();

		p.smut();
	}

}
