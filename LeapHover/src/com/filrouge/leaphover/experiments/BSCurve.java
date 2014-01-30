package com.filrouge.leaphover.experiments;

import java.util.ArrayList;

class Point2D {
	public double x, y;
	Point2D(double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	public double distanceTo(Point2D other) {
		return Math.sqrt( Math.pow(this.x - other.x, 2) + Math.pow(this.y - other.y, 2) );
	}
	public Point2D scaled(double factor) {
		return new Point2D(this.x * factor, this.y * factor);
	}
	public Point2D moved(Point2D other) {
		return new Point2D(this.x + other.x, this.y + other.y);
	}

	public String toString() {
		return "" + this.x + ", " + this.y + "";
	}
}

class PointList extends ArrayList<Point2D> {
	public String toString() {
		String result = "";
		for (Point2D p : this) {
			result += p + "\n";
		}
		return result;
	}
}

/**
 * This class allows to compute any number of points
 * of a uniform cubic beta-spline curve
 * from its control points.
 * @author Merlin Nimier-David
 */
class BSCurve {

	/** The curve's degree (k=4 => degree=3, cubic curve) */
	protected final int degree = 4;
	/** 
	 * The total curve's length (cached for speed), 
	 * i.e. the sum of the distances between its control points.
	 * Useful for sampling with constant density. */
	protected double length;
	/** Ordered list of the curve's control points */
	protected ArrayList<Point2D> controlPoints;
	/** Nodal vector (by default, t_i = i)) */
	//protected ArrayList<Point2D> nodalVector;
	
	/** A constant matrix used in the computation.
	 * We store 6 * the matrix so as to use only integers (this speeds up computations). */
	protected final int[][] sixMBS = {
		{-1, 3, -3, 1},
		{3, -6, 3, 0},
		{-3, 0, 3, 0},
		{1, 4, 1, 0}
	};
	protected ArrayList<PointList> cachedV;
	
	public BSCurve(ArrayList<Point2D> controlPoints) {
		cachedV = new ArrayList<PointList>();
		// TODO : need at least k control points
		this.controlPoints = controlPoints;
		updateLength();
		cacheVectors();
	}
	// TODO : be able to customize degree and nodal vector.
	
	protected void updateLength() {
		length = 0d;
		if (controlPoints.size() > 1) {
			Point2D previous = controlPoints.get(0);
			for(int i = 1; i < controlPoints.size(); ++i) {
				length += previous.distanceTo(controlPoints.get(i));
				previous = controlPoints.get(i);
			}
		}
	}

	/** Compute all V_I vectors for the current control points */
	protected void cacheVectors() {
		// We compute, for I = 3...(n-1) :
		// V_I (vector of points) = MBS . G_I  (matrix multiplication)
		cachedV = new ArrayList<PointList>();
		
		Point2D result, p;
		int factor;
		// For each V_I (one vector of 4 points)
		for (int I = 3; I < controlPoints.size(); ++I) {
			cachedV.add(new PointList());
			// For each point of the result vector V_I
			for (int i = 0; i < this.degree; ++i) {
				result = new Point2D(0, 0);
				for (int j = 0; j < sixMBS[0].length; ++j) {
					p = controlPoints.get( I - (3-j) );
					factor = sixMBS[i][j];
					result = result.moved(p.scaled(factor));
				}
				cachedV.get(I - 3).add(result); 
			}
				
		}
	}

	/**
	 * This method returns the point corresponding to the given
	 * parameter for this beta-spline curve.
	 * @param t Parameter between 0 (beginning of the curve) and 1 (end)
	 * @return Point2D The corresponding point
	 */
	public Point2D samplePoint(double t) {
		t = (t < 0 ? 0 : t);
		t = (t > 1 ? 1 : t);

		// Scale t so that it fits into an interval of two nodal points:
		// t belongs to [t_I, t_I+1]
		// We use nodal points in arithmetic progression: t_i = i
		// with I = 3...(n-1)
		double scaledT = (t * (controlPoints.size() - 3)) + 3;
		int I = (int)Math.floor(scaledT); // Only valid with all the above assumptions
		I = (I >= controlPoints.size() ? (I - 1) : I);
		int t_I = I;

		// We compute :
		// Q(t) = T * M_BS * G_I
		//      = T * V_I
		// We cached 6*V_I for performance, so we only need to compute:
		// Q(t) = 1/6 * T * V_I
		Point2D Q = new Point2D(0, 0);
		double difference = (scaledT - t_I);
		double factor;
		PointList referencePoints = cachedV.get(I - 3);
		for (int k = 0; k < this.degree; ++k) {
			factor = Math.pow(difference, this.degree - k - 1);
			Q = Q.moved( referencePoints.get(k).scaled(factor) );
		}

		return Q.scaled(1 / (double)6);
	}

	/**
	 * Use this method to retrieve the desired number of points
	 * sampled from this beta-spline curve. The curve is sample
	 * uniformely along its whole length.
	 * @param n The number of points to retrieve
	 * @return ArrayList<Point2D>
	 */
	public ArrayList<Point2D> getSamples(int n) {
		ArrayList<Point2D> result = new ArrayList<Point2D>();
		// We sample the curve uniformely along its whole length

		// For each point that we want to output
		// Compute the corresponding t parameter
		double delta = (1 / (double)(n - 1));
		double t = 0;
		for (int i = 0; i < n; ++i) {
			// Sample a point at t
			result.add(samplePoint(t));
			t += delta;
		}
		
		return result;
	}

	public static void main(String[] args) {
		
		// Test control points (at least this.degree)
		ArrayList<Point2D> testControlPoints = new ArrayList<Point2D>();
		testControlPoints.add(new Point2D(0, 2));
		testControlPoints.add(new Point2D(0, 2));
		testControlPoints.add(new Point2D(0, 2));
		testControlPoints.add(new Point2D(1, 3));
		testControlPoints.add(new Point2D(1.5, 6));
		testControlPoints.add(new Point2D(1.8, 8));
		testControlPoints.add(new Point2D(2.2, 7.8));
		testControlPoints.add(new Point2D(2.5, 4.5));
		testControlPoints.add(new Point2D(4, 3));
		testControlPoints.add(new Point2D(4.5, 3.1));
		testControlPoints.add(new Point2D(5.8, 6));
		testControlPoints.add(new Point2D(6, 9));
		testControlPoints.add(new Point2D(6, 9));
		testControlPoints.add(new Point2D(6, 9));
		
		BSCurve testCurve = new BSCurve(testControlPoints);

		// Sample points and check they from the above curve
		System.out.println("=== Sampling extreme and middle points:");
		System.out.println(testCurve.samplePoint(0));
		System.out.println(testCurve.samplePoint(0.5));
		System.out.println(testCurve.samplePoint(1));
		

		int n = 100;
		System.out.println("=== Sampling " + n + " points: ");
		ArrayList<Point2D> testPoints = testCurve.getSamples(n);
		for (Point2D p : testPoints) {
			System.out.println(p);
		}
	}
}
