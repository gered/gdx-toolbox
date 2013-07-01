package com.blarg.gdx.processes;

import com.badlogic.gdx.utils.Disposable;
import com.blarg.gdx.GameApp;
import com.blarg.gdx.events.Event;
import com.blarg.gdx.events.EventHandler;
import com.blarg.gdx.events.EventManager;
import com.blarg.gdx.graphics.RenderContext;
import com.blarg.gdx.states.GameState;

public abstract class GameProcess extends EventHandler implements Disposable {
	public final ProcessManager processManager;
	public final GameApp gameApp;
	public final GameState gameState;

	boolean isFinished;

	public GameProcess(ProcessManager processManager, EventManager eventManager) {
		super(eventManager);

		if (processManager == null)
			throw new IllegalArgumentException("processManager cannot be null.");

		this.processManager = processManager;
		gameState = this.processManager.gameState;
		gameApp = gameState.gameApp;
	}

	public boolean isFinished() {
		return isFinished;
	}

	public void setFinished() {
		isFinished = true;
	}

	public void onAdd() {
	}

	public void onRemove() {
	}

	public void onPause(boolean dueToOverlay) {
	}

	public void onResume(boolean fromOverlay) {
	}

	public void onAppPause() {
	}

	public void onAppResume() {
	}

	public void onResize() {
	}

	public void onRender(float delta, RenderContext renderContext) {
	}

	public void onUpdate(float delta) {
	}

	public boolean onTransition(float delta, boolean isTransitioningOut, boolean started) {
		return true;
	}

	@Override
	public boolean handle(Event e)
	{
		return false;
	}

	public void dispose() {
	}
}
