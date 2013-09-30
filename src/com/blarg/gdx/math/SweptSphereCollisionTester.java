package com.blarg.gdx.math;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Plane;
import com.badlogic.gdx.math.Vector3;

public final class SweptSphereCollisionTester {
	static final Vector3 tmp1 = new Vector3();
	static final Vector3 p1 = new Vector3();
	static final Vector3 p2 = new Vector3();
	static final Vector3 p3 = new Vector3();
	static final Plane trianglePlane = new Plane(Vector3.Zero, 0.0f);
	static final Vector3 collisionPoint = new Vector3();
	static final Vector3 planeIntersectionPoint = new Vector3();
	static final Vector3 velocity = new Vector3();
	static final Vector3 base = new Vector3();
	static final Vector3 edge = new Vector3();
	static final Vector3 baseToVertex = new Vector3();

	public static boolean test(SweptSphere sphere, Vector3 v1, Vector3 v2, Vector3 v3) {
		boolean foundCollision = false;

		sphere.toEllipsoidSpace(v1, p1);
		sphere.toEllipsoidSpace(v2, p2);
		sphere.toEllipsoidSpace(v3, p3);

		trianglePlane.set(p1, p2, p3);

		// Is the triangle front-facing to the entity's velocity?
		if (trianglePlane.isFrontFacing(sphere.esNormalizedVelocity)) {
			float t0;
			float t1;
			boolean embeddedInPlane = false;
			float distToTrianglePlane = trianglePlane.distance(sphere.esPosition);
			float normalDotVelocity = trianglePlane.normal.dot(sphere.esVelocity);

			// Is the sphere travelling parallel to the plane?
			if (normalDotVelocity == 0.0f) {
				if (Math.abs(distToTrianglePlane) >= 1.0f) {
					// Sphere is not embedded in the plane, no collision possible
					return false;
				} else {
					// Sphere is embedded in the plane, it intersects throughout the whole time period
					embeddedInPlane = true;
					t0 = 0.0f;
					t1 = 1.0f;
				}
			} else {
				// Not travelling parallel to the plane
				t0 = (-1.0f - distToTrianglePlane) / normalDotVelocity;
				t1 = (1.0f - distToTrianglePlane) / normalDotVelocity;

				// Swap so t0 < t1
				if (t0 > t1) {
					float temp = t1;
					t1 = t0;
					t0 = temp;
				}

				// Check that at least one result is within range
				if (t0 > 1.0f || t1 < 0.0f) {
					// Both values outside the range [0,1], no collision possible
					return false;
				}

				t0 = MathUtils.clamp(t0, 0.0f, 1.0f);
				t1 = MathUtils.clamp(t1, 0.0f, 1.0f);
			}

			// At this point, we have two time values (t0, t1) between which the
			// swept sphere intersects with the triangle plane
			collisionPoint.set(Vector3.Zero);
			float t = 1.0f;

			// First, check for a collision inside the triangle. This will happen
			// at time t0 if at all as this is when the sphere rests on the front
			// side of the triangle plane.
			if (!embeddedInPlane) {
				planeIntersectionPoint.set(sphere.esPosition)
				                      .sub(trianglePlane.normal)
				                      .add(tmp1.set(sphere.esVelocity)
				                               .scl(t0));

				if (IntersectionTester.test(planeIntersectionPoint, p1, p2, p3)) {
					foundCollision = true;
					t = t0;
					collisionPoint.set(planeIntersectionPoint);
				}
			}

			// If we haven't found a collision at this point, we need to check the
			// points and edges of the triangle
			if (!foundCollision) {
				velocity.set(sphere.esVelocity);
				base.set(sphere.esPosition);
				float velocitySquaredLength = velocity.len2();
				float a, b, c;
				float newT;

				// For each vertex or edge, we have a quadratic equation to be solved
				// Check against the points first

				a = velocitySquaredLength;

				// P1
				b = 2.0f * velocity.dot(tmp1.set(base).sub(p1));
				c = tmp1.set(p1).sub(base).len2() - 1.0f;
				newT = MathHelpers.getLowestQuadraticRoot(a, b, c, t);
				if (!Float.isNaN(newT)) {
					t = newT;
					foundCollision = true;
					collisionPoint.set(p1);
				}

				// P2
				b = 2.0f * velocity.dot(tmp1.set(base).sub(p2));
				c = tmp1.set(p2).sub(base).len2() - 1.0f;
				newT = MathHelpers.getLowestQuadraticRoot(a, b, c, t);
				if (!Float.isNaN(newT)) {
					t = newT;
					foundCollision = true;
					collisionPoint.set(p2);
				}

				// P3
				b = 2.0f * velocity.dot(tmp1.set(base).sub(p3));
				c = tmp1.set(p3).sub(base).len2() - 1.0f;
				newT = MathHelpers.getLowestQuadraticRoot(a, b, c, t);
				if (!Float.isNaN(newT)) {
					t = newT;
					foundCollision = true;
					collisionPoint.set(p3);
				}

				// Now check against the edges

				// P1 -> P2
				edge.set(p2).sub(p1);
				baseToVertex.set(p1).sub(base);
				float edgeSquaredLength = edge.len2();
				float edgeDotVelocity = edge.dot(velocity);
				float edgeDotBaseToVertex = edge.dot(baseToVertex);

				a = edgeSquaredLength * -velocitySquaredLength + edgeDotVelocity * edgeDotVelocity;
				b = edgeSquaredLength * (2.0f * velocity.dot(baseToVertex)) - 2.0f * edgeDotVelocity * edgeDotBaseToVertex;
				c = edgeSquaredLength * (1.0f - baseToVertex.len2()) + edgeDotBaseToVertex * edgeDotBaseToVertex;

				newT = MathHelpers.getLowestQuadraticRoot(a, b, c, t);
				if (!Float.isNaN(newT)) {
					// Check if intersection is within line segment
					float f = (edgeDotVelocity * newT - edgeDotBaseToVertex) / edgeSquaredLength;
					if (f >= 0.0f && f <= 1.0f) {
						// Intersection took place within the segment
						t = newT;
						foundCollision = true;
						collisionPoint.set(p1).add(tmp1.set(edge).scl(f));
					}
				}

				// P2 -> P3
				edge.set(p3).sub(p2);
				baseToVertex.set(p2).sub(base);
				edgeSquaredLength = edge.len2();
				edgeDotVelocity = edge.dot(velocity);
				edgeDotBaseToVertex = edge.dot(baseToVertex);

				a = edgeSquaredLength * -velocitySquaredLength + edgeDotVelocity * edgeDotVelocity;
				b = edgeSquaredLength * (2.0f * velocity.dot(baseToVertex)) - 2.0f * edgeDotVelocity * edgeDotBaseToVertex;
				c = edgeSquaredLength * (1.0f - baseToVertex.len2()) + edgeDotBaseToVertex * edgeDotBaseToVertex;

				newT = MathHelpers.getLowestQuadraticRoot(a, b, c, t);
				if (!Float.isNaN(newT)) {
					// Check if intersection is within line segment
					float f = (edgeDotVelocity * newT - edgeDotBaseToVertex) / edgeSquaredLength;
					if (f >= 0.0f && f <= 1.0f) {
						// Intersection took place within the segment
						t = newT;
						foundCollision = true;
						collisionPoint.set(p2).add(tmp1.set(edge).scl(f));
					}
				}

				// P3 -> P1
				edge.set(p1).sub(p3);
				baseToVertex.set(p3).sub(base);
				edgeSquaredLength = edge.len2();
				edgeDotVelocity = edge.dot(velocity);
				edgeDotBaseToVertex = edge.dot(baseToVertex);

				a = edgeSquaredLength * -velocitySquaredLength + edgeDotVelocity * edgeDotVelocity;
				b = edgeSquaredLength * (2.0f * velocity.dot(baseToVertex)) - 2.0f * edgeDotVelocity * edgeDotBaseToVertex;
				c = edgeSquaredLength * (1.0f - baseToVertex.len2()) + edgeDotBaseToVertex * edgeDotBaseToVertex;

				newT = MathHelpers.getLowestQuadraticRoot(a, b, c, t);
				if (!Float.isNaN(newT)) {
					// Check if intersection is within line segment
					float f = (edgeDotVelocity * newT - edgeDotBaseToVertex) / edgeSquaredLength;
					if (f >= 0.0f && f <= 1.0f) {
						// Intersection took place within the segment
						t = newT;
						foundCollision = true;
						collisionPoint.set(p3).add(tmp1.set(edge).scl(f));
					}
				}
			}

			// Set result of test
			if (foundCollision) {
				float distanceToCollision = t * sphere.esVelocity.len();

				// Does this triangle qualify for the closest collision?
				if (!sphere.foundCollision || distanceToCollision < sphere.nearestCollisionDistance) {
					sphere.nearestCollisionDistance = distanceToCollision;
					sphere.esIntersectionPoint.set(collisionPoint);
					sphere.foundCollision = true;
				}
			}
		}

		return foundCollision;
	}
}
