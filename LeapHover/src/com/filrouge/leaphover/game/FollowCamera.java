package com.filrouge.leaphover.game;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class FollowCamera extends OrthographicCamera {
	/*
	 * PROPERTIES
	 */

	/* 
	 * METHODS
	 */
	// Constructors
	public FollowCamera() {
		super();
	}
	public FollowCamera(float viewportWidth, float viewportHeight) {
		super(viewportWidth, viewportHeight);
	}
	
	/**
	 * Center the camera on the given position
	 * TODO : make sure not to cross the world's boundaries
	 * @param target
	 */
	public void follow(Vector2 position, Vector3 min, Vector3 max) {
		position.x += viewportWidth / 4f;
		position.y += viewportHeight / 2;
		
		if (position.x < min.x)
			position.x = min.x;
		else if (position.x > max.x)
			position.x = max.x;
		if (position.y < min.y)
			position.y = min.y;
		else if (position.y > max.y)
			position.y = max.y;
		
		this.position.x = position.x;
		this.position.y = position.y;
		update();
	}
	
	
	/*
	 * GETTERS & SETTERS
	 */
}
