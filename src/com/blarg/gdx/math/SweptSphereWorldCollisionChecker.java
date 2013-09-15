package com.blarg.gdx.math;

import com.badlogic.gdx.math.collision.BoundingBox;

public interface SweptSphereWorldCollisionChecker {
	void checkForCollisions(SweptSphereEntity entity, BoundingBox possibleCollisionArea);
}
