package com.filrouge.leaphover.level;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.filrouge.leaphover.game.LeapHover;
import com.filrouge.leaphover.util.BSCurve;
import com.filrouge.leaphover.util.SimpleDrawer;

/**
 * USER HILL
 * Allows user to create his own hills, thanks to adding
 * control points
 * If the hill isn't finished, it's virtual, and the drawing
 * is made by Draw method
 * When the hill is finished, (use FinishDrawing method), a physical hill is created
 * and inserted into the world. The rendering is then taken care by the main renderer.
 * 
 * @warning IMPORTANT NOTICE : the box2d engine can fail if the NB_OF_VERTICES_PER_HZTL_UNIT
 * is too important
 * 
 * @author Damien Gallet
 */


public class UserHill {
	private Body physicHill;
	/** @warning This is Java's standard library Vector2, not a Box2D Vector2 */
	private List<Vector2> controlPoints;
	private Vector2[] vertices;
	private BSCurve bscurveHill;
	private boolean hillFinished;
	private EdgeShape shape;
	private int nbOfVertices = 0;
	
	public static final int NB_OF_VERTICES_PER_UNIT = 40;
	public static final float HILL_THICKNESS = 0.01f;
	
	public UserHill() {
		hillFinished = false;
		controlPoints = new ArrayList<Vector2>();
		vertices = null;
		shape = new EdgeShape();
	}
	
	public boolean addControlPoint(Vector2 newControlPoint, World world) {
		controlPoints.add(newControlPoint);
		int size = controlPoints.size();
		// If there is enough control points, create or regenerate a new BSCurve,
		// and get its vertices
		if(size > 3) {
			if(size == 4) {
				BodyDef bodyDefinition = new BodyDef();
				bodyDefinition.type = BodyDef.BodyType.StaticBody;
				bodyDefinition.position.set(controlPoints.get(0));
				physicHill = world.createBody(bodyDefinition);
			}
			
			bscurveHill = new BSCurve(controlPoints);
			
			// Compute distance covered by the controlled points
			float distance = 0;
			for (int i = 1; i < size; ++i)
				distance += controlPoints.get(i - 1).dst(controlPoints.get(i));
			
			nbOfVertices = Math.abs(Math.round(distance * NB_OF_VERTICES_PER_UNIT));
			vertices = bscurveHill.getSamples(nbOfVertices);
			
		}
		return false;
	}
	
	/**
	 * When the drawing is finished, the virtual curve becomes a physical curve
	 * => it is instanciated in the world.
	 */
	public void finishDrawing() {
		hillFinished = true;
		boolean intersect = false;
		if(nbOfVertices>1) {
			BodyDef physicHillDef = new BodyDef();
			
			//Makes a shift of the vertices
			Vector2 position = new Vector2(vertices[0]);
			Vector2 offset = new Vector2(vertices[0]);
			position.scl(0.5f);
			offset.scl(-0.5f);
			Array<Fixture> heroFixtures = LeapHover.getInstance().getHero().getBody().getFixtureList();
			for(int i = 0; i < nbOfVertices; i++) {
				vertices[i].add(offset);
				for(int j = 0; j < heroFixtures.size; ++j) {
					if(heroFixtures.get(j).testPoint(vertices[i])) {
						intersect = true;
					}
				}
			}
			
			if(!intersect) {
				//Creates the physic hill
				//Merci Merlin!
				physicHillDef.position.set(position);
				physicHill = LeapHover.getInstance().getWorld().createBody(physicHillDef);
				
				Vector2 previous = vertices[0];
				for(int i = 1; i < nbOfVertices; ++i) {
					shape.set(previous, vertices[i]);
					physicHill.createFixture(shape, 0);
	
					previous = vertices[i];
				}
				// Below side
				Vector2 thicknessVector = new Vector2(0, - HILL_THICKNESS);	//ADD A CONSTANT HERE
				
				for(int i = nbOfVertices - 2; i >= 0; --i) {
					vertices[i].add(thicknessVector);
					shape.set(previous, vertices[i]);
					physicHill.createFixture(shape, 0);
	
					previous = vertices[i];
				}
				
				//Makes the connection between last and first vertices
				shape.set(previous, new Vector2(vertices[0].x,vertices[0].y+HILL_THICKNESS));
				physicHill.createFixture(shape, 0);
				
				shape.dispose();
			}
		}
	}
	
	/**
	 * Render the curve (only if it isn't finished)
	 */
	public void draw() {
		if(!hillFinished && nbOfVertices > 1) {
			Vector2 previous = vertices[0];
			for(Vector2 vertex : vertices) {
				SimpleDrawer.drawLine(LeapHover.getInstance().getCamera(),previous,vertex,Color.WHITE);
				previous = vertex;
			}
		}
	}
	
	public boolean isFinished() {
		return hillFinished;
	}

	public void destroy() {
		nbOfVertices = 0;
		if (this.physicHill != null) {
			LeapHover.getInstance().getWorld().destroyBody(physicHill);
			this.physicHill = null;
		}
	}
}
