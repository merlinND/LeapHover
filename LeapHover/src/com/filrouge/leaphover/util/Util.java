package com.filrouge.leaphover.util;

/**
 * Collection of useful functions
 * @author Merlin Nimier-David
 *
 */
public class Util {
	/* 
	 * METHODS
	 */
	/**
	 * @param min
	 * @param max
	 * @param progress
	 * @return The number corresponding to <code>progress</code> percent of the given interval
	 */
	public static float progress(float min, float max, float progress) {
		return progress * (max - min) + min;
	}
	
	/**
	 * @param min
	 * @param max
	 * @param x
	 * @return min <= x <= max
	 */
	public static boolean in(float min, float max, float x) {
		return (min <= x) && (x <= max);
	}
	
}
