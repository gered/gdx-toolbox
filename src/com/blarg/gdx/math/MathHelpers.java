package com.blarg.gdx.math;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public final class MathHelpers {
	static final Vector2 v2tmpA = new Vector2();

	public static final float EPSILON             = 0.0000000001f;

	public static final float UP_2D               = 90.0f;
	public static final float DOWN_2D             = 270.0f;
	public static final float LEFT_2D             = 180.0f;
	public static final float RIGHT_2D            = 0.0f;

	public static final Vector2 UP_VECTOR2        = getDirectionVector2(UP_2D);    // new Vector2(0.0f,  1.0f);
	public static final Vector2 DOWN_VECTOR2      = getDirectionVector2(DOWN_2D);  // new Vector2(0.0f,  -1.0f);
	public static final Vector2 LEFT_VECTOR2      = getDirectionVector2(LEFT_2D);  // new Vector2(-1.0f, 0.0f);
	public static final Vector2 RIGHT_VECTOR2     = getDirectionVector2(RIGHT_2D); // new Vector2(1.0f,  0.0f)

	public static final Vector3 UP_VECTOR3        = new Vector3(0.0f, 1.0f, 0.0f);
	public static final Vector3 DOWN_VECTOR3      = new Vector3(0.0f, -1.0f, 0.0f);
	public static final Vector3 FORWARD_VECTOR3   = new Vector3(0.0f, 0.0f, -1.0f);
	public static final Vector3 BACKWARD_VECTOR3  = new Vector3(0.0f, 0.0f, 1.0f);
	public static final Vector3 LEFT_VECTOR3      = new Vector3(-1.0f, 0.0f, 0.0f);
	public static final Vector3 RIGHT_VECTOR3     = new Vector3(1.0f, 0.0f, 0.0f);

	public static void getDirectionVector2(float degrees, Vector2 result) {
		result.set(1.0f, 0.0f);
		result.setAngle(degrees);
	}

	public static void getDirectionVector3FromYAxis(float yAxisDegrees, Vector3 result) {
		result.y = 0.0f;
		float adjustedAngle = rolloverClamp(yAxisDegrees - 90.0f, 0.0f, 360.0f);
		getPointOnCircle(1.0f, adjustedAngle, v2tmpA);
		result.x = v2tmpA.x;
		result.z = v2tmpA.y;
	}

	public static void getDirectionVector3FromAngles(float yawDegrees, float pitchDegrees, Vector3 result) {
		result.x = MathUtils.cosDeg(yawDegrees) * MathUtils.cosDeg(pitchDegrees);
		result.y = MathUtils.sinDeg(yawDegrees) * MathUtils.cosDeg(pitchDegrees);
		result.z = MathUtils.sinDeg(pitchDegrees);
	}

	public static float getAngleBetween2D(final Vector2 a, final Vector2 b) {
		v2tmpA.set(a);
		v2tmpA.sub(b);
		return v2tmpA.angle();
	}

	public static void getPointOnCircle(float radius, float degrees, Vector2 result) {
		result.x = radius * MathUtils.cosDeg(degrees);
		result.y = radius * MathUtils.sinDeg(degrees);
	}

	public static void getCartesianCoordsFromSpherical(float radius, float inclination, float azimuth, Vector3 result) {
		result.x = radius * MathUtils.sinDeg(inclination) * MathUtils.sinDeg(azimuth);
		result.y = radius * MathUtils.cosDeg(inclination);
		result.z = radius * MathUtils.sinDeg(inclination) * MathUtils.cosDeg(azimuth);
	}

	public static boolean areAlmostEqual(float a, float b) {
		return areAlmostEqual(a, b, EPSILON);
	}

	public static boolean areAlmostEqual(float a, float b, float epsilon)
	{
		float diff = Math.abs(a - b);
		a = Math.abs(a);
		b = Math.abs(b);

		float largest = (b > a) ? b : a;

		return (diff <= largest * epsilon);
	}

	public static float rolloverClamp(float value, float min, float max) {
		float temp = value;
		float range = Math.abs(max - min);
		do {
			if (temp < min)
				temp += range;
			if (temp > max)
				temp -= range;
		} while (temp < min || temp > max);

		return temp;
	}

	public static int rolloverClamp(int value, int min, int max) {
		int temp = value;
		int range = Math.abs(max - min);
		do {
			if (temp < min)
				temp += range;
			if (temp > max)
				temp -= range;
		} while (temp < min || temp > max);

		return temp;
	}

	public static float lerp(float a, float b, float t) {
		return a + (b - a) * t;
	}

	public static int lerp(int a, int b, int t) {
		return a + (b - a) * t;
	}

	public static float inverseLerp(float a, float b, float lerpValue) {
		return (lerpValue - a) / (b - a);
	}

	public static int inverseLerp(int a, int b, int lerpValue) {
		return (lerpValue - a) / (b - a);
	}

	public static float scaleRange(float value, float originalMin, float originalMax, float newMin, float newMax) {
		return (value / ((originalMax - originalMin) / (newMax - newMin))) + newMin;
	}

	public static int scaleRange(int value, int originalMin, int originalMax, int newMin, int newMax) {
		return (value / ((originalMax - originalMin) / (newMax - newMin))) + newMin;
	}

	public static float smoothStep(float low, float high, float t) {
		float n = MathUtils.clamp(t, 0.0f, 1.0f);
		return lerp(low, high, (n * n) * (3.0f - (2.0f * n)));
	}

	// convenience overloads that should not really be used except in non-performance-critical situations

	public static Vector2 getDirectionVector2(float degrees) {
		Vector2 result = new Vector2();
		getDirectionVector2(degrees, result);
		return result;
	}

	public static Vector3 getDirectionVector3FromYAxis(float yAxisDegrees) {
		Vector3 result = new Vector3();
		getDirectionVector3FromYAxis(yAxisDegrees, result);
		return result;
	}

	public static Vector3 getDirectionVector3FromAngles(float yawDegrees, float pitchDegrees) {
		Vector3 result = new Vector3();
		getDirectionVector3FromAngles(yawDegrees, pitchDegrees, result);
		return result;
	}

	public static Vector2 getPointOnCircle(float radius, float degrees) {
		Vector2 result = new Vector2();
		getPointOnCircle(radius, degrees, result);
		return result;
	}
}
