package nz.ac.vuw.ecs.swen225.a3.maze;

import nz.ac.vuw.ecs.swen225.a3.persistence.Saveable;

/**
 * The Entity interface, used for maze object placement. This includes the
 * Player, any Treasures, Keys, or any other objects we wish to create.
 * 
 * @author Ethan Munn - 300367257
 *
 */
public interface Entity extends Saveable {
	
	/**
	 * Implements a toString for testing purposes
	 * 
	 * @return
	 * 		String form of the entity
	 */
	public String toString();
	
}