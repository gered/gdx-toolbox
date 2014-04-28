package ca.blarg.gdx.graphics.screeneffects;

import ca.blarg.gdx.Services;
import ca.blarg.gdx.graphics.ExtendedSpriteBatch;
import ca.blarg.gdx.graphics.SolidColorTextureCache;
import ca.blarg.gdx.graphics.ViewportContext;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;

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

	ExtendedSpriteBatch spriteBatch;
	SolidColorTextureCache solidColorTextures;
	ViewportContext viewportContext;

	public float getAlpha() {
		return alpha;
	}

	public FlashScreenEffect() {
		isFlashingIn = true;
		flashInSpeed = DEFAULT_FLASH_SPEED;
		flashOutSpeed = DEFAULT_FLASH_SPEED;
		maximumIntensity = DEFAULT_MAX_INTENSITY;
		color = new Color(Color.WHITE);

		spriteBatch = Services.get(ExtendedSpriteBatch.class);
		solidColorTextures = Services.get(SolidColorTextureCache.class);
		viewportContext = Services.get(ViewportContext.class);
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
