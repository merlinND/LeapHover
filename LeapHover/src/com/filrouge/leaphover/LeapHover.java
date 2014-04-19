package com.filrouge.leaphover;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.filrouge.leaphover.experiments.FollowCamera;
import com.filrouge.leaphover.experiments.HillGenerator;
import com.filrouge.leaphover.experiments.HoverBoard;

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
	protected Body hero;
	protected HoverBoard hoverBoard;
	
	protected final float INITIAL_HERO_INCLINATION = 0f;
	protected float heroInclination = INITIAL_HERO_INCLINATION;
	
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
		
		this.hoverBoard = new HoverBoard(bodyDefinition, this.world, side);
		this.hero = this.hoverBoard.getHero();
	}
	
	@Override
	public void dispose() {
		debugRenderer.dispose();
	}
	
	@Override
	public void render() {		
		Gdx.gl.glClearColor(0, 0.1f, 0.1f, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

		hero.setTransform(hero.getPosition(), getHeroInclination());
		
		// If hero gets off screen, reset its position
		if (hero.getPosition().y < -0.5f) {
			hero.setTransform(0.1f, camera.viewportHeight, INITIAL_HERO_INCLINATION);
			hero.setLinearVelocity(new Vector2(0f, 0f));
			hero.setAngularVelocity(0);
		}
		
		// Limit speed
		//if (hero.getPosition().y <= camera.viewportHeight && hero.getLinearVelocity().x < 0.8f)
		
		this.hoverBoard.render();
			
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
		// TODO Auto-generated method stub
		
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
	public void setWorld(World world) {
		this.world = world;
	}

	public Body getHero() {
		return hero;
	}
	public void setHero(Body hero) {
		this.hero = hero;
	}

	public float getHeroInclination() {
		return heroInclination;
	}
	public void setHeroInclination(float heroInclination) {
		this.heroInclination = heroInclination;
	}
}
