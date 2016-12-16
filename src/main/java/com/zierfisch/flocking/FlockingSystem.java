package com.zierfisch.flocking;

import java.util.LinkedList;
import java.util.List;

import org.joml.Vector3f;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.zierfisch.gui.PropertyAccessor;
import com.zierfisch.gui.TweakingSystem;
import com.zierfisch.render.Pose;


/**
 * A system applying flocking behaviour rules.
 * Based on an Processing example by Daniel Shiffman which implements the flocking rules of Craig Reynold.
 */
public class FlockingSystem extends IteratingSystem {
	
	List<Rule> rulez;

	public FlockingSystem() {
		super(Family.all(FlockingComponent.class, Pose.class).get());
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
	
	@Override
	public void addedToEngine(Engine engine) {
		rulez = new LinkedList<Rule>();
		
		RuleCohesion rc = new RuleCohesion();
		
		TweakingSystem ts = getEngine().getSystem(TweakingSystem.class);
		if(ts != null){
			ts.addSlider(new PropertyAccessor(){
				public void setProperty(float val) 	{ rc.setWeight(val); }
				public float getProperty() 			{ return rc.getWeight(); }
				public float getMin() 				{ return 0; }
				public float getMax() 				{ return 2; }
			},"cohesion weight");
		}

		rulez.add(rc);
		rulez.add(new RuleSeperation());
		rulez.add(new RuleAlign());
		
		super.addedToEngine(engine);
	}
}
