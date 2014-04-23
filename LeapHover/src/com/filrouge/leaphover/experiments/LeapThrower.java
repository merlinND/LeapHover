package com.filrouge.leaphover.experiments;

import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JFrame;

import com.filrouge.leaphover.input.LeapListener;
import com.leapmotion.leap.Controller;
import com.leapmotion.leap.Gesture;

public class LeapThrower extends LeapListener {
	/*
	 * Attribute(s)
	 */
	private ArrayList<LeapListener> listeners = new ArrayList<LeapListener>();
	
	/*
	 *  Method(s)
	 */
	
	public static void main(String[]args) {
		JFrame frame = new JFrame ("test");
		frame.setSize(500, 500);
		
		HandPanel panel = new HandPanel();
		frame.setContentPane(panel);
		frame.setVisible(true);
		
		// Create a sample listener and controller
        LeapThrower listener = new LeapThrower();
        Controller controller = new Controller();

        // Have the sample listener receive events from the controller
        controller.addListener(listener);
        listener.addListener(panel.leapListener);

        // Keep this process running until Enter is pressed
        System.out.println("Press Enter to quit...");
        try {
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Remove the sample listener when done
        controller.removeListener(listener);
	}
	
	
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
    
    @Override
    public boolean handHeight(float percent) {
		for(LeapListener listener : listeners) {
			listener.handHeight(percent);
		}
		return false;
	}

	@Override
	public boolean handInclination(float percent) {
		for(LeapListener listener : listeners) {
			listener.handInclination(percent);
		}
		return false;
	}
}
