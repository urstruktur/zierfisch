package com.zierfisch.flocking;

import java.util.List;

import org.joml.Vector3f;

/***
 * 
 * @see http://devmag.org.za/2011/04/05/bzier-curves-a-tutorial/
 */
public class CubicBezierPath {
	List<Vector3f> points;
	
	public void add(Vector3f point){
		points.add(point);
	}
	
	/***
	 * Return point at t along the curve.
	 * @param t must be between 0 and 1
	 * @return Point at t
	 */
	public Vector3f get(float t){
		
		int nrOfBezierCurves = points.size()/3;
		int startPointIndex = ((int)(nrOfBezierCurves*t)) * 3;
		
		Vector3f p0 = points.get(startPointIndex);
		Vector3f p1 = points.get(startPointIndex+1);
		Vector3f p2 = points.get(startPointIndex+2);
		Vector3f p3 = points.get(startPointIndex+3);
		
		return null;
	}
	
	/***
	 * Return point at t along the curve reparameterized by arc length.
	 * This can be used to move along the curve at constant speed.
	 * @param t must be between 0 and 1
	 * @return Point at t
	 */
	public Vector3f getUniform(float t){
		// search for the 4 needed points
		
		// derivative of polynom for tangent
		// dM/dt = t≤(-3A + 9B - 9C + 3D) + t(6A - 12B + 6C) + (-3A + 3B)
		//Vector3f point = 
		
		return null;
	}
	
	public Vector3f getNearest(Vector3f point){
		return null;
	}
	
	public static Vector3f calculateBezierPoint(float t, final Vector3f p0, Vector3f p1, Vector3f p2, Vector3f p3){
	  float u = 1 - t;
	  float tt = t*t;
	  float uu = u*u;
	  float uuu = uu * u;
	  float ttt = tt * t;
	 
	  Vector3f p = p0.mul(uuu); //first term
	  p.add(p1.mul(3*uu*t));	//second term
	  p.add(p2.mul(3*u*tt));	//thrid term
	  p.add(p3.mul(ttt));		//fourth term
	 
	  return p;
	}
}
