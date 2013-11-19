package ca.blarg.gdx.math;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Plane;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;

public class SweptSphereHandler {
	final int maxRecursionDepth;
	final SweptSphereWorldCollisionChecker collisionChecker;

	public float collisionVeryCloseDistance = 0.005f;
	public float onGroundTolerance = 0.1f;

	public final Vector3 possibleCollisionAreaMinOffset = new Vector3();
	public final Vector3 possibleCollisionAreaMaxOffset = new Vector3();

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

	static final Vector3 postResponseVelocity = new Vector3();
	static final Vector3 postGravityResponseVelocity = new Vector3();
	public void handleMovement(SweptSphere sphere,
	                           Vector3 sphereVelocity,
	                           Vector3 gravityVelocity,
	                           Vector3 outVelocity,
	                           boolean canSlide,
	                           boolean onlySlideIfTooSteep,
	                           float tooSteepAngleY) {
		updateIsInMotionState(sphere, sphereVelocity);
		updateIsFallingState(sphere);

		calculatePossibleCollisionArea(sphere, sphereVelocity);

		sphere.wasSliding = sphere.isSliding;
		sphere.isSliding = false;
		postResponseVelocity.set(Vector3.Zero);
		handleMovement(sphere, sphereVelocity, postResponseVelocity, canSlide, false, 0.0f);
		if (gravityVelocity != null) {
			postGravityResponseVelocity.set(Vector3.Zero);
			handleMovement(sphere, gravityVelocity, postGravityResponseVelocity, canSlide, onlySlideIfTooSteep, tooSteepAngleY);
			postResponseVelocity.add(postGravityResponseVelocity);
		}

		outVelocity.set(postResponseVelocity);

		updateIsOnGroundState(sphere, outVelocity);
	}

	static final Vector3 esPosition = new Vector3();
	static final Vector3 esVelocity = new Vector3();
	static final Vector3 resultingVelocity = new Vector3();
	public void handleMovement(SweptSphere sphere,
	                           Vector3 velocity,
	                           Vector3 outVelocity,
	                           boolean canSlide,
	                           boolean onlySlideIfTooSteep,
	                           float tooSteepAngleY) {
		// don't attempt to process movement if the entity is not moving!
		if (velocity.len2() > 0.0f) {
			// calculate maximum possible collision area (world space)
			//calculatePossibleCollisionArea(sphere, velocity);

			// convert position and velocity to ellipsoid space
			sphere.toEllipsoidSpace(sphere.position, esPosition);
			sphere.toEllipsoidSpace(velocity, esVelocity);

			// check for and respond to any collisions along this velocity vector
			sphere.nearestCollisionDistance = 0.0f;
			sphere.nearestCollisionPoint.set(Vector3.Zero);
			sphere.foundCollision = false;
			sphere.esIntersectionPoint.set(Vector3.Zero);
			resultingVelocity.set(Vector3.Zero);
			Vector3 newEsPosition = getNewPositionForMovement(0, sphere, esPosition, esVelocity, resultingVelocity, canSlide, onlySlideIfTooSteep, tooSteepAngleY);

			// a bunch of things need to be converted back from ellipsoid space ...
			sphere.fromEllipsoidSpace(resultingVelocity);
			sphere.fromEllipsoidSpace(sphere.slidingPlaneOrigin);
			sphere.fromEllipsoidSpace(newEsPosition, sphere.position);
			if (sphere.foundCollision)
				sphere.fromEllipsoidSpace(sphere.esIntersectionPoint, sphere.nearestCollisionPoint);

			outVelocity.set(resultingVelocity);
		}
		else
			outVelocity.set(Vector3.Zero);
	}

	public void handleBasicMovementCollision(SweptSphere sphere,
	                                         Vector3 velocity,
	                                         Vector3 outVelocity) {
		// don't attempt to process movement if the entity is not moving!
		if (velocity.len2() > 0.0f) {
			// calculate maximum possible collision area (world space)
			calculatePossibleCollisionArea(sphere, velocity);

			// convert position and velocity to ellipsoid space
			sphere.toEllipsoidSpace(sphere.position, sphere.esPosition);
			sphere.toEllipsoidSpace(velocity, sphere.esVelocity);

			// check for and respond to collisions along this velocity vector
			sphere.nearestCollisionDistance = 0.0f;
			sphere.nearestCollisionPoint.set(Vector3.Zero);
			sphere.foundCollision = false;
			sphere.esIntersectionPoint.set(Vector3.Zero);
			sphere.esNormalizedVelocity.set(sphere.esVelocity).nor();

			// perform simple collision check
			collisionChecker.checkForCollisions(sphere, possibleCollisionArea);

			// if there was no collision, simply move along the velocity vector. if there was a collision
			// then we just simple stop and don't even attempt to move at all
			if (!sphere.foundCollision) {
				sphere.position.set(sphere.esPosition).add(sphere.esVelocity);
				outVelocity.set(velocity);   // keep the original input velocity as well
			} else {
				sphere.position.set(sphere.esPosition);
				outVelocity.set(Vector3.Zero);
			}

			// convert the intersection point (if there was one) and the new position back from ellipsoid space
			sphere.fromEllipsoidSpace(sphere.position);
			if (sphere.foundCollision)
				sphere.fromEllipsoidSpace(sphere.esIntersectionPoint, sphere.nearestCollisionPoint);
		}
	}

