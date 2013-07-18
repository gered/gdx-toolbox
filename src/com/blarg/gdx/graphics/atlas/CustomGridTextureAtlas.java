package com.blarg.gdx.graphics.atlas;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

public class CustomGridTextureAtlas extends TextureAtlas {
	public CustomGridTextureAtlas(Texture texture) {
		this(texture, TEXCOORD_EDGE_BLEED_OFFSET);
	}

	public CustomGridTextureAtlas(Texture texture, float edgeCoordOffset) {
		super(texture, edgeCoordOffset);
	}

	public int add(Rectangle rect) {
		return add((int)rect.x, (int)rect.y, (int)rect.getWidth(), (int)rect.getHeight());
	}

	public int add(int x, int y, int width, int height) {
		// pixel location/dimensions
		int left = x;
		int top = y;
		int right = left + width;
		int bottom = top + height;

		// texture coordinates
		// HACK: subtract edgeCoordOffset from the bottom right edges to
		//       get around floating point rounding errors (adjacent tiles will
		//       slightly bleed in otherwise)
		float u = (left + edgeCoordOffset) / (float)texture.getWidth();
		float v = (top + edgeCoordOffset) / (float)texture.getHeight();
		float u2 = ((float)right - edgeCoordOffset) / (float)texture.getWidth();
		float v2 = ((float)bottom - edgeCoordOffset) / (float)texture.getHeight();

		TextureRegion tile = new TextureRegion();
		tile.setTexture(texture);
		tile.setRegion(u, v, u2, v2);
		tiles.add(tile);

		return tiles.size - 1;
	}

	public void reset() {
		tiles.clear();
	}
}
