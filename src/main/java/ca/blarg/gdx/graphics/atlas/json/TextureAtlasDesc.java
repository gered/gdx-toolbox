package ca.blarg.gdx.graphics.atlas.json;

import ca.blarg.gdx.graphics.atlas.CustomGridTextureAtlas;
import ca.blarg.gdx.graphics.atlas.TextureAtlas;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Json;

public class TextureAtlasDesc {
	public JsonTextureAtlasDefinition load(FileHandle file) {
		Json json = new Json();
		return json.fromJson(JsonTextureAtlasDefinition.class, file);
	}

	public TextureAtlas create(JsonTextureAtlasDefinition definition) {
		return create(definition, null);
	}

	public TextureAtlas create(JsonTextureAtlasDefinition definition, AssetManager assetManager) {
		if (definition.texture == null)
			throw new RuntimeException("No texture specified.");
		if (definition.tiles == null || definition.tiles.size() == 0)
			throw new RuntimeException("No tiles defined.");

		Texture texture;
		if (assetManager != null)
			texture = assetManager.get(definition.texture, Texture.class);
		else
			texture = new Texture(Gdx.files.internal(definition.texture));

		CustomGridTextureAtlas atlas = new CustomGridTextureAtlas(texture);
		for (int i = 0; i < definition.tiles.size(); ++i) {
			JsonTextureAtlasTile tile = definition.tiles.get(i);
			// TODO: parameter value error checking
			if (tile.autogrid)
				atlas.addGrid(tile.x, tile.y, tile.tileWidth, tile.tileHeight, tile.numTilesX, tile.numTilesY, tile.border);
			else
				atlas.add(tile.x, tile.y, tile.width, tile.height);
		}

		if (definition.animations != null && definition.animations.size() > 0) {
			for (int i = 0; i < definition.animations.size(); ++i) {
				JsonTextureAtlasAnimation animation = definition.animations.get(i);
				atlas.addAnimation(animation.name, animation.tileIndex, animation.startIndex, animation.endIndex, animation.delay, animation.loop);
			}
		}

		return atlas;
	}
}
