package com.blarg.gdx.graphics.screeneffects;

import com.badlogic.gdx.utils.Disposable;
import com.blarg.gdx.graphics.RenderContext;

public abstract class ScreenEffect implements Disposable
{
	public boolean isActive;

	public ScreenEffect() {
		isActive = true;
	}

	public void onAdd() {
	}

	public void onRemove() {
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

	public void dispose() {
	}
}
