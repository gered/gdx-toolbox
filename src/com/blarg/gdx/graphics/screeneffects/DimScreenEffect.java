package com.blarg.gdx.graphics.screeneffects;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.blarg.gdx.Services;
import com.blarg.gdx.graphics.ExtendedSpriteBatch;
import com.blarg.gdx.graphics.SolidColorTextureCache;
import com.blarg.gdx.graphics.ViewportContext;

public class DimScreenEffect extends ScreenEffect
{
	public static final Color DEFAULT_DIM_COLOR = Color.BLACK;
	public static final float DEFAULT_DIM_ALPHA = 0.5f;

	public final Color color;
	public float alpha;

	Color renderColor;

	ExtendedSpriteBatch spriteBatch;
	SolidColorTextureCache solidColorTextures;
	ViewportContext viewportContext;

	public DimScreenEffect()
	{
		color = new Color(DEFAULT_DIM_COLOR);
		alpha = DEFAULT_DIM_ALPHA;

		renderColor = new Color(color);

		spriteBatch = Services.get(ExtendedSpriteBatch.class);
		solidColorTextures = Services.get(SolidColorTextureCache.class);
		viewportContext = Services.get(ViewportContext.class);
	}

	@Override
	public void onRender(float delta)
	{
		renderColor.set(color);
		renderColor.a = alpha;
		Texture texture = solidColorTextures.get(color);

		spriteBatch.begin();
		spriteBatch.setColor(renderColor);
		spriteBatch.draw(
				texture,
				0, 0,
				viewportContext.pixelScaler.getScaledWidth(), viewportContext.pixelScaler.getScaledHeight()
		);
		spriteBatch.end();
	}
}
