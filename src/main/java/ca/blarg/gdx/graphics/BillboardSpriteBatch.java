package ca.blarg.gdx.graphics;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch;
import com.badlogic.gdx.graphics.g3d.decals.DecalMaterial;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;

/***
 * <p>
 * Wrapper over libgdx's included {@link DecalBatch} with automatic easy management of
 * {@link Decal} objects. This is intended to make "on the fly" rendering of decals/billboards
 * as easy as rendering 2D sprites is with SpriteBatch / DelayedSpriteBatch.
 * </p>
 */
public class BillboardSpriteBatch implements Disposable {
	public enum Type {
		Spherical,
		Cylindrical,
		ScreenAligned
	}

	static final Vector3 temp = new Vector3();
	static final int CAPACITY_INCREMENT = 128;

	Array<Decal> sprites;
	int pointer;
	boolean hasBegun;
	final AlphaTestCameraGroupStrategy groupStrategy;
	final DecalBatch decalBatch;

	public BillboardSpriteBatch() {
		sprites = new Array<Decal>(true, CAPACITY_INCREMENT, Decal.class);
		pointer = 0;
		addNewSprites(CAPACITY_INCREMENT);

		hasBegun = false;
		groupStrategy = new AlphaTestCameraGroupStrategy(null);
		decalBatch = new DecalBatch(groupStrategy);
	}

	public void begin(Camera camera) {
		if (hasBegun)
			throw new IllegalStateException("Cannot be called within an existing begin/end block.");
		if (camera == null)
			throw new IllegalArgumentException();

		groupStrategy.setCamera(camera);
		hasBegun = true;
		pointer = 0;
	}

	public void draw(Type type, Texture texture, float x, float y, float z, float width, float height) {
		draw(type, texture, x, y, z, width, height, Color.WHITE);
	}

	public void draw(Type type, Texture texture, float x, float y, float z, float width, float height, Color tint) {
		Decal sprite = nextUsable();
		setTexture(sprite, texture);
		sprite.setWidth(width);
		sprite.setHeight(height);
		sprite.setPosition(x, y, z);
		setTintAndBlending(sprite, tint);
		setRotation(sprite, type);
	}

	public void draw(Type type, Texture texture, float x, float y, float z, float width, float height, float u, float v, float u2, float v2) {
		draw(type, texture, x, y, z, width, height, u, v, u2, v2, Color.WHITE);
	}

	public void draw(Type type, Texture texture, float x, float y, float z, float width, float height, float u, float v, float u2, float v2, Color tint) {
		Decal sprite = nextUsable();
		setTexture(sprite, texture, u, v, u2, v2);
		sprite.setWidth(width);
		sprite.setHeight(height);
		sprite.setPosition(x, y, z);
		setTintAndBlending(sprite, tint);
		setRotation(sprite, type);
	}

	public void draw(Type type, Texture texture, float x, float y, float z, float width, float height, int srcX, int srcY, int srcWidth, int srcHeight) {
		draw(type, texture, x, y, z, width, height, srcX, srcY, srcWidth, srcHeight, Color.WHITE);
	}

	public void draw(Type type, Texture texture, float x, float y, float z, float width, float height, int srcX, int srcY, int srcWidth, int srcHeight, Color tint) {
		Decal sprite = nextUsable();
		setTexture(sprite, texture, srcX, srcY, srcWidth, srcHeight);
		sprite.setWidth(width);
		sprite.setHeight(height);
		sprite.setPosition(x, y, z);
		setTintAndBlending(sprite, tint);
		setRotation(sprite, type);
	}

	public void draw(Type type, TextureRegion region, float x, float y, float z, float width, float height) {
		draw(type, region, x, y, z, width, height, Color.WHITE);
	}

	public void draw(Type type, TextureRegion region, float x, float y, float z, float width, float height, Color tint) {
		Decal sprite = nextUsable();
		setTexture(sprite, region);
		sprite.setWidth(width);
		sprite.setHeight(height);
		sprite.setPosition(x, y, z);
		setTintAndBlending(sprite, tint);
		setRotation(sprite, type);
	}

