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
		float targetHeight = 0.4f;
		float springConstant = 0.05f;
  
		//make the ray at least as long as the target distance
		Vector2 startOfRay = this.hero.getPosition();
		Vector2 endOfRay = new Vector2(0,-5);
	  
		HoverRayCastCallback callback = new HoverRayCastCallback(this.hero);
		this.world.rayCast(callback, startOfRay, endOfRay);
		
		float distanceAboveGround = callback.getDistance();
		if (distanceAboveGround != Float.MAX_VALUE) {
			System.out.println("Above ground : " + distanceAboveGround);
			if(distanceAboveGround < targetHeight) {
				float distanceAwayFromTargetHeight = targetHeight - distanceAboveGround;
				System.out.println(" => from target : " + distanceAwayFromTargetHeight);
				Vector2 force = new Vector2(0.005f, springConstant*distanceAwayFromTargetHeight);
				this.hero.applyForce(force, this.hero.getWorldCenter(), true);
			}
		}
	}
}
