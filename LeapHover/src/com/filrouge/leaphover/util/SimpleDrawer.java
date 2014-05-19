package com.filrouge.leaphover.util;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;

public class SimpleDrawer {
	/*
	 * PROPERTIES
	 */
	static protected ShapeRenderer renderer = new ShapeRenderer();

	/* 
	 * METHODS
	 */
	public static void drawLine(Camera camera, Vector2 begin, Vector2 end) {
		drawLine(camera, begin, end, Color.WHITE);
	}
	public static void drawLine(Camera camera, Vector2 begin, Vector2 end, Color color) {
		renderer.setProjectionMatrix(camera.combined);
		renderer.begin(ShapeType.Line);
	    renderer.setColor(color);
	    renderer.line(begin, end);
	    renderer.end();
	}
	
	public static void drawCircle(Camera camera, float x, float y, float radius, Color color) {
		renderer.setProjectionMatrix(camera.combined);
		renderer.begin(ShapeType.Filled);
	    renderer.setColor(color);
	    renderer.circle(x, y, radius);
	    renderer.end();
	}
	
	public static void drawCross(Camera camera, float x, float y, float radius, Color color) {
		renderer.setProjectionMatrix(camera.combined);
		renderer.begin(ShapeType.Line);
		renderer.setColor(color);

		Vector2 begin = new Vector2(x - radius, y - radius),
				end   = new Vector2(x + radius, y + radius);
		renderer.line(begin, end);
		begin = new Vector2(x - radius, y + radius);
		end   = new Vector2(x + radius, y - radius);
		renderer.line(begin, end);

		renderer.end();
	}

	/*
	 * GETTERS & SETTERS
	 */
}
