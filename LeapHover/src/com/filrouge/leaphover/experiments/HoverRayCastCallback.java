package com.filrouge.leaphover.experiments;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.RayCastCallback;

public class HoverRayCastCallback implements RayCastCallback {
	private float distance = Float.MAX_VALUE;
	private Body board;
	private Hero hoverboard;
	
	public HoverRayCastCallback(Body board, Hero hoverboard, Vector2 position) {
		this.board = board;
		this.hoverboard = hoverboard;
	}
	
	/**
	 * Warning : this method is called once per intersection with the ray,
	 * but not necessarily in the order that we would want it to.
	 */
	@Override
	public float reportRayFixture(Fixture fixture, Vector2 point, Vector2 normal,
			float fraction) {
		
		float dist = this.board.getWorldCenter().dst(point);
		//System.out.println(dist);
		if(dist < distance) {
			distance = dist;
			this.hoverboard.spring();
		}
		return 0;
	}
	
	public float getDistance() {
		return this.distance;
	}
}
