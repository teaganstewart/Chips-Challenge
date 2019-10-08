package nz.ac.vuw.ecs.swen225.a3.maze;

import javax.json.JsonObject;

import nz.ac.vuw.ecs.swen225.a3.persistence.Saveable;

public class Skeleton extends Moveable implements Saveable {

	public Skeleton(Coordinate coordinate, Direction start) {
		super(coordinate);
		super.setDirection(start);
	}

	@Override
	public boolean canWalkOn(Entity entity) {
		if (entity == null)
			return true;
		
		if(entity instanceof Key || entity instanceof Treasure || 
				entity instanceof FireBoots || entity instanceof IceBoots) 
			return true;
		
		return false;
	}

	@Override
	public JsonObject toJSON() {
		// TODO Auto-generated method stub
		return null;
	}

}