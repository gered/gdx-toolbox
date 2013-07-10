package com.blarg.gdx.graphics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Disposable;
import com.blarg.gdx.math.MathHelpers;

public class RenderContext implements Disposable {
	public final ExtendedSpriteBatch spriteBatch;
	public final BillboardSpriteBatch billboardSpriteBatch;
	public final ShapeRenderer debugGeometryRenderer2D;
	public final ShapeRenderer debugGeometryRenderer3D;
	public final ModelBatch modelBatch;
	public final ScreenPixelScaler pixelScaler;
	public final SolidColorTextureCache solidColorTextures;

	Camera perspectiveCamera;
	OrthographicCamera orthographicCamera;

	public RenderContext(boolean use2dPixelScaling) {
		Gdx.app.debug("RenderContext", "ctor");
		spriteBatch = new ExtendedSpriteBatch();
		billboardSpriteBatch = new BillboardSpriteBatch();
		debugGeometryRenderer2D = new ShapeRenderer();
		debugGeometryRenderer3D = new ShapeRenderer();
		modelBatch = new ModelBatch();
		solidColorTextures = new SolidColorTextureCache();

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

	public void clear() {
		clear(0.0f, 0.0f, 0.0f, 1.0f);
	}

	public void clear(float red, float green, float blue, float alpha) {
		Gdx.graphics.getGL20().glClearColor(red, green, blue, alpha);
		Gdx.graphics.getGL20().glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
	}

	public void onPreRender() {
		spriteBatch.setProjectionMatrix(orthographicCamera.combined);
		spriteBatch.setPixelScale(pixelScaler.getScale());
		debugGeometryRenderer2D.setProjectionMatrix(orthographicCamera.combined);
		debugGeometryRenderer3D.setProjectionMatrix(perspectiveCamera.combined);
	}

	public void onPostRender() {
	}

	public void onUpdate(float delta) {
		perspectiveCamera.update();
		orthographicCamera.update();
	}

	public void onResize(int width, int height) {
		Gdx.app.debug("RenderContext", String.format("onResize(%d, %d)", width, height));
		pixelScaler.calculateScale(width, height);
		orthographicCamera.setToOrtho(false, pixelScaler.getScaledWidth(), pixelScaler.getScaledHeight());
		perspectiveCamera.viewportWidth = width;
		perspectiveCamera.viewportHeight = height;
	}

	public void onPause() {
		Gdx.app.debug("RenderContext", String.format("onPause"));
		solidColorTextures.onPause();
	}

	public void onResume() {
		Gdx.app.debug("RenderContext", String.format("onResume"));
		solidColorTextures.onResume();
	}

	@Override
	public void dispose() {
		Gdx.app.debug("RenderContext", String.format("dispose"));
		solidColorTextures.dispose();
		modelBatch.dispose();
		billboardSpriteBatch.dispose();
		spriteBatch.dispose();
	}
}
