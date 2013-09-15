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

	public void handleMovement(SweptSphereEntity entity, Vector3 velocity, Vector3 outVelocity, boolean onlySlideIfTooSteep, float tooSteepAngleY) {
		// don't attempt to process movement if the entity is not moving!
		if (velocity.len2() > 0.0f) {
			// calculate maximum possible collision area (world space)
			calculatePossibleCollisionArea(entity, velocity);

			// convert position and velocity to ellipsoid space
			Vector3 esPosition = new Vector3();
			Vector3 esVelocity = new Vector3();
			entity.collisionPacket.toEllipsoidSpace(entity.position, esPosition);
			entity.collisionPacket.toEllipsoidSpace(velocity, esVelocity);

			// check for and respond to any collisions along this velocity vector
			entity.collisionPacket.nearestDistance = 0.0f;
			entity.collisionPacket.foundCollision = false;
			entity.collisionPacket.esIntersectionPoint.set(Vector3.Zero);
			Vector3 resultingVelocity = new Vector3();
			Vector3 newEsPosition = getNewPositionForMovement(0, entity, esPosition, esVelocity, resultingVelocity, true, onlySlideIfTooSteep, tooSteepAngleY);

			// resulting velocity will have been calculated in ellipsoid space
			entity.collisionPacket.fromEllipsoidSpace(resultingVelocity, resultingVelocity);

			entity.foundCollision = entity.collisionPacket.foundCollision;
			if (entity.collisionPacket.foundCollision)
				entity.collisionPacket.fromEllipsoidSpace(entity.collisionPacket.esIntersectionPoint, entity.nearestCollisionPoint);
			else
				entity.nearestCollisionPoint.set(Vector3.Zero);

			// convert the new position back to normal space and move the entity there
			entity.collisionPacket.fromEllipsoidSpace(newEsPosition, entity.position);

			outVelocity.set(resultingVelocity);
		}
		else
			outVelocity.set(Vector3.Zero);
	}

	private Vector3 getNewPositionForMovement(int recursionDepth,
	                                          SweptSphereEntity entity,
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
		entity.collisionPacket.esVelocity.set(velocity);
		entity.collisionPacket.esNormalizedVelocity.set(velocity.nor());
		entity.collisionPacket.esPosition.set(currentPosition);
		entity.collisionPacket.foundCollision = false;

		// perform the check
		collisionChecker.checkForCollisions(entity, possibleCollisionArea);

		// if there was no collision, simply move along the velocity vector
		if (!entity.collisionPacket.foundCollision)
			return new Vector3(currentPosition).add(velocity);

		// a collision did occur

		Vector3 destination = new Vector3(currentPosition).add(velocity);
		Vector3 newPosition = new Vector3(currentPosition);

		if (entity.collisionPacket.nearestDistance >= collisionVeryCloseDistance) {
			// we haven't yet moved up too close to the nearest collision, so
			// let's inch forward a bit

			// figure out the new position that we need to move up to
			float moveUpLength = entity.collisionPacket.nearestDistance - collisionVeryCloseDistance;

			// HACK: if the above length ends up being 0, "v" calculated below will
			//       end up with "NaN" x/y/z components which will eventually cause
			//       the resulting position from all this being "NaN" and the entity
			//       will seem to disappear entirely. If we catch this zero length
			//       condition and recalculate it so that the length is non-zero but
			//       still very small (below the VERY_CLOSE_DISTANCE threshold) then
			//       it appears to work fine.
			if (moveUpLength == 0.0f)
				moveUpLength = entity.collisionPacket.nearestDistance - (collisionVeryCloseDistance * 0.5f);

			tmp1.set(velocity);
			MathHelpers.setLengthOf(tmp1, moveUpLength);
			newPosition.set(entity.collisionPacket.esPosition).add(tmp1);

			// adjust the polygon intersection point, so the sliding plane will be
			// unaffected by the fact that we move slightly less than the collision
			// tells us
			tmp1.nor();
			tmp1.scl(collisionVeryCloseDistance);
			entity.collisionPacket.esIntersectionPoint.sub(tmp1);
		}

		if (!canSlide) {
			responseVelocity.set(Vector3.Zero);
			return newPosition;
		}

		// we can slide, so determine the sliding plane
		Vector3 slidePlaneOrigin = new Vector3(entity.collisionPacket.esIntersectionPoint);
		Vector3 slidePlaneNormal = new Vector3(newPosition).sub(entity.collisionPacket.esIntersectionPoint).nor();
		Plane slidingPlane = new Plane(slidePlaneOrigin, slidePlaneNormal);

		// determine slide angle and then check if we need to bail out on sliding
		// depending on how steep the slide plane is
		entity.slidingPlaneNormal.set(slidePlaneNormal);
		float slidingYAngle = (float)Math.acos(slidePlaneNormal.dot(Vector3.Y));

		if (onlySlideIfTooSteep && slidingYAngle < (tooSteepAngleY * MathUtils.degreesToRadians)) {
			responseVelocity.set(Vector3.Zero);
			return newPosition;
		}

		tmp1.set(slidePlaneNormal).scl(slidingPlane.distance(destination));
		Vector3 newDestination = new Vector3(destination).sub(tmp1);

		// generate the slide vector, which will become our new velocity vector
		// for the next iteration
		entity.isSliding = true;
		Vector3 newVelocity = new Vector3(newDestination).sub(entity.collisionPacket.esIntersectionPoint);
		responseVelocity.set(newVelocity);

		// don't recurse if the velocity is very small
		if (newVelocity.len() < collisionVeryCloseDistance)
			return newPosition;

		// recurse
		++recursionDepth;
		return getNewPositionForMovement(recursionDepth, entity, newPosition, newVelocity, responseVelocity, canSlide, onlySlideIfTooSteep, tooSteepAngleY);
	}

	private void calculatePossibleCollisionArea(SweptSphereEntity entity, Vector3 velocity) {
		tmp1.set(entity.position).add(velocity);  // the "end" position
		Vector3 radius = entity.collisionPacket.ellipsoidRadius;

		possibleCollisionArea.min.x = Math.min(entity.position.x, tmp1.x) - radius.x;
		possibleCollisionArea.min.y = Math.min(entity.position.y, tmp1.y) - radius.y;
		possibleCollisionArea.min.z = Math.min(entity.position.z, tmp1.z) - radius.z;

		possibleCollisionArea.max.x = Math.max(entity.position.x, tmp1.x) + radius.x;
		possibleCollisionArea.max.y = Math.max(entity.position.y, tmp1.y) + radius.y;
		possibleCollisionArea.max.z = Math.max(entity.position.z, tmp1.z) + radius.z;
	}

}
