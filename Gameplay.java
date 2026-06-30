package tetris;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Random;

import javax.swing.JPanel;

// gameplay class for tetris; this is where all the action happens //
public class Gameplay extends JPanel implements KeyListener, Runnable {
	
	int mainPanelWidth, panelHeight, sidePanelWidth; // dimensions of the two panels //
	
	Grid grid; // main panel Grid separated into square blocks //
	
	Block mainBlock, nextBlock; // main (dropping) block and side panel (next) block //
	
	BlockType[] blockTypes = BlockType.values(); // array of the seven possible block types //
	
	int squareSize; // size of the grid Squares //
	int sidePanelSquareSize; // size of the squares of the side Block (smaller than normal) //
	
	int hSquares, vSquares;
	
	Score score;
	
	double droppingNs; // nanoseconds required for the block to move down one row //
	
	double timeNow, time0;

	boolean blockAutoDrop; // block drops automatically //
	boolean downPressed; // down is pressed; block moves down faster //
	boolean moveRight, moveLeft;
	
	private final int delayH = 2; // delay for horizontal movement //
	private int timerH = 0;
	
	final int panelGap = 10; // gap between two panels //
	
	boolean gameOver;
	
	Thread gameThread;
	
	Random random = new Random();
	
	// constructor for gameplay //
	public Gameplay() {
		
		mainPanelWidth = 421; // 420 + 1 //
		panelHeight = 701; // 700 + 1 //
		sidePanelWidth = 150;
		
		// create the grid with the main panel dimensions //
		grid = new Grid(mainPanelWidth, panelHeight);
		
		hSquares = grid.getHSquares();
		vSquares = grid.getVSquares();
		squareSize = grid.getSquareSide();
		sidePanelSquareSize = 3*squareSize/4; // side panel blocks are smaller //
		
		// hSquares == 12 //
		// vSquares == 20 //
		
		// create main block (of random block type) //
		mainBlock = new Block(blockTypes[random.nextInt(7)], squareSize);
		
		// create the next block on the side panel //
		nextBlock = new Block(blockTypes[random.nextInt(7)], sidePanelSquareSize, mainPanelWidth + panelGap+ 30, 270);
		
		// create the scoring object (includes all side panel information)
		score = new Score(mainPanelWidth + panelGap + 10, 30);
		
		droppingNs = score.getDroppingNs();
		
		blockAutoDrop = true;
		moveRight = false;
		moveLeft = false;
		
		gameOver = false;
		
		addKeyListener(this);
		setFocusable(true);
		setFocusTraversalKeysEnabled(false);
		
		time0 = System.nanoTime();
		
		gameThread = new Thread(this);
		gameThread.start();
	}
	
	@Override
	public void run() {
		
		// create a loop to run the game //
		long lastTime = System.nanoTime();
		double amountOfTicks = 60.0;
	    double ns = 1000000000 / amountOfTicks;
	    double delta = 0;
	    	
	    while (true) { 
	        long now = System.nanoTime();
	        delta += (now - lastTime) / ns;
	        lastTime = now;
	        	                    
	        if (delta >= 1) {
	        	
	            if (getWidth() == 0) {
	                delta--;
	                continue; 
	            }
	            
	            // if down button is pressed, move the block down //
	            // and check if the move is valid //
	            if (downPressed) {
					
					if (checkMove(mainBlock.hitbox, mainBlock.positionX, mainBlock.positionY + 1)) {
						mainBlock.moveDown();
						
					}
					else {	
						
						// if the move is valid, check if the block has reached as low as it can //
						checkBlockBottom();
					}
	            }
	            	            
	            repaint();
	            delta--;
	        }
	        
	        try { Thread.sleep(2); } catch (InterruptedException e) { e.printStackTrace(); }
	    }		
	}
	
