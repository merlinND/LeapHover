package com.filrouge.leaphover.graphics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter;

public class ThrusterEffect extends ParticleEffect {
	/*
	 * PROPERTIES
	 */

	/* 
	 * METHODS
	 */
	public ThrusterEffect(float scale) {
		load(Gdx.files.internal("effects/particles.p"), Gdx.files.internal("effects"));
		
		// Adjust scaling to fit viewport
		float s = getEmitters().get(0).getScale().getHighMax();
	    getEmitters().get(0).getScale().setHigh(s * scale);

	    s = getEmitters().get(0).getScale().getLowMax();
	    getEmitters().get(0).getScale().setLow(s * scale);

	    s = getEmitters().get(0).getVelocity().getHighMax();
	    getEmitters().get(0).getVelocity().setHigh(s * scale);

	    s = getEmitters().get(0).getVelocity().getLowMax();
	    getEmitters().get(0).getVelocity().setLow(s * scale);
	}

	/**
	 * 
	 * @param angle In radians
	 */
	public void setRotation(float angle) {
		angle = (angle / (float)Math.PI) * 360f;
		
		for (ParticleEmitter pe : getEmitters()) {
			pe.getRotation().setActive(true);
			float 	low = pe.getRotation().getLowMin(),
					high = pe.getRotation().getHighMax(),
					delta = (high - low);
			System.out.println(low + " < " + high);
			pe.getRotation().setLow(angle - (delta / 2));
			pe.getRotation().setHigh(angle + (delta / 2));
		}
	}
	
	/*
	 * GETTERS & SETTERS
	 */
}
