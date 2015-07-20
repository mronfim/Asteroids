package com.matheusronfim.managers;

import java.io.Serializable;

public class GameData implements Serializable {
	
	private static final long serialVersionUID = 1;
	
	private final int MAX_SCORES = 10;
	private long[] highscores;
	private String[] names;
	
	private long tentativeScore;
	
	public GameData() {
		highscores = new long[MAX_SCORES];
		names = new String[MAX_SCORES];
	}
	
	// sets up empty high scores table
	public void init() {
		for(int i = 0; i < MAX_SCORES; ++i) {
			highscores[i] = 0;
			names[i] = "---";
		}
	}
	
	public long[] getHighscores() { return highscores; }
	public String[] getNames() { return names; }
	
	public long getTentativeScore() { return tentativeScore; }
	public void setTentativeScore(long i) { tentativeScore = i; }
	
	public boolean isHighScore(long score) {
		return score > highscores[MAX_SCORES - 1];
	}
	
	public void addHighScore(long newScore, String name) {
		if(isHighScore(newScore)) {
			highscores[MAX_SCORES - 1] = newScore;
			names[MAX_SCORES - 1] = name;
			sortHighScores();
		}
	}
	
	public void sortHighScores() {
		
		// insertion sort
//		int in, out;
//		for (out = 1; out < MAX_SCORES; out++) {
//			long temp = highscores[out];
//			String tempName = names[out];
//			in = out;
//
//			while (in > 0 && highscores[in - 1] >= temp) {
//				highscores[in] = highscores[in -1];
//				names[in] = names[in - 1];
//				in--;
//			}
//
//			highscores[in] = temp;
//			names[in] = tempName;
//		}
		
		for(int i = 0; i < MAX_SCORES - 1; ++i) {
			for(int j = i + 1; j < MAX_SCORES; ++j) {
				if(highscores[i] < highscores[j]) {
					long temp = highscores[i];
					highscores[i] = highscores[j];
					highscores[j] = temp;
					
					String tempName = names[i];
					names[i] = names[j];
					names[j] = tempName;
				}
			}
		}
	}
}
