package com.filrouge.leaphover.leapcontroller;

import java.util.ArrayList;

public class LeapThrower {
	/*
	 * Attribute(s)
	 */
	private ArrayList<LeapListener> listeners = new ArrayList<LeapListener>();
	
	/*
	 *  Method(s)
	 */
	public void addListener(LeapListener toAdd) {
		listeners.add(toAdd);
	}
	
	public void event() {
		// todo : call right method depending on the event
		for(LeapListener listener : listeners) {
			listener.event();
		}
	}
}
