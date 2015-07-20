package com.matheusronfim.gamestates;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.matheusronfim.main.Game;
import com.matheusronfim.managers.GameKeys;
import com.matheusronfim.managers.GameStateManager;
import com.matheusronfim.managers.Save;

public class HighScoreState extends GameState {
	
	private SpriteBatch sb;
	
	private BitmapFont font;
	
	private long[] highScores;
	private String[] names;

	public HighScoreState(GameStateManager gsm) {
		super(gsm);
	}

	public void init() {
		sb = new SpriteBatch();
		
		FreeTypeFontGenerator gen = new FreeTypeFontGenerator(Gdx.files.internal("fonts/Hyperspace Bold.ttf"));
		font = gen.generateFont(20);
		
		Save.load();
		highScores = Save.gd.getHighscores();
		names = Save.gd.getNames();
	}

	public void update(float dt) {
		handleInput();
	}

	public void draw() {
		sb.setProjectionMatrix(Game.cam.combined);
		
		sb.begin();
		
		String s;
		float w;
		
		s = "High Scores";
		w = font.getBounds(s).width;
		font.draw(sb, s, (Game.WIDTH - w) / 2, 300);
		
		for (int i = 0; i < highScores.length; ++i) {
			s = String.format("%2d. %7s %s", i + 1, highScores[i], names[i]);
			w = font.getBounds(s).width;
			font.draw(sb, s, (Game.WIDTH - w) / 2, 270 - 20 * i);
		}
		
		sb.end();
	}

	public void handleInput() {
		if(GameKeys.isPressed(GameKeys.ENTER) ||
				GameKeys.isPressed(GameKeys.ESCAPE)) {
			gsm.setState(GameStateManager.MENU);
		}
	}

	public void dispose() {
		sb.dispose();
		font.dispose();
	}

}
