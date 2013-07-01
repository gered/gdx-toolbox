package com.blarg.gdx.graphics.screeneffects;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.blarg.gdx.graphics.RenderContext;

public class DimScreenEffect extends ScreenEffect
{
	public static final Color DEFAULT_DIM_COLOR = Color.BLACK;
	public static final float DEFAULT_DIM_ALPHA = 0.5f;

	public final Color color;
	public float alpha;

	Color renderColor;

	public DimScreenEffect()
	{
		color = new Color(DEFAULT_DIM_COLOR);
		alpha = DEFAULT_DIM_ALPHA;

		renderColor = new Color(color);
	}

	@Override
	public void onRender(float delta, RenderContext renderContext)
	{
		renderColor.set(color);
		renderColor.a = alpha;
		Texture texture = renderContext.solidColorTextures.get(color);

		renderContext.delayedSpriteBatch.draw(
				texture,
				0, 0,
				renderContext.pixelScaler.getScaledWidth(), renderContext.pixelScaler.getScaledHeight(),
				renderColor);
	}
}
