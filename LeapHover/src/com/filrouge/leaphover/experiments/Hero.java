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
	protected float boardWidth;
	
	protected HoverRayCastCallback callbackFront;
	protected HoverRayCastCallback callbackBack;
	
	private float distance = Float.MAX_VALUE;
	
	public Hero(BodyDef bodyDefinition, World world, float side) {
		this.body = world.createBody(bodyDefinition);
		this.boardWidth = side * 3.5f;
		
		PolygonShape polygonShape = new PolygonShape();
		// Create board
		float boardHeight = side;
		polygonShape.setAsBox(this.boardWidth, side);
		board = body.createFixture(polygonShape, 1);
		
		// Create character
		float characterHeight = side * 1.5f;
		Vector2 characterOffset = new Vector2(0, characterHeight + boardHeight);
		polygonShape.setAsBox(side, characterHeight, characterOffset, 0f);
		// TODO: should the character have a density (and thus a mass)?
		character = body.createFixture(polygonShape, 0);
		
		polygonShape.dispose();
	}
	
	public void render() {
		//make the ray at least as long as the target distance
		Vector2 startOfRay = this.body.getWorldCenter();
		float angle = this.body.getAngle();
		
		Vector2 front = new Vector2(startOfRay).add((float) (this.boardWidth/2*Math.cos(angle)), 
													(float) (this.boardWidth/2*Math.sin(angle)));
		Vector2 back = new Vector2(startOfRay).add((float) (this.boardWidth/2*Math.cos(angle)*-1), 
													(float) (this.boardWidth/2*Math.sin(angle)*-1));
		
		Vector2 endOfRayFront = new Vector2(front.x,-5);
		Vector2 endOfRayBack = new Vector2(back.x,-5);
		
		callbackFront = new HoverRayCastCallback(body, this, front);
		body.getWorld().rayCast(callbackFront, front, endOfRayFront);
		callbackBack = new HoverRayCastCallback(body, this, back);
		body.getWorld().rayCast(callbackBack, back, endOfRayBack);
	}
	
	public void spring(Vector2 position, HoverRayCastCallback callback) {
		float targetHeight = 0.1f;
		float springConstant = 0.1f;
  
		
		// http://www.iforce2d.net/b2dtut/suspension
		float distanceAboveGround = callback.getDistance();
		while(distanceAboveGround == Float.MAX_VALUE);
		
		if (distanceAboveGround != Float.MAX_VALUE) {
			if(distanceAboveGround < targetHeight) {
				//body.applyForce(body.getWorld().getGravity().mul(body.getMass()), body.getWorldCenter(), true);
				distanceAboveGround += 0.25f * body.getLinearVelocity().y;
				float distanceAwayFromTargetHeight = targetHeight - distanceAboveGround;
				Vector2 force = new Vector2(0.001f, springConstant*distanceAwayFromTargetHeight);
				body.applyForce(force, position, true);
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
