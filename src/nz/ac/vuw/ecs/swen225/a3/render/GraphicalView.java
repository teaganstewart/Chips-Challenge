package nz.ac.vuw.ecs.swen225.a3.render;

import nz.ac.vuw.ecs.swen225.a3.maze.*;
import javax.swing.*;
import nz.ac.vuw.ecs.swen225.a3.application.*;
import nz.ac.vuw.ecs.swen225.a3.application.ui.*;

public class GraphicalView {
	private Game game;
	private Render renderer;
	private Maze maze;

	public GraphicalView(Game g,  Render r, Maze m) {
		game = g;
		renderer = r;
		maze = m;
	}

	/**
	 * 	Used to create the board grid.
	 * 
	 * @param i
	 * 		x-coordinate value of tile we are checking.
	 * @param j
	 * 		y-coordinate value of tile we are checking.
	 * @param m
	 * 		The current maze.
	 * @return
	 * 		Returns the correct image icon for this tile.
	 */
	public ImageIcon getImageIcon(int i, int j, Maze m) {
		Tile[][] tiles = m.getTiles();
		switch(tiles[i][j].getType()) {
		case FLOOR:
			return null;
		case WALL:
			return null;
		case HINT:
			return null;
		case GOAL:
			return null;
		default:
			return null;
		}
	}
	
	/**
	 * Gets the key's icon.
	 * 
	 * @param color
	 * 		The color of the key.
	 * @return
	 * 		The image of the key.
	 */
	public ImageIcon getKeyIcon(BasicColor color) {
		switch(color) {
			case RED:
				return null;
			case GREEN:
				return null;
			case YELLOW:
				return null;
			case BLUE:
				return null;
			default:
				return null;
		}
		
	}
	
	/**
	 * Gets the door's icon.
	 * 
	 * @param color
	 * 		The color of the door.
	 * @return
	 * 		The image of the door..
	 */
	public ImageIcon getDoorIcon(BasicColor color) {
		switch(color) {
			case RED:
				return null;
			case GREEN:
				return null;
			case YELLOW:
				return null;
			case BLUE:
				return null;
			default:
				return null;
		}
		
	}


	/**
	 * A method that both resets all of the players and weapons on a grid, then draws
	 * them in their correct position.
	 * 
	 */
	void drawOnGrid() {

		for (int i = 0; i < maze.getTiles().length; i++) {
			for (int j = 0; j <  maze.getTiles().length; j++) {

				try {
					renderer.getBoard()[i][j].remove(1);
				}
				catch(ArrayIndexOutOfBoundsException e) {

				}

			}
		}
	}
}
