package com.filrouge.leaphover;

import java.util.concurrent.Callable;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.World;
import com.filrouge.leaphover.experiments.CollisionDetector;
import com.filrouge.leaphover.experiments.FollowCamera;
import com.filrouge.leaphover.experiments.Hero;
import com.filrouge.leaphover.experiments.HillGenerator;

public class LeapHover implements ApplicationListener {
	
	/*
	 * PROPERTIES
	 */
	protected World world;
	
	protected float worldWidth = 20f;
	public final float BLOCK_WIDTH = 2f;
	
	protected FollowCamera camera;
	protected Vector3 initialCameraPosition, maximumCameraPosition;
	protected Box2DDebugRenderer debugRenderer;
	
	/** Rates at which the physical simulation advances */
	public static final Vector2 GRAVITY = new Vector2(0, -1f);
	public static final float BOX_STEP = (1 / 60f);  
	public static final int BOX_VELOCITY_ITERATIONS = 6;  
	public static final int BOX_POSITION_ITERATIONS = 2; 
	
	/** A reference to the main character (for testing purpose) */
	protected Hero hero;
	
	/** Angle of the hero to the horizontal (in radian) */
	public static final float INITIAL_HERO_INCLINATION = 0f;
	protected float heroInclination = INITIAL_HERO_INCLINATION;
	
	/** When this flag is up, restart the level */
	protected boolean retryFlag = false;
	
	/**
	 * Contact handler (detect collisions of the character with the environment)
	 */
	protected ContactListener contactListener;
	
	/* 
	 * METHODS
	 */
	@Override
	public void create() {	
		float w = Gdx.graphics.getWidth();
		float h = Gdx.graphics.getHeight();
		
		world = new World(GRAVITY, true);
				
		camera = new FollowCamera(1, h/w);
		initialCameraPosition = new Vector3(camera.viewportWidth / 2f, camera.viewportHeight / 2f, 0f);
		maximumCameraPosition = new Vector3(worldWidth - (camera.viewportWidth / 2f), camera.viewportHeight / 2f, 0f);
		camera.position.set(initialCameraPosition);
		camera.update();
		
		debugRenderer = new Box2DDebugRenderer();
		
		setupTestScene();
	}

	protected void setupTestScene() {
		// Ground
		BodyDef bodyDefinition = new BodyDef();
		bodyDefinition.type = BodyDef.BodyType.StaticBody;
		// Generate independant hills, each one with width BLOCK_WIDTH
		// so as to fill this.worldWidth
		int n = (int) (worldWidth / BLOCK_WIDTH);
		for (int i = 0; i < n; i++) {
			bodyDefinition.position.set(i * BLOCK_WIDTH, 0);
			Body groundBody = world.createBody(bodyDefinition);
			HillGenerator.makeHill(groundBody, BLOCK_WIDTH, camera.viewportHeight);			
		}
		
		// Falling box
		bodyDefinition.type = BodyDef.BodyType.DynamicBody;
		bodyDefinition.position.set(camera.viewportWidth / 4f, camera.viewportHeight);
		float side = camera.viewportHeight / 50f;
		
		this.hero = new Hero(bodyDefinition, this.world, side);
		
		// Detect collisions with character
		this.contactListener = new CollisionDetector(hero.getCharacter(), new Callable<Boolean>() {
			@Override
			public Boolean call() throws Exception {
				loseGame();
				return true;
			}
		});
		world.setContactListener(this.contactListener);
	}
	
	public void retryLevel() {
		Body heroBody = this.hero.getBody();
		heroBody.setTransform(0.1f, camera.viewportHeight, INITIAL_HERO_INCLINATION);
		heroBody.setLinearVelocity(new Vector2(0f, 0f));
		heroBody.setAngularVelocity(0);
	}
	public void loseGame() {
		System.out.println("You lost the game.");
		retryFlag = true;
	}
	
	@Override
	public void dispose() {
		debugRenderer.dispose();
	}
	
	@Override
	public void render() {		
		Gdx.gl.glClearColor(0, 0.1f, 0.1f, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

		Body heroBody = hero.getBody();
		//heroBody.setTransform(hero.getPosition(), getHeroInclination());
		
		// If hero gets off screen, reset its position
		if (retryFlag || hero.getPosition().y < -0.5f) {
			retryLevel();
			retryFlag = false;
		}
		
		heroBody.applyForce(new Vector2(0.001f, 0), heroBody.getPosition(), true);
		// Limit speed
		//if (hero.getPosition().y <= camera.viewportHeight && hero.getLinearVelocity().x < 0.8f)
		hero.render();
			
		// Follow the hero
		camera.follow(hero.getPosition(), initialCameraPosition, maximumCameraPosition);
		
		debugRenderer.render(world, camera.combined);
		world.step(BOX_STEP, BOX_VELOCITY_ITERATIONS, BOX_POSITION_ITERATIONS);
	}

	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub
		
	}
	
	/*
	 * GETTERS & SETTERS
	 */
	public World getWorld() {
		return world;
	}

	public Hero getHero() {
		return hero;
	}

	public float getHeroInclination() {
		return heroInclination;
	}
	public void setHeroInclination(float heroInclination) {
		this.heroInclination = heroInclination;
	}
}
