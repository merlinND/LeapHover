package com.filrouge.leaphover.experiments;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JLabel;
import javax.swing.JPanel;

import com.filrouge.leaphover.input.LeapListener;

public class HandPanel extends JPanel {

	private static final long serialVersionUID = -7333447773616900559L;
	private JLabel label;
	
	public HandPanel() {
		this.setLayout(null);
		label = new JLabel("MAIN");
		this.add(label);
		this.label.setBounds(200, 200, 50, 50);
		this.setVisible(true);
	}
	
	@Override 
	public void paintComponent(Graphics g) {
		g.clearRect(0, 0, getWidth(), getHeight());
		g.setColor(Color.BLACK);
        g.drawLine(0, 250, 500, 250);
        g.setColor(Color.RED);
        g.drawLine(0, 125, 500, 125);
        g.drawLine(0, 375, 500, 375);
    }
	
	public LeapListener leapListener = new LeapListener() {
		public boolean handHeight(float percent) {
			System.out.println("Hand is at " + percent + "% of the maximum height");
			percent = 100-percent;
			label.setBounds(200, 500 * (int)percent / 100, 50, 50);
			repaint();
			return false;
		}
	};
}
