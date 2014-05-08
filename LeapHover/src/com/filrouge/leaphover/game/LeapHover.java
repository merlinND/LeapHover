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
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.filrouge.leaphover.graphics.MessageDisplay;
import com.filrouge.leaphover.level.LevelGenerator;
import com.filrouge.leaphover.physics.CollisionDetector;
import com.filrouge.leaphover.physics.ObstacleRayCastCallback;
import com.filrouge.leaphover.score.Score;
import com.filrouge.leaphover.score.Trick;
import com.filrouge.leaphover.util.SimpleDrawer;

public class LeapHover implements ApplicationListener {
	
	/*
	 * PROPERTIES
	 */
	protected World world;
	
	protected final float WORLD_GENERATION_THRESHOLD = 1f;
	protected final float WORLD_CHUNK_WIDTH = 5 * WORLD_GENERATION_THRESHOLD;
	protected float currentWorldWidth = 0f;
	
	protected FollowCamera camera;
	protected Vector3 initialCameraPosition, maximumCameraPosition;
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
	
	/** Angle of the hero to the horizontal (in radian) */
	public static final float INITIAL_HERO_INCLINATION = 0f;
	protected float heroInclination = INITIAL_HERO_INCLINATION;
	
	/** When this flag is up, restart the level */
	protected boolean paused = false;
	protected boolean lost = false;
	
	/** Score when the user loose the game */
	protected Score score;
	protected Trick trick;
	
	/**
	 * Contact handler (detect collisions of the character with the environment)
	 */
	protected ContactListener contactListener;
	
	/** Obstacles */
	protected Body obstacle;
	protected BodyDef obstacleBodyDefinition;
	public static final float ROCK_RADIUS = 0.1f;
	protected static final double LOWER_BOUND_ODD = 0.111;
	protected static final double UPPER_BOUND_ODD = 0.112;
	public static final float TRUNK_HEIGHT = 0.1f;
	protected static final float TRUNK_WIDTH = 0.05f;
	
	/* 
	 * METHODS
	 */
	/** Private constructor (LeapHover is a Singleton) */
	private LeapHover() {
		this.score = new Score();
		this.trick = new Trick(this.score);
		
		this.obstacleBodyDefinition = new BodyDef();
		// Otherwise it doesn't fall
		this.obstacleBodyDefinition.type = BodyDef.BodyType.DynamicBody; 
		this.obstacleBodyDefinition.angularDamping = 1f;
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
			MessageDisplay.addMessage("Level "+this.score.getLevel());
			
			// TODO: leave space between current world end and next world begin
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
		this.score.reset();
		this.paused = false;
		this.lost = false;
		this.message = "";
		
		// Move to the very beginning of the level
		Vector2 position = new Vector2(camera.viewportHeight / 3f, camera.viewportHeight);
		this.hero.resetTo(position, INITIAL_HERO_INCLINATION);
		setHeroInclination(INITIAL_HERO_INCLINATION);
	}
	
	public void loseGame() {
		this.score.computeCurrentScore(this.hero.getPosition().x);
		float score = this.score.getScore();
		System.out.println(score);
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
		// If hero gets off screen, reset its position
		// TODO: remove for real gameplay
		if (hero.getPosition().y < -0.5f) {
			retryLevel();
		}
		
		this.generateObstacle();
		
		this.trick.newAngle(this.heroInclination);
		
		// Level streaming: generate more level if needed
		extendWorldIfNecessary();
		
		if(!this.paused) {
			hero.step();
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
		if (this.displayDrawing) {
			int max = this.drawingPoints.size();
			if (max >= 2) {
				for (int i = 0; i < max - 1; ++i) {
					SimpleDrawer.drawLine(this.getCamera(), this.drawingPoints.get(i), this.drawingPoints.get(i + 1));
				}
			}
		}
	}
	
	/**
	 * Randomly add obstacles
	 * TODO : Add more obstacles at each level
	 */
	private void generateObstacle() {
		double random = Math.random();
		if(random <= UPPER_BOUND_ODD && random >= LOWER_BOUND_ODD) {
			obstacle = world.createBody(this.obstacleBodyDefinition);
			
			CircleShape cshape = new CircleShape();
			cshape.setRadius(LeapHover.ROCK_RADIUS);
			obstacle.createFixture(cshape, 0);
			cshape.dispose();
			
			if(random <= (LOWER_BOUND_ODD + (UPPER_BOUND_ODD - LOWER_BOUND_ODD) / 2)) { // Tree
				PolygonShape pshape = new PolygonShape();
				pshape.setAsBox(TRUNK_WIDTH, TRUNK_HEIGHT, new Vector2(0, -ROCK_RADIUS), 0f);
				obstacle.createFixture(pshape, 0);
				pshape.dispose();
			} // Else, only rock
			
			ObstacleRayCastCallback obstacleRay = new ObstacleRayCastCallback();
			
			Vector2 heroPosition = this.hero.getPosition();
			this.world.rayCast(obstacleRay, heroPosition.add(1f, 1f), new Vector2(heroPosition.x, 0));
		}
	}
	
	public void dropObstacle(Vector2 position) {
		if(this.obstacle != null) {
			obstacle.setTransform(new Vector2(position.x, 
					position.y), 0);
			this.obstacle = null;
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

	public void addDrawing() {
		
	}

	/**
	 * Adds a control point for the drawing.
	 * @param point the point to add to the list of control points
	 */
	public void addPoint(Vector2 point) {
		drawingPoints.add(point);
	}
}
