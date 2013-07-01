package com.blarg.gdx.graphics;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch;
import com.badlogic.gdx.utils.Array;

/***
 * <p>
 * Wrapper over libgdx's included {@link DecalBatch} with automatic easy management of
 * {@link Decal} objects. This is intended to make "on the fly" rendering of decals/billboards
 * as easy as rendering 2D sprites is with SpriteBatch / DelayedSpriteBatch.
 * </p>
 */
public class BillboardSpriteBatch {
	static final int CAPACITY_INCREMENT = 128;

	Array<Decal> sprites;
	int pointer;
	boolean hasBegun;
	DecalBatch decalBatch;

	public BillboardSpriteBatch() {
		sprites = new Array<Decal>(true, CAPACITY_INCREMENT, Decal.class);
		pointer = 0;
		addNewSprites(CAPACITY_INCREMENT);

		hasBegun = false;
		decalBatch = null;
	}

	public void begin(DecalBatch decalBatch) {
		if (hasBegun)
			throw new IllegalStateException("Cannot be called within an existing begin/end block.");
		if (decalBatch == null)
			throw new IllegalArgumentException();

		this.decalBatch = decalBatch;
		hasBegun = true;
		pointer = 0;
	}

	public void flush() {
		if (!hasBegun)
			throw new IllegalStateException("Cannot call outside of a begin/end block.");

		// TODO: render decals with DecalBatch

		pointer = 0;
	}

	public void end() {
		if (!hasBegun)
			throw new IllegalStateException("Must call begin() first.");

		flush();

		hasBegun = false;
		decalBatch = null;    // don't need to hold on to this particular reference anymore
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
