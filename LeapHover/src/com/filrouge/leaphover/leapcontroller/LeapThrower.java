package com.filrouge.leaphover.leapcontroller;

import java.util.ArrayList;

import com.badlogic.gdx.InputProcessor;
import com.leapmotion.leap.*;

public class LeapThrower extends Listener {
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
	
	public void touchDown(int x, int y) {
		// todo : call right method depending on the event
		for(InputProcessor listener : listeners) {
			listener.touchDown(x, y, -1, -1);
		}
	}
	
	public void touchUp(int x, int y) {
		// todo : call right method depending on the event
		for(InputProcessor listener : listeners) {
			listener.touchUp(x, y, -1, -1);
		}
	}
	
	public void touchDragged(int x, int y) {
		// todo : call right method depending on the event
		for(InputProcessor listener : listeners) {
			listener.touchDragged(x, y, -1);
		}
	}
	
	public void keyDown(int key) {
		// todo : call right method depending on the event
		for(InputProcessor listener : listeners) {
			listener.keyDown(key);
		}
	}
	
	public void keyUp(int key) {
		// todo : call right method depending on the event
		for(InputProcessor listener : listeners) {
			listener.keyUp(key);
		}
	}
	
	public void handPosition(int percent) {
		for(LeapListener listener : listeners) {
			listener.handPosition(percent);
		}
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
            		handPosition((int) ((positionY/maxHeight)*100));
            	}
            }
        }
    }
}
