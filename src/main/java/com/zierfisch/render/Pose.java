package com.zierfisch.render;

import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import com.badlogic.ashley.core.Component;

public class Pose implements Component {

	/**
	 * <p>
	 * Holds the offset of an object as compared to the origin of the world
	 * <code>(0,0,0)</code>.
	 * </p>
	 * 
	 * <p>
	 * If you directly mutate this vector, you need to call <code>smut()</code>
	 * on the pose or use <code>setPosition(pos)</code> instead, so the model
	 * matrix will be recalculated accordingly.
	 * </p>
	 */
	public Vector3f position = new Vector3f();
	/**
	 * <p>
	 * Holds a uniform scale factor.
	 * </p>
	 * 
	 * <p>
	 * If you directly mutate this vector, you need to call <code>smut()</code>
	 * on the pose or use <code>setPosition(pos)</code> instead, so the model
	 * matrix will be recalculated accordingly..
	 * </p>
	 */
	public float scale = 1.0f;
	public Quaternionf orientation = new Quaternionf();
	public boolean dirty = false;
	
	private Matrix4f model = new Matrix4f();

	public void setScale(float scale) {
		this.scale = scale;
		dirty = true;
	}

	public void setPosition(Vector3f position) {
		this.position.set(position);
		dirty = true;
	}

	public void setOrientation(Quaternionf orientation) {
		this.orientation.set(orientation);
		dirty = true;
	}

	public void smut() {
		dirty = true;
	}

	public void clean() {
		dirty = false;
	}

	public boolean isDirty() {
		return dirty;
	}
	
	public Matrix4f getModel() {
		if(dirty) {
			model.identity();
			model.translate(position);
			model.rotate(orientation);
			model.scale(scale);
		}
		
		return model;
	}

}
