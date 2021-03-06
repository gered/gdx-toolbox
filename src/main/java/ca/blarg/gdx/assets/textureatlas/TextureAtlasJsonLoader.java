package ca.blarg.gdx.assets.textureatlas;

import ca.blarg.gdx.assets.AssetLoadingException;
import ca.blarg.gdx.assets.AssetLoadingUtils;
import ca.blarg.gdx.graphics.atlas.CustomGridTextureAtlas;
import ca.blarg.gdx.graphics.atlas.TextureAtlas;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Json;

class TextureAtlasJsonLoader {
	public static JsonTextureAtlas load(FileHandle file) {
		Json json = new Json();
		JsonTextureAtlas definition = json.fromJson(JsonTextureAtlas.class, file);

		definition.texture = AssetLoadingUtils.addPathIfNone(definition.texture, file.parent().path());

		return definition;
	}

	public static TextureAtlas create(FileHandle file, JsonTextureAtlas definition, AssetManager assetManager) {
		if (definition.texture == null)
			throw new AssetLoadingException(file.path(), "No texture specified.");
		if (definition.tiles == null || definition.tiles.size() == 0)
			throw new AssetLoadingException(file.path(), "No tiles defined.");

		Texture texture = assetManager.get(definition.texture, Texture.class);

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

		if (definition.materialMapping != null && definition.materialMapping.size() > 0) {
			for (int i = 0; i < definition.materialMapping.size(); ++i) {
				JsonMaterialMapping mapping = definition.materialMapping.get(i);
				atlas.materialTileMapping.add(mapping.name, atlas.get(mapping.tile), mapping.minU, mapping.maxU, mapping.minV, mapping.maxV);
			}
		}

		return atlas;
	}
}
