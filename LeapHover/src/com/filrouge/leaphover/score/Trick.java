package com.filrouge.leaphover.score;

public class Trick {
	/**
	 * TODO : define good amount of points
	 */
	private static final float pointsForLoop		= 10f;
	private static final float pointsForHeadDown	= 10f;
	
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
		if(floorAngle >= 160 && floorAngle <= 180) {
			// Head down, yeah! Feels great!
			this.score.performedTrick(pointsForHeadDown);
		}
	}
}
