package com.filrouge.leaphover.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.filrouge.leaphover.game.LeapHover;
import com.leapmotion.leap.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Our intermediate interface to process Leap Motion events
 * @author R��mi Martin et Gustave Monod
 */
public abstract class LeapListener extends Listener
{
	protected final double MIN_HAND_ANGLE = 0.;
	protected final double MAX_HAND_ANGLE = 0.2;
	
	public static final double MIN_HAND_INCLINATION = 0.5;
	public static final double MAX_HAND_INCLINATION = 3.2;
	public static final double MIN_TIME_BETWEEN_POINTS_MS = 100;

	public static boolean isRightHanded = true;
	
	public static final float MIN_Y = 105;
	public static final float DETECTION_HEIGHT = 200;
	public static final float MIDDLE = 0;
	public static final float DETECTION_WIDTH = 200;
	
	public static final float WALL_POSITION_Z = 100; // Where the touch is triggered

	private boolean isDrawing;
	private double timeLastPointMs;
	private boolean handBefore = false;
	
	@Override
	public void onFrame(Controller controller) {
		Frame frame = controller.frame();

		if (!frame.hands().isEmpty()) {
			this.handBefore = true;
			Hand motionHand  = null,
				 drawingHand = null;

			/*
			 * Choosing appropriate hand
			 */
			if (frame.hands().count() == 2) {
				if (isRightHanded) {
					motionHand = frame.hands().leftmost();
					drawingHand = frame.hands().rightmost();
				}
				else {
					motionHand = frame.hands().rightmost();
					drawingHand = frame.hands().leftmost();
				}
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
			else if (this.isDrawing) {
				this.isDrawing = false;
				System.out.println("Stopped touching\n");
				// Notifies observers
				endHandDrawing();
			}
		}
		else { // If there is no hand
			if (this.isDrawing) {
				this.isDrawing = false;
				System.out.println("Stopped touching\n");
				// Notifies observers
				endHandDrawing();
			}

			noHand();
		}
	}

	private boolean isInBoundaries(Vector frontPos) {
		return (frontPos.getY() > MIN_Y && frontPos.getY() < MIN_Y + DETECTION_HEIGHT) &&
				(isRightHanded ? (frontPos.getX() > MIDDLE && frontPos.getX() < MIDDLE + DETECTION_WIDTH)
				: (frontPos.getX() < MIDDLE && frontPos.getX() > MIDDLE - DETECTION_WIDTH));
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
			 * If there is a new touch (Z <= WALL_POSITION_Z)
			 */
			if (!this.isDrawing) {
				this.isDrawing = frontPos.getZ() <= WALL_POSITION_Z;
				if (this.isDrawing) { // New touch

					if (!isInBoundaries(frontPos)) {
						this.isDrawing = false;
						return;
					}

					System.out.println("New touch");
					this.timeLastPointMs = System.currentTimeMillis();
					// Notifies the observers
					newHandDrawing();
				}
			}
			
			else {
				double lastMeasure = 0;
				float averageX = 0;
				float averageY = 0;
				
				/*
				 * If the drawing stopped (Z > WALL_POSITION_Z)
				 */
				if (frontPos.getZ() > WALL_POSITION_Z || !isInBoundaries(frontPos)) {
					System.out.println("Stopped touching\n");
					this.isDrawing = false;
					// Notifies observers
					endHandDrawing();
				}
				
				double currentTimeMs = System.currentTimeMillis();
				
				double oldSampleDuration = lastMeasure - this.timeLastPointMs;
				double currentSampleDuration = currentTimeMs - this.timeLastPointMs;
				double currentMeasureDuration = currentTimeMs - lastMeasure;
				averageX = (float) (( oldSampleDuration*averageX + frontPos.getX()*currentMeasureDuration )/currentSampleDuration);
				averageY = (float) (( oldSampleDuration*averageY + frontPos.getY()*currentMeasureDuration )/currentSampleDuration);
				if (currentTimeMs - this.timeLastPointMs >= MIN_TIME_BETWEEN_POINTS_MS) {
					this.timeLastPointMs = currentTimeMs;
					this.handDraw(new Vector2(frontPos.getX(), frontPos.getY()));
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
	 * Adds a new point to be drawn.
	 * (coordinates from the leap are transformed to fit the game)
	 *
	 * @param point The point to add to the other control points
	 * @return 
	 */
	public boolean handDraw(Vector2 point) {
		return false;
	}

	/**
	 * Notifies that a new drawing is beginning.
	 * @return 
	 */
	public boolean newHandDrawing() {
		return false;
	}

	/**
	 * Notifies that a drawing has stopped.
	 * @return
	 */
	public boolean endHandDrawing() {
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
