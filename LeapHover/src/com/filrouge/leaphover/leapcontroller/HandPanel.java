package com.filrouge.leaphover.leapcontroller;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JLabel;
import javax.swing.JPanel;

public class HandPanel extends JPanel implements LeapListener {
	private JLabel label;
	
	public HandPanel() {
		this.setLayout(null);
		label=new JLabel("MAIN");
		this.add(label);
		this.label.setBounds(200, 200, 50, 50);
		this.setVisible(true);
	}
	
	@Override 
	public void paintComponent(Graphics g) { 
		g.setColor(Color.BLACK);
        g.drawLine(0, 250, 500, 250);
        g.setColor(Color.RED);
        g.drawLine(0, 125, 500, 125);
        g.drawLine(0, 375, 500, 375);
    }

	@Override
	public boolean keyDown(int arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean keyTyped(char arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean keyUp(int arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean mouseMoved(int arg0, int arg1) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean scrolled(int arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDown(int arg0, int arg1, int arg2, int arg3) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDragged(int arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchUp(int arg0, int arg1, int arg2, int arg3) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean handPosition(int percent) {
		System.out.println(percent);
		percent = 100-percent;
		this.label.setBounds(200, 500*percent/100, 50, 50);
		this.repaint();
		return false;
	}
}
