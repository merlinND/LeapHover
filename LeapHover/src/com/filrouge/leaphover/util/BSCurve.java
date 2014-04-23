package com.filrouge.leaphover.util;

import java.util.ArrayList;

import com.badlogic.gdx.math.Vector2;

class PointList extends ArrayList<Vector2> {
	private static final long serialVersionUID = -4654318152446499125L;

	public String toString() {
		String result = "";
		for (Vector2 p : this) {
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
public class BSCurve {
	/*
	 * PROPERTIES
	 */
	/** The curve's degree (k=4 => degree=3, cubic curve) */
	protected final int degree = 4;
	/** 
	 * The total curve's length (cached for speed), 
	 * i.e. the sum of the distances between its control points.
	 * Useful for sampling with constant density. */
	protected double length;
	/** Ordered list of the curve's control points */
	protected ArrayList<Vector2> controlPoints;
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
	
	/* 
	 * METHODS
	 */
	// Constructor
	public BSCurve(ArrayList<Vector2> controlPoints) {
		cachedV = new ArrayList<PointList>();
		// TODO : need at least k control points
		this.controlPoints = controlPoints;
		updateLength();
		cacheVectors();
	}
	
	protected void updateLength() {
		length = 0d;
		if (controlPoints.size() > 1) {
			Vector2 previous = controlPoints.get(0);
			for(int i = 1; i < controlPoints.size(); ++i) {
				length += previous.dst(controlPoints.get(i));
				previous = controlPoints.get(i);
			}
		}
	}

	/** Compute all V_I vectors for the current control points */
	protected void cacheVectors() {
		// We compute, for I = 3...(n-1) :
		// V_I (vector of points) = MBS . G_I  (matrix multiplication)
		cachedV = new ArrayList<PointList>();
		
		Vector2 result, p;
		int factor;
		// For each V_I (one vector of 4 points)
		for (int I = 3; I < controlPoints.size(); ++I) {
			cachedV.add(new PointList());
			// For each point of the result vector V_I
			for (int i = 0; i < this.degree; ++i) {
				result = new Vector2(0, 0);
				for (int j = 0; j < sixMBS[0].length; ++j) {
					p = controlPoints.get( I - (3-j) );
					factor = sixMBS[i][j];
					result.add(p.cpy().scl(factor));
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
	public Vector2 samplePoint(double t) {
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
		Vector2 Q = new Vector2(0, 0);
		double difference = (scaledT - t_I);
		float factor;
		PointList referencePoints = cachedV.get(I - 3);
		for (int k = 0; k < this.degree; ++k) {
			factor = (float)Math.pow(difference, this.degree - k - 1);
			Q.add( referencePoints.get(k).cpy().scl(factor) );
		}

		return Q.scl(1 / 6f);
	}

	/**
	 * Use this method to retrieve the desired number of points
	 * sampled from this beta-spline curve. The curve is sample
	 * uniformely between control points.
	 * TODO: be able to sample with constant density along length, and not only control points
	 * @param n The number of points to retrieve
	 * @return Vector2[]
	 */
	public Vector2[] getSamples(int n) {
		Vector2[] result = new Vector2[n];
		// We sample the curve uniformely along its whole length

		// For each point that we want to output
		// Compute the corresponding t parameter
		double delta = (1 / (double)(n - 1));
		double t = 0;
		for (int i = 0; i < n; ++i) {
			// Sample a point at t
			result[i] = samplePoint(t);
			t += delta;
		}
		
		return result;
	}
	
	/*
	 * GETTERS & SETTERS
	 */
}
