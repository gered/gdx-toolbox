package com.blarg.gdx.graphics;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

/***
 * <p>
 * Wrapper over libgdx's included {@link SpriteBatch} which doesn't manipulate _any_ OpenGL
 * state until the call to {@link SpriteBatch#end()}. This allows a begin/end block to
 * wrap over a large amount of code that might manipulate OpenGL state one or more times.
 * Sprites rendered with this are simply queued up until the end() call and rendered in that
 * same order with {@link SpriteBatch}.
 * </p>
 *
 * <p>
 * This "delayed" queueing behaviour will not necessarily be desirable behaviour-wise /
 * performance-wise in all situations. This class introduces a small bit of performance overhead
 * compared to using {@link SpriteBatch} directly.
 * </p>
 */
public class DelayedSpriteBatch {
	static final int CAPACITY_INCREMEMT = 128;

	Array<Sprite> sprites;
	int pointer;
	boolean hasBegun;
	SpriteBatch spriteBatch;

	public DelayedSpriteBatch() {
		sprites = new Array<Sprite>(true, CAPACITY_INCREMEMT, Sprite.class);
		pointer = 0;
		increaseCapacity();

		hasBegun = false;
		spriteBatch = null;
	}

	public void begin(SpriteBatch spriteBatch) {
		if (hasBegun)
			throw new IllegalStateException("Cannot be called within an existing begin/end block.");
		if (spriteBatch == null)
			throw new IllegalArgumentException();

		this.spriteBatch = spriteBatch;
		hasBegun = true;
		pointer = 0;
	}

	public void draw(Texture texture, float x, float y) {
		draw(texture, x, y, texture.getWidth(), texture.getHeight(), Color.WHITE);
	}

	public void draw(Texture texture, float x, float y, Color tint) {
		draw(texture, x, y, texture.getWidth(), texture.getHeight(), tint);
	}

	public void draw(Texture texture, float x, float y, float width, float height) {
		draw(texture, x, y, width, height, Color.WHITE);
	}

	public void draw(Texture texture, float x, float y, float width, float height, Color tint) {
		Sprite sprite = nextUsable();
		sprite.setTexture(texture);
		sprite.setRegion(0, 0, texture.getWidth(), texture.getHeight());
		sprite.setColor(tint);
		sprite.setSize(width, height);
		sprite.setPosition(x, y);
	}

	public void draw(Texture texture, float x, float y, float width, float height, float u, float v, float u2, float v2) {
		draw(texture, x, y, width, height, u, v, u2, v2, Color.WHITE);
	}

	public void draw(Texture texture, float x, float y, float width, float height, float u, float v, float u2, float v2, Color tint) {
		Sprite sprite = nextUsable();
		sprite.setTexture(texture);
		sprite.setRegion(u, v, u2, v2);
		sprite.setColor(tint);
		sprite.setSize(width, height);
		sprite.setPosition(x, y);
	}

	public void draw(Texture texture, float x, float y, int srcX, int srcY, int srcWidth, int srcHeight) {
		draw(texture, x, y, srcX, srcY, srcWidth, srcHeight, Color.WHITE);
	}

	public void draw(Texture texture, float x, float y, int srcX, int srcY, int srcWidth, int srcHeight, Color tint) {
		Sprite sprite = nextUsable();
		sprite.setTexture(texture);
		sprite.setRegion(srcX, srcY, srcWidth, srcHeight);
		sprite.setColor(tint);
		sprite.setSize(Math.abs(srcWidth), Math.abs(srcHeight));
		sprite.setPosition(x, y);
	}

	public void draw(TextureRegion region, float x, float y) {
		draw(region, x, y, region.getRegionWidth(), region.getRegionWidth(), Color.WHITE);
	}

	public void draw(TextureRegion region, float x, float y, Color tint) {
		draw(region, x, y, region.getRegionWidth(), region.getRegionWidth(), tint);
	}

	public void draw(TextureRegion region, float x, float y, float width, float height) {
		draw(region, x, y, width, height, Color.WHITE);
	}

	public void draw(TextureRegion region, float x, float y, float width, float height, Color tint) {
		Sprite sprite = nextUsable();
		sprite.setRegion(region);
		sprite.setColor(tint);
		sprite.setSize(width, height);
		sprite.setPosition(x, y);
	}

	public void draw(BitmapFont font, float x, float y, CharSequence str) {
		draw(font, x, y, str, Color.WHITE);
	}

	public void draw(BitmapFont font, float x, float y, CharSequence str, Color tint) {
		BitmapFont.BitmapFontData fontData = font.getData();
		Texture fontTexture = font.getRegion().getTexture();

		float currentX = x;
		float currentY = y;

		for (int i = 0; i < str.length(); ++i) {
			char c = str.charAt(i);

			// multiline support
			if (c == '\r')
				continue;  // can't render this anyway, and likely a '\n' is right behind ...
			if (c == '\n') {
				currentY -= fontData.lineHeight;
				currentX = x;
				continue;
			}

			BitmapFont.Glyph glyph = fontData.getGlyph(c);
			if (glyph == null) {
				// TODO: maybe rendering some special char here instead would be better?
				currentX += fontData.spaceWidth;
				continue;
			}

			draw(fontTexture, currentX + glyph.xoffset, currentY + glyph.yoffset, glyph.srcX, glyph.srcY, glyph.width, glyph.height, tint);

			currentX += glyph.xadvance;
		}
	}

	public void flush() {
		if (!hasBegun)
			throw new IllegalStateException("Cannot call outside of a begin/end block.");

		spriteBatch.setColor(Color.WHITE);
		spriteBatch.begin();
		for (int i = 0; i < pointer; ++i) {
			Sprite sprite = sprites.items[i];
			sprite.draw(spriteBatch);
			sprite.setTexture(null);   // don't keep references!
		}
		spriteBatch.end();

		pointer = 0;
	}

	public void end() {
		if (!hasBegun)
			throw new IllegalStateException("Must call begin() first.");

		flush();

		hasBegun = false;
		spriteBatch = null;    // don't need to hold on to this particular reference anymore
	}

	private void increaseCapacity() {
		int newCapacity = sprites.items.length + CAPACITY_INCREMEMT;
		sprites.ensureCapacity(newCapacity);

		for (int i = 0; i < CAPACITY_INCREMEMT; ++i)
			sprites.add(new Sprite());
	}

	private int getRemainingSpace() {
		return sprites.size - pointer;
	}

	private Sprite nextUsable() {
		if (getRemainingSpace() == 0)
			increaseCapacity();

		Sprite usable = sprites.items[pointer];
		pointer++;
		return usable;
	}
}
