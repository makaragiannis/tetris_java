package tetris;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

// this class not only shows and updates the score //
// but also everything else that appears on the right panel //
// (except for the next block drawing) //
// so this class should eventually be renamed to sth like "sidePanel" //
public class Score {
	
	int score;
	final int scorePositionX;
	final int scorePositionY;
	final int fontSize = 25;
	
	int level;
	int linesCleared;
	int nextSet;
	
	// below is the array of Milliseconds (ms) required for a block to drop in each level //
	
	// max level in which speed increases = 19 //
	// after that it retains a constant speed  //
	int[] droppingMsPerLevel = {1000, 950, 900, 850, 800, 750, 700, 650, 600, 550, 500, 450, 400, 350, 300, 250, 200, 150, 100, 90, 80, 70, 60, 50};
	
	// constructor //
	public Score(int scorePositionX, int scorePositionY) {
		
		initScore(); // initialise Score to 0 //
		
		this.scorePositionX = scorePositionX;
		this.scorePositionY = scorePositionY;
		
		level = 0;
		linesCleared = 0;
		
		nextSet = 10; // level increases when a set of 10 lines is cleared //
	}
	
	// drawing function //
	public void draw(Graphics g) {
		
		g.setColor(Color.white);
		g.setFont(new Font("serif", Font.BOLD, fontSize));
		
		// draw the score //
		g.drawString("SCORE", scorePositionX, scorePositionY);
		g.drawString("" + score, scorePositionX, scorePositionY + fontSize);
		
		// draw the level //
		g.drawString("LEVEL", scorePositionX, scorePositionY + 3*fontSize);
		g.drawString("" + level, scorePositionX, scorePositionY + 4*fontSize);
		
		// draw the number of lines cleared //
		g.drawString("LINES", scorePositionX, scorePositionY + 6*fontSize);
		g.drawString("" + linesCleared, scorePositionX, scorePositionY + 7*fontSize);
		
		// draw the next block (text only) //
		g.drawString("NEXT", scorePositionX, scorePositionY + 9*fontSize);
		
		// for debugging purposes, draw speed //
		
//		g.drawString("SPEED", scorePositionX, scorePositionY + 15*fontSize);
//		g.drawString("" + (double) 1000 / (getDroppingNs() / 1000000), scorePositionX, scorePositionY + 16*fontSize);
		
	}
	
	// function to increase score when lines are cleared //
	public void increaseScore(int newLinesCleared) {
		
		// maximum number of lines that can be cleared is 4 //
		
		switch (newLinesCleared) {
			case 1 -> { score += 40; break; }
			case 2 -> { score += 100; break; }
			case 3 -> { score += 300; break; }
			case 4 -> { score += 1200; break; }
			
			// this method is only called when at least a line has been cleared //
			// so a value of 0 should not occur //
			
			default -> { score += 0; System.out.println("Unexpected value."); break; }
		}
		
		linesCleared += newLinesCleared;
		
		// if a new set of 10 lines has been cleared, increase level //
		if (linesCleared >= nextSet) {
			level++;
			nextSet += 10;
		}
	}
	
	// init score to 0 //
	public void initScore() {
		score = 0;
	}
	
	// get nanoseconds (ns) required for a block to drop, based on the level //
	// the Ms per level array is accessed and the ms are converted to ns     //
	int getDroppingNs() {
		
		// only if level has maxed out, return the value on the last position (fastest) //
		if (level >= droppingMsPerLevel.length) return 1000000 * droppingMsPerLevel[droppingMsPerLevel.length - 1]; 
		
		return 1000000 * droppingMsPerLevel[level];
	}
	
}