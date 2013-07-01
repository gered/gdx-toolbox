package com.blarg.gdx.graphics;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.ObjectMap;

public class SolidColorTextureCache implements Disposable
{
	ObjectMap<Integer, Texture> cache;

	public SolidColorTextureCache() {
		cache = new ObjectMap<Integer, Texture>();
	}

	public void onResume() {
	}

	public void onPause() {
	}

	public Texture get(Color color) {
		return get(Color.rgba8888(color));
	}

	public Texture get(float r, float g, float b, float a) {
		return get(Color.rgba8888(r, g, b, a));
	}

	public Texture get(int color) {
		Texture tex = cache.get(color);
		if (tex == null) {
			tex = create(color);
			cache.put(color, tex);
		}

		return tex;
	}

	private Texture create(int color) {
		Pixmap pixmap = new Pixmap(8, 8, Pixmap.Format.RGBA8888);
		pixmap.setColor(color);
		pixmap.fill();

		Texture result = new Texture(pixmap);
		return result;
	}

	public void dispose() {
		for (ObjectMap.Entries<Integer, Texture> i = cache.entries(); i.hasNext(); ) {
			ObjectMap.Entry<Integer, Texture> entry = i.next();
			entry.value.dispose();
		}
		cache.clear();
	}
}
