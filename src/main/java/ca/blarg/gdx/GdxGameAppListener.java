package ca.blarg.gdx;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;

public class GdxGameAppListener implements ApplicationListener, GameLooper {
	int updatesPerSecond;
	int updateFrequency;
	float maxFrameTime;

	float accumulator = 0.0f;

	float updateDelta = 0.0f;
	float renderInterpolation = 0.0f;

	Class<? extends GameApp> gameAppType;
	GameApp gameApp;

	public GdxGameAppListener(Class<? extends GameApp> gameAppType) {
		this.gameAppType = gameAppType;
	}

	@Override
	public void create() {
		Gdx.app.debug("GdxGameAppListener", "create");
		Gdx.app.debug("GdxGameAppListener", String.format("Application type: %s", Gdx.app.getType()));

		Services.register(GameLooper.class, this);

		setTiming(20, 0.25f);

		try {
			gameApp = gameAppType.newInstance();
		} catch (Exception e) {
			Gdx.app.log("GdxGameAppListener", String.format("Instantiation of GameApp object failed: %s", e));
			gameApp = null;
		}

		if (gameApp == null) {
			Gdx.app.log("GdxGameAppListener", "Failed to create a GameApp. Aborting.");
			Gdx.app.exit();
			return;
		}

		gameApp.onCreate();

	}

	@Override
	public void resize(int width, int height) {
		Gdx.app.debug("GdxGameAppListener", String.format("resize(%d, %d)", width, height));
		gameApp.onResize(width, height);
	}

	@Override
	public void render() {
		float frameTime = Gdx.graphics.getRawDeltaTime();
		if (frameTime > maxFrameTime)
			frameTime = maxFrameTime;

		accumulator += frameTime;

		while (accumulator >= updateDelta) {
			gameApp.onUpdateGameState(updateDelta);
			accumulator -= updateDelta;
		}

		renderInterpolation = accumulator / updateDelta;

		gameApp.onUpdateFrame(frameTime);
		gameApp.onRender(renderInterpolation);
	}

	@Override
	public void pause() {
		Gdx.app.debug("GdxGameAppListener", "pause");
		gameApp.onPause();
	}

	@Override
	public void resume() {
		Gdx.app.debug("GdxGameAppListener", "resume");
		gameApp.onResume();
	}

	@Override
	public void dispose() {
		Gdx.app.debug("GdxGameAppListener", "dispose");
		if (gameApp != null)
			gameApp.dispose();
	}

	@Override
	public void setTiming(int updatesPerSecond, float maxFrameTimeSeconds) {
		this.updatesPerSecond = updatesPerSecond;
		updateFrequency = 1000 / this.updatesPerSecond;
		this.updateDelta = updateFrequency / 1000.0f;
		this.maxFrameTime = maxFrameTimeSeconds;
	}

	@Override
	public int getUpdateFrequency() {
		return updateFrequency;
	}

	@Override
	public float getUpdateDelta() {
		return updateDelta;
	}

	@Override
	public float getRenderInterpolation() {
		return renderInterpolation;
	}
}
