package com.zierfisch.flocking;

import org.joml.Vector3f;

import com.badlogic.ashley.core.Component;

/**
 * flag component for flocking behaviour
 */
public class Boid implements Component{
	public float influence = 1f;
	
	/**
	 * describes the maxium thrust the entity can generate itself
	 */
	public static final float maxForce = 1f;
	
	/**
	 * describes the maximum speed that the entity can reach, to simulate friction and viscous drag
	 */
	public static final float maxSpeed = 1f;
	
	/**
	 * Holds mass of an object. It determines the effect of an applied steering force on the velocity.
	 */
	public float mass = 2f;
	
	public Vector3f up = new Vector3f(0,1,0);
	
	public Boid() {}
	
	public Boid(Boid other) {
		influence = other.influence;
		mass = other.mass;
	}
}
