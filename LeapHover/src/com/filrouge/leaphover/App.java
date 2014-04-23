package com.filrouge.leaphover;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.filrouge.leaphover.game.LeapHover;
import com.filrouge.leaphover.input.InputHandler;
import com.leapmotion.leap.Controller;

public class App extends LwjglApplication {
	/*
	 * PROPERTIES
	 */
	protected LeapHover game;
	protected Controller leap;
	protected InputHandler handler;

	/* 
	 * METHODS
	 */
	public App(LeapHover game, LwjglApplicationConfiguration config) {
		super(game, config);
		this.game = game;
		this.handler = new InputHandler(game);
		this.leap = new Controller();

		attachListeners();
	}
	
	@Override
	public void exit() {
		super.exit();
		detachListeners();
	}
	
	protected void attachListeners() {
		// Listen to keyboard events
		getInput().setInputProcessor(handler);
		// Listen to Leap Motion events
		leap.addListener(handler);
	}
	
	protected void detachListeners() {
		// Remove the Leap Motion listener when done
		leap.removeListener(handler);
	}
	
	/*
	 * GETTERS & SETTERS
	 */
}
