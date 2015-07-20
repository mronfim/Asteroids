package com.matheusronfim.gamestates;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.matheusronfim.entities.Asteroid;
import com.matheusronfim.entities.Bullet;
import com.matheusronfim.entities.FlyingSaucer;
import com.matheusronfim.entities.Particle;
import com.matheusronfim.entities.Player;
import com.matheusronfim.main.Game;
import com.matheusronfim.managers.GameKeys;
import com.matheusronfim.managers.GameStateManager;
import com.matheusronfim.managers.JukeBox;
import com.matheusronfim.managers.Save;

public class PlayState extends GameState {
	
	private SpriteBatch sb;
	private ShapeRenderer sr;
	
	private BitmapFont font;
	private Player hudPlayer;
	
	private Player player;
	private ArrayList<Bullet> bullets;
	private ArrayList<Asteroid> asteroids;
	
	private FlyingSaucer flyingSaucer;
	private ArrayList<Bullet> enemyBullets;
	private float fsTimer;
	private float fsTime;
	
	private ArrayList<Particle> particles;
	
	private int level;
	private int totalAsteroids;
	private int numAsteroidsLeft;
	
	private float maxDelay;
	private float minDelay;
	private float currentDelay;
	private float bgTimer;
	private boolean playLowPulse;
	
	public PlayState(GameStateManager gsm) {
		super(gsm);
	}
	
	public void init() {
		
		sb = new SpriteBatch();
		sr = new ShapeRenderer();
		
		// set font
		FreeTypeFontGenerator gen = new FreeTypeFontGenerator(Gdx.files.internal("fonts/Hyperspace Bold.ttf"));
		font = gen.generateFont(20);
		
		bullets = new ArrayList<Bullet>();
		player = new Player(bullets);
		
		asteroids = new ArrayList<Asteroid>();
		
		particles = new ArrayList<Particle>();
		
		level = 1;
		spawnAsteroids();
		
		hudPlayer = new Player(null);
		
		fsTimer = 0;
		fsTime = 15;
		enemyBullets = new ArrayList<Bullet>();
		
		// set up bg music
		maxDelay = 1;
		minDelay = 0.25f;
		currentDelay = maxDelay;
		bgTimer = maxDelay;
		playLowPulse = true;
	}
	
	private void createParticles(float x, float y) {
		for (int i = 0; i < 6; ++i) {
			particles.add(new Particle(x, y));
		}
	}
	
	private void splitAsteroids(Asteroid a) {
		createParticles(a.getx(), a.gety());
		numAsteroidsLeft--;
		currentDelay = ((maxDelay - minDelay) * numAsteroidsLeft / totalAsteroids) + minDelay;
		if (a.getType() == Asteroid.LARGE) {
			asteroids.add(new Asteroid(a.getx(), a.gety(), Asteroid.MEDIUM));
			asteroids.add(new Asteroid(a.getx(), a.gety(), Asteroid.MEDIUM));
		}
		if (a.getType() == Asteroid.MEDIUM) {
			asteroids.add(new Asteroid(a.getx(), a.gety(), Asteroid.SMALL));
			asteroids.add(new Asteroid(a.getx(), a.gety(), Asteroid.SMALL));
		}
	}
	
	private void spawnAsteroids() {
		
		asteroids.clear();
		
		int numToSpawn = 4 + level - 1;
		totalAsteroids = numToSpawn * 7;
		numAsteroidsLeft = totalAsteroids;
		currentDelay = maxDelay;
		
		for(int i = 0; i < numToSpawn; i++) {
			
			float x = MathUtils.random(Game.WIDTH);
			float y = MathUtils.random(Game.HEIGHT);
			
			float dx = x - player.getx();
			float dy = y - player.gety();
			float dist = (float) Math.sqrt(dx * dx + dy * dy);
			
			while (dist < 100) {
				x = MathUtils.random(Game.WIDTH);
				y = MathUtils.random(Game.HEIGHT);
				dx = x - player.getx();
				dy = y - player.gety();
				dist = (float) Math.sqrt(dx * dx + dy * dy);
			}
			
			asteroids.add(new Asteroid(x, y, Asteroid.LARGE));
		}
	}
	