	public void flush() {
		if (!hasBegun)
			throw new IllegalStateException("Cannot call outside of a begin/end block.");

		for (int i = 0; i < pointer; ++i) {
			Decal sprite = sprites.items[i];
			decalBatch.add(sprite);
		}
		decalBatch.flush();

		// don't want to hang on to Texture object references
		// TODO: is this overkill? does this really make a big difference?
		for (int i = 0; i < pointer; ++i)
			sprites.items[i].getTextureRegion().setTexture(null);

		pointer = 0;
	}

	public void end() {
		if (!hasBegun)
			throw new IllegalStateException("Must call begin() first.");

		flush();

		hasBegun = false;
	}

	private void setTexture(Decal decal, Texture texture) {
		TextureRegion textureRegion = decal.getTextureRegion();
		textureRegion.setRegion(texture);
		decal.setTextureRegion(textureRegion);
	}

	private void setTexture(Decal decal, Texture texture, float u, float v, float u2, float v2) {
		TextureRegion textureRegion = decal.getTextureRegion();
		textureRegion.setRegion(texture);
		textureRegion.setRegion(u, v, u2, v2);
		decal.setTextureRegion(textureRegion);
	}

	private void setTexture(Decal decal, Texture texture, int srcX, int srcY, int srcWidth, int srcHeight) {
		TextureRegion textureRegion = decal.getTextureRegion();
		textureRegion.setRegion(texture);
		textureRegion.setRegion(srcX, srcY, srcWidth, srcHeight);
		decal.setTextureRegion(textureRegion);
	}

	private void setTexture(Decal decal, TextureRegion srcRegion) {
		// we don't want to keep a reference to the source region object (or replace the TextureRegion object
		// reference on the Decal object with a different one) so that in flush() we can, without side-effect,
		// clear the Texture object reference

		TextureRegion dstRegion = decal.getTextureRegion();
		dstRegion.setRegion(srcRegion);
		decal.setTextureRegion(dstRegion);
	}

	private void setRotation(Decal decal, Type type) {
		Camera camera = groupStrategy.getCamera();

		switch (type) {
			case Spherical:
				decal.lookAt(camera.position, Vector3.Y);
				break;

			case Cylindrical:
				temp.set(camera.position)
					.sub(decal.getPosition())
					.nor();
				temp.y = 0.0f;
				decal.setRotation(temp, Vector3.Y);
				break;

			case ScreenAligned:
				temp.set(camera.direction)
					.scl(-1.0f, -1.0f, -1.0f); // opposite direction to the camera facing dir (point directly out of the screen)
				decal.setRotation(temp, Vector3.Y);
				break;
		}
	}

	private void setTintAndBlending(Decal decal, Color tint) {
		int srcFactor = DecalMaterial.NO_BLEND;
		int destFactor = DecalMaterial.NO_BLEND;

		if (tint.a > 0.0f && tint.a < 1.0f) {
			srcFactor = GL20.GL_SRC_ALPHA;
			destFactor = GL20.GL_ONE_MINUS_SRC_ALPHA;
		}

		decal.setColor(tint);
		decal.setBlending(srcFactor, destFactor);
	}

	private void increaseCapacity() {
		int newCapacity = sprites.items.length + CAPACITY_INCREMENT;
		sprites.ensureCapacity(newCapacity);
		addNewSprites(CAPACITY_INCREMENT);
	}

	private void addNewSprites(int count) {
		for (int i = 0; i < count; ++i)
			sprites.add(Decal.newDecal(new TextureRegion()));
	}

	private int getRemainingSpace() {
		return sprites.size - pointer;
	}

	private Decal nextUsable() {
		if (getRemainingSpace() == 0)
			increaseCapacity();

		Decal usable = sprites.items[pointer];
		pointer++;
		return usable;
	}

	@Override
	public void dispose() {
		decalBatch.dispose();
		groupStrategy.dispose();
	}
}
