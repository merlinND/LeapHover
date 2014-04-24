package com.filrouge.leaphover.level;

import java.util.ArrayList;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.filrouge.leaphover.util.BSCurve;

public class HillGenerator {
	/*
	 * PROPERTIES
	 */
	protected static final int NUMBER_OF_SAMPLES = 50;
	protected static final int MIN_CONTROL_POINTS = 3;
	protected static final int MAX_CONTROL_POINTS = 500;
	/**
	 * Roughness of the generated hills.
	 * The smaller this number, the rougher the hills.
	 * TODO: make parametrizable
	 */
	protected static float roughness = 0.7f;
	
	/* 
	 * METHODS
	 */

	/**
	 * Add fixtures to the given body so as to make a hill (no underside).
	 * The hill spans from 0 to width horizontally
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
	 * @see #makeHill(Body body, float width, float height)
	 * @param body
	 * @param width
	 * @param height
	 */
	public static void makeHill(Body body, float width, float height, float thickness) {

		// Test control points (at least this.degree)
		ArrayList<Vector2> controlPoints = getRandomControlPoints(0, width, 0, height);

		// TODO : choose the number of samples in a smart way (proportional to the number of control points ?)
		int n = NUMBER_OF_SAMPLES;
		BSCurve curve = new BSCurve(controlPoints);
		Vector2[] vertices = curve.getSamples(n);

		EdgeShape shape = new EdgeShape();
		Vector2 previous = vertices[0];

		// Top side
		for(int i = 1; i < n; ++i) {
			shape.set(previous, vertices[i]);
			body.createFixture(shape, 0);

			previous = vertices[i];
		}
		// Below side
		if (thickness > 0f) {
			Vector2 thicknessVector = new Vector2(0, - thickness);
			previous.add(thicknessVector);
			for(int i = n - 2; i >= 0; --i) {
				vertices[i].add(thicknessVector);
				shape.set(previous, vertices[i]);
				body.createFixture(shape, 0);

				previous = vertices[i];
			}
		}
		shape.dispose();
	}

	/**
	 * Return a random number of control points.
	 * The number of control points is comprised between MIN_CONTROL_POINTS and MAX_CONTROL_POINTS
	 * @see #getRandomControlPoints(int n, float xMin, float xMax, float yMin, float yMax)
	 * @param xMin
	 * @param xMax
	 * @param yMin
	 * @param yMax
	 * @return
	 */
	protected static ArrayList<Vector2> getRandomControlPoints(float xMin, float xMax, float yMin, float yMax) {
		// Compute n so as to have a constant horizontal density of control points

		int n = (int) ( (xMax - xMin) * 3f);
		//System.out.println("Generating a hill with " + n + " control points.");
		n = (n < MIN_CONTROL_POINTS ? MIN_CONTROL_POINTS : n);
		n = (n > MAX_CONTROL_POINTS ? MAX_CONTROL_POINTS : n);

		n = (n % 2 == 0 ? n+1 : n); // Make sure n is odd
		return getRandomControlPoints(n, xMin, xMax, yMin, yMax);
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
		// Our working array
		Vector2[] points = new Vector2[n];

		// Generate n equally horizontally spaced points
		// all at yMin vertical position
		float delta = 1 / (float)(n - 1);
		float alpha = 0;
		for (int i = 0; i < n; ++i) {
			points[i] = new Vector2(progress(xMin, xMax, alpha), 0);
			alpha += delta;
		}

		// Apply midpoint displacement algorithm
		float range = ((yMax - yMin) / 2f);
		applyMidpointDisplacement(points, 0, n, range, 0);

		// Create output
		ArrayList<Vector2> controlPoints = new ArrayList<Vector2>();
		for (int i = 0; i < n; ++i) {
			// The first and last point must be interpolated strongly
			if (i == 0 || i == (n-1)) {
				// Force first and last control points to minimal position
				// TODO : remove that
				points[i].y = 0;
				controlPoints.add(points[i]);
				controlPoints.add(points[i].cpy());
				controlPoints.add(points[i].cpy());
			}
			else
				controlPoints.add(points[i]);
		}
		return controlPoints;
	}

	/**
	 * Apply the vertical midpoint displacement algorithm to the given points.
	 * Only points between iMin and iMax indices are displaced.
	 * @param points
	 * @param iMin
	 * @param iMax
	 * @param range The maximum random displacement to be applied to the midpoint
	 * @param offset A constant displacement to apply to all points
	 */
	protected static void applyMidpointDisplacement(Vector2[] points, int iMin, int iMax, float range, float offset) {
		if (iMin < iMax) {
			int middle = ((iMax - iMin) / 2) + iMin;
			// Displace middle point
			float displacement = random(0, range);
			points[middle].y += displacement + offset;

			// Recursively call to displace the next two midpoints
			range /= Math.pow(2, roughness);
			offset += (displacement / 2f);
			applyMidpointDisplacement(points, iMin, middle, range, offset);
			applyMidpointDisplacement(points, middle + 1, iMax, range, offset);
		}
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