	// check if the block is at its bottom-most possible position (if it cannot go any lower) //
	void checkBlockBottom() {
		int linesCleared;
		
		// if the block has reached the last row //
		// or if the block cannot move down any more //
		// add it to the grid //
		if ((mainBlock.getBottommostPosition() == vSquares - 1) || (checkMove(mainBlock.hitbox, mainBlock.positionX, mainBlock.positionY + 1) == false)){
			grid.addBlock(mainBlock);
			
			// next block is now the main block //
			// and its squares are also resized to the main panel width //
			mainBlock = nextBlock;
			mainBlock.squareWidth = squareSize;
			
			// clear all rows and get the number of cleared lines //
			linesCleared = grid.clearRows();
			
			// increase score if at least one line is cleared //
			if (linesCleared > 0) {
				score.increaseScore(linesCleared); // score depends on lines cleared //
				droppingNs = score.getDroppingNs(); // increase speed depending on the level //
			}
			
			// create a new next block on the side panel //
			nextBlock = new Block(blockTypes[random.nextInt(7)], sidePanelSquareSize, mainPanelWidth + panelGap + 30, 270);
			
			// check if the new block can be placed in its initial position //
			// if not, this is game over //
			if (checkMove(mainBlock.hitbox, mainBlock.positionX, mainBlock.positionY)== false) {
				gameOver = true;
			}
		}
	}
	
	// check if a move is valid, based on the new hitbox created by the move //
	boolean checkMove(boolean[][] newHitbox, int positionX, int positionY) {
		
		int hitboxSize = newHitbox.length;
		
		// scan the new hitbox and check if //
		// 1. the block is not out of bounds //
		// 2. all corresponding grid squares are empty //
		for (int i = 0; i < hitboxSize; i++) {
			for (int j = 0; j < hitboxSize; j++) {
				
				if (newHitbox[i][j] == true) {
					
					// check if block is out of bounds horizontally //
					if ((positionX + j < 0) || (positionX + j >= hSquares)) {
						return false; // invalid move //
					}
					// check if block is out of bounds vertically //
					else if ((positionY + i < 0) || (positionY + i >= vSquares)) {
						return false; // invalid move //
					}
					// check if block can fit into the grid in its new position //
					else if (grid.isSquareBlocked(positionX + j, positionY + i) == true) {
						return false; // invalid move //
					}
				}
			}
		}
		
		// the move is valid if none of the above were triggered //
		return true;
	}
	
	@Override
	public void keyTyped(KeyEvent e) {
		
	}

	@Override
	public void keyPressed(KeyEvent e) {
		
		// if down is pressed, move the block manually //
		if (e.getKeyCode() == KeyEvent.VK_DOWN) {
			blockAutoDrop = false;
			downPressed = true;
		}
		
		if (e.getKeyCode() == KeyEvent.VK_RIGHT) { 
			moveRight = true;
		}
		
		if (e.getKeyCode() == KeyEvent.VK_LEFT) { 
			moveLeft = true;
		}
		
		// left rotation //
		if (e.getKeyCode() == KeyEvent.VK_Q) { 
			boolean[][] rotatedHitbox = mainBlock.rotateLeftNew();
			
			// check if rotation is valid //
			if (checkMove(rotatedHitbox, mainBlock.positionX, mainBlock.positionY)) {
				mainBlock.hitbox = rotatedHitbox;
			}
			
			// we can rotate the block even if the base move is invalid //
			// we try shifting the block by one position horizontally //
			// if it works, we accept this move //
			
			else if (checkMove(rotatedHitbox, mainBlock.positionX + 1, mainBlock.positionY)) {
				mainBlock.hitbox = rotatedHitbox;
				mainBlock.positionX += 1;
			}
			else if (checkMove(rotatedHitbox, mainBlock.positionX - 1, mainBlock.positionY)) {
				mainBlock.hitbox = rotatedHitbox;
				mainBlock.positionX -= 1;
			}
			// special case for I blocks //
			else if (mainBlock.blockWidth == 4) {
				if (checkMove(rotatedHitbox, mainBlock.positionX + 2, mainBlock.positionY)) {
					mainBlock.hitbox = rotatedHitbox;
					mainBlock.positionX += 2;
				}
				else if (checkMove(rotatedHitbox, mainBlock.positionX - 2, mainBlock.positionY)) {
					mainBlock.hitbox = rotatedHitbox;
					mainBlock.positionX -= 2;
				}
			}
		}
		
		// right rotation //
		if (e.getKeyCode() == KeyEvent.VK_W) { 
			boolean[][] rotatedHitbox = mainBlock.rotateRightNew();
			
			if (checkMove(rotatedHitbox, mainBlock.positionX, mainBlock.positionY)) {
				mainBlock.hitbox = rotatedHitbox;
			}
			else if (checkMove(rotatedHitbox, mainBlock.positionX + 1, mainBlock.positionY)) {
				mainBlock.hitbox = rotatedHitbox;
				mainBlock.positionX += 1;
			}
			else if (checkMove(rotatedHitbox, mainBlock.positionX - 1, mainBlock.positionY)) {
				mainBlock.hitbox = rotatedHitbox;
				mainBlock.positionX -= 1;
			}
			// special case for I blocks //
			else if (mainBlock.blockWidth == 4) {
				if (checkMove(rotatedHitbox, mainBlock.positionX + 2, mainBlock.positionY)) {
					mainBlock.hitbox = rotatedHitbox;
					mainBlock.positionX += 2;
				}
				else if (checkMove(rotatedHitbox, mainBlock.positionX - 2, mainBlock.positionY)) {
					mainBlock.hitbox = rotatedHitbox;
					mainBlock.positionX -= 2;
				}
			}

		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_DOWN) {
			blockAutoDrop = true;
			downPressed = false;
		}
		if (e.getKeyCode() == KeyEvent.VK_RIGHT) { 
			moveRight = false;
		}
		
		if (e.getKeyCode() == KeyEvent.VK_LEFT) { 
			moveLeft = false;
		}

	}
	
