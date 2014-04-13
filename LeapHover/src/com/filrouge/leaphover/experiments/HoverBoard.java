package com.filrouge.leaphover.experiments;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.filrouge.leaphover.leapcontroller.LeapListener;

public class HoverBoard implements LeapListener {
	protected static float MAX_JUMP_FORCE=0.2f;
	
	protected Body hero;
	protected int time=0;
	protected int leapTime=0;
	protected int percentSum=0;
	
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
		if(Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
			this.time++;
		} else if(!Gdx.input.isKeyPressed(Input.Keys.DOWN) && this.time > 0) {
			this.time=0;
			this.hero.applyForce(new Vector2(0, 0.3f), hero.getPosition(), false);
		}
	}

	@Override
	public boolean keyDown(int arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean keyTyped(char arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean keyUp(int arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean mouseMoved(int arg0, int arg1) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean scrolled(int arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDown(int arg0, int arg1, int arg2, int arg3) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDragged(int arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchUp(int arg0, int arg1, int arg2, int arg3) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean handPosition(int percent) {
		if(percent >= 50 && this.leapTime > 0) {
			int sum = this.percentSum;
			this.percentSum=0;
			float avg = sum / this.leapTime;
			this.leapTime=0;
			
			this.hero.applyForce(new Vector2(0, (avg*MAX_JUMP_FORCE)/100), this.hero.getPosition(), false);
		} else if(percent < 40) {
			this.percentSum+=100-percent;
			++this.leapTime;
		}
		return false;
	}
}
