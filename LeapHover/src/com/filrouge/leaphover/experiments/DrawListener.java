package com.filrouge.leaphover.experiments;

import com.badlogic.gdx.math.Vector2;
import com.leapmotion.leap.*;

import java.util.ArrayList;
import java.util.List;

class DrawListener extends Listener {

	private List<Vector2> points;
	private boolean isTouching;

	public void onFrame (Controller controller) {
		// Get the most recent frame and report some basic information
		Frame frame = controller.frame();

		PointableList pointables = frame.pointables();
		if (!pointables.isEmpty()) {
			// Calculate the pointable front most Z coordinate
			Vector frontPos = pointables.frontmost().tipPosition();
			if (!this.isTouching) {
				this.isTouching = frontPos.getZ() <= 0;
				if (this.isTouching) { // New touch
					System.out.println("New touch");
					if (this.points.size() > 0) {
						this.points = new ArrayList<Vector2>();
					}
				}
			} else {
				this.isTouching = frontPos.getZ() <= 0;
				if (!this.isTouching) {
					System.out.println("Stopped touching");
					System.out.println("Point list: [");
					System.out.println(points);
					System.out.println("]");
				}
				this.points.add(new Vector2(frontPos.getX(), frontPos.getY()));
			}
		}
	}

	public void waitForPoints () {
		
	}

	public List<Vector2> getPoints () {
		return points;
	}
}