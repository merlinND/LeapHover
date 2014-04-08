package com.me.mygdxgame;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;


public class HoverBoard {
	private Mesh boardMesh;
	private Body boardBody;
	private float[][] points;
	
	public HoverBoard(World currentWorld) {
		points = new float[][] {{-0.1f,-00f,0},{0.1f,-00f,0},{0.1f,0.1f,0},{-0.1f,0.1f,0}};

		if(boardMesh==null)
		{
			//Forme
			boardMesh = new Mesh(true,4,4,
							new VertexAttribute(Usage.Position, 3 , "a_position"));
			boardMesh.setVertices(new float[] {	points[0][0],points[0][1],points[0][2],
												points[1][0],points[1][1],points[1][2],
												points[2][0],points[2][1],points[2][2],
												points[3][0],points[3][1],points[3][2]});
			boardMesh.setIndices(new short[] {0,1,2,3});		
		}
		
		//Box2D BoardObject initialization
		if(boardBody==null){
			
			//BOARD
			BodyDef boardDef = new BodyDef();
			boardDef.position.set(0,PublicConstants.DEFAULT_ALTITUDE);
			boardDef.type = BodyType.DYNAMIC;
			boardBody = currentWorld.createBody(boardDef);
			
			PolygonShape boardPoly = new PolygonShape();
			boardPoly.setAsBox(0.1f, 0.1f);
			
			FixtureDef fxDefBoard = new FixtureDef();
			fxDefBoard.shape = boardPoly;
			fxDefBoard.density = 5f;
			fxDefBoard.friction = 0.5f;
			fxDefBoard.restitution = 0.2f;
			
			boardBody.createFixture(fxDefBoard);
		}
	}

	
	public void updatePosition ()
	{
		float [] vertices = new float [12];
		float boardX = boardBody.getPosition().x;
		float boardY = boardBody.getPosition().y;
		int num = 0;
		for(int nPoint=0;nPoint<4;nPoint++){
			vertices[num] 	= boardX*PublicConstants.BOX_TO_LIB + points[nPoint][0]; 
			vertices[num+1] = boardY*PublicConstants.BOX_TO_LIB + points[nPoint][1];
			num+=3;
		}
		boardMesh.setVertices(vertices);
	}
	
	public void render()
	{
		boardMesh.render(GL10.GL_TRIANGLE_FAN);
	}
	
	public void applyForce (float forceX, float forceY)
	{
		boardBody.applyForce(new Vec2(forceX,forceY), boardBody.getPosition());
	}
	
	public float getAltitude ()
	{
		return boardBody.getPosition().y;
	}
}
