package nz.ac.vuw.ecs.swen225.a3.maze;

/**
 * The door class, prevents the players from passing through the door
 * if the player does not have the right key equipped.
 * 
 * @author Ethan Munn
 *
 */
public abstract class Door implements Entity {

	private boolean locked;
	
	/**
	 * Creates a new door object. By default, every new door should be locked.
	 *
	 */
	public Door() {
		this.locked = true;
	}
	
	/**
	 * Used to check whether or not the door is locked.
	 * @return
	 * 		if the door is locked or unlocked
	 */
	public boolean isLocked() {
		return locked;
	}

	/**
	 * A protected method for unlocking the door. We don't want this
	 * to be public, as we don't want the state of the door being
	 * controlled by something outside of the door.
	 */
	public void unlock() {
		locked = false;
	}
	
	/**
	 * Used for the different forms of unlocking
	 * @param player
	 */
	public abstract boolean onTouch(Player player);
	
}
