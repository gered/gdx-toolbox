package com.blarg.gdx.graphics.atlas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Json;
import com.blarg.gdx.io.FileHelpers;

public final class TextureAtlasLoader {
	public static TextureAtlas load(String configFile) {
		return load(configFile, null);
	}

	public static TextureAtlas load(String configFile, TextureAtlasAnimator animator) {
		return load(Gdx.files.internal(configFile), animator);
	}

	public static TextureAtlas load(FileHandle configFile) {
		return load(configFile, null);
	}

	public static TextureAtlas load(FileHandle configFile, TextureAtlasAnimator animator) {
		String path = FileHelpers.getPath(configFile);

		Json json = new Json();
		JsonTextureAtlasDefinition config = json.fromJson(JsonTextureAtlasDefinition.class, configFile);

		if (config.texture == null)
			throw new RuntimeException("No texture specified.");
		if (config.tiles == null || config.tiles.size() == 0)
			throw new RuntimeException("No tiles defined.");

		// TODO: loading via AssetManager
		Texture texture = new Texture(FileHelpers.open(configFile.type(), path + config.texture));
		CustomGridTextureAtlas atlas = new CustomGridTextureAtlas(texture);

		for (int i = 0; i < config.tiles.size(); ++i) {
			JsonTextureAtlasTile tile = config.tiles.get(i);
			// TODO: parameter value error checking
			if (tile.autogrid)
				atlas.addGrid(tile.x, tile.y, tile.tileWidth, tile.tileHeight, tile.numTilesX, tile.numTilesY, tile.border);
			else
				atlas.add(tile.x, tile.y, tile.width, tile.height);
		}

		if (config.animations != null && config.animations.size() > 0 && animator != null) {
			for (int i = 0; i < config.animations.size(); ++i) {
				JsonTextureAtlasAnimation animation = config.animations.get(i);
				// TODO: parameter value error checking
				animator.addSequence(animation.name,
				                     atlas,
				                     animation.tileIndex,
				                     animation.startIndex,
				                     animation.endIndex,
				                     animation.delay,
				                     animation.loop);
			}
		}

		return atlas;
	}
}
