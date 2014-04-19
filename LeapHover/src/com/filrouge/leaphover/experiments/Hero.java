package com.filrouge.leaphover.experiments;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Transform;
import com.badlogic.gdx.physics.box2d.World;

public class Hero {
	public static float MAX_JUMP_FORCE = 0.2f;
	
	protected Body body;
	protected Fixture board, character;
	
	protected HoverRayCastCallback callbackFront;
	protected HoverRayCastCallback callbackBack;
	
	private float distance = Float.MAX_VALUE;
	
	public Hero(BodyDef bodyDefinition, World world, float side) {
		this.body = world.createBody(bodyDefinition);
		
		// Create board
		PolygonShape polygonShape = new PolygonShape();
		polygonShape.setAsBox(side * 3.5f, side);
		board = body.createFixture(polygonShape, 1);
		
		// Create character
		// TODO
		
		polygonShape.dispose();
	}
	
	public void render() {
		//make the ray at least as long as the target distance
		Vector2 startOfRay = this.body.getPosition();
		Vector2 endOfRay = new Vector2(0,-5);
		
		Vector2 front = new Vector2(startOfRay).add(0, 0);
		Vector2 bottom = new Vector2(endOfRay).add(0, 0);
		
//		callbackFront = new HoverRayCastCallback(body, this, front);
//		body.getWorld().rayCast(callbackFront, front, endOfRay);
//		callbackBack = new HoverRayCastCallback(body, this, bottom);
//		body.getWorld().rayCast(callbackBack, bottom, endOfRay);
		
	}
	
	public void spring() {
		float targetHeight = 0.2f;
		float springConstant = 0.1f;
  
		
		// http://www.iforce2d.net/b2dtut/suspension
		float distanceAboveGround = this.callbackFront.getDistance();
		while(distanceAboveGround == Float.MAX_VALUE);
		
		if (distanceAboveGround != Float.MAX_VALUE) {
			if(distanceAboveGround < targetHeight) {
				body.applyForce(body.getWorld().getGravity().mul(body.getMass()),
						body.getWorldCenter(), true);
				distanceAboveGround += 0.25f * body.getLinearVelocity().y;
				float distanceAwayFromTargetHeight = targetHeight - distanceAboveGround;
				Vector2 force = new Vector2(0.001f, springConstant*distanceAwayFromTargetHeight);
				body.applyForce(force, body.getWorldCenter(), true);
			}
		}
	}
	
	/*
	 * METHOD DELEGATION
	 */
	public Transform getTransform() {
		return body.getTransform();
	}

	public Vector2 getPosition() {
		return body.getPosition();
	}
	
	/*
	 * GETTERS & SETTERS
	 */
	public Body getBody() {
		return this.body;
	}
	public Fixture getBoard() {
		return this.board;
	}
	public Fixture getCharacter() {
		return this.character;
	}
}
