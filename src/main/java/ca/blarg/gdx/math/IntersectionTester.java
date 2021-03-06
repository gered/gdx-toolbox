package ca.blarg.gdx.math;

import com.badlogic.gdx.math.Plane;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.math.collision.Sphere;

/**
 * Various intersection tests between 3D geometric shapes.
 * Note that libgdx's own {@link com.badlogic.gdx.math.Intersector} does implement some of these
 * tests already, but not all of them. I've just included all of these here for consistency's sake
 * and to make porting some of my other projects easier.
 */
public final class IntersectionTester {
	static final Vector3 tmp1 = new Vector3();
	static final Vector3 tmp2 = new Vector3();
	static final Vector3 tmp3 = new Vector3();
	static final Vector3 point = new Vector3();

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

	public static boolean test(BoundingBox box, Vector3 point) {
		if ((point.x >= box.min.x && point.x <= box.max.x) &&
		    (point.y >= box.min.y && point.y <= box.max.y) &&
		    (point.z >= box.min.z && point.z <= box.max.z))
			return true;
		else
			return false;
	}

	public static boolean test(Sphere sphere, Vector3 point) {
		if (Math.abs(point.dst2(sphere.center)) < (sphere.radius * sphere.radius))
			return true;
		else
			return false;
	}

	public static boolean test(BoundingBox box, Vector3[] vertices, Vector3 outFirstIntersection) {
		for (int i = 0; i < vertices.length; ++i) {
			if ((vertices[i].x >= box.min.x && vertices[i].x <= box.max.x) &&
			    (vertices[i].y >= box.min.y && vertices[i].y <= box.max.y) &&
			    (vertices[i].z >= box.min.z && vertices[i].z <= box.max.z)) {
				if (outFirstIntersection != null)
					outFirstIntersection.set(vertices[i]);
				return true;
			}
		}
		return false;
	}

	public static boolean test(Sphere sphere, Vector3[] vertices, Vector3 outFirstIntersection) {
		for (int i = 0; i < vertices.length; ++i) {
			if (Math.abs(vertices[i].dst2(sphere.center)) < (sphere.radius * sphere.radius)) {
				if (outFirstIntersection != null)
					outFirstIntersection.set(vertices[i]);
				return true;
			}
		}
		return false;
	}

	public static boolean test(BoundingBox a, BoundingBox b) {
		if (a.max.x < b.min.x || a.min.x > b.max.x)
			return false;
		else if (a.max.y < b.min.y || a.min.y > b.max.y)
			return false;
		else if (a.max.z < b.min.z || a.min.z > b.max.z)
			return false;
		else
			return true;
	}

	public static boolean test(Sphere a, Sphere b) {
		tmp1.set(a.center).sub(b.center);
		float distanceSquared = tmp1.dot(tmp1);
		float radiusSum = a.radius + b.radius;
		if (distanceSquared <= (radiusSum * radiusSum))
			return true;
		else
			return false;
	}

	public static boolean test(Sphere sphere, Plane plane) {
		float distance = sphere.center.dot(plane.normal) - plane.d;
		if (Math.abs(distance) <= sphere.radius)
			return true;
		else
			return false;
	}

	public static boolean test(BoundingBox box, Plane plane) {
		tmp1.set(box.max).add(box.min).scl(0.5f);   // (box.max + box.min) / 2.0f
		tmp2.set(box.max).sub(tmp1);

		float radius = (tmp2.x * Math.abs(plane.normal.x)) +
		               (tmp2.y * Math.abs(plane.normal.y)) +
		               (tmp2.z * Math.abs(plane.normal.z));

		float distance = plane.normal.dot(tmp1) - plane.d;

		if (Math.abs(distance) <= radius)
			return true;
		else
			return false;
	}

	public static boolean test(Ray ray, Plane plane, Vector3 outIntersection) {
		float denominator = ray.direction.dot(plane.normal);
		if (denominator == 0.0f)
			return false;

		float t = ((-plane.d - ray.origin.dot(plane.normal)) / denominator);
		if (t < 0.0f)
			return false;

		if (outIntersection != null)
			ray.getEndPoint(outIntersection, t);

		return true;
	}

	public static boolean test(Ray ray, Sphere sphere, Vector3 outFirstIntersection) {
		tmp1.set(ray.origin).sub(sphere.center);

		float b = tmp1.dot(ray.direction);
		float c = tmp1.dot(tmp1) - (sphere.radius * sphere.radius);

		if (c > 0.0f && b > 0.0f)
			return false;

		float discriminant = b * b - c;
		if (discriminant < 0.0f)
			return false;

		float t = -b - (float)Math.sqrt(discriminant);
		if (t < 0.0f)
			t = 0.0f;

		if (outFirstIntersection != null)
			ray.getEndPoint(outFirstIntersection, t);

		return true;
	}

