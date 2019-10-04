package nz.ac.vuw.ecs.swen225.a3.maze;

import nz.ac.vuw.ecs.swen225.a3.persistence.Saveable;

import javax.json.JsonObject;

public class Crate extends Moveable implements Saveable {

    /**
     * @param coordinate
     */
    public Crate(Coordinate coordinate) {
        super(coordinate);
    }

    @Override
    public boolean canWalkOn(Entity entity) {
        return false;
    }

    @Override
    public JsonObject toJSON() {
        // TODO write this JSON code
        return null;
    }
}