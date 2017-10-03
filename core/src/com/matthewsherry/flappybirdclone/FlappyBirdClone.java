package com.matthewsherry.flappybirdclone;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.sun.prism.impl.TextureResourcePool;

import java.util.Random;

import sun.rmi.runtime.Log;

public class FlappyBirdClone extends ApplicationAdapter {
	//Textures and Sprites//
	SpriteBatch batch;
	Texture background;
	Texture[] birdSprite;
	Texture topTube;
	Texture bottomTube;
	Texture gameOver;

	//Bird Sprite and animation handling//
	int birdSpriteState = 0;
	int animationDelay = 0;
	int animationDelayCap;
	Random rand;

	//motion variables and game state//
	float birdY = 0;
	float velocity = 0;
	float gravity = 1.5f;
	int gameState = 0;

	//tubes, the 'gap', and tube locations//
	float gap = 400;
	float maxTubeOffset;
	Random randomGenerator;
	float tubeVelocity = 5;
	int numberOfTubes = 4;
	float[] tubeX = new float[numberOfTubes];
	float[] tubeOffset = new float[numberOfTubes];
	float distanceBetweenTubes;

	//collision//
	//ShapeRenderer shapeRenderer;
	Circle birdCircle;
	Rectangle[] topTubeRectanges;
	Rectangle[] bottomTubeRectanges;

	//scoring//
	int score = 0;
	int scoringTube = 0;

	//fonts//
	BitmapFont font;

	public void startGame(){
		birdY = (Gdx.graphics.getHeight()/2)-(birdSprite[0].getHeight()/2);

		//setting up tubeX[]//
		for (int i = 0; i < numberOfTubes; i++){
			tubeOffset[i] = (randomGenerator.nextFloat()- 0.5f) * (Gdx.graphics.getHeight() - gap - maxTubeOffset);
			tubeX[i] =Gdx.graphics.getWidth()/2-topTube.getWidth()/2 + Gdx.graphics.getWidth() + i * distanceBetweenTubes;
			topTubeRectanges[i] = new Rectangle();
			bottomTubeRectanges[i] = new Rectangle();
		}
	}


	@Override
	public void create () {
		batch = new SpriteBatch();
		background = new Texture("bg.png");
		gameOver = new Texture("gameover.png");
		birdSprite = new Texture[2];
		birdSprite[0] = new Texture("bird.png");
		birdSprite[1] = new Texture("bird2.png");
		rand = new Random();
		animationDelayCap = rand.nextInt(6)+3;
		topTube = new Texture("toptube.png");
		bottomTube = new Texture("bottomtube.png");
		maxTubeOffset = Gdx.graphics.getHeight()/2 - gap/2 - 50;
		randomGenerator = new Random();
		distanceBetweenTubes = Gdx.graphics.getWidth() * 3/4;
		//shapeRenderer = new ShapeRenderer();
		birdCircle = new Circle();
		topTubeRectanges = new Rectangle[numberOfTubes];
		bottomTubeRectanges = new Rectangle[numberOfTubes];
		font = new BitmapFont();
		font.setColor(Color.WHITE);
		font.getData().setScale(10);
		startGame();

	}

	@Override
	public void render () {
		//drawing background and beginning batch//
		batch.begin();
		batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

		if (gameState == 1) {
			//check if bird survived a tube, then score//
			if (tubeX[scoringTube] < Gdx.graphics.getWidth()/2 - birdSprite[birdSpriteState].getWidth()/2 - topTube.getWidth()/2){
				score++;
				if (scoringTube < numberOfTubes - 1){
					scoringTube ++;
				} else {
					scoringTube = 0;
				}
			}
			//if touched, push the bird up//
			if (Gdx.input.justTouched()) {
				velocity -= 35;
			}
			//tube movement and reset when reaching the left//
			for (int i = 0; i < numberOfTubes; i++) {
				if (tubeX[i] < 0 - topTube.getWidth()){
					tubeX[i] += numberOfTubes * distanceBetweenTubes;
					tubeOffset[i] = (randomGenerator.nextFloat()- 0.5f) * (Gdx.graphics.getHeight() - gap - maxTubeOffset);
				}
				tubeX[i] -= tubeVelocity;
				batch.draw(topTube, tubeX[i], Gdx.graphics.getHeight() / 2 + gap / 2 + tubeOffset[i]);
				batch.draw(bottomTube, tubeX[i], Gdx.graphics.getHeight() / 2 - gap / 2 - bottomTube.getHeight() + tubeOffset[i]);
			}

			//Moving Flappy Bird, aka: gravity or tapping//
			if (birdY > 0 && birdY < Gdx.graphics.getHeight() - birdSprite[birdSpriteState].getHeight()) {
				velocity += gravity;
				birdY -= velocity;
			} else {
				gameState = 2;
			}
		//Start gameState when there is the first touch//
		} else if (gameState == 0){
			if (Gdx.input.justTouched()) {
				gameState = 1;
			}
		} else if (gameState == 2){
			batch.draw(gameOver, Gdx.graphics.getWidth()/2 - gameOver.getWidth()/2, Gdx.graphics.getHeight()/2 - gameOver.getHeight()/2);
			if (Gdx.input.justTouched()) {
				gameState = 1;
				startGame();
				score = 0;
				scoringTube = 0;
				velocity = 0;
			}

		}

		//Animate Flappy Bird with Sprite change every 3-6 frames///
		if (animationDelay > animationDelayCap) {
			animationDelay = 0;
			rand = new Random();
			animationDelayCap = rand.nextInt(6) + 3;
			if (birdSpriteState == 0) {
				birdSpriteState = 1;
			} else {
				birdSpriteState = 0;
			}
		} else {
			animationDelay += 1;
		}

		//Drawing the bird and ending the batch//
		batch.draw(birdSprite[birdSpriteState], (Gdx.graphics.getWidth() / 2) - (birdSprite[birdSpriteState].getWidth() / 2), birdY);
		font.draw(batch,String.valueOf(score), 100, 200);
		batch.end();

		//Rendering the birdCirlce for collision//
		//shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
		//shapeRenderer.setColor(Color.RED);
		birdCircle.set(Gdx.graphics.getWidth()/2, birdY + birdSprite[birdSpriteState].getHeight()/2, birdSprite[birdSpriteState].getWidth()/2);
		//shapeRenderer.circle(birdCircle.x,birdCircle.y,birdCircle.radius);
		//Rendering for the tubeRectangles for collision//
		for (int i = 0; i < numberOfTubes; i ++){
			topTubeRectanges[i].set(tubeX[i], Gdx.graphics.getHeight() / 2 + gap / 2 + tubeOffset[i], topTube.getWidth(), topTube.getHeight());
			bottomTubeRectanges[i].set(tubeX[i],Gdx.graphics.getHeight() / 2 - gap / 2 - bottomTube.getHeight() + tubeOffset[i],bottomTube.getWidth() ,bottomTube.getHeight());
			//shapeRenderer.rect(topTubeRectanges[i].x,topTubeRectanges[i].y,topTubeRectanges[i].width,topTubeRectanges[i].height);
			//shapeRenderer.rect(bottomTubeRectanges[i].x,bottomTubeRectanges[i].y,bottomTubeRectanges[i].width,bottomTubeRectanges[i].height);

			//check for collision between birdCircle and tubeRectangles//
			if (Intersector.overlaps(birdCircle, topTubeRectanges[i]) || Intersector.overlaps(birdCircle, bottomTubeRectanges[i])) {
				gameState = 2;
			}
		}
		//shapeRenderer.end();
	}

	@Override
	public void dispose () {
		batch.dispose();

	}
}
