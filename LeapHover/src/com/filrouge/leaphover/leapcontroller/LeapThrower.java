package com.filrouge.leaphover.leapcontroller;

import java.util.ArrayList;

import com.leapmotion.leap.Controller;
import com.leapmotion.leap.Frame;
import com.leapmotion.leap.Gesture;
import com.leapmotion.leap.Hand;

public class LeapThrower extends LeapListener {
	/*
	 * Attribute(s)
	 */
	private ArrayList<LeapListener> listeners = new ArrayList<LeapListener>();
	private int maxHeight;
	
	/*
	 *  Method(s)
	 */
	public void addListener(LeapListener toAdd) {
		listeners.add(toAdd);
	}
	
	// LEAP
	public void onInit(Controller controller) {
        System.out.println("Initialized");
    }

    public void onConnect(Controller controller) {
        System.out.println("Connected");
        controller.enableGesture(Gesture.Type.TYPE_SWIPE);
        controller.enableGesture(Gesture.Type.TYPE_CIRCLE);
        controller.enableGesture(Gesture.Type.TYPE_SCREEN_TAP);
        controller.enableGesture(Gesture.Type.TYPE_KEY_TAP);
    }

    public void onDisconnect(Controller controller) {
        //Note: not dispatched when running in a debugger.
        System.out.println("Disconnected");
    }

    public void onExit(Controller controller) {
        System.out.println("Exited");
    }

    public void onFrame(Controller controller) {
        // Get the most recent frame and report some basic information
        Frame frame = controller.frame();

        maxHeight=(int)frame.interactionBox().height();
        if (!frame.hands().isEmpty()) {
            // Get the first hand
        	// TODO : Recognize what hand it is
            Hand hand = frame.hands().get(0);
            
            if(hand.isValid()) {
            	/*
            	 * Hand up/down movement 
            	 */
            	float normalX = hand.palmNormal().get(0);
            	// "Flat" hand
            	if(Math.abs(normalX) < 0.2 && Math.abs(normalX) > 0) {
            		float positionY = hand.palmPosition().get(1);
            		positionY = (positionY < maxHeight) ? positionY : maxHeight;
            		handHeight((int) ((positionY/maxHeight)*100));
            	}
            }
        }
    }
    
    @Override
    public boolean handHeight(int percent) {
		for(LeapListener listener : listeners) {
			listener.handHeight(percent);
		}
		return false;
	}
}
