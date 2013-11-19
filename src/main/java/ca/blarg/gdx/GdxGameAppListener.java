package ca.blarg.gdx;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;

public class GdxGameAppListener implements ApplicationListener {
	Class<? extends GameApp> gameAppType;
	GameApp gameApp;

	public GdxGameAppListener(Class<? extends GameApp> gameAppType) {
		this.gameAppType = gameAppType;
	}

	@Override
	public void create() {
		Gdx.app.debug("GdxGameAppListener", "create");
		Gdx.app.debug("GdxGameAppListener", String.format("Application type: %s", Gdx.app.getType()));

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
		// TODO: probably not the best idea to share the same delta with both renders and updates...
		float delta = Gdx.graphics.getDeltaTime();
		gameApp.onUpdate(delta);
		gameApp.onRender(delta);
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
}
