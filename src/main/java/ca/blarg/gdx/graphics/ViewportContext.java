package ca.blarg.gdx.graphics;

import ca.blarg.gdx.math.MathHelpers;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

/**
 * Manages graphics state relating to the viewport dimensions and camera(s).
 */
public class ViewportContext {
	Viewport perspectiveViewport;
	ScreenViewport orthographicViewport;

	public ViewportContext(boolean useLarge2dPixelScaling) {
		Gdx.app.debug("ViewportContext", "ctor");
		if (useLarge2dPixelScaling)
			orthographicViewport = new LargePixelScreenViewport();
		else
			orthographicViewport = new ScreenViewport();

		Camera perspectiveCamera = newDefaultPerspectiveCamera();
		perspectiveViewport = new ScreenViewport(perspectiveCamera);
	}

	public Viewport getPerspectiveViewport() {
		return perspectiveViewport;
	}

	public ScreenViewport getOrthographicViewport() {
		return orthographicViewport;
	}

	public Camera getPerspectiveCamera() {
		return perspectiveViewport.getCamera();
	}

	public OrthographicCamera getOrthographicCamera() {
		return (OrthographicCamera)orthographicViewport.getCamera();
	}

	public void setPerspectiveCamera(Camera camera) {
		if (camera == null)
			throw new IllegalArgumentException();
		perspectiveViewport.setCamera(camera);
	}

	public void setDefaultPerspectiveCamera() {
		setPerspectiveCamera(newDefaultPerspectiveCamera());
	}

	public void onPreRender() {

	}

	public void onPostRender() {

	}

	public void onUpdateFrame(float delta) {
	}

	public void onResize(int width, int height) {
		Gdx.app.debug("ViewportContext", String.format("onResize(%d, %d)", width, height));
		perspectiveViewport.update(width, height, true);
		orthographicViewport.update(width, height, true);
	}

	public void onPause() {
		Gdx.app.debug("ViewportContext", String.format("onPause"));
	}

	public void onResume() {
		Gdx.app.debug("ViewportContext", String.format("onResume"));
	}

	private Camera newDefaultPerspectiveCamera() {
		PerspectiveCamera camera = new PerspectiveCamera(60.0f, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		camera.position.set(0.0f, 0.0f, 0.0f);
		camera.lookAt(MathHelpers.FORWARD_VECTOR3);
		camera.near = 0.1f;
		camera.far = 100.0f;
		camera.update();
		return camera;
	}
}
