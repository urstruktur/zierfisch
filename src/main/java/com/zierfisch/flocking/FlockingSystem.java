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
			applyForce(entity, rule.calcForce(entity, getEntities()).mul(deltaTime*0.1f));
		}
	}

	private void applyForce(Entity entity, Vector3f force){
		entity.getComponent(Pose.class).acceleration.add(force);
	}
	
	@Override
	public void addedToEngine(Engine engine) {
		rulez = new LinkedList<Rule>();
		
		RuleCohesion rc = new RuleCohesion();
		RuleSeperation rs = new RuleSeperation();
		RuleAlign ra = new RuleAlign();
		
		// add tweaking sliders
		TweakingSystem ts = getEngine().getSystem(TweakingSystem.class);
		if(ts != null){
			ts.addSlider(new PropertyAccessor(){
				public void setProperty(float val) 	{ rc.setWeight(val); }
				public float getProperty() 			{ return rc.getWeight(); }
				public float getMin() 				{ return 0; }
				public float getMax() 				{ return 1; }
			},"cohesion weight");
			
			ts.addSlider(new PropertyAccessor(){
				public void setProperty(float val) 	{ rc.setDist(val); }
				public float getProperty() 			{ return rc.getDist(); }
				public float getMin() 				{ return 0; }
				public float getMax() 				{ return 10; }
			},"cohesion distance");
			
			ts.addSlider(new PropertyAccessor(){
				public void setProperty(float val) 	{ ra.setWeight(val); }
				public float getProperty() 			{ return ra.getWeight(); }
				public float getMin() 				{ return 0; }
				public float getMax() 				{ return 3; }
			},"align weight");
			
			ts.addSlider(new PropertyAccessor(){
				public void setProperty(float val) 	{ ra.setDist(val); }
				public float getProperty() 			{ return ra.getDist(); }
				public float getMin() 				{ return 0; }
				public float getMax() 				{ return 10; }
			},"align distance");
			
			ts.addSlider(new PropertyAccessor(){
				public void setProperty(float val) 	{ rs.setWeight(val); }
				public float getProperty() 			{ return rs.getWeight(); }
				public float getMin() 				{ return 0; }
				public float getMax() 				{ return 1; }
			},"seperation weight");
			
			ts.addSlider(new PropertyAccessor(){
				public void setProperty(float val) 	{ rs.setDist(val); }
				public float getProperty() 			{ return rs.getDist(); }
				public float getMin() 				{ return 0; }
				public float getMax() 				{ return 10; }
			},"seperation distance");
		}

		rulez.add(rc);
		rulez.add(rs);
		rulez.add(ra);
		
		super.addedToEngine(engine);
	}
}
