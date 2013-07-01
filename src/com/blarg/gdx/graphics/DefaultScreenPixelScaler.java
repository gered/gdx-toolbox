package com.blarg.gdx.graphics;

public class DefaultScreenPixelScaler implements ScreenPixelScaler {
	int scale = 0;
	int viewportWidth = 0;
	int viewportHeight = 0;
	int scaledViewportWidth = 0;
	int scaledViewportHeight = 0;

	@Override
	public int getScale() {
		return scale;
	}

	@Override
	public int getScaledWidth() {
		return scaledViewportWidth;
	}

	@Override
	public int getScaledHeight() {
		return scaledViewportHeight;
	}

	@Override
	public void calculateScale(int screenWidth, int screenHeight) {
		viewportWidth = screenWidth;
		viewportHeight = screenHeight;

		// TODO: these might need tweaking, this is fairly arbitrary
		if (viewportWidth < 640 || viewportHeight < 480)
			scale = 1;
		else if (viewportWidth < 960 || viewportHeight < 720)
			scale = 2;
		else if (viewportWidth < 1280 || viewportHeight < 960)
			scale = 3;
		else if (viewportWidth < 1920 || viewportHeight < 1080)
			scale = 4;
		else
			scale = 5;

		// TODO: desktop "retina" / 4K display sizes? 1440p?

		scaledViewportWidth = viewportWidth / scale;
		scaledViewportHeight = viewportHeight / scale;
	}
}
