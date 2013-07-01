package com.blarg.gdx.entities;

import com.blarg.gdx.events.Event;
import com.blarg.gdx.events.EventHandler;
import com.blarg.gdx.events.EventManager;
import com.blarg.gdx.graphics.RenderContext;

public abstract class ComponentSystem extends EventHandler {
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

	public void onRender(float delta, RenderContext renderContext) {
	}

	public void onUpdate(float delta) {
	}

	@Override
	public boolean handle(Event e) {
		return false;
	}
}
