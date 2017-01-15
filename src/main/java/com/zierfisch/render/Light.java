package com.zierfisch.render;

import org.joml.Vector3f;

import com.badlogic.ashley.core.Component;

public class Light implements Component {
	
	public Vector3f color = new Vector3f(1.0f, 1.0f, 1.0f);
	public float intensity = 1.0f;
	
	public Light() {}
	
	public Light(Light other) {
		color.set(other.color);
		intensity = other.intensity;
	}
	
}
