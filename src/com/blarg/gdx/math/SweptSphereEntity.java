package com.blarg.gdx.math;

import com.badlogic.gdx.math.Vector3;

public class SweptSphereEntity {
	public final Vector3 position = new Vector3();

	public boolean foundCollision;
	public final Vector3 nearestCollisionPoint = new Vector3();
	public boolean isSliding;
	public final Vector3 slidingPlaneNormal = new Vector3();

	public final SweptSphereCollisionPacket collisionPacket = new SweptSphereCollisionPacket();

	public SweptSphereEntity() {
	}

	public void setSize(float radius) {
		collisionPacket.ellipsoidRadius.set(radius, radius, radius);
	}

	public void setSize(float radiusX, float radiusY, float radiusZ) {
		collisionPacket.ellipsoidRadius.set(radiusX, radiusY, radiusZ);
	}
}
