package ca.blarg.gdx.graphics;

import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class LargePixelScreenViewport extends ScreenViewport {
	@Override
	public void update(int screenWidth, int screenHeight, boolean centerCamera) {
		// TODO: these might need tweaking, this is fairly arbitrary
		if (screenWidth < 640 || screenHeight < 480)
			setUnitsPerPixel(1.0f);
		else if (screenWidth < 960 || screenHeight < 720)
			setUnitsPerPixel(1.0f / 2.0f);
		else if (screenWidth < 1280 || screenHeight < 960)
			setUnitsPerPixel(1.0f / 3.0f);
		else if (screenWidth < 1920 || screenHeight < 1080)
			setUnitsPerPixel(1.0f / 4.0f);
		else
			setUnitsPerPixel(1.0f / 5.0f);

		// TODO: desktop "retina" / 4K display sizes?

		super.update(screenWidth, screenHeight, centerCamera);
	}
}
