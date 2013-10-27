package com.blarg.gdx.graphics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.glutils.ImmediateModeRenderer;
import com.badlogic.gdx.graphics.glutils.ImmediateModeRenderer10;
import com.badlogic.gdx.graphics.glutils.ImmediateModeRenderer20;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.math.collision.Segment;
import com.badlogic.gdx.math.collision.Sphere;

public class DebugGeometryRenderer {
	static final Color COLOR_1 = new Color(Color.YELLOW);
	static final Color COLOR_2 = new Color(Color.RED);

	final ImmediateModeRenderer renderer;

	public DebugGeometryRenderer() {
		if (Gdx.graphics.isGL20Available())
			renderer = new ImmediateModeRenderer20(false, true, 0);
		else
			renderer = new ImmediateModeRenderer10();
	}

	private void vtx(Vector3 v, Color c) {
		renderer.color(c.r, c.g, c.b, c.a);
		renderer.vertex(v.x, v.y, v.z);
	}

	private void vtx(float x, float y, float z, Color c) {
		renderer.color(c.r, c.g, c.b, c.a);
		renderer.vertex(x, y, z);
	}

	public void begin(Camera camera) {
		renderer.begin(camera.combined, GL10.GL_LINES);
	}

	public void end() {
		renderer.end();
	}

	public void box(BoundingBox box) {
		box(box, COLOR_1);
	}

	public void box(BoundingBox box, Color color) {
		// removed lines which are duplicated by more then one face
		// left and right faces don't need to be drawn at all (entirely duplicated lines)

		// top
		vtx(box.min.x, box.max.y, box.min.z, color);
		vtx(box.max.x, box.max.y, box.min.z, color);

		vtx(box.max.x, box.max.y, box.min.z, color);
		vtx(box.max.x, box.max.y, box.max.z, color);

		vtx(box.max.x, box.max.y, box.max.z, color);
		vtx(box.min.x, box.max.y, box.max.z, color);

		vtx(box.min.x, box.max.y, box.max.z, color);
		vtx(box.min.x, box.max.y, box.min.z, color);

		// back
		vtx(box.min.x, box.min.y, box.min.z, color);
		vtx(box.max.x, box.min.y, box.min.z, color);

		vtx(box.max.x, box.min.y, box.min.z, color);
		vtx(box.max.x, box.max.y, box.min.z, color);

		vtx(box.min.x, box.max.y, box.min.z, color);
		vtx(box.min.x, box.min.y, box.min.z, color);

		// front
		vtx(box.min.x, box.min.y, box.max.z, color);
		vtx(box.max.x, box.min.y, box.max.z, color);

		vtx(box.max.x, box.min.y, box.max.z, color);
		vtx(box.max.x, box.max.y, box.max.z, color);

		vtx(box.min.x, box.max.y, box.max.z, color);
		vtx(box.min.x, box.min.y, box.max.z, color);

		// bottom
		vtx(box.max.x, box.min.y, box.min.z, color);
		vtx(box.max.x, box.min.y, box.max.z, color);

		vtx(box.min.x, box.min.y, box.max.z, color);
		vtx(box.min.x, box.min.y, box.min.z, color);
	}

	public void sphere(Sphere sphere) {
		sphere(sphere, COLOR_1);
	}

	public void sphere(Sphere sphere, Color color) {
		float ax, ay, az;
		float bx, by, bz;
		float cx = 0.0f, cy = 0.0f, cz = 0.0f;
		float dx = 0.0f, dy = 0.0f, dz = 0.0f;
		float theta1, theta2, theta3;

		int n = 12;
		for (int j = 0; j < n / 2; ++j) {
			theta1 = j * (float)Math.PI * 2 / n - (float)Math.PI / 2;
			theta2 = (j + 1) * (float)Math.PI * 2 / n - (float)Math.PI / 2;

			for (int i = 0; i <= n; ++i) {
				theta3 = i * (float)Math.PI * 2 / n;
				ax = sphere.center.x + sphere.radius * (float)Math.cos(theta2) * (float)Math.cos(theta3);
				ay = sphere.center.y + sphere.radius * (float)Math.sin(theta2);
				az = sphere.center.z + sphere.radius * (float)Math.cos(theta2) * (float)Math.sin(theta3);

				bx = sphere.center.x + sphere.radius * (float)Math.cos(theta1) * (float)Math.cos(theta3);
				by = sphere.center.y + sphere.radius * (float)Math.sin(theta1);
				bz = sphere.center.z + sphere.radius * (float)Math.cos(theta1) * (float)Math.sin(theta3);

				if (j > 0 || i > 0) {
					vtx(ax, ay, az, color);
					vtx(bx, by, bz, color);

					vtx(bx, by, bz, color);
					vtx(dx, dy, dz, color);

					vtx(dx, dy, dz, color);
					vtx(cx, cy, cz, color);

					vtx(cx, cy, cz, color);
					vtx(ax, ay, az, color);
				}

				cx = ax;
				cy = ay;
				cz = az;
				dx = bx;
				dy = by;
				dz = bz;
			}
		}
	}

	public void ray(Ray ray, float length) {
		ray(ray, length, COLOR_1, COLOR_2);
	}

	static final Vector3 endPoint = new Vector3();
	public void ray(Ray ray, float length, Color originColor, Color endPointColor) {
		ray.getEndPoint(endPoint, length);

		vtx(ray.origin, originColor);
		vtx(endPoint, endPointColor);
	}

	public void line(Segment line) {
		line(line, COLOR_1);
	}

	public void line(Segment line, Color color) {
		vtx(line.a, color);
		vtx(line.b, color);
	}

	public void triangle(Vector3 a, Vector3 b, Vector3 c) {
		triangle(a, b, c, COLOR_1);
	}

	public void triangle(Vector3 a, Vector3 b, Vector3 c, Color color) {
		vtx(a, color);
		vtx(b, color);

		vtx(a, color);
		vtx(c, color);

		vtx(b, color);
		vtx(c, color);
	}

	public void rect(Vector3 a, Vector3 b, Vector3 c, Vector3 d) {
		rect(a, b, c, d, COLOR_1);
	}

	public void rect(Vector3 a, Vector3 b, Vector3 c, Vector3 d, Color color) {
		vtx(a, color);
		vtx(b, color);

		vtx(a, color);
		vtx(c, color);

		vtx(c, color);
		vtx(d, color);

		vtx(b, color);
		vtx(d, color);
	}
}
