package com.filrouge.leaphover.leapcontroller;

import com.badlogic.gdx.InputProcessor;

public interface LeapListener extends InputProcessor
{
	// Method(s)
	public boolean handPosition(int percent);
}
