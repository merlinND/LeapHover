package com.filrouge.leaphover.experiments;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Transform;
import com.badlogic.gdx.physics.box2d.World;
import com.filrouge.leaphover.LeapHover;

public class Hero {
	public static final float MAX_JUMP_FORCE = 0.1f;
	public static final float TARGET_HEIGHT = 0.15f;
	public static final float SPRING_CONSTANT = 0.1f;
	public static final float MAX_SPRING_FORCE = 0.1f;
	
	protected Body body;
	protected Fixture board, character;
	protected float boardWidth;
	
	protected HoverRayCastCallback callbackFront;
	protected HoverRayCastCallback callbackBack;
	
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
		
		Vector2 front = new Vector2(startOfRay).add((float) (this.boardWidth / 2*Math.cos(angle)), 
													(float) (this.boardWidth / 2*Math.sin(angle)));
		Vector2 back = new Vector2(startOfRay).add((float) (- this.boardWidth / 2*Math.cos(angle)), 
													(float) (- this.boardWidth / 2*Math.sin(angle)));
		
		Vector2 normal = new Vector2(TARGET_HEIGHT * (float)Math.sin(angle),
									 - TARGET_HEIGHT * (float)Math.cos(angle));
		Vector2 endOfRayFront = front.cpy().add(normal);
		Vector2 endOfRayBack = back.cpy().add(normal);
		
		// Debug display
		SimpleDrawer.drawLine(LeapHover.getInstance().getCamera(), front, endOfRayFront);
		SimpleDrawer.drawLine(LeapHover.getInstance().getCamera(), back, endOfRayBack);
		
		callbackFront = new HoverRayCastCallback(this, front);
		callbackBack = new HoverRayCastCallback(this, back);
		// If there's no intersection, no callback will be called
		body.getWorld().rayCast(callbackFront, front, endOfRayFront);
		body.getWorld().rayCast(callbackBack, back, endOfRayBack);
	}

	/**
	 * 
	 * @param position Point at which to apply the springing force
	 * @param callback
	 * @see http://www.iforce2d.net/b2dtut/suspension
	 */
	public void spring(Vector2 position, HoverRayCastCallback callback) {
		Float distanceToGround = callback.getDistance();
		// TODO: find a better way to wait for callback to be executed
		while(distanceToGround == null);
		System.out.println("Distance to ground reported : " + distanceToGround);
		distanceToGround = Math.abs(distanceToGround);
		if(distanceToGround < TARGET_HEIGHT) {
			// Dampening
			// TODO : fix dampening computation to take into account both directions
			//distanceToGround += 0.25f * body.getLinearVelocity().y;
			
			float magnitude = SPRING_CONSTANT * (TARGET_HEIGHT - distanceToGround);
			magnitude = Math.abs(magnitude);
			
			float angle = getBody().getAngle();

			Vector2 force = new Vector2(- magnitude * (float)Math.sin(angle), magnitude * (float)Math.cos(angle));
			if (force.len() > MAX_SPRING_FORCE)
				force = force.nor().scl(MAX_SPRING_FORCE);
			
			force.x = 0;
			body.applyForce(force, position, true);

			//System.out.println("Springing with force : " + force + " => " + force.len());
			Vector2 end = position.cpy().add(force.cpy().scl(10f));
			SimpleDrawer.drawLine(LeapHover.getInstance().getCamera(), position, end, Color.RED);
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