	static final Vector3 resultingPosition = new Vector3();
	static final Vector3 tmpDestination = new Vector3();
	static final Vector3 tmpNewPosition = new Vector3();
	static final Vector3 tmpNewDestination = new Vector3();
	static final Vector3 tmpNewVelocity = new Vector3();
	static final Plane slidingPlane = new Plane(Vector3.Zero, 0.0f);
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
			return resultingPosition.set(currentPosition);

		responseVelocity.set(velocity);

		// set up the collision check information
		sphere.esVelocity.set(velocity);
		sphere.esNormalizedVelocity.set(velocity).nor();
		sphere.esPosition.set(currentPosition);
		sphere.foundCollision = false;

		// perform the check
		collisionChecker.checkForCollisions(sphere, possibleCollisionArea);

		// if there was no collision, simply move along the velocity vector
		if (!sphere.foundCollision)
			return resultingPosition.set(currentPosition)
			                        .add(velocity);

		// a collision did occur

		tmpDestination.set(currentPosition)
		              .add(velocity);
		tmpNewPosition.set(currentPosition);

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

			MathHelpers.setLengthOf(tmp1.set(velocity), moveUpLength);
			tmpNewPosition.set(sphere.esPosition)
			              .add(tmp1);

			// adjust the polygon intersection point, so the sliding plane will be
			// unaffected by the fact that we move slightly less than the collision
			// tells us
			tmp1.nor()
			    .scl(collisionVeryCloseDistance);
			sphere.esIntersectionPoint.sub(tmp1);
		}

		if (!canSlide) {
			responseVelocity.set(Vector3.Zero);
			return resultingPosition.set(tmpNewPosition);
		}

		// we can slide, so determine the sliding plane
		sphere.slidingPlaneOrigin.set(sphere.esIntersectionPoint);
		sphere.slidingPlaneNormal.set(tmpNewPosition).sub(sphere.esIntersectionPoint).nor();
		slidingPlane.set(sphere.slidingPlaneOrigin, sphere.slidingPlaneNormal);

		// determine slide angle and then check if we need to bail out on sliding
		// depending on how steep the slide plane is
		float slidingYAngle = (float)Math.acos(sphere.slidingPlaneNormal.dot(Vector3.Y)) * MathUtils.degreesToRadians;

		if (onlySlideIfTooSteep && slidingYAngle < tooSteepAngleY) {
			responseVelocity.set(Vector3.Zero);
			return resultingPosition.set(tmpNewPosition);
		}

		tmpNewDestination.set(tmpDestination)
		                 .sub(tmp1.set(sphere.slidingPlaneNormal)
		                          .scl(slidingPlane.distance(tmpDestination)));

		// generate the slide vector, which will become our new velocity vector
		// for the next iteration
		sphere.isSliding = true;
		tmpNewVelocity.set(tmpNewDestination)
		              .sub(sphere.esIntersectionPoint);
		responseVelocity.set(tmpNewVelocity);

		// don't recurse if the velocity is very small
		if (tmpNewVelocity.len() < collisionVeryCloseDistance)
			return resultingPosition.set(tmpNewPosition);

		// recurse
		++recursionDepth;
		return getNewPositionForMovement(recursionDepth, sphere, tmpNewPosition, tmpNewVelocity, responseVelocity, canSlide, onlySlideIfTooSteep, tooSteepAngleY);
	}

	private void calculatePossibleCollisionArea(SweptSphere sphere, Vector3 velocity) {
		tmp1.set(sphere.position).add(velocity);  // the "end" position
		Vector3 radius = sphere.radius;

		possibleCollisionArea.min.x = Math.min(sphere.position.x, tmp1.x) - radius.x;
		possibleCollisionArea.min.y = Math.min(sphere.position.y, tmp1.y) - radius.y;
		possibleCollisionArea.min.z = Math.min(sphere.position.z, tmp1.z) - radius.z;
		possibleCollisionArea.min.add(possibleCollisionAreaMinOffset);

		possibleCollisionArea.max.x = Math.max(sphere.position.x, tmp1.x) + radius.x;
		possibleCollisionArea.max.y = Math.max(sphere.position.y, tmp1.y) + radius.y;
		possibleCollisionArea.max.z = Math.max(sphere.position.z, tmp1.z) + radius.z;
		possibleCollisionArea.max.add(possibleCollisionAreaMaxOffset);
	}

	private void updateIsInMotionState(SweptSphere sphere, Vector3 velocity) {
		sphere.wasInMotion = sphere.isInMotion;

		if (MathHelpers.areAlmostEqual(velocity.len(), 0.0f))
			sphere.isInMotion = false;
		else
			sphere.isInMotion = true;
	}

	private void updateIsFallingState(SweptSphere sphere) {
		float currentYPosition = sphere.position.y;

		sphere.wasFalling = sphere.isFalling;

		if (sphere.isOnGround) {
			// not falling anymore, total fall distance will be recorded for the
			// rest of this tick, but will be cleared next tick when this runs again
			sphere.isFalling = false;
			sphere.fallDistance = sphere.currentFallDistance;
			sphere.currentFallDistance = 0.0f;
		} else if (currentYPosition < sphere.lastPositionY) {
			// falling (current Y coord is lower then the one from last tick)
			sphere.isFalling = true;
			sphere.currentFallDistance += (sphere.lastPositionY - currentYPosition);
		}

		sphere.lastPositionY = currentYPosition;
	}

	private void updateIsOnGroundState(SweptSphere sphere, Vector3 velocity) {
		sphere.wasOnGround = sphere.isOnGround;
		if (sphere.foundCollision && MathHelpers.areAlmostEqual(velocity.y, 0.0f, onGroundTolerance))
			sphere.isOnGround = true;
		else
			sphere.isOnGround = false;
	}
}
