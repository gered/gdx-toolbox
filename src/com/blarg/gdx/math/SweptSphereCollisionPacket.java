package com.blarg.gdx.math;

import com.badlogic.gdx.math.Vector3;

public final class SweptSphereCollisionPacket {
	// defines the x/y/z radius of the entity being checked
	public final Vector3 ellipsoidRadius = new Vector3();

	public boolean foundCollision;
	public float nearestDistance;

	// the below fields are all in "ellipsoid space"

	public final Vector3 esVelocity = new Vector3();            // velocity of the entity
	public final Vector3 esNormalizedVelocity = new Vector3();
	public final Vector3 esPosition = new Vector3();            // current position of the entity

	public final Vector3 esIntersectionPoint = new Vector3();   // if an intersection is found

	public void toEllipsoidSpace(Vector3 v, Vector3 out) {
		out.x = v.x / ellipsoidRadius.x;
		out.y = v.y / ellipsoidRadius.y;
		out.z = v.z / ellipsoidRadius.z;
	}

	public void fromEllipsoidSpace(Vector3 v, Vector3 out) {
		out.x = v.x * ellipsoidRadius.x;
		out.y = v.y * ellipsoidRadius.y;
		out.z = v.z * ellipsoidRadius.z;
	}

	public void reset() {
		ellipsoidRadius.set(Vector3.Zero);
		foundCollision = false;
		nearestDistance = 0.0f;
		esVelocity.set(Vector3.Zero);
		esNormalizedVelocity.set(Vector3.Zero);
		esPosition.set(Vector3.Zero);
		esIntersectionPoint.set(Vector3.Zero);
	}
}
