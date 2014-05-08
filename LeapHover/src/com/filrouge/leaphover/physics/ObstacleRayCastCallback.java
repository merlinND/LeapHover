package com.filrouge.leaphover.physics;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import com.filrouge.leaphover.game.LeapHover;

public class ObstacleRayCastCallback implements RayCastCallback {

	/**
	 * Warning : this method is called once per intersection with the ray,
	 * but not necessarily in the order that we would want it to.
	 */
	@Override
	public float reportRayFixture(Fixture fixture, Vector2 point, Vector2 normal,
			float fraction) {
		
		Vector2 position = point.cpy();
		position.add(0, LeapHover.TRUNK_HEIGHT + 2 * LeapHover.ROCK_RADIUS);
		LeapHover.getInstance().dropObstacle(position);
		
		return 0;
	}
}
