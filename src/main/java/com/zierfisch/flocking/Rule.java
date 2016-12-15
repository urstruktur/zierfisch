package com.zierfisch.flocking;
import org.joml.Vector3f;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.utils.ImmutableArray;

/**
 * A rule determines one aspect of boid movement by calculating a steering force vector.
 */
public interface Rule {
	Vector3f calcForce(Entity target, ImmutableArray<Entity> neighbours);
}
