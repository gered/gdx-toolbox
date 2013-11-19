package ca.blarg.gdx.states;

import ca.blarg.gdx.GameApp;
import ca.blarg.gdx.events.EventHandler;
import ca.blarg.gdx.graphics.screeneffects.ScreenEffectManager;
import com.badlogic.gdx.utils.Disposable;
import ca.blarg.gdx.GameApp;
import ca.blarg.gdx.events.Event;
import ca.blarg.gdx.events.EventHandler;
import ca.blarg.gdx.events.EventManager;
import ca.blarg.gdx.graphics.screeneffects.ScreenEffectManager;
import ca.blarg.gdx.processes.ProcessManager;

public abstract class GameState extends EventHandler implements Disposable {
	public final StateManager stateManager;
	public final ScreenEffectManager effectManager;
	public final ProcessManager processManager;
	public final GameApp gameApp;

	boolean isFinished;

	public GameState(StateManager stateManager, EventManager eventManager) {
		super(eventManager);

		if (stateManager == null)
			throw new IllegalArgumentException("stateManager cannot be null.");

		this.stateManager = stateManager;
		gameApp = stateManager.gameApp;

		effectManager = new ScreenEffectManager();
		processManager = new ProcessManager(this);
	}

	public boolean isTransitioning() {
		return stateManager.isStateTransitioning(this);
	}

	public boolean isTopState() {
		return stateManager.isTopState(this);
	}

	public boolean isFinished() {
		return isFinished;
	}

	public void setFinished() {
		isFinished = true;
	}

	public void dispose() {
		effectManager.dispose();
		processManager.dispose();
	}

	public void onPush() {
	}

	public void onPop() {
	}

	public void onPause(boolean dueToOverlay) {
		processManager.onPause(dueToOverlay);
	}

	public void onResume(boolean fromOverlay) {
		processManager.onResume(fromOverlay);
	}

	public void onAppPause() {
		processManager.onAppPause();
		effectManager.onAppPause();
	}

	public void onAppResume() {
		processManager.onAppResume();
		effectManager.onAppResume();
	}

	public void onResize() {
		processManager.onResize();
		effectManager.onResize();
	}

	public void onRender(float delta) {
		// switch it up and do effects before processes here so that processes
		// (which would commonly be used for UI overlay elements) don't get
		// overwritten by local effects (e.g. flashes, etc.)
		effectManager.onRenderLocal(delta);
		processManager.onRender(delta);
	}

	public void onUpdate(float delta) {
		effectManager.onUpdate(delta);
		processManager.onUpdate(delta);
	}

	public boolean onTransition(float delta, boolean isTransitioningOut, boolean started) {
		return true;
	}

	@Override
	public boolean handle(Event e)
	{
		return false;
	}
}
