package ca.blarg.gdx.graphics.screeneffects;

import ca.blarg.gdx.Services;
import ca.blarg.gdx.graphics.ExtendedSpriteBatch;
import ca.blarg.gdx.graphics.SolidColorTextureCache;
import ca.blarg.gdx.graphics.ViewportContext;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;

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
	public void onRender(float interpolation)
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
