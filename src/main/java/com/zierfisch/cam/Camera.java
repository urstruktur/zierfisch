package com.zierfisch.cam;

import org.joml.Matrix4f;

import com.badlogic.ashley.core.Component;

public class Camera implements Component {

	public Matrix4f view = new Matrix4f();
	public Matrix4f projection = new Matrix4f();
	/**
	 * Holds the vertical field of view angle in radians,
	 * with a maximum of PI.
	 */
	public float fovy = (float) Math.toRadians(70.0);
	public float nearPlaneDist = 0.01f;
	public float farPlaneDist = 100f;
	
}
