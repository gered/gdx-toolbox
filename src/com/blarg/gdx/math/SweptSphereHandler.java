package com.blarg.gdx.math;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Plane;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;

public class SweptSphereHandler {
	final int maxRecursionDepth;
	final SweptSphereWorldCollisionChecker collisionChecker;

	public float collisionVeryCloseDistance = 0.005f;
	public float onGroundTolerance = 0.1f;

	static final Vector3 tmp1 = new Vector3();
	static final BoundingBox possibleCollisionArea = new BoundingBox();

	public SweptSphereHandler(SweptSphereWorldCollisionChecker collisionChecker, int maxRecursionDepth) {
		if (collisionChecker == null)
			throw new IllegalArgumentException("collisionChecker can not be null.");
		if (maxRecursionDepth <= 0)
			throw new IllegalArgumentException("maxRecursionDepth must be > 0.");

		this.collisionChecker = collisionChecker;
		this.maxRecursionDepth = maxRecursionDepth;
	}

	public void handleMovement(SweptSphere sphere, Vector3 velocity, Vector3 outVelocity, boolean onlySlideIfTooSteep, float tooSteepAngleY) {
		// don't attempt to process movement if the entity is not moving!
		if (velocity.len2() > 0.0f) {
			// calculate maximum possible collision area (world space)
			calculatePossibleCollisionArea(sphere, velocity);

			// convert position and velocity to ellipsoid space
			Vector3 esPosition = new Vector3();
			Vector3 esVelocity = new Vector3();
			SweptSphere.toEllipsoidSpace(sphere.position, sphere.radius, esPosition);
			SweptSphere.toEllipsoidSpace(velocity, sphere.radius, esVelocity);

			// check for and respond to any collisions along this velocity vector
			sphere.nearestCollisionDistance = 0.0f;
			sphere.foundCollision = false;
			sphere.esIntersectionPoint.set(Vector3.Zero);
			Vector3 resultingVelocity = new Vector3();
			Vector3 newEsPosition = getNewPositionForMovement(0, sphere, esPosition, esVelocity, resultingVelocity, true, onlySlideIfTooSteep, tooSteepAngleY);

			// resulting velocity will have been calculated in ellipsoid space
			SweptSphere.fromEllipsoidSpace(resultingVelocity, sphere.radius);

			if (sphere.foundCollision)
				SweptSphere.fromEllipsoidSpace(sphere.esIntersectionPoint, sphere.radius, sphere.nearestCollisionPoint);
			else
				sphere.nearestCollisionPoint.set(Vector3.Zero);

			// sliding plane origin will be in ellipsoid space still...
			SweptSphere.fromEllipsoidSpace(sphere.slidingPlaneOrigin, sphere.radius);

			// convert the new position back to normal space and move the entity there
			SweptSphere.fromEllipsoidSpace(newEsPosition, sphere.radius, sphere.position);

			outVelocity.set(resultingVelocity);
		}
		else
			outVelocity.set(Vector3.Zero);
	}

