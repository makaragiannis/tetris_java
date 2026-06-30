package tetris;

import java.awt.Color;
import java.awt.Graphics;

// class for a Grid Square //
public class GridSquare {
	
	Color color;
	boolean isBlocked; // whether this square contains a value (true) or is empty (false)
	final int squareWidth;
	
	// constructor for GridSquare where color is specified //
	// I do not think this constructor is used or needed   //
	public GridSquare(int positionX, int positionY, Color color, boolean isBlocked, int squareWidth) {

		this.color = color;
		this.isBlocked = isBlocked;
		this.squareWidth = squareWidth;
	}
	// constructor for gridSquare where color is not specified //
	public GridSquare(int positionX, int positionY, boolean isBlocked, int squareWidth) {
		this.isBlocked = isBlocked;
		this.squareWidth = squareWidth;
	}
	
	// fill the square (add color and mark it as Blocked) //
	public void addSquare(int positionX, int positionY, Color color) {

		this.color = color;
		this.isBlocked = true;
	}
	
	// draw the grid square //
	public void draw(Graphics g, int positionX, int positionY) {
		
		// get the real position by using the fixed squareWidth value //
		int squareX = (positionX)*squareWidth;
		int squareY = (positionY)*squareWidth;
		
		// draw the base square //
		g.setColor(color);
		g.fillRect(squareX, squareY, squareWidth, squareWidth);
		
		// also draw a border around //
		g.setColor(color.darker());
		g.drawRect(squareX, squareY, squareWidth, squareWidth);
	}
}
