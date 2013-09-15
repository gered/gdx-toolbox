package com.blarg.gdx.math;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;

public final class MathHelpers {
	static final Vector2 v2tmpA = new Vector2();

	public static final float FLOAT_EPSILON       = 1.401298E-45f;   // smallest floating point value greater then zero
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
		float yaw = MathUtils.degreesToRadians * yawDegrees;
		float pitch = MathUtils.degreesToRadians * pitchDegrees;

		// TODO: this appears to be consistent with OpenGL's coordinate system, but needs more testing to be 100% sure!
		result.x = (float)Math.sin(yaw);
		result.y = -((float)Math.sin(pitch) * (float)Math.cos(yaw));
		result.z = -((float)Math.cos(pitch) * (float)Math.cos(yaw));
	}

	public static float getAngleBetween2D(final Vector2 a, final Vector2 b) {
		v2tmpA.set(a);
		v2tmpA.sub(b);
		return v2tmpA.angle();
	}

	public static void getPointOnCircle(float radius, float degrees, Vector2 result) {
		float radians = MathUtils.degreesToRadians * degrees;
		result.x = radius * (float)Math.cos(radians);
		result.y = radius * (float)Math.sin(radians);
	}

	public static float getAngleFromPointOnCircle(float x, float y) {
		return MathUtils.radiansToDegrees * (float)Math.atan2(y, x);
	}

	public static void getCartesianCoordsFromSpherical(float radius, float inclination, float azimuth, Vector3 result) {
		float inclinationRadians = MathUtils.degreesToRadians * inclination;
		float azimuthRadians = MathUtils.degreesToRadians * azimuth;

		result.x = radius * (float)Math.sin(inclinationRadians) * (float)Math.sin(azimuthRadians);
		result.y = radius * (float)Math.cos(inclinationRadians);
		result.z = radius * (float)Math.sin(inclinationRadians) * (float)Math.cos(azimuthRadians);
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

	public static float fastInverseSqrt(float x) {
		float xhalf = 0.5f * x;
		int i = Float.floatToIntBits(x);
		i = 0x5f3759df - (i >> 1);
		x = Float.intBitsToFloat(i);
		x = x * (1.5f - xhalf * x * x);
		return x;
	}

	public static double fastInverseSqrt(double x) {
		double xhalf = 0.5d * x;
		long i = Double.doubleToLongBits(x);
		i = 0x5fe6ec85e7de30daL - (i >> 1);
		x = Double.longBitsToDouble(i);
		x = x * (1.5d - xhalf * x * x);
		return x;
	}

	public static int pow(int number, int power) {
		int result = 1;
		for (int i = 0; i < power; ++i)
			result *= number;
		return result;
	}

	public static long pow(long number, int power) {
		long result = 1;
		for (int i = 0; i < power; ++i)
			result *= number;
		return result;
	}

	public static int sign(int value) {
		if (value < 0)
			return -1;
		else if (value > 0)
			return 1;
		else
			return 0;
	}

	public static float sign(float value) {
		if (value < 0.0f)
			return -1.0f;
		else if (value > 0.0f)
			return 1.0f;
		else
			return 0.0f;
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

	public static void getScaleFactor(BoundingBox originalSize, BoundingBox desiredSize, Vector3 result) {
		getScaleFactor(originalSize.getDimensions(), desiredSize.getDimensions(), result);
	}

	public static void getScaleFactor(Vector3 originalSize, Vector3 desiredSize, Vector3 result) {
		result.x = desiredSize.x / originalSize.x;
		result.y = desiredSize.y / originalSize.y;
		result.z = desiredSize.z / originalSize.z;
	}

	public static void setLengthOf(Vector3 v, float length) {
		float scaleFactor = length / v.len();
		v.x = v.x * scaleFactor;
		v.y = v.y * scaleFactor;
		v.z = v.z * scaleFactor;
	}

	/**
	 * Basically the same as {@link com.badlogic.gdx.math.Intersector#getLowestPositiveRoot} except for the addition
	 * of a parameter maxR which limits the maximum root value we accept. Anything over this will also result
	 * in a Float.NaN return value. A Float.NaN return means "no valid solution."
	 */
	public static float getLowestQuadraticRoot (float a, float b, float c, float maxR) {
		float determinant = (b * b) - (4.0f * a * c);
		// if the determinant is negative, there is no solution (can't square root a negative)
		if (determinant < 0.0f)
			return Float.NaN;

		float sqrtDeterminant = (float)Math.sqrt(determinant);
		float root1 = (-b - sqrtDeterminant) / (2.0f * a);
		float root2 = (-b + sqrtDeterminant) / (2.0f * a);

		// sort so root1 <= root2
		if (root1 > root2) {
			float tmp = root2;
			root2 = root1;
			root1 = tmp;
		}

		// get the lowest root
		if (root1 > 0.0f && root1 < maxR)
			return root1;
		if (root2 > 0.0f && root2 < maxR)
			return root2;

		// no valid solutions found
		return Float.NaN;
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
