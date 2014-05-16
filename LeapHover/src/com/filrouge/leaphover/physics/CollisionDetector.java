package com.filrouge.leaphover.physics;

import java.util.concurrent.Callable;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.filrouge.leaphover.game.LeapHover;
import com.filrouge.leaphover.level.LevelGenerator;

public class CollisionDetector implements ContactListener {
	protected Fixture target;
	protected Callable<Boolean> callback;
	
	public CollisionDetector(Fixture target, Callable<Boolean> callback) {
		this.target = target;
		this.callback = callback;
	}
	
	@Override
	public void beginContact(Contact contact) {
		Fixture a = contact.getFixtureA();
        Fixture b = contact.getFixtureB();
        if (a.equals(target) || b.equals(target) && 
        		(a.getShape().getRadius() == LevelGenerator.BONUS_RADIUS ||
        		b.getShape().getRadius() == LevelGenerator.BONUS_RADIUS)) {
        	try {
				callback.call();
			} catch (Exception e) {
				e.printStackTrace();
			}
        }
	}

	@Override
	public void endContact(Contact arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void postSolve(Contact arg0, ContactImpulse arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void preSolve(Contact contact, Manifold arg1) {
		Fixture a = contact.getFixtureA();
        Fixture b = contact.getFixtureB();
        
        // TODO: make bonus detection cleaner (avoid hit & death)
        if((a.getShape().getRadius() == LevelGenerator.BONUS_RADIUS ||
        		b.getShape().getRadius() == LevelGenerator.BONUS_RADIUS) &&
        		(LeapHover.getInstance().getHero().getBody().getFixtureList().contains(a, false) || 
        				LeapHover.getInstance().getHero().getBody().getFixtureList().contains(b, false))) {
        	Fixture bonus = (a.getShape().getRadius() == LevelGenerator.BONUS_RADIUS) ? a : b;
        	
        	LeapHover.getInstance().bonusPicked(bonus);
        }
	}
}
