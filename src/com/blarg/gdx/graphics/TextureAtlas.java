package com.blarg.gdx.graphics;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

public abstract class TextureAtlas {
	public static final float TEXCOORD_EDGE_BLEED_OFFSET = 0.02f;

	public final Texture texture;

	protected final float edgeCoordOffset;
	protected Array<TextureRegion> tiles = new Array<TextureRegion>(TextureRegion.class);

	public TextureAtlas(Texture texture, float edgeCoordOffset) {
		this.texture = texture;
		this.edgeCoordOffset = edgeCoordOffset;
	}

	public TextureRegion get(int index) {
		return tiles.items[index];
	}

	public int count() {
		return tiles.size;
	}
}
