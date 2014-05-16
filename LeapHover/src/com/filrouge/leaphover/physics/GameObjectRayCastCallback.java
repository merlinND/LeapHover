package com.filrouge.leaphover.physics;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import com.filrouge.leaphover.level.GameObjectType;
import com.filrouge.leaphover.level.LevelGenerator;

public class GameObjectRayCastCallback implements RayCastCallback {
	protected GameObjectType type;
	protected Body objectToDrop;
	
	public GameObjectRayCastCallback(GameObjectType type, Body object) {
		this.type = type;
		this.objectToDrop = object;
	}
	/**
	 * Warning : this method is called once per intersection with the ray,
	 * but not necessarily in the order that we would want it to.
	 */
	@Override
	public float reportRayFixture(Fixture fixture, Vector2 point, Vector2 normal,
			float fraction) {
		
		Vector2 position = point.cpy();
		float offset = LevelGenerator.ROCK_RADIUS;
		if(type == GameObjectType.TREE) {
			offset += LevelGenerator.TRUNK_HEIGHT;
		} else if(type == GameObjectType.BONUS) {
			offset = LevelGenerator.BONUS_RADIUS * 2;
		}
		position.add(0, offset);
		
		dropGameObject(position);
		return 0;
	}
	
	public void dropGameObject(Vector2 position) {
		if(objectToDrop != null) {
			objectToDrop.setTransform(new Vector2(position.x, position.y), 0);
			objectToDrop = null;
		}
	}
}