	// paint everything in this method //
	public void paint(Graphics g) {
			
		// main panel //
		g.setColor(Color.black);
		g.fillRect(0, 0, mainPanelWidth, panelHeight);
		
		// side panel //
		g.setColor(Color.black);
		g.fillRect(mainPanelWidth + panelGap, 0, sidePanelWidth, panelHeight);
		
		// if game over is true, paint only the essential objects and quit //
		if (gameOver == true) { 
			grid.draw(g);
			score.draw(g);
			drawGameOver(g);
			g.dispose();
			return;
		}
		
		// if the block is moving automatically, move it based on the speed //
		timeNow = System.nanoTime();
		
		double timeDiff = Math.abs(timeNow - time0);
		if (timeDiff >= droppingNs) {
			
			time0 = timeNow;
			
			if (blockAutoDrop) {
				
				if (checkMove(mainBlock.hitbox, mainBlock.positionX, mainBlock.positionY + 1)) {
					mainBlock.moveDown();
				}
				
				else {	
					checkBlockBottom();
				}
			}
		}
		
		// draw blocks (main and side panel) //
		mainBlock.draw(g);
		nextBlock.drawSidePanel(g);
		
		grid.draw(g); // draw grid //
		score.draw(g); // draw score //
		
		// below is a way to move blocks horizontally without excessive speed //
		// basically the movement occurs every delayH frames //
		
		if (timerH > 0) {
			timerH--;
		}
		else {
			if (moveRight) {
//				if ((block.positionX + block.blockWidth < hSquares) || (block.getRightmostPosition() != hSquares - 1)) {
					if (checkMove(mainBlock.hitbox, mainBlock.positionX + 1, mainBlock.positionY)) {
						mainBlock.moveRight();
					}
//				}
				timerH = delayH;
			}
			else if (moveLeft) {
				
//				if ((block.positionX > 0) || (block.getLeftmostPosition() != 0)) {
					if (checkMove(mainBlock.hitbox, mainBlock.positionX - 1, mainBlock.positionY)) {
						mainBlock.moveLeft();
					}
//				}
				timerH = delayH;
			}
		}
		
		g.dispose();
	}
	
	// draw a big game over text in the middle //
	void drawGameOver(Graphics g) {
		
		// draw a black background //
		g.setColor(Color.black);
		g.fillRect(100, 265, 255, 40);
		
		// draw the text //
		g.setColor(Color.white);
		g.setFont(new Font("serif", Font.BOLD, 40));
		g.drawString("GAME OVER", 100, 300);
	}

}
