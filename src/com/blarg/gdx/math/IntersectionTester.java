package com.blarg.gdx.math;

import com.badlogic.gdx.math.Plane;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.math.collision.Sphere;

// providing support for the odd gaps in testing methods in libgdx's own Intersector class ...
// (plus a few extras here and there!)

public class IntersectionTester {
	static final Vector3 tmp1 = new Vector3();
	static final Vector3 tmp2 = new Vector3();

	public static float getSquaredDistanceFromPointToBox(Vector3 point, BoundingBox box) {
		float distanceSq = 0.0f;
		float v;

		v = point.x;
		if (v < box.min.x)
			distanceSq += (box.min.x - v) * (box.min.x - v);
		if (v > box.max.x)
			distanceSq += (v - box.max.x) * (v - box.max.x);

		v = point.y;
		if (v < box.min.y)
			distanceSq += (box.min.y - v) * (box.min.y - v);
		if (v > box.max.y)
			distanceSq += (v - box.max.y) * (v - box.max.y);

		v = point.z;
		if (v < box.min.z)
			distanceSq += (box.min.z - v) * (box.min.z - v);
		if (v > box.max.z)
			distanceSq += (v - box.max.z) * (v - box.max.z);

		return distanceSq;
	}

	public static float getDistanceFromPointToBox(Vector3 point, BoundingBox box) {
		return (float)Math.sqrt(getSquaredDistanceFromPointToBox(point, box));
	}

	public static boolean overlaps(BoundingBox a, BoundingBox b) {
		if (a.max.x < b.min.x || a.min.x > b.max.x)
			return false;
		else if (a.max.y < b.min.y || a.min.y > b.max.y)
			return false;
		else if (a.max.z < b.min.z || a.min.z > b.max.z)
			return false;
		else
			return true;
	}

	public static boolean overlaps(BoundingBox box, Sphere sphere) {
		float distanceSq = getSquaredDistanceFromPointToBox(sphere.center, box);

		if (distanceSq <= (sphere.radius * sphere.radius))
			return true;
		else
			return false;
	}

	public static boolean contains(Sphere sphere, Vector3 point) {
		if (Math.abs(point.dst(sphere.center)) < sphere.radius)
			return true;
		else
			return false;
	}

	public static boolean intersects(Sphere sphere, Plane plane) {
		float distance = sphere.center.dot(plane.normal) - plane.d;
		if (Math.abs(distance) <= sphere.radius)
			return true;
		else
			return false;
	}

	public static boolean intersects(BoundingBox box, Plane plane) {
		tmp1.set(box.max).add(box.min).scl(0.5f);   // (box.max + box.min) / 2.0f
		tmp2.set(box.max).sub(tmp1);

		float radius = (tmp2.x * Math.abs(plane.normal.x))
				+ (tmp2.y * Math.abs(plane.normal.y))
				+ (tmp2.z * Math.abs(plane.normal.z));

		float distance = plane.normal.dot(tmp1) - plane.d;

		if (Math.abs(distance) <= radius)
			return true;
		else
			return false;
	}
}