	public void update(float dt) {
		
		// get user input
		handleInput();
		
		// next level
		if(asteroids.size() == 0) {
			level++;
			spawnAsteroids();
		}
		
		// update player
		player.update(dt);
		if (player.isDead()){
			if(player.getLives() == 0) {
				JukeBox.stopAll();
				Save.gd.setTentativeScore(player.getScore());
				gsm.setState(GameStateManager.GAMEOVER);
				return;
			}
			player.reset();
			player.loseLife();
			flyingSaucer = null;
			JukeBox.stop("smallsaucer");
			JukeBox.stop("largesaucer");
			return;
		}
		
		// update player bullets
		for(int i = 0; i < bullets.size(); ++i) {
			bullets.get(i).update(dt);
			if (bullets.get(i).shouldRemove()) {
				bullets.remove(i);
				i--;
			}
		}
		
		// update flying saucer
		if (flyingSaucer == null) {
			fsTimer += dt;
			if(fsTimer >= fsTime) {
				fsTimer = 0;
				int type = MathUtils.random() < 0.5 ? 
						FlyingSaucer.SMALL : FlyingSaucer.LARGE;
				int direction = MathUtils.random() < 0.5 ? 
						FlyingSaucer.RIGHT : FlyingSaucer.LEFT;
				flyingSaucer = new FlyingSaucer(type, direction, player, enemyBullets);
			}
		}
		// if there is a flying saucer already
		else {
			flyingSaucer.update(dt);
			if(flyingSaucer.shouldRemove()) {
				flyingSaucer = null;
				JukeBox.stop("smallsaucer");
				JukeBox.stop("largesaucer");
			}
		}
		
		// update flying saucer bullets
		for(int i = 0; i < enemyBullets.size(); ++i) {
			enemyBullets.get(i).update(dt);
			if (enemyBullets.get(i).shouldRemove()) {
				enemyBullets.remove(i);
				i--;
			}
		}
		
		// update asteroids
		for (int i = 0; i < asteroids.size(); ++i) {
			asteroids.get(i).update(dt);
			if (asteroids.get(i).shouldRemove()) {
				asteroids.remove(i);
				i--;
			}
		}
		
		// update particles
		for (int i = 0; i < particles.size(); ++i) {
			particles.get(i).update(dt);
			if(particles.get(i).shouldRemove()) {
				particles.remove(i);
				i--;
			}
		}
		
		// check collisions
		checkCollisions();
		
		// play bg music
		bgTimer += dt;
		if (!player.isHit() && bgTimer >= currentDelay) {
			if(playLowPulse) {
				JukeBox.play("pulselow");
			} else {
				JukeBox.play("pulsehigh");
			}
			playLowPulse = !playLowPulse;
			bgTimer = 0;
		}
	}
	
