package com.blarg.gdx.graphics;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;

public final class GraphicsHelpers {
	public static void renderCoordinateSystemAxis(ShapeRenderer shapeRenderer, ExtendedSpriteBatch spriteBatch, Camera projectionCamera, BitmapFont font, Vector3 origin) {
		renderCoordinateSystemAxis(shapeRenderer, spriteBatch, projectionCamera, font, origin, 5.0f);
	}

	public static void renderCoordinateSystemAxis(ShapeRenderer shapeRenderer, ExtendedSpriteBatch spriteBatch, Camera projectionCamera, BitmapFont font, Vector3 origin, float axisLength) {
		shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
		spriteBatch.begin(projectionCamera);

		shapeRenderer.setColor(Color.WHITE);
		shapeRenderer.line(origin.x, origin.y, origin.z, origin.x + 0.0f, origin.y + axisLength, origin.z + 0.0f);
		spriteBatch.setColor(Color.WHITE);
		spriteBatch.draw(font, origin.x + 0.0f, origin.y + axisLength, origin.z + 0.0f, "UP (+Y)", 0.5f);

		shapeRenderer.setColor(Color.BLACK);
		shapeRenderer.line(origin.x, origin.y, origin.z, origin.x + 0.0f, origin.y + -axisLength, origin.z + 0.0f);
		spriteBatch.setColor(Color.BLACK);
		spriteBatch.draw(font, origin.x + 0.0f, origin.y + -axisLength, origin.z + 0.0f, "DOWN (-Y)", 0.5f);

		shapeRenderer.setColor(Color.GREEN);
		shapeRenderer.line(origin.x, origin.y, origin.z, origin.x + -axisLength, origin.y + 0.0f, origin.z + 0.0f);
		spriteBatch.setColor(Color.GREEN);
		spriteBatch.draw(font, origin.x + -axisLength, origin.y + 0.0f, origin.z + 0.0f, "LEFT (-X)", 0.5f);

		shapeRenderer.setColor(Color.RED);
		shapeRenderer.line(origin.x, origin.y, origin.z, origin.x + axisLength, origin.y + 0.0f, origin.z + 0.0f);
		spriteBatch.setColor(Color.RED);
		spriteBatch.draw(font, origin.x + axisLength, origin.y + 0.0f, origin.z + 0.0f, "RIGHT (+X)", 0.5f);

		shapeRenderer.setColor(Color.CYAN);
		shapeRenderer.line(origin.x, origin.y, origin.z, origin.x + 0.0f, origin.y + 0.0f, origin.z + -axisLength);
		spriteBatch.setColor(Color.CYAN);
		spriteBatch.draw(font, origin.x + 0.0f, origin.y + 0.0f, origin.z + -axisLength, "FORWARD (-Z)", 0.5f);

		shapeRenderer.setColor(Color.YELLOW);
		shapeRenderer.line(origin.x, origin.y, origin.z, origin.x + 0.0f, origin.y + 0.0f, origin.z + axisLength);
		spriteBatch.setColor(Color.YELLOW);
		spriteBatch.draw(font, origin.x + 0.0f, origin.y + 0.0f, origin.z + axisLength, "BACKWARD (+Z)", 0.5f);

		spriteBatch.end();
		shapeRenderer.end();
	}
}
