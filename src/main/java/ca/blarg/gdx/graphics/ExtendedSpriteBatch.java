package ca.blarg.gdx.graphics;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector3;

/**
 * Various additional {@link SpriteBatch#draw} methods mostly for convenience. Most of these additional overloads
 * are to make drawing sprites at projected screen coordinates easier.
 *
 * Projected screen coordinate sprite rendering requires a perspective camera to project the 3D coordinates correctly.
 * A camera _must_ be set via {@link #begin(Camera)} (instead of using {@link #begin()}) before any of these draw
 * methods are called.
 */
public class ExtendedSpriteBatch extends SpriteBatch {
	static final float DEFAULT_COLOR = Color.WHITE.toFloatBits();

	static final Vector3 tmp1 = new Vector3();

	Camera projectionCamera;
	int pixelScale = 1;

	public ExtendedSpriteBatch() {
		super();
	}

	public ExtendedSpriteBatch(int size) {
		super(size);
	}

	public ExtendedSpriteBatch(int size, ShaderProgram defaultShader) {
		super(size, defaultShader);
	}

	public ExtendedSpriteBatch(int size, int buffers) {
		super(size, buffers);
	}

	public ExtendedSpriteBatch(int size, int buffers, ShaderProgram defaultShader) {
		super(size, buffers, defaultShader);
	}

	public void setPixelScale(int pixelScale) {
		if (pixelScale <= 0)
			throw new IllegalArgumentException();
		this.pixelScale = pixelScale;
	}

	@Override
	public void begin() {
		super.begin();
		setColor(DEFAULT_COLOR);
	}

	public void begin(Camera projectionCamera) {
		begin();
		this.projectionCamera = projectionCamera;
	}

	@Override
	public void end() {
		super.end();
		this.projectionCamera = null;
	}

	/**************************************************************************/

	public void draw(Texture texture, float x, float y, float z) {
		draw(texture, x, y, z, texture.getWidth(), texture.getHeight(), 0.0f, 1.0f, 1.0f, 0.0f);
	}

	public void draw(Texture texture, float x, float y, float z, float width, float height) {
		draw(texture, x, y, z, width, height, 0.0f, 1.0f, 1.0f, 0.0f);
	}

	public void draw(Texture texture, float x, float y, float z, float width, float height, float u, float v, float u2, float v2) {
		getProjectedCenteredPosition(x, y, z, width, height, tmp1);
		draw(texture, tmp1.x, tmp1.y, width, height, u, v, u2, v2);
	}

	public void draw(Texture texture, float x, float y, float z, int srcX, int srcY, int srcWidth, int srcHeight) {
		draw(texture, x, y, z, Math.abs(srcWidth), Math.abs(srcHeight), srcX, srcY, srcWidth, srcHeight);
	}

	public void draw(Texture texture, float x, float y, float z, float width, float height, int srcX, int srcY, int srcWidth, int srcHeight) {
		getProjectedCenteredPosition(x, y, z, width, height, tmp1);
		draw(texture, tmp1.x, tmp1.y, width, height, srcX, srcY, srcWidth, srcHeight, false, false);
	}

	/**************************************************************************/

	public void draw(TextureRegion region, float x, float y, float z) {
		draw(region, x, y, z, region.getRegionWidth(), region.getRegionHeight());
	}

	public void draw(TextureRegion region, float x, float y, float z, float width, float height) {
		getProjectedCenteredPosition(x, y, z, width, height, tmp1);
		draw(region, tmp1.x, tmp1.y, width, height);
	}

	/**************************************************************************/

	// NOTE: these are not going to be as fast as BitmapFont's draw() method due to it's use of BitmapFontCache
	//       probably don't need these ones with x,y only and just keep the x,y,z variants ...

	public void draw(BitmapFont font, float x, float y, CharSequence str) {
		draw(font, x, y, str, 1.0f);
	}

	public void draw(BitmapFont font, float x, float y, CharSequence str, float scale) {
		BitmapFont.BitmapFontData fontData = font.getData();
		Texture fontTexture = font.getRegion().getTexture();

		float currentX = x;
		float currentY = y;
		float lineHeight = fontData.lineHeight * scale;
		float spaceWidth = fontData.spaceWidth * scale;

		for (int i = 0; i < str.length(); ++i) {
			char c = str.charAt(i);

			// multiline support
			if (c == '\r')
				continue;  // can't render this anyway, and likely a '\n' is right behind ...
			if (c == '\n') {
				currentY -= lineHeight;
				currentX = x;
				continue;
			}

			BitmapFont.Glyph glyph = fontData.getGlyph(c);
			if (glyph == null) {
				// TODO: maybe rendering some special char here instead would be better?
				currentX += spaceWidth;
				continue;
			}

			float glyphWidth = ((float)glyph.width * scale);
			float glyphHeight = ((float)glyph.height * scale);
			float glyphXoffset = ((float)glyph.xoffset * scale);
			float glyphYoffset = ((float)glyph.yoffset * scale);
			draw(
					fontTexture,
					currentX + glyphXoffset, currentY + glyphYoffset,
					glyphWidth, glyphHeight,
					glyph.u, glyph.v, glyph.u2, glyph.v2
			);

			currentX += ((float)glyph.xadvance * scale);
		}
	}

	/**************************************************************************/

	public void draw(BitmapFont font, float x, float y, float z, CharSequence str) {
		draw(font, x, y, z, str, 1.0f);
	}

	public void draw(BitmapFont font, float x, float y, float z, CharSequence str, float scale) {
		BitmapFont.TextBounds bounds = font.getMultiLineBounds(str);
		float scaledBoundsWidth = bounds.width * scale;
		float scaledBoundsHeight = bounds.height * scale;

		getProjectedCenteredPosition(x, y, z, scaledBoundsWidth, scaledBoundsHeight, tmp1);

		// getProjectedCenteredPosition will actually center the Y coord incorrectly... we need to add
		// instead of subtract, but since that's already been done we need to add twice... (hence, *2)
		// TODO: this is the only place we need to do this right now, but if that changes, should
		//       probably just move the centering calcs to each method
		tmp1.y += (scaledBoundsHeight / 2) * 2.0f;

		draw(font, tmp1.x, tmp1.y, str, scale);
	}

	/**************************************************************************/

	public void getProjectedCenteredPosition(float x, float y, float z, float width, float height, Vector3 result) {
		if (projectionCamera == null)
			throw new IllegalStateException("Cannot project 3D coordinates to screen space when no projection camera is set.");

		result.set(x, y, z);
		projectionCamera.project(result);

		// screen coordinates will be unscaled, need to apply appropriate scaling
		result.x /= pixelScale;
		result.y /= pixelScale;

		// now center them on the bounds given
		result.x -= (width / 2.0f);
		result.y -= (height / 2.0f);
	}
}
