package com.blarg.gdx.math;

import com.badlogic.gdx.math.collision.BoundingBox;

public interface SweptSphereWorldCollisionChecker {
	boolean checkForCollisions(SweptSphere sphere, BoundingBox possibleCollisionArea);
}
