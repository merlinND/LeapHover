package com.filrouge.leaphover.experiments;

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

	/*
	 * GETTERS & SETTERS
	 */
}
