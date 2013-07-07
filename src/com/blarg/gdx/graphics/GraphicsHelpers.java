package com.blarg.gdx.graphics;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;

public final class GraphicsHelpers {
	public static void renderCoordinateSystemAxis(ShapeRenderer shapeRenderer, DelayedSpriteBatch spriteBatch, BitmapFont font, Vector3 origin) {
		renderCoordinateSystemAxis(shapeRenderer, spriteBatch, font, origin, 5.0f);
	}

	public static void renderCoordinateSystemAxis(ShapeRenderer shapeRenderer, DelayedSpriteBatch spriteBatch, BitmapFont font, Vector3 origin, float axisLength) {
		shapeRenderer.setColor(Color.WHITE);
		shapeRenderer.line(origin.x, origin.y, origin.z, origin.x + 0.0f, origin.y + axisLength, origin.z + 0.0f);
		spriteBatch.draw(font, origin.x + 0.0f, origin.y + axisLength, origin.z + 0.0f, 0.5f, "UP (+Y)", Color.WHITE);

		shapeRenderer.setColor(Color.BLACK);
		shapeRenderer.line(origin.x, origin.y, origin.z, origin.x + 0.0f, origin.y + -axisLength, origin.z + 0.0f);
		spriteBatch.draw(font, origin.x + 0.0f, origin.y + -axisLength, origin.z + 0.0f, 0.5f, "DOWN (-Y)", Color.BLACK);

		shapeRenderer.setColor(Color.GREEN);
		shapeRenderer.line(origin.x, origin.y, origin.z, origin.x + -axisLength, origin.y + 0.0f, origin.z + 0.0f);
		spriteBatch.draw(font, origin.x + -axisLength, origin.y + 0.0f, origin.z + 0.0f, 0.5f, "LEFT (-X)", Color.GREEN);

		shapeRenderer.setColor(Color.RED);
		shapeRenderer.line(origin.x, origin.y, origin.z, origin.x + axisLength, origin.y + 0.0f, origin.z + 0.0f);
		spriteBatch.draw(font, origin.x + axisLength, origin.y + 0.0f, origin.z + 0.0f, 0.5f, "RIGHT (+X)", Color.RED);

		shapeRenderer.setColor(Color.CYAN);
		shapeRenderer.line(origin.x, origin.y, origin.z, origin.x + 0.0f, origin.y + 0.0f, origin.z + -axisLength);
		spriteBatch.draw(font, origin.x + 0.0f, origin.y + 0.0f, origin.z + -axisLength, 0.5f, "FORWARD (-Z)", Color.CYAN);

		shapeRenderer.setColor(Color.YELLOW);
		shapeRenderer.line(origin.x, origin.y, origin.z, origin.x + 0.0f, origin.y + 0.0f, origin.z + axisLength);
		spriteBatch.draw(font, origin.x + 0.0f, origin.y + 0.0f, origin.z + axisLength, 0.5f, "BACKWARD (+Z)", Color.YELLOW);
	}
}
