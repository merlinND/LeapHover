package com.filrouge.leaphover.level;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.World;

/**
 * Generate an infinitely long level made of increasingly difficult hills.
 * @author Merlin Nimier-David
 */
public class LevelGenerator {
	/*
	 * PROPERTIES
	 */
	/**
	 * The default width of a hill
	 * TODO: randomize and make it depend on the difficulty
	 */
	public static final float BLOCK_WIDTH = 2f;
	/** TODO: make configurable */
	protected static final float DIFFICULTY_FACTOR = 0.1f;

	/* 
	 * METHODS
	 */
	/**
	 * Add hills to fill up the world from <tt>from</tt> to <tt>to</tt>
	 * TODO: add difficulty parameter
	 * @param world
	 * @param from
	 * @param to
	 * @param height
	 */
	public static void generate(World world, float from, float to, float height) {
		float width = to - from;
		
		// Generate independant hills, each one with width BLOCK_WIDTH
		// so as to fill the demanded width
		// TODO: do not to go above the specified `to`
		BodyDef bodyDefinition = new BodyDef();
		bodyDefinition.type = BodyDef.BodyType.StaticBody;
		int n = (int)Math.ceil(width / BLOCK_WIDTH);
		for (int i = 0; i < n; i++) {
			float beginX = from + i * BLOCK_WIDTH;
			float minY = (height / 10f);
			
			float smoothness = getSmoothnessAtPosition(beginX);
			System.out.println("This block starting at " + beginX + " has smoothness " + smoothness);
			bodyDefinition.position.set(beginX, 0);
			Body groundBody = world.createBody(bodyDefinition);
			HillGenerator.makeHill(groundBody, BLOCK_WIDTH, height, new Vector2(0f, minY), smoothness);
		}
	}
	
	/**
	 * The more advanced the position, the rougher the generated hills.
	 * @param x
	 * @return
	 */
	protected static float getSmoothnessAtPosition(float x) {
		// TODO: adjust smoothness progression curve
		return 1 + (1 / (float)(Math.log(x * DIFFICULTY_FACTOR + 1.001f)));
	}

	/*
	 * GETTERS & SETTERS
	 */
}
