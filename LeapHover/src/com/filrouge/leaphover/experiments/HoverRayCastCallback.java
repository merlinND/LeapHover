package com.filrouge.leaphover.experiments;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.RayCastCallback;

public class HoverRayCastCallback implements RayCastCallback {
	private float distance = Float.MAX_VALUE;
	private Body hero;
	private HoverBoard hoverboard;
	
	public HoverRayCastCallback(Body hero, HoverBoard hoverboard, Vector2 position) {
		this.hero=hero;
		this.hoverboard=hoverboard;
	}
	
	@Override
	public float reportRayFixture(Fixture fixture, Vector2 point, Vector2 normal,
			float fraction) {
		// not ordered
		
		float dist = this.hero.getWorldCenter().dst(point);
		//System.out.println(dist);
		if(dist < distance) {
			distance=dist;
			this.hoverboard.spring();
		}
		return 0;
	}
	
	public float getDistance() {
		return this.distance;
	}
}
