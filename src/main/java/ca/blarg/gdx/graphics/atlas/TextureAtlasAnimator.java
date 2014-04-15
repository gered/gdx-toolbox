package ca.blarg.gdx.graphics.atlas;

import ca.blarg.gdx.graphics.GraphicsHelpers;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.TextureData;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.ObjectMap;

/**
 * Animation manager class for animation sequences made up of tiles in a {@link TextureAtlas}. This class should only
 * be used as a last resort when your rendering needs to animate TextureAtlas tiles as it performs animation by
 * modifying the atlas's OpenGL texture every time an animation frame has to be changed. For most types of 2D rendering,
 * there are better and faster ways of doing performing animation.
 *
 * The real intended use case is when a large 3D mesh is being rendered and textured with more then one different tile
 * from a TextureAtlas where scanning through each vertex and animating UV coords is impractical or where doing the
 * same type of UV coordinate animation in a shader is also impractical.
 */
public class TextureAtlasAnimator implements Disposable {
	ObjectMap<String, AnimationSequence> animations;

	public TextureAtlasAnimator() {
		animations = new ObjectMap<String, AnimationSequence>();
	}

	public void add(TextureAtlas atlas) {
		if (atlas == null)
			throw new IllegalArgumentException("atlas cannot be null.");

		for (ObjectMap.Entry<String, TextureAtlas.Animation> i : atlas.getAnimations()) {
			TextureAtlas.Animation animation = i.value;
			addSequence(i.key, atlas, animation.destTileIndex, animation.start, animation.stop, animation.delay, animation.loop);
		}
	}

	public void addAllAtlases(AssetManager assetManager) {
		for (String filename : assetManager.getAssetNames()) {
			if (TextureAtlas.class.isAssignableFrom(assetManager.getAssetType(filename))) {
				TextureAtlas atlas = assetManager.get(filename, TextureAtlas.class);
				add(atlas);
			}
		}
	}

	public void addSequence(String name, TextureAtlas atlas, int tileToBeAnimated, int start, int stop, float delay, boolean loop) {
		if (animations.containsKey(name))
			throw new UnsupportedOperationException("Duplicate animation sequence name.");
		if (atlas.texture.getTextureData().getType() != TextureData.TextureDataType.Pixmap)
			throw new UnsupportedOperationException("This only works with Textures backed by Pixmap texture data.");

		AnimationSequence sequence = new AnimationSequence();
		sequence.atlas = atlas;
		sequence.animatingIndex = tileToBeAnimated;
		sequence.start = start;
		sequence.stop = stop;
		sequence.delay = delay;
		sequence.isAnimating = true;
		sequence.loop = loop;
		sequence.frames = new Pixmap[sequence.getNumFrames()];
		sequence.current = sequence.start;
		sequence.currentFrameTime = 0.0f;

		// build up some CPU-side cache's of image data from the original TextureAtlas texture. we grab the image data
		// for the tile we're placing animation frames to so we can restore the original texture when/if needed. and we
		// grab each of the tiles that appear in the animation sequence so we can quickly upload it to the texture when
		// we're at that frame

		// grab the TextureAtlas texture's image data
		TextureData textureData = atlas.texture.getTextureData();
		if (!textureData.isPrepared())
			textureData.prepare();
		Pixmap textureImage = textureData.consumePixmap();

		// copy the image data for the destination animation tile region
		TextureRegion tileRegion = atlas.get(tileToBeAnimated);
		sequence.originalAnimatingTile = new Pixmap(tileRegion.getRegionWidth(), tileRegion.getRegionHeight(), textureData.getFormat());
		sequence.originalAnimatingTile.drawPixmap(
				textureImage, 0, 0,
				tileRegion.getRegionX(), tileRegion.getRegionY(), tileRegion.getRegionWidth(), tileRegion.getRegionHeight()
		);

		// copy image data for each of the animation sequence tiles (from "start" to and including "stop")
		for (int i = 0; i < sequence.getNumFrames(); ++i) {
			TextureRegion tile = atlas.get(i + sequence.start);
			sequence.frames[i] = new Pixmap(tileRegion.getRegionWidth(), tileRegion.getRegionHeight(), textureData.getFormat());
			sequence.frames[i].drawPixmap(
					textureImage, 0, 0,
					tile.getRegionX(), tile.getRegionY(), tile.getRegionWidth(), tile.getRegionHeight()
			);
		}

		// may need to dispose of the TextureAtlas's texture Pixmap that we obtained (depending on TextureData implementation)
		if (textureData.disposePixmap())
			textureImage.dispose();

		// all good!
		animations.put(name, sequence);
	}

	public void resetSequence(String name) {
		AnimationSequence sequence = animations.get(name);
		if (sequence == null)
			throw new RuntimeException("No sequence with that name.");

		sequence.isAnimating = true;
		sequence.current = sequence.start;
		sequence.currentFrameTime = 0.0f;

		updateTextureWithCurrentTileFrame(sequence);
	}

	public void stopSequence(String name, boolean restoreOriginalTile) {
		AnimationSequence sequence = animations.get(name);
		if (sequence == null)
			throw new RuntimeException("No sequence with that name.");

		sequence.isAnimating = false;
		sequence.current = sequence.stop;
		sequence.currentFrameTime = 0.0f;

		if (restoreOriginalTile)
			restoreTextureWithOriginalTile(sequence);
		else
			updateTextureWithCurrentTileFrame(sequence);
	}

	public void enableSequence(String name, boolean enable) {
		AnimationSequence sequence = animations.get(name);
		if (sequence == null)
			throw new RuntimeException("No sequence with that name.");

		sequence.isAnimating = enable;
		sequence.currentFrameTime = 0.0f;

		updateTextureWithCurrentTileFrame(sequence);
	}

	public void onUpdate(float delta) {
		for (ObjectMap.Entry<String, AnimationSequence> i : animations.entries()) {
			AnimationSequence sequence = i.value;
			if (!sequence.isAnimationFinished() && sequence.isAnimating) {
				sequence.currentFrameTime += delta;
				if (sequence.currentFrameTime >= sequence.delay) {
					// move to the next frame
					sequence.currentFrameTime = 0.0f;
					++sequence.current;
					if (sequence.current > sequence.stop)
							sequence.current = sequence.start;

					updateTextureWithCurrentTileFrame(sequence);
				}
			}
		}
	}

	public void onResume() {
		for (ObjectMap.Entry<String, AnimationSequence> i : animations.entries())
			updateTextureWithCurrentTileFrame(i.value);
	}

	private void updateTextureWithCurrentTileFrame(AnimationSequence sequence) {
		int frameIndex = sequence.current - sequence.start;
		Pixmap frameImage = sequence.frames[frameIndex];
		TextureRegion tile = sequence.atlas.get(sequence.animatingIndex);
		GraphicsHelpers.drawToTexture(sequence.atlas.texture, frameImage, tile.getRegionX(), tile.getRegionY());
	}

	private void restoreTextureWithOriginalTile(AnimationSequence sequence) {
		TextureRegion tile = sequence.atlas.get(sequence.animatingIndex);
		GraphicsHelpers.drawToTexture(sequence.atlas.texture, sequence.originalAnimatingTile, tile.getRegionX(), tile.getRegionY());
	}

	public void reset() {
		for (ObjectMap.Entry<String, AnimationSequence> i : animations.entries()) {
			restoreTextureWithOriginalTile(i.value);
			i.value.dispose();
		}
		animations.clear();
	}

	@Override
	public void dispose() {
		reset();
	}
}
