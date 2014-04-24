package com.filrouge.leaphover;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.filrouge.leaphover.game.LeapHover;
import com.filrouge.leaphover.graphics.ThrusterEffect;

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
		//TestGame test = new TestGame();
		//new LwjglApplication(test, cfg);
	}
}

class TestGame implements ApplicationListener {

	ThrusterEffect effect;
	
	@Override
	public void create() {
		effect = new ThrusterEffect(1f);
		effect.setPosition(Gdx.graphics.getWidth() / 2f, Gdx.graphics.getHeight() / 2f);
	}

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void render() {
		// TODO Auto-generated method stub
		Gdx.gl.glClearColor(0, 0.1f, 0.1f, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);		
		
		effect.setRotation(1f);
		
		float delta = Gdx.graphics.getDeltaTime();
		SpriteBatch batch = new SpriteBatch();
		batch.begin();
		effect.draw(batch, delta);
		batch.end();
		batch.flush();
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		
	}
	
}