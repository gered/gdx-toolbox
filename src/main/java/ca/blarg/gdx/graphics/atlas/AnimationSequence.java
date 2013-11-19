package ca.blarg.gdx.graphics.atlas;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.utils.Disposable;

class AnimationSequence implements Disposable {
	public TextureAtlas atlas;
	public int animatingIndex;
	public int start;
	public int stop;
	public int current;
	public float delay;
	public float currentFrameTime;
	public boolean isAnimating;
	public boolean loop;
	public Pixmap originalAnimatingTile;
	public Pixmap[] frames;

	public int getNumFrames() {
		return stop - start + 1;
	}

	public boolean isAnimationFinished() {
		return (isAnimating && !loop && current == stop);
	}

	@Override
	public void dispose() {
		originalAnimatingTile.dispose();
		for (int i = 0; i < frames.length; ++i)
			frames[i].dispose();
	}
}