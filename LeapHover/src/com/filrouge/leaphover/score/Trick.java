package com.filrouge.leaphover.score;

import com.filrouge.leaphover.graphics.MessageDisplay;

public class Trick {
	/**
	 * TODO : define good amount of points
	 */
	//private static final float POINTS_FOR_LOOP		= 10f;
	
	/** Head down constants */
	private static final float POINTS_FOR_HEAD_DOWN	= 10f;
	
	protected Score score;
	
	/** 
	 * We don't want the user to win a lot of bonus points
	 * for one head down
	 */
	protected boolean alreadyHeadDown;
	
	// Constructor(s)
	public Trick(Score score) {
		this.score = score;
		this.alreadyHeadDown = false;
	}
	
	// Method(s)
	/**
	 * @param angle
	 * TODO : Try to detect loop
	 */
	public void newAngle(float angle) {
		angle = (int) (angle / Math.PI);
		if(angle == -1 && !this.alreadyHeadDown) {
			// Head down, yeah! Feels great!
			this.score.performedTrick(POINTS_FOR_HEAD_DOWN);
			
			this.alreadyHeadDown = true;
			
			MessageDisplay.addMessage("Head down !");
		} else {
			if(angle == 0) { // initial position
				this.alreadyHeadDown = false;
			}
		}
	}
}
