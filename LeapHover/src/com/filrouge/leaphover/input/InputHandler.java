package com.filrouge.leaphover.input;

import java.util.ArrayList;
import java.util.List;

import com.leapmotion.leap.Pointable;
import org.lwjgl.input.Mouse;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Vector2;
import com.filrouge.leaphover.game.LeapHover;

public class InputHandler extends LeapListener implements InputProcessor {
	/*
	 * PROPERTIES
	 */
	protected LeapHover game;
	
	/** Number of cycles of hand being "down" */
	protected int numberOfLeapSamples = 0;
	/** Used to accumulate received hand heights and then compute an average */
	protected float percentSum = 0;

	/** When set to true, allows the mouse to draw */
	protected boolean mouseDraw = false;
	protected double timeLastPointMs = 0;

	protected List<Float> inclinationSamples = new ArrayList<Float>();
	protected final int MAX_SAMPLE_NUMBER = 15;
	protected final float INIT_POS_X = 0.5f;
	protected final float INIT_POS_Y = 0.375f;
	protected final float ANGLE_CONTRIBUTION_RATIO = 0.2f;
	
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
	 * Makes the hero jump
	 */
	public void makeJump() {
		game.getHero().triggerJump();
	}

	/**
	 * Makes a relative change to the hero's angle.
	 * @param angle Relative change to make to the hero's inclination
	*/
	public void makeInclination(float angle) {
		makeInclination(angle, true);
	}
	
	/**
	 * Makes a relative change to the hero's angle.
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
		// Trigger jump
		if(amount >= 0.5 && this.numberOfLeapSamples > 0) {
			//float averageHeight = this.percentSum / this.numberOfLeapSamples;
			makeJump();

			// Reset counters for next jump
			this.percentSum = 0;
			this.numberOfLeapSamples = 0;
		}
		// Accumulate "force"
		else if(amount <= 0.4) {
			if(this.percentSum == 0)
				this.game.getHero().startChargingJump();

			this.percentSum += (1 - amount);
			game.getHero().setCurrentHandHeight(amount);
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
	
	@Override
	/**
	 * Adds a new point to be drawn.
	 * (coordinates from the leap are transformed to fit the game)
	 * 
	 * @param point The point to add to the other control points
	 */
	public boolean handDraw(Vector2 frontMost) {
		/*
		 * Converting coordinates from leap coordinates to game coordinates
		 */
		if (!isRightHanded) {
			frontMost.x = MIDDLE - frontMost.x;
		}

		System.out.println("leapX: "+frontMost.x+" leapY: "+frontMost.y);

		float x = frontMost.x / (DETECTION_WIDTH / this.game.getCamera().viewportWidth),
			  y = frontMost.y / (DETECTION_HEIGHT / this.game.getCamera().viewportHeight);

		x += game.getCamera().position.x - INIT_POS_X;
		y += game.getCamera().position.y - 2 * INIT_POS_Y;

		System.out.println("x: "+x+" y: "+y);

		this.game.addDrawPoint(new Vector2(x, y));
		
		return false;
	}
	
	public boolean newHandDrawing() {
		this.game.setDisplayDrawing(true);
		return false;
	}
	
	public boolean endHandDrawing() {
		this.game.validateDrawing();

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
		// Charge jump
		/*
		 * TODO: if the hero can't actually start charging right now
		 * but the player keeps the key down, we do want to start
		 * charging as soon as possible
		 */
		case Input.Keys.DOWN:
			game.getHero().startChargingJump();
			break;

		// Turns on the mouse drawing mode
		case Input.Keys.SPACE:
			this.mouseDraw = true;
			
			int screenX = Mouse.getX(),
				screenY = Gdx.graphics.getHeight() - Mouse.getY(); // converts y so that origin is on top
			this.mouseMoved(screenX, screenY);

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
			game.getHero().triggerJump();
			break;

		// Turns off the mouse drawing mode
		case Input.Keys.SPACE:
			this.mouseDraw = false;
			break;
		
		default:
			break;
		}
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		return false;
	}

	/**
	 * Handles the mouse movement with explicit coordinates.
	 *
	 * @param screenX X in pixels with origin on the left
	 * @param screenY Y in pixels with origin on top
	 * @return
	 */
	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		screenY = Gdx.graphics.getHeight() - screenY; // Puts the origin back on the bottom
		if (this.mouseDraw) {
			// If the mouse is on the screen
			if (0 <= screenX && screenX <= Gdx.graphics.getWidth()
					&& 0 <= screenY && screenY <= Gdx.graphics.getHeight()) {

				double currentTimeMs = System.currentTimeMillis();
				if (this.timeLastPointMs == 0 || currentTimeMs - this.timeLastPointMs >= MIN_TIME_BETWEEN_POINTS_MS) {
					this.timeLastPointMs = currentTimeMs;
					
					/*
					 * Converting the coordinates from pixels to game coordinates
					 */
					float x = (float) screenX / (Gdx.graphics.getWidth() / this.game.getCamera().viewportWidth),
					y = (float) screenY / (Gdx.graphics.getHeight() / this.game.getCamera().viewportHeight);
				
					x += this.game.getCamera().position.x - INIT_POS_X;
					y += this.game.getCamera().position.y - INIT_POS_Y;

					this.game.setDisplayDrawing(true);
					this.game.addDrawPoint(new Vector2(x, y));
				}
			}
		}
		
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		return false;
	}

	/*
	 * GETTERS & SETTERS
	 */
}
