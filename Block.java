package tetris;

import java.awt.Color;
import java.awt.Graphics;

// class for Block object //
public class Block {
	
	// fields that will be obtained from blockType //
	boolean[][] hitbox;
	int blockWidth;
	int blockHeight;
	Color color;
	
	int squareWidth; // real width of the block square //
	
	int positionX; // starting x position of the hitbox relative to the grid//
	int positionY; // starting y position of the hitbox relative to the grid //
	
	int drawPositionX; // real x position (used when drawing the block in the side panel) //
	int drawPositionY; // real y position (used when drawing the block in the side panel) //
		
	// constructor used when drawing the block on the main panel //
	public Block(BlockType blockType, int squareWidth) {
		
		this.hitbox = blockType.getHitbox();
		this.blockWidth = blockType.getWidth();
		this.color = blockType.getColor();
		
		this.positionX = (6 - blockWidth/2); // initialization to middle //
		this.positionY = 0; // initialization to row 0 //
		
		this.squareWidth = squareWidth;
						
	}
	
	// constructor for sideBlock //
	public Block(BlockType blockType, int squareWidth, int drawPositionX, int drawPositionY) {
		
		this.hitbox = blockType.getHitbox();
		this.blockWidth = blockType.getWidth();
		this.color = blockType.getColor();
		
		this.positionX = (6 - blockWidth/2); // initialization to middle //
		this.positionY = 0; // initialization to row 0 //
		
		this.squareWidth = squareWidth;
		
		this.drawPositionX = drawPositionX;
		this.drawPositionY = drawPositionY;
						
	}
	
	// draw the block by drawing each of its squares according to the hitbox //
	// this is only for the main panel block //
	public void draw(Graphics g) {
		
		g.setColor(color);
		
		for (int i = 0; i < hitbox.length; i++) {
			for (int j = 0; j < hitbox[0].length; j++) {
				if (hitbox[i][j] == true) {
					
					// starting point is positionX,Y //
					// and i,j is relative to that point //
					int squareX = (positionX + j)*squareWidth;
					int squareY = (positionY + i)*squareWidth;
					
					// draw the square with its color //
					g.setColor(color);
					g.fillRect(squareX, squareY, squareWidth, squareWidth);
					
					// also draw a border around //
					g.setColor(color.darker());
					g.drawRect(squareX, squareY, squareWidth, squareWidth);
				}
				// for debugging purposes, the empty hitbox squares can also be drawn //
//				else {
//					int squareX = (positionX + j)*squareWidth;
//					int squareY = (positionY + i)*squareWidth;
//					
//					g.setColor(Color.WHITE);
//					g.fillRect(squareX, squareY, squareWidth, squareWidth);
//										
//					g.setColor(color.darker());
//					g.drawRect(squareX, squareY, squareWidth, squareWidth);
//				}
			}
		}
		
	} 
	
	// draw the block on the side panel //
	public void drawSidePanel(Graphics g) {
		
		g.setColor(color);
		
		// all drawings must start from the same X //
		// so scan the hitbox vertically, to get the first non-empty column //
		
		int startingColumn = 0;
		boolean startingColumnFound = false;
		
		for (int j = 0; j < hitbox[0].length; j++) {
			for (int i = 0; i < hitbox[0].length; i++) {
				if (hitbox[i][j] == true) {
					
					// draw relative to the starting column //
					int squareX = drawPositionX + (j - startingColumn)*squareWidth;
					int squareY = drawPositionY + i*squareWidth;
					
					// draw the square with its color //
					g.setColor(color);
					g.fillRect(squareX, squareY, squareWidth, squareWidth);
					
					// also draw a border around //
					g.setColor(color.darker());
					g.drawRect(squareX, squareY, squareWidth, squareWidth);
					
					// found a non-empty column //
					startingColumnFound = true;
				}
			}
			
			// if the column is empty, increment counter until a non-empty one is found //
			if (startingColumnFound == false) startingColumn++;
		}
		
	} 
	
	// move the block down by one position //
	public void moveDown() {
		this.positionY += 1;
	}
	
	// move the block to the left by one position //
	public void moveLeft() {
		this.positionX -= 1;
	}

	// move the block to the right by one position //
	public void moveRight() {
		this.positionX += 1;
	}
	
	// rotate block counter-clockwise //
	// this method returns the rotated array //
	// this array is transferred to the hitbox only if the rotation is valid //
	public boolean[][] rotateRightNew() {
		
		int size = hitbox.length;
		boolean[][] rotated = new boolean[size][size];
		
		// rotate the array (notice that the hitbox is square, so dimensions stay the same) //
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				rotated[i][j] = hitbox[size -1 - j][i];
			}
		}
		
		return rotated;
	}
	
	// rotate block clock-wise //
	// this method returns the rotated array //
	// this array is transferred to the hitbox only if the rotation is valid //
	public boolean[][] rotateLeftNew() {
		
		int size = hitbox.length;
		boolean[][] rotated = new boolean[size][size];
		
		// rotate the array (notice that the hitbox is square, so dimensions stay the same) //
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				rotated[i][j] = hitbox[j][size - 1 - i];
			}
		}
		
		return rotated;
	}
	
	// get the actual bottom-most position of the block //
	// (which may not be the last row of this hitbox) //
	public int getBottommostPosition() {
		
		int bottommostPosition = this.positionY; // lower bound //
		
		// scan the hitbox from bottom to top //
		// if we find a non-empty row, return it //
		
		for (int i = blockWidth - 1; i >= 0; i--) {
			for (int j = 0; j < blockWidth; j++) {
				if (hitbox[i][j] == true) {
					bottommostPosition = this.positionY + i;
															
					return bottommostPosition;
				}
			}
		}
		
		// no empty blocks exist, so the return condition below is perhaps redundant //
		return bottommostPosition;
	}
}
