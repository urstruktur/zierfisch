package com.zierfisch.flocking;

import java.util.LinkedList;
import java.util.List;

import org.joml.Vector3f;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.zierfisch.render.Pose;


/**
 * A system applying flocking behaviour rules.
 * Based on an Processing example by Daniel Shiffman which implements the flocking rules of Craig Reynold.
 */
public class FlockingSystem extends IteratingSystem {
	
	List<Rule> rulez;

	public FlockingSystem() {
		super(Family.all(FlockingComponent.class, Pose.class).get());
		rulez = new LinkedList<Rule>();
		
		rulez.add(new RuleSeperation());
		rulez.add(new RuleCohesion());
		rulez.add(new RuleAlign());
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		for(Rule rule : rulez){
			applyForce(entity, rule.calcForce(entity, getEntities()));
		}
	}

	private void applyForce(Entity entity, Vector3f force){
		entity.getComponent(Pose.class).acceleration.add(force);
	}
}
