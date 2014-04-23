package com.filrouge.leaphover.physics;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import com.filrouge.leaphover.game.Hero;

public class HoverRayCastCallback implements RayCastCallback {
	private Float distance = null;
	private Hero hoverboard;
	private Vector2 position;
	
	public HoverRayCastCallback(Hero hoverboard, Vector2 position) {
		this.hoverboard = hoverboard;
		this.position = position;
	}
	
	/**
	 * Warning : this method is called once per intersection with the ray,
	 * but not necessarily in the order that we would want it to.
	 */
	@Override
	public float reportRayFixture(Fixture fixture, Vector2 point, Vector2 normal,
			float fraction) {
		
		float dist = position.dst(point);
		
		if(distance == null || dist < distance) {
			distance = dist;
			this.hoverboard.spring(this.position, this);
		}
		return 0;
	}
	
	public float getDistance() {
		return this.distance;
	}
}
