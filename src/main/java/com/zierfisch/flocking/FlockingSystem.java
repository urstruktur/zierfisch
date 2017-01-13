package com.zierfisch.flocking;

import java.util.LinkedList;
import java.util.List;

import org.joml.Vector3f;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.CatmullRomSpline;
import com.badlogic.gdx.math.Vector3;
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
		super(Family.all(Boid.class, Pose.class).get());
	}

	@Override
	public void addedToEngine(Engine engine) {
		rulez = new LinkedList<Rule>();
		
		RuleCohesion rc = new RuleCohesion();
		RuleSeperation rs = new RuleSeperation();
		RuleAlign ra = new RuleAlign();
		
		Vector3[] controlPoints = new Vector3[]{
				new Vector3(0,0,1),
				new Vector3(2,-2.6f,-7.6f),
				new Vector3(3.1f,-1.8f,-15.4f),
				new Vector3(4.2f,-1.3f,-23.8f),
				new Vector3(4.6f,-0.4f,-29.8f),
				new Vector3(4.7f,-0.4f,-31f), // höhlenausgang
				new Vector3(4.8f,0.7f,-37f),
				new Vector3(11.1f,0.5f,-33f), // schiffsbug
				new Vector3(12.8f,-0.4f,-42.4f), // behind ship
				new Vector3(12.8f,-0.7f,-44f),};
		PreciseCatmullRomSpline<Vector3> spline = new PreciseCatmullRomSpline<Vector3>(Vector3.class, controlPoints, false, 0.1f);
		RulePathFollow rf = new RulePathFollow(spline,0.2f);
		
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
			
			ts.addSlider(new PropertyAccessor(){
				public void setProperty(float val) 	{ rf.setWeight(val); }
				public float getProperty() 			{ return rf.getWeight(); }
				public float getMin() 				{ return 0; }
				public float getMax() 				{ return 1; }
			},"follow path");
		}

		rulez.add(rc);
		rulez.add(rs);
		rulez.add(ra);
		rulez.add(rf);
		
		super.addedToEngine(engine);
	}
	
	
	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		
		// calculate and apply forces
		for(Rule rule : rulez){
			entity.getComponent(Pose.class).acceleration.add(rule.calcForce(entity, getEntities()).mul(deltaTime));
		}

		// move boid according to velocity & acceleration
		Pose p = entity.getComponent(Pose.class);
		Boid b = entity.getComponent(Boid.class);
		p.velocity.add(p.acceleration);
		
		// clamp to maxSpeed
		if(p.velocity.lengthSquared() > Boid.maxSpeed*Boid.maxSpeed){
			p.velocity.normalize();
			p.velocity.mul(Boid.maxSpeed);
		}
		
		p.position.add(p.velocity.mul(deltaTime, new Vector3f()));
		
		// calculate orientation
		if(p.velocity.lengthSquared() > 0){
			p.orientation.identity();
			
		    //Vector3f new_forward = new Vector3f(p.velocity).normalize();
		   // b.up.normalize();      
		   // Vector3f new_side = new Vector3f(new_forward).cross(b.up);
		   // b.up = new_forward.cross(new_side);
			//p.orientation.lookAlong(new_forward, b.up);

			// simple method
			p.orientation.lookAlong(new Vector3f(p.velocity).normalize(), new Vector3f(0,1,0));
		}
		
		// reset acceleration
		p.acceleration.zero();

		p.smut();
	}
	

	/**
	 * @see http://www.red3d.com/cwr/steer/gdc99/ vehicle model
	 */
	public void applySteeringForce(Vector3f force){
		
	}
}
