package com.filrouge.leaphover.experiments;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import com.badlogic.gdx.physics.box2d.World;
import com.filrouge.leaphover.LeapHover;
import com.filrouge.leaphover.leapcontroller.LeapListener;

public class HoverBoard {
	public static float MAX_JUMP_FORCE = 0.2f;
	
	protected Body hero;
	protected World world;
	protected HoverRayCastCallback callback;
	
	private float distance = Float.MAX_VALUE;
	
	public HoverBoard(BodyDef bodyDefinition, World world, float side) {
		this.hero = world.createBody(bodyDefinition);
		this.world=this.hero.getWorld();
		
		PolygonShape polygonShape = new PolygonShape();
		polygonShape.setAsBox(side * 3.5f, side);
		hero.createFixture(polygonShape, 1);
		
		polygonShape.dispose();
	}
	
	public Body getHero() {
		return this.hero;
	}
	
	public void render() {
		//make the ray at least as long as the target distance
		Vector2 startOfRay = this.hero.getPosition();
		Vector2 endOfRay = new Vector2(0,-5);
		
		this.callback = new HoverRayCastCallback(this.hero, this);
		this.world.rayCast(this.callback, startOfRay, endOfRay);
		
		this.hero.applyForce(new Vector2(0.001f, 0), this.hero.getPosition(), true);
		
	}
	
	public void spring() {
		float targetHeight = 0.2f;
		float springConstant = 0.1f;
  
		
		// http://www.iforce2d.net/b2dtut/suspension
		float distanceAboveGround = this.callback.getDistance();
		while(distanceAboveGround == Float.MAX_VALUE);
		
		if (distanceAboveGround != Float.MAX_VALUE) {
			System.out.println("Above ground : " + distanceAboveGround);
			if(distanceAboveGround < targetHeight) {
				this.hero.applyForce(this.world.getGravity().mul(this.hero.getMass()),
                        this.hero.getWorldCenter(), true);
				distanceAboveGround += 0.25f * this.hero.getLinearVelocity().y;
				float distanceAwayFromTargetHeight = targetHeight - distanceAboveGround;
				System.out.println(" => from target : " + distanceAwayFromTargetHeight);
				Vector2 force = new Vector2(0.001f, springConstant*distanceAwayFromTargetHeight);
				this.hero.applyForce(force, this.hero.getWorldCenter(), true);
			}
		}
	}
}
