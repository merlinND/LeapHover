package com.filrouge.leaphover.experiments;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;

public class BodyCollision implements ContactListener {
	protected static final float hoverForce = 0.05f;

	@Override
	public void beginContact(Contact contact) {
		/*Fixture fixtureA = contact.getFixtureA();
        Fixture fixtureB = contact.getFixtureB();
        
        // We don't have to find out which body is the hoverboard, we can apply the force to both of them
        // it doesn't matter, the ground is static.
        fixtureA.getBody().applyForce(new Vector2(0, BodyCollision.hoverForce), fixtureA.getBody().getPosition(), false);
        fixtureB.getBody().applyForce(new Vector2(0, BodyCollision.hoverForce), fixtureB.getBody().getPosition(), false);
        Gdx.app.log("beginContact", "between " + fixtureA.toString() + " and " + fixtureB.toString());*/
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
