package ca.blarg.gdx.graphics.atlas;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ObjectMap;

public class MaterialTileMapping {
	public class TileTexture {
		public TextureRegion region;
		public float materialMinU = 0.0f;
		public float materialMaxU = 1.0f;
		public float materialMinV = 0.0f;
		public float materialMaxV = 1.0f;
	}

	final ObjectMap<String, TileTexture> mappings;
	public final TextureAtlas atlas;

	public MaterialTileMapping(TextureAtlas atlas) {
		if (atlas == null)
			throw new IllegalArgumentException("atlas cannot be null.");

		this.mappings = new ObjectMap<String, TileTexture>();
		this.atlas = atlas;
	}

	public MaterialTileMapping add(String materialName, TextureRegion region) {
		if (mappings.containsKey(materialName))
			throw new UnsupportedOperationException(String.format("Material '%s' already exists.", materialName));

		TileTexture tileTexture = new TileTexture();
		tileTexture.region = region;
		mappings.put(materialName, tileTexture);
		return this;
	}

	public MaterialTileMapping add(String materialName, TextureRegion region, float materialMinU, float materialMaxU, float materialMinV, float materialMaxV) {
		if (mappings.containsKey(materialName))
			throw new UnsupportedOperationException(String.format("Material '%s' already exists.", materialName));

		TileTexture tileTexture = new TileTexture();
		tileTexture.region = region;
		tileTexture.materialMinU = materialMinU;
		tileTexture.materialMaxU = materialMaxU;
		tileTexture.materialMinV = materialMinV;
		tileTexture.materialMaxV = materialMaxV;
		mappings.put(materialName, tileTexture);
		return this;
	}

	public TileTexture get(String materialName) {
		return mappings.get(materialName);
	}

	public boolean hasMappings() {
		return mappings.size > 0;
	}

	public void scaleUV(String materialName, Vector2 srcTexCoord, Vector2 out) {
		TileTexture tileTexture = mappings.get(materialName);
		if (tileTexture == null)
			throw new IllegalArgumentException("No matching material.");

		out.x = TextureAtlas.scaleTexCoordU(srcTexCoord.x, tileTexture.materialMinU, tileTexture.materialMaxU, tileTexture.region);
		out.y = TextureAtlas.scaleTexCoordV(srcTexCoord.y, tileTexture.materialMinV, tileTexture.materialMaxV, tileTexture.region);
	}

	public void scaleUV(String materialName, float srcU, float srcV, Vector2 out) {
		TileTexture tileTexture = mappings.get(materialName);
		if (tileTexture == null)
			throw new IllegalArgumentException("No matching material.");

		out.x = TextureAtlas.scaleTexCoordU(srcU, tileTexture.materialMinU, tileTexture.materialMaxU, tileTexture.region);
		out.y = TextureAtlas.scaleTexCoordV(srcV, tileTexture.materialMinV, tileTexture.materialMaxV, tileTexture.region);
	}

	public void clear() {
		mappings.clear();
	}
}
