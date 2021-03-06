package nz.ac.vuw.ecs.swen225.a3.maze;

import nz.ac.vuw.ecs.swen225.a3.persistence.Saveable;

import javax.json.Json;
import javax.json.JsonObject;

/**
 * Class which represents a crate that can only be pushed by the player 
 * into free floor tiles.
 * 
 * @author Josh O'Hagan - 300442801
 *
 */
public class Crate extends Moveable implements Saveable {

    /**
     * Constructor for the Crate object.
     * @param coordinate The coordinate of the crate at the start.
     */
    public Crate(Coordinate coordinate) {
        super(coordinate);
    }

    @Override
    public boolean canWalkOn(Entity entity) {
        return false;
    }
    
    public String toString() {
    	return "Crate: " + super.getCoordinate().toString() + " moving to " + super.getNextPos();
    }

    @Override
    public JsonObject toJSON() {
        JsonObject value = Json.createObjectBuilder()
                .add("Coordinate", getCoordinate().toJSON()).build();
        return value;
    }
}
