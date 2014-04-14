package ca.blarg.gdx.graphics.atlas;

import ca.blarg.gdx.math.MathHelpers;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

public abstract class TextureAtlas {
	public static final float TEXCOORD_EDGE_BLEED_OFFSET = 0.02f;

	public final Texture texture;

	protected final float edgeCoordOffset;
	protected Array<TextureRegion> tiles = new Array<TextureRegion>(TextureRegion.class);
	protected ObjectMap<String, AnimationSequence> animations = new ObjectMap<String, AnimationSequence>();

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

	public boolean hasAnimations() {
		return animations.size > 0;
	}

	public ObjectMap.Entries<String, AnimationSequence> getAnimations() {
		return animations.entries();
	}

	public static void scaleTexCoord(Vector2 texCoord, TextureRegion tile, Vector2 out) {
		out.x = scaleTexCoordU(texCoord.x, tile);
		out.y = scaleTexCoordV(texCoord.y, tile);
	}

	public static void scaleTexCoord(Vector2 texCoord, float srcMin, float srcMax, TextureRegion tile, Vector2 out) {
		out.x = scaleTexCoordU(texCoord.x, srcMin, srcMax, tile);
		out.y = scaleTexCoordV(texCoord.y, srcMin, srcMax, tile);
	}

	public static float scaleTexCoordU(float u, TextureRegion tile) {
		return MathHelpers.scaleRange(u, 0.0f, 1.0f, tile.getU(), tile.getU2());
	}

	public static float scaleTexCoordU(float u, float srcMinU, float srcMaxU, TextureRegion tile) {
		return MathHelpers.scaleRange(u, srcMinU, srcMaxU, tile.getU(), tile.getU2());
	}

	public static float scaleTexCoordV(float v, TextureRegion tile) {
		return MathHelpers.scaleRange(v, 0.0f, 1.0f, tile.getV(), tile.getV2());
	}

	public static float scaleTexCoordV(float v, float srcMinV, float srcMaxV, TextureRegion tile) {
		return MathHelpers.scaleRange(v, srcMinV, srcMaxV, tile.getV(), tile.getV2());
	}
}
