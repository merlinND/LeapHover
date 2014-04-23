package com.filrouge.leaphover.game;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Transform;
import com.badlogic.gdx.physics.box2d.World;
import com.filrouge.leaphover.physics.HoverRayCastCallback;
import com.filrouge.leaphover.util.SimpleDrawer;

public class Hero {
	public static final float MAX_LINEAR_VELOCITY = 1.5f;
	
	public static final float MAX_JUMP_FORCE = 0.1f;
	public static final boolean ALLOW_MID_AIR_JUMP = false;
	
	public static final float REACTOR_HEIGHT = 0.15f;
	public static final float SPRING_CONSTANT = 0.1f;
	public static final float MAX_SPRING_FORCE = 0.1f;
	public static final float DAMPENING_FACTOR = 0.1f;
	
	protected Body body;
	protected Fixture board, character;
	protected float boardWidth;
	
	protected HoverRayCastCallback callbackFront;
	protected HoverRayCastCallback callbackBack;
	protected boolean isCloseToGround;
	
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
		
		// Other initializations
		callbackFront = new HoverRayCastCallback(this);
		callbackBack = new HoverRayCastCallback(this);
		isCloseToGround = false;
	}
	
	public void step() {
		//make the ray at least as long as the target distance
		Vector2 startOfRay = this.body.getWorldCenter();
		float angle = this.body.getAngle();
		
		Vector2 front = new Vector2(startOfRay).add((float) (this.boardWidth / 2*Math.cos(angle)), 
													(float) (this.boardWidth / 2*Math.sin(angle)));
		Vector2 back = new Vector2(startOfRay).add((float) (- this.boardWidth / 2*Math.cos(angle)), 
													(float) (- this.boardWidth / 2*Math.sin(angle)));
		
		Vector2 normal = new Vector2(REACTOR_HEIGHT * (float)Math.sin(angle),
									 - REACTOR_HEIGHT * (float)Math.cos(angle));
		Vector2 endOfRayFront = front.cpy().add(normal);
		Vector2 endOfRayBack = back.cpy().add(normal);
		
		// Debug display
		SimpleDrawer.drawLine(LeapHover.getInstance().getCamera(), front, endOfRayFront);
		SimpleDrawer.drawLine(LeapHover.getInstance().getCamera(), back, endOfRayBack);
		
		callbackFront.reset(front);
		callbackBack.reset(back);
		// If there's no intersection, no callback will be called
		this.isCloseToGround = false;
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
		this.isCloseToGround = true;
		
		distanceToGround = Math.abs(distanceToGround);
		if(distanceToGround < REACTOR_HEIGHT) {
			// Dampening
			// TODO : fix dampening computation to take into account both directions?
			distanceToGround += DAMPENING_FACTOR * body.getLinearVelocity().y;
			
			float magnitude = SPRING_CONSTANT * (REACTOR_HEIGHT - distanceToGround);
			magnitude = Math.abs(magnitude);
			
			float angle = getBody().getAngle();

			Vector2 force = new Vector2(- magnitude * (float)Math.sin(angle), magnitude * (float)Math.cos(angle));
			if (force.len() > MAX_SPRING_FORCE)
				force = force.nor().scl(MAX_SPRING_FORCE);
			
			body.applyForce(force, position, true);

			Vector2 end = position.cpy().add(force.cpy().scl(10f));
			SimpleDrawer.drawLine(LeapHover.getInstance().getCamera(), position, end, Color.RED);
		}
	}
	
	/**
	 * Jump perpendicular to current inclination
	 * TODO: define unit of amount
	 * TODO: change jump mechanism to take advantage of the spring effect
	 * @param amount
	 */
	public void jump(float amount) {
		if (canJump()) {
			float angle = getBody().getAngle();
			Vector2 force = new Vector2(- (float)Math.sin(angle),
					 					(float)Math.cos(angle));
			force = force.scl(amount * MAX_JUMP_FORCE);
			getBody().applyForce(force, getPosition(), true);
		}
	}
	/**
	 * The hero can jump either if it is allowed to jump mid-air
	 * or if it has at least one "reactor" close to the ground
	 * @return
	 */
	public boolean canJump() {
		if (ALLOW_MID_AIR_JUMP)
			return true;
		else
			return isCloseToGround;
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
