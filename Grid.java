package tetris;

import java.awt.Color;
import java.awt.Graphics;

// class for Grid which contains multiple instances of GridSquare //
public class Grid {
	
	private int hSquares = 12;
	private int vSquares;
	private int squareSide;
		
	GridSquare[][] gridSquares;
	
	public Grid(int panelWidth, int panelHeight) {
		
		// below a calculation is used to determine vertical squares and square size //
		// based on panel width and horizontal squares, which are fixed //
		// however this seems unnecessary and should eventually change //
		
		// in this implementation, hSquares = 12, vSquares = 20 and size is 35 // 
		this.squareSide = panelWidth / hSquares;
		this.vSquares = (panelHeight / squareSide);
		
		// make an array of gridSquares //
		gridSquares = new GridSquare[vSquares][hSquares];
		
		// create each gridSquare object (mark it as empty initially)
		for (int i = 0; i < vSquares; i++) {
			for (int j = 0; j < hSquares; j++) {
				gridSquares[i][j] = new GridSquare(i, j, false, squareSide);

			}
		}
	}
	
	// get number of horizontal squares //
	public int getHSquares() {
		return this.hSquares;
	}
	
	// get number of vertical squares //
	public int getVSquares() {
		return this.vSquares;
	}
	
	// get size of square side //
	public int getSquareSide() {
		return this.squareSide;
	}
	
	// add a block to the grid //
	public void addBlock(Block block) {
		
		int size = block.blockWidth; // size of the hitbox (same in x and y) //
		int positionX = block.positionX;
		int positionY = block.positionY;
			
		Color color = block.color;
		
		// examine the hitbox of the block //
		// for every non-empty square, add it in the respective grid position with its color //
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				if (block.hitbox[i][j] == true) {					
					gridSquares[positionY + i][positionX + j].addSquare(positionX + j, positionY + i, color);
				}
			}
		}
		
	}
	
	// clear rows if a row has been completed //
	// TODO: must decide whether the naming "rows" or "lines" is preferable //
	public int clearRows() {
		
		boolean rowFull;
		int linesCleared = 0;
		
		// access all rows from bottom to top //
		// and check if each one is full (and so should be removed) //
		for (int i = vSquares - 1; i >= 1; i--) {
			
			rowFull = true; // initialise //
			
			// check all squares of a row //
			// if at least one is empty, break and go to next row //
			for (int j = 0; j < hSquares; j++) {
				if (gridSquares[i][j].isBlocked == false) {
					rowFull = false;
					break;
				}
			}
			// if no square is empty, row is full and so everything below should drop one level //
			// but the current row (i) must not be increased, since another completed row may follow //
			// so increment i here, to balance the decrement in the for loop //
			if (rowFull == true) {
				linesCleared++;
				dropRow(i);
				i++;
			}
		}
		
		return linesCleared;
	}
	
	// drop (remove) a full row and drop everything above by one level
	void dropRow(int row) {
		
		// this function currently drops all rows even if most are empty //
		// this could be optimised by keeping track of max row //
		
		// TODO: fix below statement (row == 1 should work) //
		if (row == 5) return; // break the recursion until we introduce max row //
		
		// copy the gridsquares of the row to the next row //
		for (int j = 0; j < hSquares; j++) {
			
			gridSquares[row][j].color = gridSquares[row-1][j].color;
			gridSquares[row][j].isBlocked = gridSquares[row-1][j].isBlocked;
		}
		
		// keep doing this recursively until we reach the top //
		dropRow(row-1);
	}
	
	// check if a square is blocked (non-empty)
	public boolean isSquareBlocked(int column, int row) {
		return gridSquares[row][column].isBlocked;
	}
	
	// draw the grid (by drawing each non-empty grid square //
	public void draw(Graphics g) {
				
		for (int i = 0; i < vSquares; i++) {
			for (int j = 0; j < hSquares; j++) {
				if (gridSquares[i][j].isBlocked == true) {
					gridSquares[i][j].draw(g, j, i);
				}
			}
		}

	}

}
