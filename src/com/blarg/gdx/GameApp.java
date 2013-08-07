package com.blarg.gdx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.TimeUtils;
import com.blarg.gdx.events.EventManager;
import com.blarg.gdx.graphics.RenderContext;
import com.blarg.gdx.states.StateManager;

public abstract class GameApp implements Disposable {
	public final EventManager eventManager;
	public final StateManager stateManager;
	public final RenderContext renderContext;

	boolean logHeapMemUsage = false;
	long lastHeapMemLogTime = 0;

	public GameApp() {
		Gdx.app.debug("GameApp", "ctor");

		eventManager = new EventManager();
		stateManager = new StateManager(this, eventManager);
		renderContext = new RenderContext(true);
	}

	protected void toggleHeapMemUsageLogging(boolean enable) {
		logHeapMemUsage = enable;
		if (enable)
			lastHeapMemLogTime = TimeUtils.millis();
	}

	public abstract void onCreate();

	public void onResize(int width, int height) {
		Gdx.app.debug("GameApp", String.format("onResize(%d, %d)", width, height));
		renderContext.onResize(width, height);
	}

	public void onRender(float delta) {
		renderContext.onPreRender();
		stateManager.onRender(delta, renderContext);
		renderContext.onPostRender();
	}

	public void onUpdate(float delta) {
		renderContext.onUpdate(delta);
		eventManager.onUpdate(delta);
		stateManager.onUpdate(delta);
		if (stateManager.isEmpty()) {
			Gdx.app.debug("GameApp", "No states running. Quitting.");
			Gdx.app.exit();
			return;
		}

		if (logHeapMemUsage) {
			long currentTime = TimeUtils.millis();
			if (currentTime - lastHeapMemLogTime > 1000) {
				lastHeapMemLogTime = currentTime;
				Gdx.app.debug("GameApp", String.format("Heap memory usage: %d", Gdx.app.getJavaHeap()));
			}
		}
	}

	public void onPause() {
		Gdx.app.debug("GameApp", "onPause");
		stateManager.onAppPause();
		renderContext.onPause();
	}

	public void onResume() {
		Gdx.app.debug("GameApp", "onResume");
		renderContext.onResume();
		stateManager.onAppResume();
	}

	@Override
	public void dispose() {
		Gdx.app.debug("GameApp", "dispose");
		stateManager.dispose();
		renderContext.dispose();
	}
}
