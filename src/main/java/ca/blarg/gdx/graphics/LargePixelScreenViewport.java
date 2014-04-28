package ca.blarg.gdx.graphics;

import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class LargePixelScreenViewport extends ScreenViewport {
	@Override
	public void update(int screenWidth, int screenHeight, boolean centerCamera) {
		// TODO: these might need tweaking, this is fairly arbitrary
		if (viewportWidth < 640 || viewportHeight < 480)
			setUnitsPerPixel(1);
		else if (viewportWidth < 960 || viewportHeight < 720)
			setUnitsPerPixel(2);
		else if (viewportWidth < 1280 || viewportHeight < 960)
			setUnitsPerPixel(3);
		else if (viewportWidth < 1920 || viewportHeight < 1080)
			setUnitsPerPixel(4);
		else
			setUnitsPerPixel(5);

		// TODO: desktop "retina" / 4K display sizes?

		super.update(screenWidth, screenHeight, centerCamera);
	}
}
