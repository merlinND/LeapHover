package com.filrouge.leaphover.physics;

import java.util.concurrent.Callable;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;

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
        if (a.equals(target) || b.equals(target)) {
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
	public void preSolve(Contact arg0, Manifold arg1) {
		// TODO Auto-generated method stub
		
	}
}
