package com.filrouge.leaphover;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.filrouge.leaphover.input.InputHandler;
import com.leapmotion.leap.Controller;

public class Main {
	public static void main(String[] args) {
		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
		cfg.title = "LeapHover";
		cfg.useGL20 = false;
		cfg.width = 1024;
		cfg.height = 768;
		//cfg.fullscreen=true;
		
		LeapHover game = new LeapHover();
		LwjglApplication app = new LwjglApplication(game, cfg);
		
		InputHandler handler = new InputHandler(game);
		// Listen to keyboard events
		app.getInput().setInputProcessor(handler);
		// Listen to Leap Motion events
		Controller leap = new Controller();
	    leap.addListener(handler);
	}
}
