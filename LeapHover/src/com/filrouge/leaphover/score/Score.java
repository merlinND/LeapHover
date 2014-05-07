package com.filrouge.leaphover.score;

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
	private static final float 	START_EXPONENT 	= 	1.2f;
	
	/**
	 * Start & reset level
	 */
	private static final int 	INITIAL_LEVEL 	= 	0;
	private static final int 	RESET_LEVEL		=	1;
	
	protected float score;
	protected int level;
	protected float exponent;
	protected float lastX;
	
	protected Trick trick;
	
	// Constructor(s)
	public Score() {
		this.level 		= Score.INITIAL_LEVEL;
		this.score 		= 0;
		this.lastX 		= 0;
		this.exponent 	= Score.START_EXPONENT;
	}
	
	// Getter(s)
	public float getScore() {
		return this.score;
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
			this.score += this.level * Math.pow(x - this.lastX, this.exponent);
		
			this.lastX = x;
		}
	}
	
	public void reset() {
		this.level = Score.RESET_LEVEL;
		this.lastX = 0;
		this.score = 0;
		this.exponent = Score.START_EXPONENT;
	}
	
	/**
	 * A new trick has been performed. Score points are added to the global score.
	 * @param points
	 * 
	 * TODO : Multiply points by level ?
	 * TODO : Apply pow on points ?
	 */
	public void performedTrick(float points) {
		this.score += points;
	}
	
	// ----- Getter(s) & Setter(s)
	public int getLevel() {
		return this.level;
	}
}
