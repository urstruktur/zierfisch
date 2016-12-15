package com.zierfisch.render;

import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import com.badlogic.ashley.core.Component;

/**
 * This component holds properties for position and movement of an entity.
 * <p> The concept of movement is based on the <em>simple vehicle model</em> by Craig W. Reynolds @see http://www.red3d.com/cwr/steer/gdc99/ </p>
 */
public class Pose implements Component {

	public static final float maxSpeed = 2f;
	public static final float maxForce = 0.03f;
	
	/**
	 * Holds mass of an object. It determines the effect of an applied steering force on the velocity.
	 */
	public float mass = 1f;
	
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
	/**
	 * <p>
	 * Holds the orientation for calculation of the rotation matrix.
	 * </p>
	 * 
	 * <p>
	 * You can change this orientation directly, but do not forget to call
	 * <code>smut()</code> after, or the new orientation will not be applied
	 * immediately.
	 * </p>
	 */
	public Quaternionf orientation = new Quaternionf();
	/**
	 * If <code>true</code> the model matrix of the pose is out of sync with the
	 * rest of the transform properties and must be recalculated when next
	 * accessed with <code>getModel()</code>.
	 */
	public boolean dirty = false;
	/**
	 * Holds the model matrix that applies all transformations specified in the
	 * pose transform parameters. If <code>dirty</code> the model matrix must be
	 * recalculated.
	 */
	public Vector3f velocity = new Vector3f();
	public Vector3f acceleration = new Vector3f();
	
	private Matrix4f model = new Matrix4f();

	public void setScale(float scale) {
		this.scale = scale;
		smut();
	}

	public void setPosition(Vector3f position) {
		this.position.set(position);
		smut();
	}

	public void setOrientation(Quaternionf orientation) {
		this.orientation.set(orientation);
		smut();
	}

	public void smut() {
		dirty = true;
	}

	private void clean() {
		dirty = false;
	}

	public boolean isDirty() {
		return dirty;
	}

	public Matrix4f getModel() {
		if (dirty) {
			model.identity();
			model.translate(position);
			model.rotate(orientation);
			model.scale(scale);
			
			clean();
		}

		return model;
	}

}
