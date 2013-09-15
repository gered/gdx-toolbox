package com.blarg.gdx.math;

import com.badlogic.gdx.math.Vector3;

public class SweptSphere {
	public final Vector3 position = new Vector3();

	public boolean foundCollision;
	public float nearestCollisionDistance;
	public final Vector3 nearestCollisionPoint = new Vector3();
	public boolean isOnGround;
	public boolean isSliding;
	public final Vector3 slidingPlaneNormal = new Vector3();
	public final Vector3 slidingPlaneOrigin = new Vector3();

	public final Vector3 radius = new Vector3();

	// "ellipsoid space" fields, equivalent to the above similarly named fields
	// they probably shouldn't be used directly

	public final Vector3 esPosition = new Vector3();
	public final Vector3 esVelocity = new Vector3();
	public final Vector3 esNormalizedVelocity = new Vector3();
	public final Vector3 esIntersectionPoint = new Vector3();

	public void setRadius(float radius) {
		this.radius.set(radius, radius, radius);
	}

	public void setRadius(float radiusX, float radiusY, float radiusZ) {
		this.radius.set(radiusX, radiusY, radiusZ);
	}

	public void reset() {
		position.set(Vector3.Zero);
		foundCollision = false;
		nearestCollisionDistance = 0.0f;
		nearestCollisionPoint.set(Vector3.Zero);
		isOnGround = false;
		isSliding = false;
		slidingPlaneNormal.set(Vector3.Zero);
		slidingPlaneOrigin.set(Vector3.Zero);
		radius.set(Vector3.Zero);
		esPosition.set(Vector3.Zero);
		esVelocity.set(Vector3.Zero);
		esNormalizedVelocity.set(Vector3.Zero);
		esIntersectionPoint.set(Vector3.Zero);
	}

	public static void toEllipsoidSpace(Vector3 in, Vector3 ellipsoidRadius, Vector3 out) {
		out.x = in.x / ellipsoidRadius.x;
		out.y = in.y / ellipsoidRadius.y;
		out.z = in.z / ellipsoidRadius.z;
	}

	public static void toEllipsoidSpace(Vector3 v, Vector3 ellipsoidRadius) {
		v.x /= ellipsoidRadius.x;
		v.y /= ellipsoidRadius.y;
		v.z /= ellipsoidRadius.z;
	}

	public static void fromEllipsoidSpace(Vector3 in, Vector3 ellipsoidRadius, Vector3 out) {
		out.x = in.x * ellipsoidRadius.x;
		out.y = in.y * ellipsoidRadius.y;
		out.z = in.z * ellipsoidRadius.z;
	}

	public static void fromEllipsoidSpace(Vector3 v, Vector3 ellipsoidRadius) {
		v.x *= ellipsoidRadius.x;
		v.y *= ellipsoidRadius.y;
		v.z *= ellipsoidRadius.z;
	}
}
