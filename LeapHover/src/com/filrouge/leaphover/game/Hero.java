package com.filrouge.leaphover.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Transform;
import com.badlogic.gdx.physics.box2d.World;
import com.filrouge.leaphover.graphics.ThrusterEffect;
import com.filrouge.leaphover.physics.HoverRayCastCallback;
import com.filrouge.leaphover.util.SimpleDrawer;

public class Hero {
	/* 
	 * PROPERTIES
	 */
	public static final float MAX_LINEAR_VELOCITY = 1.0f;
	
	protected Body body;
	protected Fixture board, character;
	protected float boardWidth;
	
	// ----- Jump management
	public static final float MAX_JUMP_CHARGE = 1f,
							  JUMP_CHARGE_RATIO = 0.008f,
							  MAX_JUMP_FORCE = 1.35f;
	public static final boolean ALLOW_MID_AIR_JUMP = false;
	public static final boolean ALLOW_DOUBLE_JUMP = false;
	protected boolean isJumping = false, isChargingJump = false;
	/** In number of calls of the <tt>step</tt> function */
	protected int jumpSteps = 0, jumpDuration = 0;
	protected float chargingBeginningX = 0;
	protected static final float MAX_DIST_CHARGE = 2;
	
	// ----- Spring effect
	public static final float REACTOR_HEIGHT = 0.15f;
	public static final float SPRING_CONSTANT = 0.15f;
	public static final float MAX_SPRING_FORCE = 1f;
	public static final float DAMPENING_FACTOR = 0.11f;	
	protected HoverRayCastCallback callbackFront;
	protected HoverRayCastCallback callbackBack;
	protected float currentTargetHeight = REACTOR_HEIGHT;
	protected boolean isCloseToGround;
	protected float currentHandHeight = 0.4f;
	
	// ----- Graphical effects
	protected ThrusterEffect thrusterFront, thrusterBack;
	
	/* 
	 * METHODS
	 */
	public Hero(World world, float width) {
		BodyDef bodyDefinition = new BodyDef();
		bodyDefinition.type = BodyDef.BodyType.DynamicBody;
		bodyDefinition.angularDamping = 1f;
		this.body = world.createBody(bodyDefinition);
		this.boardWidth = width;
		
		PolygonShape polygonShape = new PolygonShape();
		// Create board
		float boardHeight = width / 7f;
		polygonShape.setAsBox(this.boardWidth, boardHeight);
		board = body.createFixture(polygonShape, 1);
		
		// Create character
		float characterHeight = width / 2f;
		Vector2 characterOffset = new Vector2(0, characterHeight + boardHeight);
		polygonShape.setAsBox(width / 5f, characterHeight, characterOffset, 0f);
		// TODO: should the character have a density (and thus a mass)?
		character = body.createFixture(polygonShape, 0);
		
		polygonShape.dispose();
		
		// Other initializations
		callbackFront = new HoverRayCastCallback(this);
		callbackBack = new HoverRayCastCallback(this);
		isCloseToGround = false;
		
		// TODO: deduce scale from camera
		thrusterFront = new ThrusterEffect(0.0005f);
		thrusterBack = new ThrusterEffect(0.0005f);
	}
	
	public void step() {
		// Manage jump charging and jump duration
		if (isChargingJump) {
			// TODO: check computations
			// TODO: stop charging jump automatically when the ability to jump is lost
			
			/*float charged = (jumpSteps * JUMP_CHARGE_RATIO);
			charged = Math.min(charged, MAX_JUMP_CHARGE);
			float p = 1f - (charged / MAX_JUMP_CHARGE);
			p = Math.max(p, 0.3f);
			currentTargetHeight = (p * REACTOR_HEIGHT);*/
			
			// The jump charge depends on the current height of the hand
			if(this.getBody().getPosition().x - this.chargingBeginningX < Hero.MAX_DIST_CHARGE) {
				this.currentTargetHeight = 0.25f*this.currentHandHeight;
			
				jumpSteps++;
			} else {
				triggerJump();
			}
		}
		else if (isJumping()) {
			if (jumpSteps >= jumpDuration)
				stopJumping();
			else
				jumpSteps++;
		}
		
		// Make the ray at least as long as the target distance
		Vector2 startOfRay = this.body.getWorldCenter();
		float angle = this.body.getAngle();
		
		Vector2 front = new Vector2(startOfRay).add((float) (this.boardWidth / 2*Math.cos(angle)), 
													(float) (this.boardWidth / 2*Math.sin(angle)));
		Vector2 back = new Vector2(startOfRay).add((float) (- this.boardWidth / 2*Math.cos(angle)), 
												   (float) (- this.boardWidth / 2*Math.sin(angle)));
		
		/**
		 * Ray to detect bonus items
		 * TODO : Add RayCastCallBack
		 */
		/*Vector2 startRayBonus = new Vector2(startOfRay).add((float) (this.boardWidth * Math.cos(angle)), 
													(float) (this.boardWidth * Math.sin(angle)));
		Vector2 endRayBonus = new Vector2(startRayBonus).add((float) (this.boardWidth / 5 * Math.cos(angle)), 
															(float) (this.boardWidth / 5 * Math.sin(angle)));
		*/
		Vector2 normal = new Vector2((float)Math.sin(angle),
								   - (float)Math.cos(angle));
		normal.scl(currentTargetHeight);
		
		Vector2 endOfRayFront = front.cpy().add(normal);
		Vector2 endOfRayBack = back.cpy().add(normal);
		
		// Debug display
		//SimpleDrawer.drawLine(LeapHover.getInstance().getCamera(), startRayBonus, endRayBonus);
		//SimpleDrawer.drawCircle(LeapHover.getInstance().getCamera(), this.body.getWorldCenter().x, this.body.getWorldCenter().y, 0.5f, Color.RED);
		//SimpleDrawer.drawLine(LeapHover.getInstance().getCamera(), front, endOfRayFront);
		//SimpleDrawer.drawLine(LeapHover.getInstance().getCamera(), back, endOfRayBack);
		
		callbackFront.reset(front);
		callbackBack.reset(back);
		// If there's no intersection, no callback will be called
		this.isCloseToGround = false;
		body.getWorld().rayCast(callbackFront, front, endOfRayFront);
		body.getWorld().rayCast(callbackBack, back, endOfRayBack);
		
		// Update the thruster's particle effect position
		// TODO: map ParticleEmitter.velocity to currentTargetHeight
		Vector2 frontPosition = front.cpy().add(normal.cpy().scl(0.1f));
		thrusterFront.setPosition(frontPosition.x, frontPosition.y);
		thrusterFront.setRotation(angle);
		Vector2 backPosition = back.cpy().add(normal.cpy().scl(0.1f));
		thrusterBack.setPosition(backPosition.x, backPosition.y);
		thrusterBack.setRotation(angle);
	}
	
