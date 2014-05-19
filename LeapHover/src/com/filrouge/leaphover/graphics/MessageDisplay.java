package com.filrouge.leaphover.graphics;

import java.util.LinkedList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class MessageDisplay {
	private static LinkedList<Message> messageQueue;
	private static final int DURATION = 200;
	private static final int OFFSET_STEP = 30;
	private static final int FIRST_Y = Gdx.graphics.getHeight() - OFFSET_STEP;
	private static final int X = 4 * Gdx.graphics.getWidth() / 5;
	
	public static void initiate() {
		messageQueue = new LinkedList<Message>();
	}
	
	/**
	 * @warning This method calls spriteBatch.begin() itself! (It must call it once per message)
	 * @param displayFont
	 * @param spriteBatch
	 */
	public static void displayMessages(BitmapFont displayFont, SpriteBatch spriteBatch) {
		displayFont.setScale(2f);
		for(int i = 0; i < messageQueue.size(); ++i) {
			spriteBatch.begin();
			displayFont.draw(spriteBatch, messageQueue.get(i).getText(),
							X, FIRST_Y - (i * OFFSET_STEP));
			if(messageQueue.get(i).decrementDuration()) {
				messageQueue.remove(i);
				--i;
			}
			spriteBatch.end();
		}
	}
	
	public static void addMessage(Message message) {
		messageQueue.addFirst(message);
	}
	
	public static void addMessage(String text) {
		messageQueue.addFirst(new Message(text, DURATION));
	}
	
	public static void addMessage(String text, int duration) {
		messageQueue.addFirst(new Message(text, duration));
	}
}
