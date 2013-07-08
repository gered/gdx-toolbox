package com.blarg.gdx.graphics.screeneffects;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.blarg.gdx.graphics.RenderContext;

public class FlashScreenEffect extends ScreenEffect
{
	public static final float DEFAULT_FLASH_SPEED = 16.0f;
	public static final float DEFAULT_MAX_INTENSITY = 1.0f;

	public float flashInSpeed;
	public float flashOutSpeed;
	public float maximumIntensity;
	public final Color color;

	boolean isFlashingIn;
	float alpha;

	public float getAlpha() {
		return alpha;
	}

	public FlashScreenEffect() {
		isFlashingIn = true;
		flashInSpeed = DEFAULT_FLASH_SPEED;
		flashOutSpeed = DEFAULT_FLASH_SPEED;
		maximumIntensity = DEFAULT_MAX_INTENSITY;
		color = new Color(Color.WHITE);
	}

	@Override
	public void onRender(float delta, RenderContext renderContext)
	{
		Texture texture = renderContext.solidColorTextures.get(Color.WHITE);
		color.a = alpha;

		renderContext.spriteBatch.begin();
		renderContext.spriteBatch.setColor(color);
		renderContext.spriteBatch.draw(
				texture,
				0, 0,
				renderContext.pixelScaler.getScaledWidth(), renderContext.pixelScaler.getScaledHeight()
		);
		renderContext.spriteBatch.end();
	}

	@Override
	public void onUpdate(float delta)
	{
		if (isFlashingIn) {
			alpha += (delta * flashInSpeed);
			if (alpha >= maximumIntensity) {
				alpha = maximumIntensity;
				isFlashingIn = false;
			}
		} else {
			alpha -= (delta * flashOutSpeed);
			if (alpha < 0.0f)
				alpha = 0.0f;
		}

		if (alpha == 0.0f && isFlashingIn == false)
			isActive = false;
	}
}