	public void render(SpriteBatch batch) {
		thrusterFront.draw(batch, Gdx.graphics.getDeltaTime());
		thrusterBack.draw(batch, Gdx.graphics.getDeltaTime());
	}
	
	public void resetTo(Vector2 position, float inclination) {
		body.setTransform(position, inclination);
		body.setLinearVelocity(new Vector2(0f, 0f));
		body.setAngularVelocity(0);
	}

	/**
	 * TODO: reduce "rebound" effect: the spring should not allow make us rebound after falling from a large height
	 * @param position Point at which to apply the springing force
	 * @param callback
	 * @see http://www.iforce2d.net/b2dtut/suspension
	 */
	public void spring(Vector2 position, HoverRayCastCallback callback) {
		Float distanceToGround = callback.getDistance();
		this.isCloseToGround = true;
		
		distanceToGround = Math.abs(distanceToGround);
		if(distanceToGround < currentTargetHeight) {
			// Dampening
			// TODO : fix dampening computation to take into account both directions?
			distanceToGround += DAMPENING_FACTOR * body.getLinearVelocity().y;
			
			float magnitude = SPRING_CONSTANT * (currentTargetHeight - distanceToGround);
			magnitude = Math.min(Math.abs(magnitude), MAX_SPRING_FORCE);
			
			float angle = getBody().getAngle();

			Vector2 force = new Vector2(- (float)Math.sin(angle),
										  (float)Math.cos(angle));
			force.scl(magnitude);
			body.applyForce(force, position, true);
			
			// Debug drawing
			Vector2 end = position.cpy().add(force.cpy().scl(10f));
			SimpleDrawer.drawLine(LeapHover.getInstance().getCamera(), position, end, Color.RED);
		}
		
	}
	
	/**
	 * Start accumulating "force" for the jump over time
	 * @see #step()
	 */
	public void startChargingJump() {
		if (canJump()) {
			this.chargingBeginningX = this.getBody().getPosition().x;
			this.isChargingJump = true;
		}
	}
	
	/**
	 * Trigger the jump after having charged it
	 * @see #startChargingJump()
	 * @see #jump(float)
	 */
	public void triggerJump() {
		jump(jumpSteps * JUMP_CHARGE_RATIO);
	}
	/**
	 * Jump perpendicular to current inclination
	 * @param amount A value in [0; 1]
	 */
	public void jump(float amount) {
		this.isChargingJump = false;
		this.jumpSteps = 0;
		if (canJump()) {
			this.jumpDuration = (int)(Gdx.graphics.getFramesPerSecond() * (float)amount);
			this.isJumping = true;
			this.currentTargetHeight = MAX_JUMP_FORCE * REACTOR_HEIGHT;
		}
		else
			this.currentTargetHeight = REACTOR_HEIGHT;
	}
	public void stopJumping() {
		this.jumpSteps = 0;
		this.jumpDuration = 0;
		this.isJumping = false;
		this.isChargingJump = false;
		this.currentTargetHeight = REACTOR_HEIGHT;
	}
	
	/**
	 * The hero can jump either if it is allowed to jump mid-air
	 * or if it has at least one "reactor" close to the ground
	 * @return
	 */
	public boolean canJump() {
		if (!ALLOW_DOUBLE_JUMP && isJumping())
			return false;
			
		if (ALLOW_MID_AIR_JUMP)
			return true;
		else
			return isCloseToGround;
	}
	
	/*
	 * METHOD DELEGATION
	 */
	public Transform getTransform() {
		return body.getTransform();
	}

	public Vector2 getPosition() {
		return body.getPosition();
	}
	public void setPosition(Vector2 position) {
		body.setTransform(position, body.getAngle());
	}
	
	public void setCurrentHandHeight(float handHeight) {
		this.currentHandHeight=handHeight;
	}
	/*
	 * GETTERS & SETTERS
	 */
	public Body getBody() {
		return this.body;
	}
	public Fixture getBoard() {
		return this.board;
	}
	public Fixture getCharacter() {
		return this.character;
	}

	public boolean isJumping() {
		return this.isJumping;
	}
}
