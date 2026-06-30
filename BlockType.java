package tetris;

import java.awt.Color;

// enumeration for the seven different types of block //
// each block consists of a square matrix which is the hitbox //
// and also the size of this array, and the color of the block //
public enum BlockType {
	
	IBLOCK(
			new boolean[][] {{false, true, false, false},
							{false, true, false, false},
							{false, true, false, false},
							{false, true, false, false}},
					4,
					Color.yellow),
	
	JBLOCK(
			new boolean[][] {{false, true, true},
							{false, true, false},
							{false, true, false}},
					3,
					Color.blue),
	
	LBLOCK(
			new boolean[][] {{true, true, false},
							{false, true, false},
							{false, true, false}},
					3,
					Color.pink),
	
	SBLOCK(
			new boolean[][] {{true, false, false},
							{true, true, false},
							{false, true, false}},
					3,
					Color.orange),
	
	ZBLOCK(
			new boolean[][] {{false, true, false},
							{true, true, false},
							{true, false, false}},
					3,
					Color.MAGENTA),
	
	OBLOCK(
			new boolean[][] {{true, true},
							{true, true}},
					2,
					Color.cyan),
	
	TBLOCK(
			new boolean[][] {{false, true, false},
							{true, true, false},
							{false, true, false}},
					3,
					Color.red);
	
	private final boolean[][] hitbox;
	private final Color color;
	private final int width;
	
	// constructor for block type //
	BlockType(boolean[][] hitbox, int width, Color color) {
		this.hitbox = hitbox;
		this.width = width;
		this.color = color;
	}
	
	// get the hitbox of the block //
	public boolean[][] getHitbox() {
		return this.hitbox;
	};
	
	// get the width of the block hitbox //
	public int getWidth() {
		return this.width;
	}
	
	// get the color of the block //
	public Color getColor() {
		return this.color;
	}
	
}
