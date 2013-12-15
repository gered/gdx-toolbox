package ca.blarg.gdx;

public interface GameLooper {
	void setTiming(int updatesPerSecond, int maxFrameSkip);

	int getUpdateFrequency();

	float getUpdateDelta();
	float getRenderDelta();
}
