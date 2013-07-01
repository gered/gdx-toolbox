package com.blarg.gdx.graphics;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch;
import com.badlogic.gdx.graphics.g3d.decals.DecalMaterial;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

/***
 * <p>
 * Wrapper over libgdx's included {@link DecalBatch} with automatic easy management of
 * {@link Decal} objects. This is intended to make "on the fly" rendering of decals/billboards
 * as easy as rendering 2D sprites is with SpriteBatch / DelayedSpriteBatch.
 * </p>
 */
public class BillboardSpriteBatch {
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
	DecalBatch decalBatch;
	Camera camera;

	public BillboardSpriteBatch() {
		sprites = new Array<Decal>(true, CAPACITY_INCREMENT, Decal.class);
		pointer = 0;
		addNewSprites(CAPACITY_INCREMENT);

		hasBegun = false;
		decalBatch = null;
	}

	public void begin(DecalBatch decalBatch, Camera camera) {
		if (hasBegun)
			throw new IllegalStateException("Cannot be called within an existing begin/end block.");
		if (decalBatch == null)
			throw new IllegalArgumentException();
		if (camera == null)
			throw new IllegalArgumentException();

		this.decalBatch = decalBatch;
		this.camera = camera;
		hasBegun = true;
		pointer = 0;
	}

	public void draw(Type type, Texture texture, float x, float y, float z, float width, float height) {
		draw(type, texture, x, y, z, width, height, Color.WHITE);
	}

	public void draw(Type type, Texture texture, float x, float y, float z, float width, float height, Color tint) {
		Decal sprite = nextUsable();
		TextureRegion textureRegion = sprite.getTextureRegion();
		textureRegion.setRegion(texture);
		sprite.setTextureRegion(textureRegion);
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
		TextureRegion textureRegion = sprite.getTextureRegion();
		textureRegion.setRegion(texture);
		textureRegion.setRegion(u, v, u2, v2);
		sprite.setTextureRegion(textureRegion);
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
		// don't need to hold on to these references anymore
		decalBatch = null;
		camera = null;
	}

	private void setRotation(Decal decal, Type type) {
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
			srcFactor = GL10.GL_SRC_ALPHA;
			destFactor = GL10.GL_ONE_MINUS_SRC_ALPHA;
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
}
