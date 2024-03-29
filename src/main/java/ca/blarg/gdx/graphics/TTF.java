package ca.blarg.gdx.graphics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;

public final class TTF
{
	public static BitmapFont make(String file, int size) {
		FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal(file));
		FreeTypeFontGenerator.FreeTypeFontParameter params = new FreeTypeFontGenerator.FreeTypeFontParameter();
		params.size = size;
		BitmapFont result = generator.generateFont(params);
		generator.dispose();

		return result;
	}
}
