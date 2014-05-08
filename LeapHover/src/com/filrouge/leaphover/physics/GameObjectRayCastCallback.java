package com.filrouge.leaphover.physics;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import com.filrouge.leaphover.game.LeapHover;
import com.filrouge.leaphover.level.GameObjectType;

public class GameObjectRayCastCallback implements RayCastCallback {
	protected GameObjectType type;

	public GameObjectRayCastCallback(GameObjectType type) {
		this.type = type;
	}
	/**
	 * Warning : this method is called once per intersection with the ray,
	 * but not necessarily in the order that we would want it to.
	 */
	@Override
	public float reportRayFixture(Fixture fixture, Vector2 point, Vector2 normal,
			float fraction) {
		
		Vector2 position = point.cpy();
		float offset = LeapHover.ROCK_RADIUS;
		if(type == GameObjectType.TREE) {
			offset += LeapHover.TRUNK_HEIGHT;
		} else if(type == GameObjectType.BONUS) {
			offset = LeapHover.BONUS_RADIUS * 2;
		}
		position.add(0, offset);
		LeapHover.getInstance().dropGameObject(position);
		
		return 0;
	}
}
