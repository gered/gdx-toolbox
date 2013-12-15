package ca.blarg.gdx;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.TimeUtils;

public class GdxGameAppListener implements ApplicationListener, GameLooper {
	int updatesPerSecond;
	int updateFrequency;
	int maxFrameSkip;

	long nextTick = 0;
	int loops;

	float updateDelta = 0.0f;
	float renderDelta = 0.0f;

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

		setTiming(20, 5);

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

		nextTick = TimeUtils.millis();
	}

	@Override
	public void resize(int width, int height) {
		Gdx.app.debug("GdxGameAppListener", String.format("resize(%d, %d)", width, height));
		gameApp.onResize(width, height);
	}

	@Override
	public void render() {
		loops = 0;
		while (TimeUtils.millis() > nextTick && loops < maxFrameSkip) {
			gameApp.onUpdate(updateDelta);

			nextTick += updateFrequency;
			++loops;
		}

		renderDelta = (float)(TimeUtils.millis() + updateFrequency - nextTick) / (float)updateFrequency;

		gameApp.onRender(renderDelta);
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
	public void setTiming(int updatesPerSecond, int maxFrameSkip) {
		this.updatesPerSecond = updatesPerSecond;
		updateFrequency = 1000 / this.updatesPerSecond;
		this.maxFrameSkip = maxFrameSkip;
		this.updateDelta = updateFrequency / 1000.0f;
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
	public float getRenderDelta() {
		return renderDelta;
	}
}
