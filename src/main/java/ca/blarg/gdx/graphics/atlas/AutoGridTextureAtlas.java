package ca.blarg.gdx.graphics.atlas;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class AutoGridTextureAtlas extends TextureAtlas {
	public final int tileWidth;
	public final int tileHeight;
	public final int tileBorder;

	public AutoGridTextureAtlas(Texture texture, int tileWidth, int tileHeight, int tileBorder) {
		this(texture, tileWidth, tileHeight, tileBorder, TEXCOORD_EDGE_BLEED_OFFSET);
	}

	public AutoGridTextureAtlas(Texture texture, int tileWidth, int tileHeight, int tileBorder, MaterialTileMapping materialTileMapping) {
		this(texture, tileWidth, tileHeight, tileBorder, materialTileMapping , TEXCOORD_EDGE_BLEED_OFFSET);
	}

	public AutoGridTextureAtlas(Texture texture, int tileWidth, int tileHeight, int tileBorder, float edgeCoordOffset) {
		this(texture, tileWidth, tileHeight, tileBorder, null, edgeCoordOffset);
	}

	public AutoGridTextureAtlas(Texture texture, int tileWidth, int tileHeight, int tileBorder, MaterialTileMapping materialTileMapping, float edgeCoordOffset) {
		super(texture, materialTileMapping, edgeCoordOffset);
		this.tileWidth = tileWidth;
		this.tileHeight = tileHeight;
		this.tileBorder = tileBorder;

		generateGrid();
	}

	private void generateGrid() {
		int actualTileWidth = tileWidth + tileBorder;
		int actualTileHeight = tileHeight + tileBorder;

		int tilesX = (texture.getWidth() - tileBorder) / (tileWidth + tileBorder);
		int tilesY = (texture.getHeight() - tileBorder) / (tileHeight + tileBorder);

		for (int y = 0; y < tilesY; ++y)
		{
			for (int x = 0; x < tilesX; ++x)
			{
				// pixel location/dimensions
				int left = tileBorder + x * actualTileWidth;
				int top = tileBorder + y * actualTileHeight;
				int right = left + actualTileWidth - tileBorder;
				int bottom = top + actualTileHeight - tileBorder;

				// texture coordinates
				// HACK: subtract edgeCoordOffset from the bottom right edges to
				//       get around floating point rounding errors (adjacent tiles will
				//       slightly bleed in otherwise)
				float u = (left - tileBorder + edgeCoordOffset) / (float)texture.getWidth();
				float v = (top - tileBorder + edgeCoordOffset) / (float)texture.getHeight();
				float u2 = ((float)right + tileBorder - edgeCoordOffset) / (float)texture.getWidth();
				float v2 = ((float)bottom + tileBorder - edgeCoordOffset) / (float)texture.getHeight();

				TextureRegion tile = new TextureRegion();
				tile.setTexture(texture);
				tile.setRegion(u, v, u2, v2);
				tiles.add(tile);
			}
		}
	}
}
