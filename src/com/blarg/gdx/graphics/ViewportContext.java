package com.blarg.gdx.graphics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.blarg.gdx.math.MathHelpers;

/**
 * Manages graphics state relating to the viewport dimensions and camera(s).
 */
public class ViewportContext {
	public final ScreenPixelScaler pixelScaler;

	Camera perspectiveCamera;
	OrthographicCamera orthographicCamera;

	public ViewportContext(boolean use2dPixelScaling) {
		Gdx.app.debug("ViewportContext", "ctor");
		if (use2dPixelScaling)
			pixelScaler = new DefaultScreenPixelScaler();
		else
			pixelScaler = new NoScaleScreenPixelScaler();
		pixelScaler.calculateScale(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		orthographicCamera = new OrthographicCamera(pixelScaler.getScaledWidth(), pixelScaler.getScaledHeight());

		setDefaultPerspectiveCamera();
	}

	public Camera getPerspectiveCamera() {
		return perspectiveCamera;
	}

	public OrthographicCamera getOrthographicCamera() {
		return orthographicCamera;
	}

	public void setPerspectiveCamera(Camera camera) {
		if (camera == null)
			throw new IllegalArgumentException();
		perspectiveCamera = camera;
	}

	public void setDefaultPerspectiveCamera() {
		PerspectiveCamera camera = new PerspectiveCamera(60.0f, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		camera.position.set(0.0f, 0.0f, 0.0f);
		camera.lookAt(MathHelpers.FORWARD_VECTOR3);
		camera.near = 0.1f;
		camera.far = 100.0f;
		camera.update();
		setPerspectiveCamera(camera);
	}

	public void onPreRender() {

	}

	public void onPostRender() {

	}

	public void onUpdate(float delta) {
		perspectiveCamera.update();
		orthographicCamera.update();
	}

	public void onResize(int width, int height) {
		Gdx.app.debug("ViewportContext", String.format("onResize(%d, %d)", width, height));
		pixelScaler.calculateScale(width, height);
		orthographicCamera.setToOrtho(false, pixelScaler.getScaledWidth(), pixelScaler.getScaledHeight());
		perspectiveCamera.viewportWidth = width;
		perspectiveCamera.viewportHeight = height;
	}

	public void onPause() {
		Gdx.app.debug("ViewportContext", String.format("onPause"));
	}

	public void onResume() {
		Gdx.app.debug("ViewportContext", String.format("onResume"));
	}
}
