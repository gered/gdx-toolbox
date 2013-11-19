package ca.blarg.gdx.graphics;

public interface ScreenPixelScaler {
	int getScale();

	int getScaledWidth();
	int getScaledHeight();

	void calculateScale(int screenWidth, int screenHeight);
}
