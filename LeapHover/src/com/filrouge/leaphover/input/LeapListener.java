package com.filrouge.leaphover.input;

import com.badlogic.gdx.math.Vector2;
import com.leapmotion.leap.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Our intermediate interface to process Leap Motion events
 * @author R��mi Martin
 */
public abstract class LeapListener extends Listener
{
	protected final double MIN_HAND_ANGLE = 0.;
	protected final double MAX_HAND_ANGLE = 0.2;
	
	public static final double MIN_HAND_INCLINATION = 0.5;
	public static final double MAX_HAND_INCLINATION = 3.2;
	public static final double MIN_TIME_BETWEEN_POINTS_MS = 100;
	// TODO incremental drawing instead of drawing all points at once
	public static final int    NB_POINTS_TO_DRAW = 2;

	private List<Vector2> drawingPoints = new ArrayList<Vector2>();
	private boolean isDrawing;
	private double timeLastPointMs;
	private boolean handBefore = false;
	
	@Override
	public void onFrame(Controller controller) {
		Frame frame = controller.frame();

		if (!frame.hands().isEmpty()) {
			this.handBefore=true;
			Hand motionHand,
				 drawingHand = null;

			/*
			 * Choosing appropriate hand
			 */
			if (frame.hands().count() == 2) {
				// TODO : parametrize which hand does which action
				motionHand = frame.hands().leftmost();
				drawingHand = frame.hands().rightmost();
			}
			else { // Only one hand: movement
				motionHand = frame.hands().get(0);
			}

			/*
			 * Hand up/down movement
			 */
			if (motionHand.isValid()) {
				motion(motionHand, frame.interactionBox().height());
		    }

			/*
			 * Drawing
			 */
			if (drawingHand != null) {
				drawAnalysis(drawingHand);
			}
		}
		else {
			noHand();
		}
	}

	/**
	 * Creation of the list of drawingPoints the hand draws
	 */
	private void drawAnalysis (Hand drawingHand) {
		PointableList pointables = drawingHand.pointables();
		if (!pointables.isEmpty()) {
			// Calculate the pointable front most Z coordinate
			Vector frontPos = pointables.frontmost().tipPosition();
			
			/*
			 * If there is a new touch (Z <= 0)
			 */
			if (!this.isDrawing) {
				this.isDrawing = frontPos.getZ() <= 0;
				if (this.isDrawing) { // New touch
					System.out.println("New touch");
					this.timeLastPointMs = System.currentTimeMillis();
					// Resets the list
					if (this.drawingPoints.size() > 0) {
						this.drawingPoints = new ArrayList<Vector2>();
					}
				}
			}
			
			else {
				double lastMeasure = 0;
				float averageX = 0;
				float averageY = 0;
				
				this.isDrawing = frontPos.getZ() <= 0;
				
				double currentTimeMs = System.currentTimeMillis();
				
				double oldSampleDuration = lastMeasure - this.timeLastPointMs;
				double currentSampleDuration = currentTimeMs - this.timeLastPointMs;
				double currentMeasureDuration = currentTimeMs - lastMeasure;
				lastMeasure = currentTimeMs;
				averageX = (float) (( oldSampleDuration*averageX + frontPos.getX()*currentMeasureDuration )/currentSampleDuration);
				averageY = (float) (( oldSampleDuration*averageY + frontPos.getY()*currentMeasureDuration )/currentSampleDuration);
				if (currentTimeMs - this.timeLastPointMs >= MIN_TIME_BETWEEN_POINTS_MS) {
					this.timeLastPointMs = currentTimeMs;
					this.drawingPoints.add(new Vector2(averageX,averageY));
					if (drawingPoints.size() == NB_POINTS_TO_DRAW) {
						this.handDraw(this.drawingPoints.get(0), this.drawingPoints.get(1));
					}
				}
				
				/*
				 * If the drawing stopped (Z > 0)
				 */
				if (!this.isDrawing) {
					System.out.println("Stopped touching");
					System.out.println("Point list: [");
					System.out.println(drawingPoints);
					System.out.println("]");
				}
			}
		}
	}

	/**
	 * The movement of the hand means movement of the board
	 */
	private void motion (Hand motionHand, float maxHeight) {

		float normalX = motionHand.palmNormal().get(0);
		// "Flat" hand
		// TODO : do we really want the hand to be "flat" ?
		if(Math.abs(normalX) > MIN_HAND_ANGLE && Math.abs(normalX) < MAX_HAND_ANGLE) {
			float y = motionHand.palmPosition().get(1);
			y = Math.min(y, maxHeight);

			handHeight(y / maxHeight);
		}

		float angle = - motionHand.palmNormal().pitch();
		handInclination(angle / (float)(MAX_HAND_INCLINATION - MIN_HAND_INCLINATION));
	}

	/**
	 * Process variations of one hand's height
	 * @param percent
	 */
	public boolean handHeight(float percent) {
		return false;
	}
	
	/**
	 * Process variations of one hand's inclination
	 * @param percent of rotation with respect to the negative z-axis ("pitch")
	 * @return
	 */
	public boolean handInclination(float percent) {
		return false;
	}

	/**
	 * Draws a line with the two points.
	 *
	 * @param begin The point from which to start the line
	 * @param end   The point at which to stop the line
	 */
	public boolean handDraw(Vector2 begin, Vector2 end) {
		return false;
	}
	
	/**
	 * Event dispatched when no hand is detected.
	 * Useful to reset controls to default value, for example.
	 * @return
	 */
	public boolean noHand() {
		if(this.handBefore) {
			handHeight(1);
			this.handBefore=false;
		}
		return false;
	}
}
