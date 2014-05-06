package com.filrouge.leaphover.score;

public class Trick {
	/**
	 * TODO : define good amount of points
	 */
	private static final float POINTS_FOR_LOOP		= 10f;
	
	/** Hand down constants */
	private static final float POINTS_FOR_HEAD_DOWN	= 10f;
	private static final double MIN_ANGLE_HEAD_DOWN	= (2 * 3) * Math.PI;
	private static final double MAX_ANGLE_HEAD_DOWN	= Math.PI;
	
	protected Score score;
	
	// Constructor(s)
	public Trick(Score score) {
		this.score=score;
	}
	
	// Method(s)
	/**
	 * @param angle
	 * TODO : Try to detect loop
	 */
	public void newAngle(float angle) {
		int floorAngle = (int) Math.floor(angle);
		if(floorAngle >= MIN_ANGLE_HEAD_DOWN && floorAngle <= MAX_ANGLE_HEAD_DOWN) {
			// Head down, yeah! Feels great!
			this.score.performedTrick(POINTS_FOR_HEAD_DOWN);
		}
	}
}
