package com.blarg.gdx.graphics.screeneffects;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.blarg.gdx.graphics.RenderContext;

public class FadeScreenEffect extends ScreenEffect
{
	public static final float DEFAULT_FADE_SPEED = 3.0f;

	float fadeSpeed;
	boolean isFadingOut;
	float alpha;
	Color color;
	float fadeToAlpha;
	boolean isDoneFading;

	public FadeScreenEffect() {
		color = new Color();
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
	public void onRender(float delta, RenderContext renderContext)
	{
		Texture texture = renderContext.solidColorTextures.get(Color.WHITE);
		color.a = alpha;

		renderContext.delayedSpriteBatch.draw(
				texture,
				0, 0,
				renderContext.pixelScaler.getScaledWidth(), renderContext.pixelScaler.getScaledHeight(),
				color);
	}

	@Override
	public void onUpdate(float delta)
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
