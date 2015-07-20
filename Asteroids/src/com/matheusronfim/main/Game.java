package com.matheusronfim.main;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.matheusronfim.managers.GameInputProcessor;
import com.matheusronfim.managers.GameKeys;
import com.matheusronfim.managers.GameStateManager;
import com.matheusronfim.managers.JukeBox;

public class Game implements ApplicationListener {
	
	public static int WIDTH;
	public static int HEIGHT;
	
	public static OrthographicCamera cam;
	
	private GameStateManager gsm;
	
	public void create() {
		
		WIDTH = Gdx.graphics.getWidth();
		HEIGHT = Gdx.graphics.getHeight();
		
		cam = new OrthographicCamera(WIDTH, HEIGHT);
		cam.translate(WIDTH / 2, HEIGHT / 2);
		cam.update();
		
		Gdx.input.setInputProcessor(new GameInputProcessor());
		
		JukeBox.load("sounds/explode.ogg", "explode");
		JukeBox.load("sounds/extralife.ogg", "extralife");
		JukeBox.load("sounds/largesaucer.ogg", "largesaucer");
		JukeBox.load("sounds/pulsehigh.ogg", "pulsehigh");
		JukeBox.load("sounds/pulselow.ogg", "pulselow");
		JukeBox.load("sounds/saucershoot.ogg", "saucershoot");
		JukeBox.load("sounds/shoot.ogg", "shoot");
		JukeBox.load("sounds/smallsaucer.ogg", "smallsaucer");
		JukeBox.load("sounds/thruster.ogg", "thruster");
		
		gsm = new GameStateManager();
	}
	
	public void render() {
		
		// clear screen to black
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL30.GL_COLOR_BUFFER_BIT);
		
		gsm.update(Gdx.graphics.getDeltaTime());
		gsm.draw();
		
		GameKeys.update();
	}
	
	public void resize(int width, int height) {
		
	}
	
	public void pause() {
		
	}
	
	public void resume() {
		
	}
	
	public void dispose() {
		
	}
}
