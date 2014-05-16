package com.filrouge.leaphover.physics;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import com.filrouge.leaphover.game.Hero;
import com.filrouge.leaphover.level.LevelGenerator;

public class HoverRayCastCallback implements RayCastCallback {
	private Hero hoverboard;
	private Float distance = null;
	private Vector2 position = null;
	
	public HoverRayCastCallback(Hero hoverboard) {
		this.hoverboard = hoverboard;
	}
	
	/**
	 * Warning : this method is called once per intersection with the ray,
	 * but not necessarily in the order that we would want it to.
	 */
	@Override
	public float reportRayFixture(Fixture fixture, Vector2 point, Vector2 normal,
			float fraction) {
		// If not bonus
		// TODO: make cleaner
		if(fixture.getShape().getRadius() != LevelGenerator.BONUS_RADIUS) {
		
			float dist = position.dst(point);
			
			if(distance == null || dist < distance) {
				distance = dist;
				this.hoverboard.spring(this.position, this);
			}
		}
		return 0;
	}
	
	public void reset(Vector2 position) {
		this.distance = null;
		this.position = position;
	}
	
	public float getDistance() {
		return this.distance;
	}
}
