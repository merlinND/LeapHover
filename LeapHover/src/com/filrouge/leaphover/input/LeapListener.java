package com.filrouge.leaphover.input;

import com.leapmotion.leap.Controller;
import com.leapmotion.leap.Frame;
import com.leapmotion.leap.Hand;
import com.leapmotion.leap.Listener;

/**
 * Our intermediate interface to process Leap Motion events
 * @author Rémi Martin
 */
public abstract class LeapListener extends Listener
{
	protected final double MIN_HAND_ANGLE = 0.;
	protected final double MAX_HAND_ANGLE = 0.2;
	
	public static final double MIN_HAND_INCLINATION = 0.5;
	public static final double MAX_HAND_INCLINATION = 3.2;
	
	@Override
	public void onFrame(Controller controller) {
		Frame frame = controller.frame();

		float maxHeight = frame.interactionBox().height();
		
		/*
		 * Hand up/down movement 
		 */
		if (!frame.hands().isEmpty()) {
		    // Get one hand
			// TODO : Recognize which hand it is
			Hand hand = frame.hands().get(0);
			
			if(hand.isValid()) {
				float normalX = hand.palmNormal().get(0);
				// "Flat" hand
				// TODO : do we really want the hand to be "flat" ?
		    	if(Math.abs(normalX) > MIN_HAND_ANGLE && Math.abs(normalX) < MAX_HAND_ANGLE) {
		    		float y = hand.palmPosition().get(1);
		    		y = Math.min(y, maxHeight);
		    		
		    		handHeight(y / maxHeight);
		    	}
		    	
		    	float angle = - hand.palmNormal().pitch();
		    	handInclination(angle / (float)(MAX_HAND_INCLINATION - MIN_HAND_INCLINATION));
		    }
		}
		else {
			noHand();
		}
	}
	
	/**
	 * Process variations of one hand's height
	 * @param percent
	 */
	public boolean handHeight(float percent) {
		return false;
	}
	
	/**
	 * Process variations of one hand's inclination
	 * @param percent of rotation with repspect to the negative z-axis ("pitch")
	 * @return
	 */
	public boolean handInclination(float percent) {
		return false;
	}
	
	/**
	 * Event dispatched when no hand is detected.
	 * Useful to reset controls to default value, for example.
	 * @return
	 */
	public boolean noHand() {
		return false;
	}
}
