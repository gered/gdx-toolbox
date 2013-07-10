package com.blarg.gdx.graphics;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.blarg.gdx.math.MathHelpers;

/**
 * Simple 3D perspective camera class using euler angles to control orientation. Yaw (Y-axis) and pitch (X-axis) are
 * the only angles used. Roll is not used. Yaw controls left/right directions. Pitch controls vertical directions. This
 * class was mainly created because I find it far easier/intuitive to control a camera via euler angles and haven't
 * really ever had a problem with any gimbal lock situations. This type of camera may not be best for all kinds of
 * situations though (e.g. where some heavy-duty / crazy rotations are taking place). For most kinds of first person /
 * "up-right" type of cameras this is probably quite suitable.
 *
 * Changes to {@link Camera#direction} are ignored completely. However, this field will be updated appropriately
 * whenever the other rotation-related properties are recalculated (e.g. {@link #forward} and {@link #rotation}).
 * {@link Camera} methods which modify {@link Camera#direction} will all throw {@link UnsupportedOperationException}.
 * Camera direction is solely controlled by the yaw and pitch attributes added by this class.
 *
 * Modification of position and orientation should all be performed via the provided methods: turn, pitch, move, etc.
 * These methods handle updating the internal Quaternion rotations and forward/target vectors. Modifying
 * {@link Camera#position} directly may result in odd behaviour.
 *
 * As per usual with Camera classes in libgdx, make sure to call {@link #update()} after you've manipulated any
 * attributes of the camera (position, orientation, viewport size, etc).
 */
public class EulerPerspectiveCamera extends Camera {
	static final Vector3 tmp1 = new Vector3();

	public float fieldOfView;

	float yaw;
	float pitch;
	final Vector3 forward = new Vector3();
	final Vector3 tmpForward = new Vector3();
	final Vector3 target = new Vector3();
	final Quaternion rotation = new Quaternion();
	final Quaternion tmpRotation = new Quaternion();
	final Quaternion rotationX = new Quaternion();
	final Quaternion rotationY = new Quaternion();

	public EulerPerspectiveCamera(float fieldOfView, float viewportWidth, float viewportHeight) {
		this.fieldOfView = fieldOfView;
		this.viewportWidth = viewportWidth;
		this.viewportHeight = viewportHeight;

		yaw = 0.0f;
		pitch = 0.0f;
		updateRotation();
		updateTarget();

		update();
	}

	public float getYaw() {
		return yaw;
	}

	public float getPitch() {
		return pitch;
	}

	public Vector3 getForward() {
		tmpForward.set(forward);
		return forward;
	}

	public Quaternion getRotation() {
		tmpRotation.set(rotation);
		return tmpRotation;
	}

	public void turn(float degrees) {
		yaw += degrees;
		yaw = MathHelpers.rolloverClamp(yaw, 0.0f, 360.0f);
		updateRotation();
		updateTarget();
	}

	public void turnTo(float degrees) {
		yaw = MathHelpers.rolloverClamp(degrees, 0.0f, 360.0f);
		updateRotation();
		updateTarget();
	}

	public void pitch(float degrees) {
		pitch += degrees;
		pitch = MathHelpers.rolloverClamp(pitch, 0.0f, 360.0f);
		updateRotation();
		updateTarget();
	}

	public void pitchTo(float degrees) {
		pitch = MathHelpers.rolloverClamp(degrees, 0.0f, 360.0f);
		updateRotation();
		updateTarget();
	}

	public void orient(float yawDegrees, float pitchDegrees) {
		yaw += yawDegrees;
		yaw = MathHelpers.rolloverClamp(yaw, 0.0f, 360.0f);
		pitch += pitchDegrees;
		pitch = MathHelpers.rolloverClamp(pitch, 0.0f, 360.0f);
		updateRotation();
		updateTarget();
	}

	public void orientTo(float yawDegrees, float pitchDegrees) {
		yaw = MathHelpers.rolloverClamp(yawDegrees, 0.0f, 360.0f);
		pitch = MathHelpers.rolloverClamp(pitchDegrees, 0.0f, 360.0f);
		updateRotation();
		updateTarget();
	}

	private void updateRotation() {
		// these angles must be negated and the multiplication order here is important!
		rotationX.setFromAxis(Vector3.X, -pitch);
		rotationY.setFromAxis(Vector3.Y, -yaw);
		rotation.set(rotationY).mul(rotationX);

		forward.set(MathHelpers.FORWARD_VECTOR3).mul(rotation);
		direction.set(forward);
		up.set(MathHelpers.UP_VECTOR3).mul(rotation);
	}

	private void updateTarget() {
		target.set(forward).add(position);
	}

	public void move(Vector3 offset) {
		tmp1.set(offset).mul(rotation);
		position.add(tmp1);
		updateTarget();
	}

	public void moveTo(Vector3 position) {
		this.position.set(position);
		updateTarget();
	}

	public void moveTo(float x, float y, float z) {
		this.position.set(x, y, z);
		updateTarget();
	}

	@Override
	public void update() {
		update(true);
	}

	@Override
	public void update(boolean updateFrustum) {
		updateRotation();
		updateTarget();

		float aspect = viewportWidth / viewportHeight;
		projection.setToProjection(Math.abs(near), Math.abs(far), fieldOfView, aspect);
		view.setToLookAt(position, target, up);
		combined.set(projection);
		Matrix4.mul(combined.val, view.val);

		if (updateFrustum) {
			invProjectionView.set(combined);
			Matrix4.inv(invProjectionView.val);
			frustum.update(invProjectionView);
		}
	}

	@Override
	public void translate(Vector3 vec) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void translate(float x, float y, float z) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void transform(Matrix4 transform) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void rotateAround(Vector3 point, Vector3 axis, float angle) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void rotate(Quaternion quat) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void rotate(Matrix4 transform) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void rotate(Vector3 axis, float angle) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void rotate(float angle, float axisX, float axisY, float axisZ) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void normalizeUp() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void lookAt(Vector3 target) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void lookAt(float x, float y, float z) {
		throw new UnsupportedOperationException();
	}
}
