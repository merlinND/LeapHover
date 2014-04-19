package com.filrouge.leaphover.input;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Vector2;
import com.filrouge.leaphover.LeapHover;
import com.filrouge.leaphover.experiments.Hero;
import com.filrouge.leaphover.leapcontroller.LeapListener;

public class InputHandler extends LeapListener implements InputProcessor {
	/*
	 * PROPERTIES
	 */
	protected LeapHover game;
	
	protected int time = 0;
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
	 * 
	 * TODO: define unit of amount
	 * @param amount
	 */
	public void makeJump(float amount) {
		Vector2 force = new Vector2(0, (amount * Hero.MAX_JUMP_FORCE) / 100);
		game.getHero().getBody().applyForce(force, game.getHero().getPosition(), true);
	}
	
	public void makeInclination(float angle) {
		// Smooth over the last few frames
		inclinationSamples.add(angle);
		if (inclinationSamples.size() > MAX_SAMPLE_NUMBER)
			inclinationSamples.remove(0);
		
		float targetAngle = 0;
		for (Float a : inclinationSamples)
			targetAngle += a;
		targetAngle /= (float)inclinationSamples.size();
		
		// Only make a relative contribution (influence) to the hero's angle
		game.setHeroInclination(game.getHeroInclination() + targetAngle * ANGLE_CONTRIBUTION_RATIO);
	}
	
	
	/* -----------------------
	 * LEAP EVENTS
	 */
	@Override
	public boolean handHeight(float percent) {
		// TODO: only allow jumping if not already mid-air
		// TODO: make jump perpendicular to the hero, not always vertical
		
		// Trigger jump
		if(percent >= 0.5 && this.numberOfLeapSamples > 0) {
			float averageHeight = this.percentSum / this.numberOfLeapSamples;
			
			// [100, 61] --> [0.2, 0]
			makeJump(averageHeight);

			// Reset counters for next jump
			this.percentSum = 0;
			this.numberOfLeapSamples = 0;
		}
		// Accumulate "force"
		else if(percent < 0.4) {
			// [0;39] --> [100, 61]
			this.percentSum += 100 - percent;
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
		// Accumulate "force"
		if(Input.Keys.DOWN == keycode) {
			this.time++;
		} else if(Input.Keys.ENTER == keycode) {
			game.retryLevel();
		}
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		// Trigger jump
		if (Input.Keys.DOWN == keycode && this.time > 0) {
			// TODO: adjust
			float amount = this.time * 100f;
			makeJump(amount);
			// Reset for next jump
			this.time = 0;
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
