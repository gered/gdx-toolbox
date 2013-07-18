package com.blarg.gdx.graphics.atlas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Json;

public final class TextureAtlasLoader {
	public static TextureAtlas load(FileHandle configFile) {
		Json json = new Json();
		JsonTextureAtlasDefinition config = json.fromJson(JsonTextureAtlasDefinition.class, configFile);

		if (config.texture == null)
			throw new RuntimeException("No texture specified.");
		if (config.tiles == null || config.tiles.size() == 0)
			throw new RuntimeException("No tiles defined.");

		// TODO: loading via AssetManager
		Texture texture = new Texture(Gdx.files.internal(config.texture));
		CustomGridTextureAtlas atlas = new CustomGridTextureAtlas(texture);

		for (int i = 0; i < config.tiles.size(); ++i) {
			JsonTextureAtlasTile tile = config.tiles.get(i);
			// TODO: parameter value error checking
			if (tile.autogrid)
				atlas.addGrid(tile.x, tile.y, tile.tileWidth, tile.tileHeight, tile.numTilesX, tile.numTilesY, tile.border);
			else
				atlas.add(tile.x, tile.y, tile.width, tile.height);
		}

		return atlas;
	}
}
