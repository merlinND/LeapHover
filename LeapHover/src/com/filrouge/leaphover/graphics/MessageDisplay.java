package com.filrouge.leaphover.graphics;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class MessageDisplay {
	private static List<Message> messageQueue;
	private static final int DURATION = 10000;
	private static final int FIRST_Y = Gdx.graphics.getHeight();
	private static final int OFFSET_STEP = -40;
	private static final int X = 600;
	
	public static void initiate() {
		messageQueue = new ArrayList<Message>();
	}
	
	public static void displayMessages(BitmapFont displayFont, SpriteBatch spriteBatch) {
		for(int i = 0; i < messageQueue.size(); ++i) {
			displayFont.draw(spriteBatch,
					messageQueue.get(i).getText(),
					X,
					FIRST_Y + (i * OFFSET_STEP));
			if(messageQueue.get(i).decrementDuration()) {
				messageQueue.remove(i);
				--i;
			}
		}
	}
	
	public static void addMessage(Message message) {
		messageQueue.add(message);
	}
	
	public static void addMessage(String text) {
		messageQueue.add(new Message(text, DURATION));
	}
	
	public static void addMessage(String text, int duration) {
		messageQueue.add(new Message(text, duration));
	}
}
