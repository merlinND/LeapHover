package com.filrouge.leaphover.graphics;

/**
 * @author remimartin
 * TODO : Add position ?
 */
public class Message {
	protected String text;
	protected int duration;
	
	public Message(String text, int duration) {
		this.text=text;
		this.duration=duration;
	}
	
	// Getter(s)
	public String getText() {
		return this.text;
	}
	
	public int getDuration() {
		return this.duration;
	}
	
	// Method(s)
	public boolean decrementDuration() {
		if(this.duration > 0) {
			--this.duration;
			return false;
		} else {
			return true;
		}
	}
	
	public boolean decrementDuration(int amount) {
		this.duration-=amount;
		if(this.duration < 0) {
			return true;
		}
		return false;
	}
}
