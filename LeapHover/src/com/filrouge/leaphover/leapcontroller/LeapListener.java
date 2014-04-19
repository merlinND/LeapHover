package com.filrouge.leaphover.leapcontroller;

import com.leapmotion.leap.Listener;

/**
 * Our intermediate interface to process Leap Motion events
 * @author Rémi Martin
 */
public abstract class LeapListener extends Listener
{
	/**
	 * Process variations of one hand's height
	 * @param percent
	 */
	public abstract boolean handHeight(int percent);
}
