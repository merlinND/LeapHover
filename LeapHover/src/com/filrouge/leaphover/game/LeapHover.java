package com.filrouge.leaphover.game;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.World;
import com.filrouge.leaphover.graphics.MessageDisplay;
import com.filrouge.leaphover.level.LevelGenerator;
import com.filrouge.leaphover.level.UserHill;
import com.filrouge.leaphover.physics.CollisionDetector;
import com.filrouge.leaphover.score.Score;
import com.filrouge.leaphover.score.Trick;

public class LeapHover implements ApplicationListener {
	
	/*
	 * PROPERTIES
	 */
	protected World world;
	
	protected final float WORLD_GENERATION_THRESHOLD = 1f;
	protected final float WORLD_CHUNK_WIDTH = 5 * WORLD_GENERATION_THRESHOLD;
	/** We consider the game lost if the player gets under this height */
	protected final float WORLD_MINIMUM_Y = -0.5f;
	protected float currentWorldWidth = 0f;
	
	protected FollowCamera camera;
	protected Vector3 initialCameraPosition, maximumCameraPosition;
	
	public static final float ADAPTATIVE_ZOOM_POW = 2;
	public static final float ADAPTATIVE_ZOOM_CONST = 1;
	
	protected Box2DDebugRenderer debugRenderer;
	protected SpriteBatch spriteBatch;
	protected BitmapFont displayFont;
	protected String message = "";
	
	/** Rates at which the physical simulation advances */
	public static final Vector2 GRAVITY = new Vector2(0, -1f);
	public static final float BOX_STEP = (1 / 60f);  
	public static final int BOX_VELOCITY_ITERATIONS = 6;  
	public static final int BOX_POSITION_ITERATIONS = 2; 
	
	/** A reference to the main character (for testing purpose) */
	protected Hero hero;
	
	/** Properties for the drawing */
	protected boolean displayDrawing = false;
	protected List<Vector2> drawingPoints = new ArrayList<Vector2>();
	
	protected List<UserHill> userHills = new ArrayList<UserHill>();
	
	/** Angle of the hero to the horizontal (in radian) */
	public static final float INITIAL_HERO_INCLINATION = 0f;
	protected float heroInclination = INITIAL_HERO_INCLINATION;
	
	/** When this flag is up, restart the level */
	protected boolean paused = false;
	protected boolean lost = false;
	
	/** Score when the user looses the game */
	protected Score score;
	protected Trick trick;
	public static final float BONUS_POINTS = 20;
	
	/** Contact handler (detect collisions of the character with the environment) */
	protected ContactListener contactListener;
	
	/** Environment */
	protected Fixture toBeDeleted = null;
	
	/* 
	 * METHODS
	 */
	/** Private constructor (LeapHover is a Singleton) */
	private LeapHover() {
		this.score = new Score();
		this.trick = new Trick(this.score);
	}
	
