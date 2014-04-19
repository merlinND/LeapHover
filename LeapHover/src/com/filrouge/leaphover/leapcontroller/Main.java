package com.filrouge.leaphover.leapcontroller;

import java.io.IOException;

import javax.swing.JFrame;

import com.leapmotion.leap.*;

public class Main {
	
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
}
