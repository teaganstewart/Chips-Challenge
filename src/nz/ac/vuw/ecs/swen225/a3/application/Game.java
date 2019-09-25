package nz.ac.vuw.ecs.swen225.a3.application;

import nz.ac.vuw.ecs.swen225.a3.maze.*;
import nz.ac.vuw.ecs.swen225.a3.persistence.LoadUtils;

public class Game {
    int level;
    int time;
    int treasures;

    private Player player;
    private Maze maze;
    boolean endGame;

    public Game(){
        loadGame();
    }

    public Maze getMaze(){
        return maze;
    }
    
    /** Just for integration day **/
    public void setTiles(Tile[][] t){
       this.maze.setTiles(t);
    }

    public Player getPlayer(){
        return player;
    }
 
    public void setPlayer(Player p) {
    	player = p;
    }
    
    public void setMaze(Maze m) {
    	maze = m;
    }


    public void update(){

    }

    public void loadGame(){
        Level level;
        try{
            level = LoadUtils.resumeGame();
        }
        catch (NullPointerException e){
            level = LoadUtils.loadLevel(1);
        }

        maze = level.getMaze();
        player = maze.getPlayer();


    }

    public void saveGame(){

    }

    public void setTime(int t){
        time = t;
    }
    public void setLevel(int l){
        level = l;
    }
    public void setTreasures(int t){
        treasures = t;
    }

    public int getTime(){
        return time;
    }
    public int getLevel(){
        return level;
    }
    public int getTreasures(){
        return treasures;
    }
}
