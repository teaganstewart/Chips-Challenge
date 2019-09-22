package nz.ac.vuw.ecs.swen225.a3.persistence;

import nz.ac.vuw.ecs.swen225.a3.maze.*;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import java.io.*;


/**
 * The LoadUtils class contains methods that are used for loading games and levels
 *
 * @author Matt Rothwell
 */
public class LoadUtils {

    public static String LEVELS_DIRECTORY = "levels";

    /**
     * Resumes the game from the last save made.
     */
    public static Maze resumeGame(){
        File recentSave = getMostRecentSave();
        if (recentSave != null){
            try {
                InputStream inputStream = new FileInputStream(recentSave);
                JsonReader reader = Json.createReader(inputStream);

                JsonObject save = reader.readObject();

                return loadMaze(save);


            } catch (FileNotFoundException e) {
                return null;
            }
        }
        return null;
    }

    /**
     * Loads a new game from the starting level
     */
    public static void restartGame(){}
        //TODO implementation
    /**
     * Loads the specified level in JSON format from file
     */
    private static void loadLevel(int i){
        //TODO implementation
    }

    /**
     * Searches through the directory of saves, finding the most recent saved game.
     * @return the most recent saved game's json file.
     */
    private static File getMostRecentSave(){
        File newest = null;
        File directory = new File(SaveUtils.SAVES_DIRECTORY);
        FileFilter filter = pathname -> pathname.isFile() && pathname.toString().endsWith(".json");
        File[] files = directory.listFiles(filter);

        if (files != null) {
            for (File f : files){
                if (newest == null || f.lastModified() > newest.lastModified()){
                    newest = f;
                }
            }
        }

        return newest;
    }

    /**
     * Create a player object from JSON object notation, filling in their inventory and coordinates
     * @param player Player representation in JSON object notation
     * @return a player object identical tho that represented in JSON
     */
    private static Player loadPlayer(JsonObject player){
        Player newPlayer = new Player(loadCoordinate(player.getJsonObject("Coordinate")));
        JsonArray inventory = player.getJsonArray("Inventory");
        for (int i = 0; i < inventory.size(); i++){
            JsonObject inventoryItem = inventory.getJsonObject(i);
            newPlayer.addToInventory(loadEntity(inventoryItem));
        }
        return newPlayer;
    }

    /**
     * Create a new Tile object from JSON object notation
     * @param tile the JSON object that contains all the information about this tile
     * @return the Java tile Object
     */
    private static Tile loadTile(JsonObject tile){
        Tile newTile;
        Tile.TileType tileType = Tile.TileType.valueOf(tile.getString("TileType"));
        Coordinate tileCoordinate = loadCoordinate(tile.getJsonObject("Coordinate"));

        if (tileType == Tile.TileType.HINT){
            newTile = new HintTile(tileCoordinate, tile.getString("Message"));
        }
        else{
            newTile = new Tile(tileCoordinate, tileType);
        }

        if (tile.getJsonObject("Entity") != null){
            newTile.setEntity(loadEntity(tile.getJsonObject("Entity")));
        }
        return newTile;
    }

    /**
     * Load a Coordinate object in from JSON format
     * @param coordinate as a json object
     * @return coordinate in Java Object form
     */
    private static Coordinate loadCoordinate(JsonObject coordinate){
        return new Coordinate(coordinate.getInt("row"), coordinate.getInt("col"));
    }

    /**
     * Load an Entity from JSON
     * @param entity the Json object representing an entity
     * @return Java object identical to that represented in file form
     */
    private static Entity loadEntity(JsonObject entity){
        String entityClass = entity.getString("EntityClass");
        if (entityClass.equals("Key")){
            BasicColor basicColor = BasicColor.valueOf(entity.getString("BasicColor"));
            return new Key(basicColor);
        }
        else if (entityClass.equals("KeyDoor")){
            BasicColor basicColor = BasicColor.valueOf(entity.getString("BasicColor"));
            KeyDoor newKeyDoor = new KeyDoor(basicColor);
            boolean locked = entity.getBoolean("locked");
            if (!locked){
                newKeyDoor.unlock();
            }
            return newKeyDoor;
        }
        else if (entityClass.equals("Treasure")){
            return new Treasure();
        }
        else if (entityClass.equals("TreasureDoor")){
            TreasureDoor treasureDoor = new TreasureDoor();
            boolean locked = entity.getBoolean("locked");
            if (!locked){
                treasureDoor.unlock();
            }
            return treasureDoor;
        }
        return null;
    }

    /**
     * Load a maze object from a JsonObject
     * @param maze the maze object in JSON object form
     * @return a Maze object with the properties from the file.
     */
    private static Maze loadMaze(JsonObject maze){
        Player player = loadPlayer(maze.getJsonObject("player"));
        int rows = maze.getInt("rows");
        int cols = maze.getInt("cols");
        Tile[][] tiles = new Tile[rows][cols];

        JsonArray jsonArray = maze.getJsonArray("tiles");

        int index = 0;
        for (int row = 0; row < tiles[0].length; row++){
            for (int col = 0; col < tiles.length; col++){
                tiles[row][col] = loadTile(jsonArray.getJsonObject(index++));
            }
        }

        JsonObject treasureData = maze.getJsonObject("treasureData");
        int totalInLevel = treasureData.getInt("totalInLevel");
        int totalCollected = treasureData.getInt("totalCollected");

        Treasure.setTreasureCountersUponLoad(totalInLevel, totalCollected);

        return new Maze(tiles, player);
    }

}
