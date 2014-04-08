package com.me.mygdxgame;

import org.jbox2d.collision.Distance;
import org.jbox2d.collision.DistanceInput;
import org.jbox2d.collision.DistanceOutput;
import org.jbox2d.collision.Distance.DistanceProxy;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;

import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;



public class PhysicObject extends PhysicsEffects {
	/*public void create() {		
		//Creation des objets RENDUS
		if(mesh==null && ground==null)
		{
			//Forme
			mesh = new Mesh(true,4,4,
							new VertexAttribute(Usage.Position, 3 , "a_position"));
			mesh.setVertices(new float[] {-0.1f,-0.1f,0,
											0.1f,-0.1f,0,
											0.1f,0.1f,0,
											-0.1f,0.1f,0});
			mesh.setIndices(new short[] {0,1,2,3});
			
			//SOL
			ground = new Mesh(true,4,4,
					new VertexAttribute(Usage.Position, 3 , "ground_position"));
			ground.setVertices(new float[] {-10f,-00f,0,
											-10f,-10f,0,
											10f,-10f,0,
											10f,-00f,0});
			ground.setIndices(new short[] {0,1,2,3});			
		}
		altitudeConsigne = 20;
		force = new Vec2(0,12);
		//Creation des objets Box2D
		if(world==null){
			gravity = new Vec2(0,-10);
			world = new World(gravity);
			
			//BOARD
			BodyDef boardDef = new BodyDef();
			boardDef.position.set(0,altitudeConsigne);
			boardDef.type = BodyType.DYNAMIC;
			board = world.createBody(boardDef);
			
			PolygonShape boardPoly = new PolygonShape();
			boardPoly.setAsBox(0.1f, 0.1f);
			
			FixtureDef fxDefBoard = new FixtureDef();
			fxDefBoard.shape = boardPoly;
			fxDefBoard.density = 5f;
			fxDefBoard.friction = 0.5f;
			fxDefBoard.restitution = 0.2f;
			
			board.createFixture(fxDefBoard);
			
			//GROUND
			BodyDef groundDef = new BodyDef();
			groundDef.position.set(0, -10);
			
			groundBD = world.createBody(groundDef);
			
			PolygonShape groundBox = new PolygonShape();
			groundBox.setAsBox(50.0f, 10.0f);
			FixtureDef fxGndDef=new FixtureDef();
			fxGndDef.shape = groundBox;
			
			groundBD.createFixture(fxGndDef);
			
			distance = new Distance();
			distanceIn = new DistanceInput();
			distanceOut = new DistanceOutput();
			dPxyGround = new DistanceProxy();
			dPxyBoard = new DistanceProxy();
		}
	}*/
}
