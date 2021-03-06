package ca.blarg.gdx.entities;

import ca.blarg.gdx.events.Event;
import ca.blarg.gdx.events.EventHandler;
import ca.blarg.gdx.events.EventManager;
import com.badlogic.gdx.utils.Disposable;

public abstract class ComponentSystem extends EventHandler implements Disposable {
	public final EntityManager entityManager;

	public ComponentSystem(EntityManager entityManager, EventManager eventManager) {
		super(eventManager);
		if (entityManager == null)
			throw new IllegalArgumentException("entityManager can not be null.");

		this.entityManager = entityManager;
	}

	public void onAppPause() {
	}

	public void onAppResume() {
	}

	public void onResize() {
	}

	public void onRender(float interpolation) {
	}

	public void onUpdateGameState(float delta) {
	}

	public void onUpdateFrame(float delta) {
	}

	@Override
	public boolean handle(Event e) {
		return false;
	}

	@Override
	public void dispose() {
	}
}
