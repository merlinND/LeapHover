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
	/** Parameters for the width of hills blocks */
	protected static final float MIN_BLOCK_WIDTH = 0.3f;
	protected static final float BASIC_BLOCK_WIDTH = 2f;
	protected static final float BLOCK_WIDTH_RANDOM_OFFSET_RANGE = 1f;
	/** Parameters for the gaps between blocks */
	protected static final float MAX_GAP_WIDTH = 1f;
	protected static final float BASIC_GAP_WIDTH = 0.3f;
	protected static final float GAP_RANDOM_OFFSET_RANGE = 0.1f;
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
		// Generate independant hills, each one with a random width
		// so as to fill the demanded width
		// TODO: do not to go above the specified `to`
		BodyDef bodyDefinition = new BodyDef();
		bodyDefinition.type = BodyDef.BodyType.StaticBody;
		float currentX = from;
		while (currentX < to) {
			float minY = (height / 10f);
			float width = getRandomWidthAtPosition(currentX);
			// Caution: the gap width can be negative for small `currentX`
			float gapWidth = getRandomGapAtPosition(currentX);
			float smoothness = getSmoothnessAtPosition(currentX);
			
			System.out.println("This block starting at " + currentX + " has gap width " + gapWidth);
			bodyDefinition.position.set(currentX + gapWidth, 0);
			Body groundBody = world.createBody(bodyDefinition);
			HillGenerator.makeHill(groundBody, width, height, new Vector2(0f, minY), smoothness);
			currentX += width + gapWidth;
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
	
	/**
	 * The more advanced the position, the narrower the blocks.
	 * @param x
	 * @return
	 */
	protected static float getRandomWidthAtPosition(float x) {
		// TODO: adjust width progression curve
		float width = BASIC_BLOCK_WIDTH * (3f - (float)Math.log10(4f * x + 1.001f));
		float offset = BLOCK_WIDTH_RANDOM_OFFSET_RANGE * (float)(Math.random() - 0.5f);
		return Math.max(MIN_BLOCK_WIDTH, width + offset);
	}

	/**
	 * The more advanced the position, the longer the gaps.
	 * For small x, the gaps can be negative (thus we obtain hills overlap).
	 * @param x
	 * @return
	 */
	protected static float getRandomGapAtPosition(float x) {
		// TODO: adjust width progression curve
		float width = BASIC_GAP_WIDTH * (float)(Math.log10(4f * x + 10f) - 2f);
		float offset = GAP_RANDOM_OFFSET_RANGE * (float)(Math.random() - 0.5f);
		
		return Math.min(MAX_GAP_WIDTH, width + offset);
	}

	/*
	 * GETTERS & SETTERS
	 */
}
