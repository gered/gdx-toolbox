package ca.blarg.gdx.graphics.screeneffects;

import com.badlogic.gdx.utils.Disposable;

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

	public void onRender(float delta) {
	}

	public void onUpdateGameState(float delta) {
	}

	public void onUpdateFrame(float delta) {
	}

	public void dispose() {
	}
}
