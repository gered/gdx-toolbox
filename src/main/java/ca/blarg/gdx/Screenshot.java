package ca.blarg.gdx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;

import java.nio.ByteBuffer;

public class Screenshot {
	static int counter = 1;

	public static void take(String createInPath) {
		String prefix;
		if (Strings.isNullOrEmpty(createInPath))
			prefix = "screenshot";
		else if (createInPath.endsWith("/"))
			prefix = createInPath + "screenshot";
		else
			prefix = createInPath + "/screenshot";

		try {
			FileHandle file;
			do {
				file = new FileHandle(prefix + counter + ".png");
				counter++;
			} while (file.exists());
			Pixmap pixmap = getFrameBufferPixmap(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
			PixmapIO.writePNG(file, pixmap);
			pixmap.dispose();
			Gdx.app.log("Screenshot", String.format("Saved screenshot to %s", file.file().getAbsolutePath()));
		} catch (Exception e) {
		}
	}

	public static void take() {
		take(null);
	}

	// Why this method instead of libgdx's own ScreenUtils.getFrameBufferPixmap() ?
	// This one converts to a 24bit (RGB) PNG which seems to look right a lot more consistently.
	// ScreenUtils.getFrameBufferPixmap() creates 32bit (RGBA) Pixmaps which tend to look a bit weird when
	// the image contains some partially transparent pixels (we would rather assume a black background for any
	// kind of partially transparent pixels for screenshot purposes).

	private static Pixmap getFrameBufferPixmap(int x, int y, int w, int h, boolean flipY) {
		Gdx.gl.glPixelStorei(GL20.GL_PACK_ALIGNMENT, 1);

		final Pixmap pixmap = new Pixmap(w, h, Pixmap.Format.RGB888);
		ByteBuffer pixels = pixmap.getPixels();
		Gdx.gl.glReadPixels(x, y, w, h, GL20.GL_RGB, GL20.GL_UNSIGNED_BYTE, pixels);

		final int numBytes = w * h * 3;
		byte[] lines = new byte[numBytes];
		if (flipY) {
			final int numBytesPerLine = w * 3;
			for (int i = 0; i < h; i++) {
				pixels.position((h - i - 1) * numBytesPerLine);
				pixels.get(lines, i * numBytesPerLine, numBytesPerLine);
			}
			pixels.clear();
			pixels.put(lines);
		} else {
			pixels.clear();
			pixels.get(lines);
		}

		return pixmap;
	}
}