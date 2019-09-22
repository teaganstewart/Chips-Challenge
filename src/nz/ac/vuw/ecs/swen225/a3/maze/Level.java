package nz.ac.vuw.ecs.swen225.a3.maze;

import nz.ac.vuw.ecs.swen225.a3.persistence.Saveable;

import javax.json.Json;
import javax.json.JsonObject;

/**
 * The level class stores information relating to the current level being played.
 * It stores the time details and
 * the maze, as well as the levelNumber
 *
 * @author Matt Rothwell
 */
public class Level implements Saveable {

    private long levelBeginTime;
    private long levelRunningTime;

    private long levelStartTime;

    private int level;
    private Maze maze;

    /**
     * Constructor for a new Level.
     * @param levelNumber the number of this level from file.
     * @param maze the maze object that stores the game data.
     * @param levelBeginTime the time that the level was first loaded. (Could be used for replaying)
     * @param levelRunningTime the total time this level has been in play.
     */
    public Level(int levelNumber, Maze maze, long levelBeginTime, long levelRunningTime){
        this.levelRunningTime = levelRunningTime;
        this.levelBeginTime = levelBeginTime;
        level = levelNumber;
        this.maze = maze;
    }

    /**
     * Starts the timer by setting the start time to the current time.
     */
    public void startTimer(){
        levelStartTime = System.currentTimeMillis();
    }

    /**
     * Report how long this level has been running in seconds.
     */
    public int reportCurrentTime(){
        return (int) (((System.currentTimeMillis() - levelStartTime) + levelRunningTime)/1000);
    }

    /**
     * Stop the timer, and add the time taken to the running time.
     */
    public void stopTimer(){
        levelRunningTime += (System.currentTimeMillis() - levelStartTime);
    }

    /**
     * Save this level object to a Json object so that it can be saved and reloaded
     * @return Json object representing this Java Object
     */
    @Override
    public JsonObject toJSON() {
        JsonObject level = Json.createObjectBuilder()
                .add("levelNumber", this.level)
                .add("levelBeginTime", levelBeginTime)
                .add("totalRunningTime", levelRunningTime)
                .add("completed", "false")
                .add("maze", maze.toJSON())
                .build();

        return level;
    }
}