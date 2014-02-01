package com.filrouge.leaphover;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.filrouge.leaphover.experiments.HillGenerator;

public class LeapHover implements ApplicationListener {
	private World world;
	private OrthographicCamera camera;
	private Box2DDebugRenderer debugRenderer;
	
	/** Rates at which the physical simulation advances */
	public static final Vector2 GRAVITY = new Vector2(0, -1f);
	public static final float BOX_STEP = (1 / 60f);  
	public static final int BOX_VELOCITY_ITERATIONS = 6;  
	public static final int BOX_POSITION_ITERATIONS = 2; 
	
	@Override
	public void create() {		
		float w = Gdx.graphics.getWidth();
		float h = Gdx.graphics.getHeight();
		
		world = new World(GRAVITY, true);
				
		camera = new OrthographicCamera(1, h/w);
		camera.position.set(camera.viewportWidth / 2f, camera.viewportHeight / 2f, 0f);
		camera.update();
		
		debugRenderer = new Box2DDebugRenderer();
		
		setupTestScene();
	}

	protected void setupTestScene() {
		// Ground
		BodyDef bodyDefinition = new BodyDef();
		bodyDefinition.type = BodyDef.BodyType.StaticBody;
		bodyDefinition.position.set(camera.viewportWidth / 2f, 0);
		Body groundBody = world.createBody(bodyDefinition);
		HillGenerator.makeHill(groundBody, camera.viewportWidth, camera.viewportHeight);
		
		// Falling box
		bodyDefinition.type = BodyDef.BodyType.DynamicBody;
		bodyDefinition.position.set(camera.viewportWidth / 2f, camera.viewportHeight);
		Body boxBody = world.createBody(bodyDefinition);
		
		float side = camera.viewportHeight / 20f;
		PolygonShape polygonShape = new PolygonShape();
		polygonShape.setAsBox(side, side);
		boxBody.createFixture(polygonShape, 1);
		
		polygonShape.dispose();
	}
	
	@Override
	public void dispose() {
		debugRenderer.dispose();
	}

	@Override
	public void render() {		
		Gdx.gl.glClearColor(0, 0.1f, 0.1f, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		
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
	}
}