	public static boolean test(Ray ray, BoundingBox box, Vector3 outFirstIntersection) {
		float tmin = 0.0f;
		float tmax = Float.MAX_VALUE;

		if (Math.abs(ray.direction.x) < MathHelpers.FLOAT_EPSILON) {
			if (ray.origin.x < box.min.x || ray.origin.x > box.max.x)
				return false;
		} else {
			float invD = 1.0f / ray.direction.x;
			float t1 = (box.min.x - ray.origin.x) * invD;
			float t2 = (box.max.x - ray.origin.x) * invD;

			if (t1 > t2) {
				float tswap = t1;
				t1 = t2;
				t2 = tswap;
			}

			tmin = Math.max(tmin, t1);
			tmax = Math.min(tmax, t2);

			if (tmin > tmax)
				return false;
		}

		if (Math.abs(ray.direction.y) < MathHelpers.FLOAT_EPSILON) {
			if (ray.origin.y < box.min.y || ray.origin.y > box.max.y)
				return false;
		} else {
			float invD = 1.0f / ray.direction.y;
			float t1 = (box.min.y - ray.origin.y) * invD;
			float t2 = (box.max.y - ray.origin.y) * invD;

			if (t1 > t2) {
				float tswap = t1;
				t1 = t2;
				t2 = tswap;
			}

			tmin = Math.max(tmin, t1);
			tmax = Math.min(tmax, t2);

			if (tmin > tmax)
				return false;
		}

		if (Math.abs(ray.direction.z) < MathHelpers.FLOAT_EPSILON) {
			if (ray.origin.z < box.min.z || ray.origin.z > box.max.z)
				return false;
		} else {
			float invD = 1.0f / ray.direction.z;
			float t1 = (box.min.z - ray.origin.z) * invD;
			float t2 = (box.max.z - ray.origin.z) * invD;

			if (t1 > t2) {
				float tswap = t1;
				t1 = t2;
				t2 = tswap;
			}

			tmin = Math.max(tmin, t1);
			tmax = Math.min(tmax, t2);

			if (tmin > tmax)
				return false;
		}

		if (outFirstIntersection != null)
			ray.getEndPoint(outFirstIntersection, tmin);

		return true;
	}

	public static boolean test(BoundingBox box, Sphere sphere) {
		float distanceSq = getSquaredDistanceFromPointToBox(sphere.center, box);
		if (distanceSq <= (sphere.radius * sphere.radius))
			return true;
		else
			return false;
	}

	public static boolean test(Ray ray, Vector3 a, Vector3 b, Vector3 c, Vector3 outIntersection) {
		float r;
		float num1;
		float num2;

		tmp1.set(b).sub(a);
		tmp2.set(c).sub(a);
		tmp3.set(tmp1).crs(tmp2);
		if (tmp3.x == 0.0f && tmp3.y == 0.0f && tmp3.z == 0.0f)
			return false;

		tmp1.set(ray.origin).sub(a);
		num1 = -tmp3.dot(tmp1);
		num2 = tmp3.dot(ray.direction);
		if (Math.abs(num2) < MathHelpers.FLOAT_EPSILON) {
			if (num1 == 0.0f) {
				if (outIntersection != null)
					outIntersection.set(ray.origin);
				return true;
			} else
				return false;
		}

		r = num1 / num2;
		if (r < 0.0f)
			return false;

		ray.getEndPoint(point, r);
		if (test(point, a, b, c)) {
			if (outIntersection != null)
				outIntersection.set(point);
			return true;
		} else
			return false;
	}

	public static boolean test(Vector3 point, Vector3 a, Vector3 b, Vector3 c) {
		tmp1.set(c).sub(a);
		tmp2.set(b).sub(a);
		tmp3.set(point).sub(a);

		float dot00 = (tmp1.x * tmp1.x) + (tmp1.y * tmp1.y) + (tmp1.z * tmp1.z);
		float dot01 = (tmp1.x * tmp2.x) + (tmp1.y * tmp2.y) + (tmp1.z * tmp2.z);
		float dot02 = (tmp1.x * tmp3.x) + (tmp1.y * tmp3.y) + (tmp1.z * tmp3.z);
		float dot11 = (tmp2.x * tmp2.x) + (tmp2.y * tmp2.y) + (tmp2.z * tmp2.z);
		float dot12 = (tmp2.x * tmp3.x) + (tmp2.y * tmp3.y) + (tmp2.z * tmp3.z);

		float denom = dot00 * dot11 - dot01 * dot01;
		if (denom == 0)
			return false;

		float u = (dot11 * dot02 - dot01 * dot12) / denom;
		float v = (dot00 * dot12 - dot01 * dot02) / denom;

		if (u >= 0 && v >= 0 && u + v <= 1)
			return true;
		else
			return false;
	}
}

