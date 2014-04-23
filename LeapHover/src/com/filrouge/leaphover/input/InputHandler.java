package com.filrouge.leaphover.input;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.filrouge.leaphover.game.LeapHover;

public class InputHandler extends LeapListener implements InputProcessor {
	/*
	 * PROPERTIES
	 */
	protected LeapHover game;
	
	/** Number of cycles of hand being "down" */
	protected int numberOfLeapSamples = 0;
	/** Used to accumulate received hand heights and then compute an average */
	protected int percentSum = 0;
	
	protected List<Float> inclinationSamples = new ArrayList<Float>();
	protected final int MAX_SAMPLE_NUMBER = 15;
	protected final float ANGLE_CONTRIBUTION_RATIO = 0.08f;
	
	/* 
	 * METHODS
	 */
	
	/**
	 * @param game A reference to the game
	 */
	public InputHandler(LeapHover game) {
		this.game = game;
	}
	
	/**
	 * @param amount Value in [0,1]
	 */
	public void makeJump(float amount) {
		game.getHero().jump(amount);
	}
	
	public void makeInclination(float angle) {
		makeInclination(angle, true);
	}
	/**
	 * Make a relative change to the hero's angle.
	 * @param angle Relative change to make to the hero's inclination
	 * @param smooth The inclination is smoothed over the <tt>MAX_SAMPLE_NUMBER</tt> calls
	 */
	public void makeInclination(float angle, boolean smooth) {
		// Smooth over the last few frames
		float targetAngle = angle;
		if (smooth) {
			inclinationSamples.add(angle);
			if (inclinationSamples.size() > MAX_SAMPLE_NUMBER)
				inclinationSamples.remove(0);
			
			targetAngle = 0;
			for (Float a : inclinationSamples)
				targetAngle += a;
			targetAngle /= (float)inclinationSamples.size();
		}
		
		// Only make a relative contribution (influence) to the hero's angle
		game.setHeroInclination(game.getHeroInclination() + targetAngle * ANGLE_CONTRIBUTION_RATIO);
	}
	
	
	/* -----------------------
	 * LEAP EVENTS
	 */
	@Override
	public boolean handHeight(float amount) {
		// TODO: only allow jumping if not already mid-air
		
		// Trigger jump
		if(amount >= 0.5 && this.numberOfLeapSamples > 0) {
			float averageHeight = this.percentSum / this.numberOfLeapSamples;
			
			// [100, 61] --> [0.2, 0]
			makeJump(averageHeight);

			// Reset counters for next jump
			this.percentSum = 0;
			this.numberOfLeapSamples = 0;
		}
		// Accumulate "force"
		else if(amount < 0.4) {
			// [0;39] --> [100, 61]
			this.percentSum += 1 - amount;
			++this.numberOfLeapSamples;
		}
		
		return false;
	}

	@Override
	public boolean handInclination(float percent) {
		float convertedAngle = (0.5f - percent) * (float)Math.PI;
		makeInclination(convertedAngle);
		
		return false;
	}
	
	/* -----------------------
	 * KEYBOARD EVENTS
	 */
	@Override
	public boolean keyDown(int keycode) {
		switch (keycode) {
		// Augment board inclination
		case Input.Keys.LEFT:
			makeInclination((float)Math.PI / 2f, false);
			break;
		// Reduce board inclination
		case Input.Keys.RIGHT:
			makeInclination(- (float)Math.PI / 2f, false);
			break;
		// Retry lever after losing
		case Input.Keys.ENTER:
			game.retryLevel();
			break;
			
		default:
			break;
		}
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		switch (keycode) {
		// Trigger jump
		case Input.Keys.DOWN:
			makeJump(1f);
			break;

		default:
			break;
		}
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		// TODO Auto-generated method stub
		return false;
	}

	/*
	 * GETTERS & SETTERS
	 */
}
