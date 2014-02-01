package com.filrouge.leaphover.experiments;

import java.util.ArrayList;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.EdgeShape;

public class HillGenerator {
	/*
	 * PROPERTIES
	 */
	protected static final int MIN_CONTROL_POINTS = 3;
	protected static final int MAX_CONTROL_POINTS = 15;
	
	/* 
	 * METHODS
	 */
	
	/**
	 * Add fixtures to the given body so as to make a hill (no underside).
	 * The hill spans from (-width/2) to (width/2) horizontally
	 * and from 0 to height vertically.
	 * @param body
	 * @param width 
	 * @param height
	 */
	public static void makeHill(Body body, float width, float height) {
		makeHill(body, width, height, 0);
	}
	
	/**
	 * Make a hill with an underside to create a thickness.
	 * @see makeHill(Body body, float width, float height)
	 * @param body
	 * @param width 
	 * @param height
	 */
	public static void makeHill(Body body, float width, float height, float thickness) {
		
		// Test control points (at least this.degree)
		ArrayList<Vector2> controlPoints = getRandomControlPoints(-width / 2f, width / 2f, 0, height);
		
		// TODO : choose the number of samples in a smart way (proportional to the number of control points ?)
		int n = 100;
		BSCurve curve = new BSCurve(controlPoints);
		Vector2[] vertices = curve.getSamples(n);
		
		EdgeShape shape = new EdgeShape();
		Vector2 previous = vertices[0];
		
		// Top side
		for(int i = 1; i < n; ++i) {
			shape.set(previous, vertices[i]);
			body.createFixture(shape, 1);
			
			previous = vertices[i];
		}
		// Below side
		if (thickness > 0f) {
			Vector2 thicknessVector = new Vector2(0, - thickness);
			previous.add(thicknessVector);
			for(int i = n - 2; i >= 0; --i) {
				vertices[i].add(thicknessVector);
				shape.set(previous, vertices[i]);
				body.createFixture(shape, 1);
				
				previous = vertices[i];
			}
		}
		shape.dispose();
	}
	
	/**
	 * Return a random number of control points.
	 * The number of control points is comprised between MIN_CONTROL_POINTS and MAX_CONTROL_POINTS
	 * @see getRandomControlPoints(int n, float xMin, float xMax, float yMin, float yMax)
	 * @param xMin
	 * @param xMax
	 * @param yMin
	 * @param yMax
	 * @return
	 */
	protected static ArrayList<Vector2> getRandomControlPoints(float xMin, float xMax, float yMin, float yMax) {
		return getRandomControlPoints((int) random(MIN_CONTROL_POINTS, MAX_CONTROL_POINTS), xMin, xMax, yMin, yMax);
	}
	/**
	 * Return homogeneously horizontally spaced control points. 
	 * The vertical position is chosen at random between yMin and yMax for each point.
	 * @param n The number of control points to generate
	 * @param xMin The horizontal position of the first control point
	 * @param xMax The horizontal position of the last control point
	 * @param yMin
	 * @param yMax
	 * @return
	 */
	protected static ArrayList<Vector2> getRandomControlPoints(int n, float xMin, float xMax, float yMin, float yMax) {
		ArrayList<Vector2> controlPoints = new ArrayList<Vector2>();
		
		
		// Generate n random control points (equally spaced)
		Vector2 current = new Vector2();
		float delta = 1 / (float)(n - 1);
		float alpha = 0;		
		for (int i = 0; i < n; ++i) {
			current.set(progress(xMin, xMax, alpha), random(yMin, yMax));
			alpha += delta;
			
			// The first and last point must be interpolated strongly
			controlPoints.add(current.cpy());
			if (i == 0 || i == (n-1)) {
				controlPoints.add(current.cpy());
				controlPoints.add(current.cpy());
			}
		}
		
		return controlPoints;
	}
	
	/**
	 * @param min
	 * @param max
	 * @return A random floating point number between min and max
	 * @uses Math.random()
	 */
	protected static float random(float min, float max) {
		return (float)(Math.random() * (max - min)) + min;
	}
	
	/**
	 * @param min
	 * @param max
	 * @param progress
	 * @return The number corresponding to <code>progress</code> percent of the given interval
	 */
	protected static float progress(float min, float max, float progress) {
		return progress * (max - min) + min;
	}
	
	/*
	 * GETTERS & SETTERS
	 */
}
