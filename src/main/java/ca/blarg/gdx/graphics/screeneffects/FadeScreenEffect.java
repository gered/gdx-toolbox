package ca.blarg.gdx.graphics.screeneffects;

import ca.blarg.gdx.Services;
import ca.blarg.gdx.graphics.ExtendedSpriteBatch;
import ca.blarg.gdx.graphics.SolidColorTextureCache;
import ca.blarg.gdx.graphics.ViewportContext;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;

public class FadeScreenEffect extends ScreenEffect
{
	public static final float DEFAULT_FADE_SPEED = 0.01f;

	float fadeSpeed;
	boolean isFadingOut;
	float alpha;
	Color color;
	float fadeToAlpha;
	boolean isDoneFading;

	ExtendedSpriteBatch spriteBatch;
	SolidColorTextureCache solidColorTextures;
	ViewportContext viewportContext;

	public FadeScreenEffect() {
		color = new Color();

		spriteBatch = Services.get(ExtendedSpriteBatch.class);
		solidColorTextures = Services.get(SolidColorTextureCache.class);
		viewportContext = Services.get(ViewportContext.class);
	}

	public boolean isDoneFading() {
		return isDoneFading;
	}

	public void fadeOut(float toAlpha, Color color) {
		fadeOut(alpha, color, DEFAULT_FADE_SPEED);
	}

	public void fadeOut(float toAlpha, Color color, float speed) {
		if (toAlpha < 0.0f || toAlpha > 1.0f)
			throw new IllegalArgumentException("toAlpha needs to be between 0.0 and 1.0");

		color.set(color);
		fadeSpeed = speed;
		isFadingOut = true;
		alpha = 0.0f;
		fadeToAlpha = toAlpha;
	}

	public void fadeIn(float toAlpha, Color color) {
		fadeIn(alpha, color, DEFAULT_FADE_SPEED);
	}

	public void fadeIn(float toAlpha, Color color, float speed) {
		if (toAlpha < 0.0f || toAlpha > 1.0f)
			throw new IllegalArgumentException("toAlpha needs to be between 0.0 and 1.0");

		color.set(color);
		fadeSpeed = speed;
		isFadingOut = false;
		alpha = 1.0f;
		fadeToAlpha = toAlpha;
	}

	@Override
	public void onRender(float interpolation)
	{
		Texture texture = solidColorTextures.get(Color.WHITE);
		color.a = alpha;

		spriteBatch.begin();
		spriteBatch.setColor(color);
		spriteBatch.draw(
				texture,
				0, 0,
				viewportContext.getOrthographicViewport().getWorldWidth(), viewportContext.getOrthographicViewport().getWorldHeight()
		);
		spriteBatch.end();
	}

	@Override
	public void onUpdateFrame(float delta)
	{
		if (isDoneFading)
			return;

		if (isFadingOut) {
			alpha += (delta + fadeSpeed);
			if (alpha >= fadeToAlpha) {
				alpha = fadeToAlpha;
				isDoneFading = true;
			}
		} else {
			alpha -= (delta + fadeSpeed);
			if (alpha < fadeToAlpha) {
				alpha = fadeToAlpha;
				isDoneFading = true;
			}
		}
	}
}