	private static class SingletonHolder {
		/** Instance unique non préinitialisée */
		private final static LeapHover instance = new LeapHover();
	}
	/** 
	 * Get the unique instance of LeapHover (Singleton pattern)
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

		camera.position.set(initialCameraPosition);
		camera.update();
		
		debugRenderer = new Box2DDebugRenderer();
		spriteBatch = new SpriteBatch();
		displayFont = new BitmapFont();
		displayFont.setScale(3f);
		
		setupTestScene();
	}

	protected void setupTestScene() {
		// Marty McFly
		float side = camera.viewportHeight / 12f;
		this.hero = new Hero(this.world, side);
		this.hero.setPosition(new Vector2(camera.viewportWidth / 4f, camera.viewportHeight));
		
		// Detect collisions with character
		this.contactListener = new CollisionDetector(hero.getCharacter(), new Callable<Boolean>() {
			@Override
			public Boolean call() throws Exception {
				loseGame();
				return true;
			}
		});
		world.setContactListener(this.contactListener);
		
		// Messages display
		MessageDisplay.initiate();
		
		// Ground
		extendWorldIfNecessary();
	}
	
	protected void extendWorldIfNecessary() {
		float distanceToEnd = currentWorldWidth - hero.getPosition().x;
		if (Math.max(0, distanceToEnd) < WORLD_GENERATION_THRESHOLD) {
			this.score.incLevel(this.hero.getPosition().x);
			MessageDisplay.addMessage("Level " + this.score.getLevel());
			
			LevelGenerator.generate(world, currentWorldWidth, currentWorldWidth + WORLD_CHUNK_WIDTH, camera.viewportHeight);
			currentWorldWidth += WORLD_CHUNK_WIDTH;
			
			// Update the camera max x position
			float maxX = currentWorldWidth - (camera.viewportWidth / 2f);
			maxX = Math.max(camera.viewportWidth, maxX);
			maximumCameraPosition = new Vector3(maxX, camera.viewportHeight / 2f, 0f);
			
			System.out.println("Generated world up to " + currentWorldWidth);
		}
	}
	
	public void retryLevel() {
		for (UserHill hill : userHills) {
			hill.destroy();
		}

		this.score.reset();
		this.paused = false;
		this.lost = false;
		this.message = "";

		// TODO: make sure all user lines (drawings) are cleared properly
		
		// Move to the very beginning of the level
		Vector2 position = new Vector2(camera.viewportHeight / 3f, camera.viewportHeight);
		this.hero.resetTo(position, INITIAL_HERO_INCLINATION);
		setHeroInclination(INITIAL_HERO_INCLINATION);
	}

	public void bonusPicked(Fixture fixture) {
		this.toBeDeleted = fixture;
		
		this.score.performedTrick(BONUS_POINTS);
		MessageDisplay.addMessage("Bonus picked");
	}
	
	private void deleteBonus() {
		if (this.toBeDeleted != null) {
			this.world.destroyBody(this.toBeDeleted.getBody());
			this.toBeDeleted = null;
		}
	}
	
	public void loseGame() {
		this.score.computeCurrentScore(this.hero.getPosition().x);
		float score = this.score.getScore();
		this.paused = true;
		this.lost = true;
		
		this.message = "You lost the game. Your scored : " + Math.round(score) + " points.";
	}
	
	@Override
	public void dispose() {
		debugRenderer.dispose();
	}
	
	/**
	 * Contains all game logic
	 * @param delta
	 */
	public void step(float delta) {
		// If hero falls off screen, reset its position
		if (hero.getPosition().y < WORLD_MINIMUM_Y)
			loseGame();
		
		this.trick.newAngle(this.heroInclination);
		
		// Level streaming: generate more level if needed
		extendWorldIfNecessary();
		
		if(!this.paused) {
			hero.step();
			camera.zoom = (float) Math.pow(hero.getBody().getPosition().y, ADAPTATIVE_ZOOM_POW) + ADAPTATIVE_ZOOM_CONST;
			world.step(BOX_STEP, BOX_VELOCITY_ITERATIONS, BOX_POSITION_ITERATIONS);
			
			drawingStep();

			// Apply hero inclination smoothly
			float angle = (hero.getBody().getAngle() + getHeroInclination()) / 2f;
			hero.getBody().setTransform(hero.getPosition(), angle);

			// Limit linear velocity
			Vector2 velocity = hero.getBody().getLinearVelocity();
			if (velocity.len() > Hero.MAX_LINEAR_VELOCITY) {
				velocity = velocity.nor().scl(Hero.MAX_LINEAR_VELOCITY);
				hero.getBody().setLinearVelocity(velocity);
			}
		}
	}
	
	public void drawingStep() {
		for(int i = 0; i < userHills.size(); i++) {
			userHills.get(i).draw();
		}
	}
	
	/**
	 * Contains <strong>only</strong> display-related code
	 */
	@Override
	public void render() {
		// Clear screen
		Gdx.gl.glClearColor(0, 0.1f, 0.1f, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		spriteBatch.begin();
		
		this.deleteBonus();

		// ----- Update game logic
		step(Gdx.graphics.getDeltaTime());
		// Make the camera follow the hero
		camera.follow(hero.getPosition(), initialCameraPosition, maximumCameraPosition);
		
		// ----- Do rendering
		hero.render(spriteBatch);
		if (message.length() > 0)
			displayFont.draw(spriteBatch, message, 100, 100);
		
		MessageDisplay.displayMessages(displayFont, spriteBatch);
		
		spriteBatch.end();

		debugRenderer.render(world, camera.combined);
	}



	/**
	 * Adds a control point for the drawing.
	 * @param point the point to add to the list of control points
	 */
	public void addDrawPoint(Vector2 point) {
		//	Create a new hill if there is no current one
		if(userHills.size()==0 || userHills.get(userHills.size()-1).isFinished()) {
			UserHill newUserHill = new UserHill();
			newUserHill.addControlPoint(point, world);
			userHills.add(newUserHill);
		}
		userHills.get(userHills.size()-1).addControlPoint(point, world);
		//drawingPoints.add(point);
	}

	/**
	 * Manages the end of the drawing
	 */
	public void finishDrawing() {
		if(userHills.size() > 0) {
			UserHill latestHill = userHills.get(userHills.size() - 1);
			if (!latestHill.isFinished())
				latestHill.finishDrawing();
		}
	}

	public void validateDrawing() {
		finishDrawing();
		drawingPoints.clear();
		setDisplayDrawing(false);
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

	/**
	 * Sets the displayDrawing parameter, used to know whether something should be drawn on the screen.
	 * @param draw whether to draw
	 */
	public void setDisplayDrawing (boolean draw) {
		this.displayDrawing = draw;
	}
}
