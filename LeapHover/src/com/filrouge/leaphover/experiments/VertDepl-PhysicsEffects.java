package com.me.mygdxgame;




import org.jbox2d.collision.Distance;
import org.jbox2d.collision.Distance.DistanceProxy;
import org.jbox2d.collision.Distance.SimplexCache;
import org.jbox2d.collision.DistanceInput;
import org.jbox2d.collision.DistanceOutput;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
//import com.badlogic.gdx.math.Vector2;
/*import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;*/


public class PhysicsEffects implements ApplicationListener {
	private OrthographicCamera camera;
	private SpriteBatch batch;
	private Texture texture;
	
	private float[][] HLSPoints;
	private Mesh ground;
	private Mesh HLS;
	
	private HoverBoard hoverBoard;
	
	private World world;
	private Body board;
	private Body groundBD;
	private Vec2 force;
	
	private int powaaa;
	private double handPosition;
	
	private int relaxTime;
	private float altitudeDepart;
	
/*	private DistanceProxy dPxyGround;
	private DistanceProxy dPxyBoard;
	private Distance distance;
	private DistanceInput distanceIn;
	private DistanceOutput distanceOut;*/
	@Override
	public void create() {
		relaxTime = 0;
		handPosition = PublicConstants.DEFAULT_ALTITUDE;
		//Creation des objets RENDUS
		if(ground==null)
		{
			//Hand line simulation
			HLSPoints = new float[][] {{-1f,-0.025f,0},{1f,-0.025f,0},{1f,0.025f,0},{-1f,0.025f,0}};

			HLS = new Mesh(true,4,4,
							new VertexAttribute(Usage.Position, 3 , "a_position"));
			HLS.setVertices(new float[] {	HLSPoints[0][0],HLSPoints[0][1],HLSPoints[0][2],
											HLSPoints[1][0],HLSPoints[1][1],HLSPoints[1][2],
											HLSPoints[2][0],HLSPoints[2][1],HLSPoints[2][2],
											HLSPoints[3][0],HLSPoints[3][1],HLSPoints[3][2]});
			HLS.setIndices(new short[] {0,1,2,3});
			
			//SOL
			ground = new Mesh(true,4,4,
					new VertexAttribute(Usage.Position, 3 , "ground_position"));
			ground.setVertices(new float[] {-10f,-00f,0,
											-10f,-10f,0,
											10f,-10f,0,
											10f,-00f,0});
			ground.setIndices(new short[] {0,1,2,3});			
		}
		
		//Creation des objets Box2D
		if(world==null){
			Vec2 gravity = new Vec2(0,-10);
			world = new World(gravity);			
			//GROUND
			BodyDef groundDef = new BodyDef();
			groundDef.position.set(0, -10);
			
			groundBD = world.createBody(groundDef);
			
			PolygonShape groundBox = new PolygonShape();
			groundBox.setAsBox(50.0f, 10.0f);
			FixtureDef fxGndDef=new FixtureDef();
			fxGndDef.shape = groundBox;
			
			groundBD.createFixture(fxGndDef);
			
			/*distance = new Distance();
			distanceIn = new DistanceInput();
			distanceOut = new DistanceOutput();
			dPxyGround = new DistanceProxy();
			dPxyBoard = new DistanceProxy();*/
		}
		hoverBoard=new HoverBoard(world);
		
	}

	@Override
	public void dispose() {
		batch.dispose();
		texture.dispose();
	}

	@Override
	public void render() {	
		world.step(1.0f/60.0f, 8, 3);
		boolean downKey = false;
		if(Gdx.input.isKeyPressed(20)){
			relaxTime = 0;
			if(handPosition>0)
				handPosition-=PublicConstants.DOWNING_COEFF;
		}else if(Gdx.input.isKeyPressed(19)){
			relaxTime = 0;
			if(handPosition<20)
				handPosition+=PublicConstants.DOWNING_COEFF;
		}else if(Gdx.input.isKeyPressed(60)) {
			handPosition = 20;
			relaxTime =0;
		}else {
			if(relaxTime==0) {
				altitudeDepart = (float) handPosition;
				//System.out.println(altitudeDepart);
			}
			relaxTime++;
			if(handPosition<PublicConstants.DEFAULT_ALTITUDE-1)
				handPosition = altitudeDepart+((PublicConstants.DEFAULT_ALTITUDE-altitudeDepart)/(Math.PI))*(Math.atan(PublicConstants.UP_SPEED*relaxTime-4)+Math.PI/2-0.24);
			else if(handPosition>PublicConstants.DEFAULT_ALTITUDE-1 && handPosition<PublicConstants.DEFAULT_ALTITUDE+1)
				handPosition = PublicConstants.DEFAULT_ALTITUDE;
			System.out.println(handPosition);
			//System.out.println(relaxTime);
		}
		
		//Calcul de la distance
		/*dPxyGround.set(groundBD.getFixtureList().getShape(), 1);
		dPxyBoard.set(board.getFixtureList().getShape(), 1);
		distanceIn.proxyA = dPxyGround;
		distanceIn.proxyB = dPxyBoard;
		SimplexCache cache = new SimplexCache();
		distance.distance(distanceOut, cache, distanceIn);
		float distanceF = distanceOut.distance;*/
		
		float [] vertices = new float [12];
		HLS.getVertices(vertices);
		int num = 0;
		for(int nPoint=0;nPoint<4;nPoint++){
			//vertices[num] = (float) (handPosition + HLSPoints[nPoint][1]);
			vertices[num+1] =(float) (handPosition*PublicConstants.BOX_TO_LIB + HLSPoints[nPoint][1]);
			/*System.out.println(num);
			System.out.println(vertices[num+0]);
			System.out.println(vertices[num+1]);
			System.out.println(vertices[num+2]);*/
			num+=3;
		}
		HLS.setVertices(vertices);
		
		float groundReaction = 0;
		float userPressure = 0;
		float altitudeHover = hoverBoard.getAltitude();
		if(altitudeHover<=PublicConstants.DEFAULT_ALTITUDE+0.1)
		{
			groundReaction = (float) (10+(PublicConstants.DEFAULT_ALTITUDE-altitudeHover)*3);
			userPressure = (float) (-(PublicConstants.DEFAULT_ALTITUDE-handPosition));
		}
		/*else if(altitudeHover==20)
			groundReaction =10;*/
		hoverBoard.applyForce(0,groundReaction+userPressure);
		
		
		hoverBoard.updatePosition();
		
		
		Gdx.gl.glClearColor(1, 0, 0, 0);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		ground.render(GL10.GL_TRIANGLE_FAN);
		hoverBoard.render();
		HLS.render(GL10.GL_TRIANGLE_FAN);
		/*batch.setProjectionMatrix(camera.combined);
		batch.begin();
		sprite.draw(batch);
		batch.end();*/
	}

	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}
}
