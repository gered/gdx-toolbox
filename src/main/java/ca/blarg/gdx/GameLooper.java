package ca.blarg.gdx;

public interface GameLooper {
	void setTiming(int updatesPerSecond, float maxFrameTimeSeconds);

	int getUpdateFrequency();

	float getUpdateDelta();
	float getRenderDelta();
}
