package com.filrouge.leaphover.game;

/**
 *	Basic Class which handles score
 *	@author remimartin
 *	TODO : Add points for special tricks
 *	TODO : Change exponent values?
 */
public class Score {
	/**
	 * Exponent at the beginning of the game
	 * TODO : Inc exponent over the levels?
	 */
	private static final float 	startExponent 	= 	1.2f;
	
	/**
	 * Start & reset level
	 */
	private static final int 	initialLevel 	= 	0;
	private static final int 	resetLevel		=	1;
	
	protected float score;
	protected int level;
	protected float exponent;
	protected float lastX;
	
	// Constructor(s)
	public Score() {
		this.level = Score.initialLevel;
		this.score = 0;
		this.lastX = 0;
		this.exponent = Score.startExponent;
	}
	
	// Getter(s)
	public float getScore() {
		return this.level;
	}
	
	// Setter(s)
	
	// Method(s)
	/**
	 * Compute the score and increment the current level
	 * @param x
	 */
	public void incLevel(float x) {
		// Score computation
		this.computeCurrentScore(x);
		
		++this.level;
	}
	
	public void computeCurrentScore(float x) {
		// If the user go back, he wins no point
		if(x > this.lastX) {
			this.score+=this.level * Math.pow(x-this.lastX, this.exponent);
		
			this.lastX = x;
		}
	}
	
	public void reset() {
		this.level=Score.resetLevel;
		this.lastX=0;
		this.score=0;
		this.exponent=Score.startExponent;
	}
}