	private void checkCollisions() {
		
		// player-asteroid collision
		if (!player.isHit()) {
			for (int i = 0; i < asteroids.size(); ++i) {
				Asteroid a = asteroids.get(i);
				if (a.intersects(player)) {
					player.hit();
					asteroids.remove(i);
					i--;
					splitAsteroids(a);
					JukeBox.play("explode");
					break;
				}
			}
		}
		
		// bullet-asteroid collision
		for (int i = 0; i < bullets.size(); ++i) {
			Bullet b = bullets.get(i);
			for (int j = 0; j < asteroids.size(); ++j) {
				Asteroid a = asteroids.get(j);
				if (a.contains(b.getx(), b.gety())) {
					bullets.remove(i);
					i--;
					asteroids.remove(j);
					j--;
					splitAsteroids(a);
					player.incrementScore(a.getScore());
					JukeBox.play("explode");
					break;
				}
			}
			
			// bullet-flying saucer collision
			if (flyingSaucer != null) {
				if (flyingSaucer.contains(b.getx(), b.gety())) {
					bullets.remove(i);
					i--;
					createParticles(flyingSaucer.getx(), flyingSaucer.gety());
					player.incrementScore(flyingSaucer.getScore());
					flyingSaucer = null;
					JukeBox.stop("smallsaucer");
					JukeBox.stop("largesaucer");
					JukeBox.play("explode");
				}
			}
		}
		
		// player-flying saucer collision
		if (flyingSaucer != null) {
			if(player.intersects(flyingSaucer)) {
				player.hit();
				createParticles(player.getx(), player.gety());
				createParticles(flyingSaucer.getx(), flyingSaucer.gety());
				flyingSaucer = null;
				JukeBox.stop("smallsaucer");
				JukeBox.stop("largesaucer");
				JukeBox.play("explode");
			}
		}
		
		// player-enemy bullets collision
		if (!player.isHit()) {
			for (int i = 0; i < enemyBullets.size(); i++) {
				Bullet b = enemyBullets.get(i);
				if (player.contains(b.getx(), b.gety())) {
					player.hit();
					enemyBullets.remove(i);
					i--;
					JukeBox.play("explode");
					break;
				}
			}
		}
		
		// flying saucer-asteroid collision
		if (flyingSaucer != null) {
			for (int i = 0; i < asteroids.size(); i++){
				Asteroid a = asteroids.get(i);
				if (a.intersects(flyingSaucer)) {
					asteroids.remove(i);
					i--;
					splitAsteroids(a);
					createParticles(a.getx(), a.gety());
					createParticles(flyingSaucer.getx(), flyingSaucer.gety());
					flyingSaucer = null;
					JukeBox.stop("smallsaucer");
					JukeBox.stop("largesaucer");
					JukeBox.play("explode");
					break;
				}
			}
		}
		
		// asteroid-enemy bullet collision
		for (int i = 0; i < enemyBullets.size(); ++i) {
			Bullet b = enemyBullets.get(i);
			for (int j = 0; j < asteroids.size(); ++j) {
				Asteroid a = asteroids.get(j);
				if (a.contains(b.getx(), b.gety())) {
					enemyBullets.remove(i);
					i--;
					asteroids.remove(j);
					j--;
					splitAsteroids(a);
					JukeBox.play("explode");
					break;
				}
			}
		}
	}
	
	public void draw() {
		
		sb.setProjectionMatrix(Game.cam.combined);
		sr.setProjectionMatrix(Game.cam.combined);
		
		// draw player
		player.draw(sr);
		
		// draw bullets
		for (int i = 0; i < bullets.size(); ++i) {
			bullets.get(i).draw(sr);
		}
		
		// draw flying saucer
		if (flyingSaucer != null) {
			flyingSaucer.draw(sr);
		}
		
		// draw flying saucer bullets
		for (int i = 0; i < enemyBullets.size(); ++i) {
			enemyBullets.get(i).draw(sr);
		}
		
		// draw asteroids
		for (int i = 0; i < asteroids.size(); ++i) {
			asteroids.get(i).draw(sr);
		}
		
		// draw particles
		for (int i = 0; i < particles.size(); ++i) {
			particles.get(i).draw(sr);
		}
		
		// draw score
		sb.begin();
		font.setColor(1, 1, 0, 1);
		font.draw(sb, Long.toString(player.getScore()), 10, 390);
		sb.end();
		
		// draw lives
		for (int i = 0; i < player.getLives(); ++i) {
			hudPlayer.setPosition(Game.WIDTH - 20 - i * 15, Game.HEIGHT - 20);
			hudPlayer.draw(sr);
		}
	}
	
	public void handleInput() {
		
		if(!player.isHit()) {
			player.setLeft(GameKeys.isDown(GameKeys.LEFT));
			player.setRight(GameKeys.isDown(GameKeys.RIGHT));
			player.setUp(GameKeys.isDown(GameKeys.UP));
			if (GameKeys.isPressed(GameKeys.SPACE)) {
				player.shoot();
			}
		}
	}
	
	public void dispose() {
		sb.dispose();
		sr.dispose();
		font.dispose();
	}
}
