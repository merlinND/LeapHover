package com.filrouge.leaphover.game;

import java.util.concurrent.Callable;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.World;
import com.filrouge.leaphover.level.HillGenerator;
import com.filrouge.leaphover.physics.CollisionDetector;

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
	SpriteBatch spriteBatch;
	
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
	protected boolean paused = false;
	protected boolean lost = false;
	
	/** Score when the user loose the game */
	protected double score = 0;
	
	/**
	 * Contact handler (detect collisions of the character with the environment)
	 */
	protected ContactListener contactListener;
	
	
	/* 
	 * METHODS
	 */
	/** Private constructor (LeapHover is a Singleton) */
	private LeapHover() {
		
	}
	private static class SingletonHolder {
		/** Instance unique non préinitialisée */
		private final static LeapHover instance = new LeapHover();
	}
	/** 
	 * Récupérer l'unique instance de ce LeapHover (pattern Singleton)
	 * Source : http://thecodersbreakfast.net/index.php?post/2008/02/25/26-de-la-bonne-implementation-du-singleton-en-java
	 */
	public static LeapHover getInstance() {
		return SingletonHolder.instance;
	}
	
	
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
		spriteBatch = new SpriteBatch();
		
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
		this.paused = false;
		this.lost = false;
		
		setHeroInclination(INITIAL_HERO_INCLINATION);
		Body heroBody = this.hero.getBody();
		heroBody.setTransform(0.1f, camera.viewportHeight, INITIAL_HERO_INCLINATION);
		heroBody.setLinearVelocity(new Vector2(0f, 0f));
		heroBody.setAngularVelocity(0);
	}
	public void loseGame() {
		this.score = Math.pow(this.hero.getPosition().x, 1.2);
		this.paused = true;
		this.lost = true;
	}
	
	@Override
	public void dispose() {
		debugRenderer.dispose();
	}
	
	@Override
	public void render() {		
		Gdx.gl.glClearColor(0, 0.1f, 0.1f, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		
		// If hero gets off screen, reset its position
		if (hero.getPosition().y < -0.5f) {
			retryLevel();
		}
		
		// Make the camera follow the hero
		camera.follow(hero.getPosition(), initialCameraPosition, maximumCameraPosition);
		debugRenderer.render(world, camera.combined);

		spriteBatch.begin();
		if(!this.paused) {
			hero.step();
			world.step(BOX_STEP, BOX_VELOCITY_ITERATIONS, BOX_POSITION_ITERATIONS);

			// Apply hero inclination smoothly
			float angle = (hero.getBody().getAngle() + getHeroInclination()) / 2f;
			hero.getBody().setTransform(hero.getPosition(), angle);

			// Limit linear velocity
			Vector2 velocity = hero.getBody().getLinearVelocity();
			if (velocity.len() > Hero.MAX_LINEAR_VELOCITY) {
				velocity = velocity.nor().scl(Hero.MAX_LINEAR_VELOCITY);
				hero.getBody().setLinearVelocity(velocity);
			}
			
			hero.render(spriteBatch);
		}
		if (this.lost) {
			BitmapFont font = new BitmapFont();
			font.setScale(3f);
			String str="You lost the game. Your scored : " + Math.round(this.score) + " points.";
			font.draw(spriteBatch, str, 100, 100);
		}
		spriteBatch.end();
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

	public FollowCamera getCamera() {
		return camera;
	}
	
	public float getHeroInclination() {
		return heroInclination;
	}
	public void setHeroInclination(float heroInclination) {
		this.heroInclination = heroInclination;
	}
}