	private Vector3 getNewPositionForMovement(int recursionDepth,
	                                          SweptSphere sphere,
	                                          Vector3 currentPosition,
	                                          Vector3 velocity,
	                                          Vector3 responseVelocity,
	                                          boolean canSlide,
	                                          boolean onlySlideIfTooSteep,
	                                          float tooSteepAngleY) {
		// don't recurse too much
		if (recursionDepth > maxRecursionDepth)
			return currentPosition;

		responseVelocity.set(velocity);

		// set up the collision check information
		sphere.esVelocity.set(velocity);
		sphere.esNormalizedVelocity.set(velocity.nor());
		sphere.esPosition.set(currentPosition);
		sphere.foundCollision = false;

		// perform the check
		collisionChecker.checkForCollisions(sphere, possibleCollisionArea);

		// if there was no collision, simply move along the velocity vector
		if (!sphere.foundCollision)
			return new Vector3(currentPosition).add(velocity);

		// a collision did occur

		Vector3 destination = new Vector3(currentPosition).add(velocity);
		Vector3 newPosition = new Vector3(currentPosition);

		if (sphere.nearestCollisionDistance >= collisionVeryCloseDistance) {
			// we haven't yet moved up too close to the nearest collision, so
			// let's inch forward a bit

			// figure out the new position that we need to move up to
			float moveUpLength = sphere.nearestCollisionDistance - collisionVeryCloseDistance;

			// HACK: if the above length ends up being 0, "v" calculated below will
			//       end up with "NaN" x/y/z components which will eventually cause
			//       the resulting position from all this being "NaN" and the entity
			//       will seem to disappear entirely. If we catch this zero length
			//       condition and recalculate it so that the length is non-zero but
			//       still very small (below the VERY_CLOSE_DISTANCE threshold) then
			//       it appears to work fine.
			if (moveUpLength == 0.0f)
				moveUpLength = sphere.nearestCollisionDistance - (collisionVeryCloseDistance * 0.5f);

			tmp1.set(velocity);
			MathHelpers.setLengthOf(tmp1, moveUpLength);
			newPosition.set(sphere.esPosition).add(tmp1);

			// adjust the polygon intersection point, so the sliding plane will be
			// unaffected by the fact that we move slightly less than the collision
			// tells us
			tmp1.nor();
			tmp1.scl(collisionVeryCloseDistance);
			sphere.esIntersectionPoint.sub(tmp1);
		}

		if (!canSlide) {
			responseVelocity.set(Vector3.Zero);
			return newPosition;
		}

		// we can slide, so determine the sliding plane
		sphere.slidingPlaneOrigin.set(sphere.esIntersectionPoint);
		sphere.slidingPlaneNormal.set(newPosition).sub(sphere.esIntersectionPoint).nor();
		Plane slidingPlane = new Plane(sphere.slidingPlaneNormal, sphere.slidingPlaneOrigin);

		// determine slide angle and then check if we need to bail out on sliding
		// depending on how steep the slide plane is
		float slidingYAngle = (float)Math.acos(sphere.slidingPlaneNormal.dot(Vector3.Y));

		if (onlySlideIfTooSteep && slidingYAngle < (tooSteepAngleY * MathUtils.degreesToRadians)) {
			responseVelocity.set(Vector3.Zero);
			return newPosition;
		}

		tmp1.set(sphere.slidingPlaneNormal).scl(slidingPlane.distance(destination));
		Vector3 newDestination = new Vector3(destination).sub(tmp1);

		// generate the slide vector, which will become our new velocity vector
		// for the next iteration
		sphere.isSliding = true;
		Vector3 newVelocity = new Vector3(newDestination).sub(sphere.esIntersectionPoint);
		responseVelocity.set(newVelocity);

		// don't recurse if the velocity is very small
		if (newVelocity.len() < collisionVeryCloseDistance)
			return newPosition;

		// recurse
		++recursionDepth;
		return getNewPositionForMovement(recursionDepth, sphere, newPosition, newVelocity, responseVelocity, canSlide, onlySlideIfTooSteep, tooSteepAngleY);
	}

	private void calculatePossibleCollisionArea(SweptSphere sphere, Vector3 velocity) {
		tmp1.set(sphere.position).add(velocity);  // the "end" position
		Vector3 radius = sphere.radius;

		possibleCollisionArea.min.x = Math.min(sphere.position.x, tmp1.x) - radius.x;
		possibleCollisionArea.min.y = Math.min(sphere.position.y, tmp1.y) - radius.y;
		possibleCollisionArea.min.z = Math.min(sphere.position.z, tmp1.z) - radius.z;

		possibleCollisionArea.max.x = Math.max(sphere.position.x, tmp1.x) + radius.x;
		possibleCollisionArea.max.y = Math.max(sphere.position.y, tmp1.y) + radius.y;
		possibleCollisionArea.max.z = Math.max(sphere.position.z, tmp1.z) + radius.z;
	}

}
