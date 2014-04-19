package com.filrouge.leaphover.experiments;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

public class HoverBoard {
	public static float MAX_JUMP_FORCE = 0.2f;
	
	protected Body hero;
	
	public HoverBoard(BodyDef bodyDefinition, World world, float side) {
		this.hero = world.createBody(bodyDefinition);
		
		PolygonShape polygonShape = new PolygonShape();
		polygonShape.setAsBox(side, side);
		hero.createFixture(polygonShape, 1);
		
		polygonShape.dispose();
	}
	
	public Body getHero() {
		return this.hero;
	}
	
	public void render() {
		this.hero.applyForce(new Vector2(0.003f, 0), hero.getPosition(), false);
	}
}
