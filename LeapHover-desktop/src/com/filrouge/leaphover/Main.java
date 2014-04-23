package com.filrouge.leaphover;

import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.filrouge.leaphover.game.LeapHover;

public class Main {
	
	public static void main(String[] args) {
		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
		cfg.title = "LeapHover";
		cfg.useGL20 = false;
		cfg.width = 1024;
		cfg.height = 768;
		//cfg.fullscreen = true;
		
		LeapHover game = LeapHover.getInstance();
		new App(game, cfg);
	}
}
