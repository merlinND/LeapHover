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
		load(Gdx.files.internal("effects/lines.p"), Gdx.files.internal("effects"));
		
		// We'll want to control the rotation / emission angle
		for (ParticleEmitter pe : getEmitters()) {
			pe.getAngle().setActive(true);
			pe.getRotation().setActive(true);
		}
		
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
		angle = (angle / (float)Math.PI) * 180f - 90;
		
		for (ParticleEmitter pe : getEmitters()) {
			float 	low = pe.getAngle().getLowMin(),
					high = pe.getAngle().getHighMax(),
					delta = (high - low);

			pe.getAngle().setLow(angle - (delta / 2));
			pe.getAngle().setHigh(angle + (delta / 2));
			pe.getRotation().setLow(angle + 90 - (delta / 2));
			pe.getRotation().setHigh(angle + 90 + (delta / 2));
		}
	}
	
	/*
	 * GETTERS & SETTERS
	 */
}
