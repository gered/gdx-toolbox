package com.blarg.gdx.graphics;

import com.badlogic.gdx.Gdx;

public class NoScaleScreenPixelScaler implements ScreenPixelScaler {
	@Override
	public int getScale() {
		return 1;
	}

	@Override
	public int getScaledWidth() {
		return Gdx.graphics.getWidth();
	}

	@Override
	public int getScaledHeight() {
		return Gdx.graphics.getHeight();
	}

	@Override
	public void calculateScale(int screenWidth, int screenHeight) {
		// nothing!
	}
}
