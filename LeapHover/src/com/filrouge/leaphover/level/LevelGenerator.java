package com.filrouge.leaphover.level;

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
		BodyDef bodyDefinition = new BodyDef();
		bodyDefinition.type = BodyDef.BodyType.StaticBody;
		int n = (int)Math.ceil(width / BLOCK_WIDTH);
		for (int i = 0; i < n; i++) {
			// TODO: make sure there not to go above the specified `to`!
			bodyDefinition.position.set(from + i * BLOCK_WIDTH, 0);
			Body groundBody = world.createBody(bodyDefinition);
			HillGenerator.makeHill(groundBody, BLOCK_WIDTH, height);
		}
	}

	/*
	 * GETTERS & SETTERS
	 */
}
