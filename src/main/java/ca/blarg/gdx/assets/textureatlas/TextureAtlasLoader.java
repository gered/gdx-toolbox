package ca.blarg.gdx.assets.textureatlas;

import ca.blarg.gdx.graphics.atlas.TextureAtlas;
import ca.blarg.gdx.graphics.atlas.TextureAtlasAnimator;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.AsynchronousAssetLoader;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Array;

@SuppressWarnings("unchecked")
public class TextureAtlasLoader extends AsynchronousAssetLoader<TextureAtlas, TextureAtlasLoader.TextureAtlasParameter> {
	public TextureAtlasLoader(FileHandleResolver resolver) {
		super(resolver);
	}

	JsonTextureAtlasDefinition definition;
	TextureAtlas atlas;

	@Override
	public Array<AssetDescriptor> getDependencies(String fileName, FileHandle file, TextureAtlasParameter parameter) {
		definition = TextureAtlasJsonLoader.load(file);
		Array<AssetDescriptor> deps = new Array<AssetDescriptor>();
		deps.add(new AssetDescriptor(definition.texture, Texture.class));
		return deps;
	}

	@Override
	public void loadAsync(AssetManager manager, String fileName, FileHandle file, TextureAtlasParameter parameter) {
		atlas = TextureAtlasJsonLoader.create(file, definition, manager);
	}

	@Override
	public TextureAtlas loadSync(AssetManager manager, String fileName, FileHandle file, TextureAtlasParameter parameter) {
		if (parameter != null && parameter.animator != null)
			parameter.animator.add(atlas);
		return atlas;
	}

	public static class TextureAtlasParameter extends AssetLoaderParameters<TextureAtlas> {
		public TextureAtlasAnimator animator;
	}
}
