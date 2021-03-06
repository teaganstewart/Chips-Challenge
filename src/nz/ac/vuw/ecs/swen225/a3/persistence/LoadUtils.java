package nz.ac.vuw.ecs.swen225.a3.persistence;

import nz.ac.vuw.ecs.swen225.a3.maze.*;
import nz.ac.vuw.ecs.swen225.a3.recnplay.ActionRecord;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.stream.JsonParsingException;

import java.io.*;
import java.util.*;

/**
 * The LoadUtils class contains methods that are used for loading games and
 * levels.
 *
 * @author Matt Rothwell - 300434822
 */
public class LoadUtils {

	/**
	 * The directory of all the levels.
	 */
	private static String LEVELS_DIRECTORY = "levels";

	/**
	 * Resumes the game from the last save made.
	 *
	 * @return the most recent level played by the player, null if there is not a
	 *         save
	 */
	public static Level resumeGame() {
		File recentSave = getMostRecentSave();
		JsonObject jsonObjectRecentSave = extractLevel(readJsonFromFile(recentSave));
		return loadLevel(jsonObjectRecentSave);
	}

	/**
	 * Loads the specified level in JSON format from default source
	 *
	 * @param levelNumber - the level to load
	 * @return Returns the level associated with the levelNumber.
	 */
	public static Level loadLevel(int levelNumber) {
		File levelFile = new File(LEVELS_DIRECTORY + "\\" + levelNumber + ".json");
		JsonObject jsonObject = extractLevel(readJsonFromFile(levelFile));
		return loadLevel(jsonObject);
	}

	/**
	 * Load a level by it's level ID
	 * @param saveID the ID that is the main component of the file name
	 * @return the constructed level object
	 */
	public static Level loadById(Long saveID){
		File file = new File(SaveUtils.SAVES_DIRECTORY+"\\"+saveID+".json");
		try {
			return loadLevel(extractLevel(readJsonFromFile(file)));
		}
		catch (NullPointerException e){
			return null;
		}
	}

	/**
	 * Creates a HashMap from ID -> Formatted String for GUI.
	 * @return a HashMap containing ID's to a neatly formatted string for GUI display.
	 */
	public static Map<String, Long> getSavesByID(){
		Map<String, Long> namesToId = new HashMap<>();

		File directory = new File(SaveUtils.SAVES_DIRECTORY);
		FileFilter filter = pathname -> pathname.isFile() && pathname.toString().endsWith(".json");
		File[] files = directory.listFiles(filter);

		if (files != null) {
			for (File f : files) {
				try {

					JsonObject save = readJsonFromFile(f);

					JsonObject level;

					//This is for compatibility with older file formats
					try {
						level = extractLevel(save);
					}
					catch (NullPointerException e){
						level = save;
					}

					int levelNumber = level.getInt("levelNumber");

					String id = f.getName().substring(0, f.getName().length()-5);

					StringBuilder sb = new StringBuilder();

					if (!save.getString("LevelName").trim().isEmpty()){
						sb.append(save.getString("LevelName"));
						sb.append(" - ");
					}

					sb.append("Level: ");
					sb.append(levelNumber);
					sb.append(" - ");

					long saveTime = Long.parseLong(id);

					Date date = new Date(saveTime);
					sb.append(date.toString());


					namesToId.put(sb.toString(), saveTime);

				}
				// When a file loads that simply stores the level number, not a name, we ignore it.
				catch (NullPointerException ignored){}
			}
		}

		return Collections.unmodifiableMap(namesToId);
	}

	/**
	 * Check inside the levels folder and count how many levels have been installed.
	 * @return amount of installed levels
	 */
	public static int getAmountOfInstalledLevels(){
		File directory = new File(LEVELS_DIRECTORY);
		FileFilter filter = pathname -> pathname.isFile() && pathname.toString().endsWith(".json");
		File[] files = directory.listFiles(filter);

		if (files != null) {
			return files.length;
		}
		return 0;
	}

	/**
	 * Produces a Level object from the JSON input given
	 *
	 * @param level Json representation of a game
	 * @return the Json deserialised back into Object form
	 */
	private static Level loadLevel(JsonObject level) {
		try {
			int levelNumber = level.getInt("levelNumber");
			long levelStartTime = Long.parseLong(level.getString("levelBeginTime"));
			long levelRunningTime = Long.parseLong(level.getString("totalRunningTime"));

			// Marker for a new level, set the starting time
			if (levelStartTime == -1) {
				levelStartTime = System.currentTimeMillis();
			}

			int timeAllowed = level.getInt("timeAllowed");

			Maze maze = loadMaze(level.getJsonObject("maze"));

			return new Level(levelNumber, maze, levelStartTime, levelRunningTime, timeAllowed);
		}
		//If the game just saved what number level the player was on when they quit.
		catch (NullPointerException e){
			int lvl = level.getInt("LevelNum");
			return LoadUtils.loadLevel(lvl);
		}

	}

	/**
	 * Searches through the directory of saves, finding the most recent saved game.
	 *
	 * @return the most recent saved game's json file.
	 */
	private static File getMostRecentSave() {
		File newest = null;
		File directory = new File(SaveUtils.SAVES_DIRECTORY);
		FileFilter filter = pathname -> pathname.isFile() && pathname.toString().endsWith(".json");
		File[] files = directory.listFiles(filter);

		if (files != null) {
			for (File f : files) {
				if (newest == null || f.lastModified() > newest.lastModified()) {
					newest = f;
				}
			}
		}

		return newest;
	}

	/**
	 * Reads a file and reads it into a new JsonObject
	 *
	 * @param file the file to read json from
	 * @return Json object version of that file, null if not found.
	 */
	public static JsonObject readJsonFromFile(File file) {
		if (file != null) {
			try {
				InputStream inputStream = new FileInputStream(file);
				JsonReader reader = Json.createReader(inputStream);

				JsonObject obj = reader.readObject();

				inputStream.close();
				reader.close();

				return obj;

			}
			catch (IOException | JsonParsingException e) {
                return null;
            }
		}

		return null;
	}

	/**
	 * Create a player object from JSON object notation, filling in their inventory
	 * and coordinates
	 *
	 * @param player Player representation in JSON object notation
	 * @return a player object identical tho that represented in JSON
	 */
	private static Player loadPlayer(JsonObject player) {
		Player newPlayer = new Player(loadCoordinate(player.getJsonObject("Coordinate")));
		JsonArray inventory = player.getJsonArray("Inventory");
		try {
			newPlayer.setDirection(Direction.valueOf(player.getString("direction")));
		}
		catch (NullPointerException e){
			newPlayer.setDirection(Direction.DOWN);
		}

		for (int i = 0; i < inventory.size(); i++) {
			JsonObject inventoryItem = inventory.getJsonObject(i);
			newPlayer.addToInventory(loadEntity(inventoryItem));
		}

		return newPlayer;
	}

	/**
	 * Create a new Tile object from JSON object notation
	 *
	 * @param tile the JSON object that contains all the information about this tile
	 * @return the Java tile Object
	 */
	private static Tile loadTile(JsonObject tile) {
		Tile newTile;
		Tile.TileType tileType = Tile.TileType.valueOf(tile.getString("TileType"));
		Coordinate tileCoordinate = loadCoordinate(tile.getJsonObject("Coordinate"));

		if (tileType == Tile.TileType.HINT) {
			newTile = new HintTile(tileCoordinate, tile.getString("Message"));
		}
		else {
			newTile = new Tile(tileCoordinate, tileType);
		}

		if (tile.getJsonObject("Entity") != null) {
			newTile.setEntity(loadEntity(tile.getJsonObject("Entity")));
		}
		return newTile;
	}

	/**
	 * Load a Coordinate object in from JSON format
	 *
	 * @param coordinate as a json object
	 * @return coordinate in Java Object form
	 */
	private static Coordinate loadCoordinate(JsonObject coordinate) {
		return new Coordinate(coordinate.getInt("row"), coordinate.getInt("col"));
	}

	/**
	 * Load an Entity from JSON
	 *
	 * @param entity the Json object representing an entity
	 * @return Java object identical to that represented in file form
	 */
	private static Entity loadEntity(JsonObject entity) {
		String entityClass = entity.getString("EntityClass");
		if (entityClass.equals("Key")) {
			BasicColor basicColor = BasicColor.valueOf(entity.getString("BasicColor"));
			return new Key(basicColor);
		}
		else if (entityClass.equals("KeyDoor")) {
			BasicColor basicColor = BasicColor.valueOf(entity.getString("BasicColor"));
			KeyDoor newKeyDoor = new KeyDoor(basicColor);
			boolean locked = entity.getBoolean("locked");
			if (!locked) {
				newKeyDoor.unlock();
			}
			return newKeyDoor;
		}
		else if (entityClass.equals("Treasure")) {
			return new Treasure();
		}
		else if (entityClass.equals("TreasureDoor")) {
			TreasureDoor treasureDoor = new TreasureDoor();
			boolean locked = entity.getBoolean("locked");
			if (!locked) {
				treasureDoor.unlock();
			}
			return treasureDoor;
		}
		else if (entityClass.equals("FireBoots")){
			return new FireBoots();
		}
		else if (entityClass.equals("IceBoots")){
			return new IceBoots();
		}
		return null;
	}

	/**
	 * Load a crate Object from a Json object
	 * @param crateJson the raw json of a crate object
	 * @return the object form of crate
	 */
	private static Crate loadCrate(JsonObject crateJson){
		return new Crate(loadCoordinate(crateJson.getJsonObject("Coordinate")));
	}

	/**
	 * Load an actionRecord from Json
	 * @param actionRecord A ActionRecord in Json form
	 * @return actionRecord in Java object form
	 */
	public static ActionRecord loadActionRecord(JsonObject actionRecord){
		int id = actionRecord.getInt("timestamp");
		Maze maze = loadMaze(actionRecord.getJsonObject("maze"));
		return new ActionRecord(id, maze);
	}

	/**
	 * Load a maze object from a JsonObject
	 *
	 * @param maze the maze object in JSON object form
	 * @return a Maze object with the properties from the file.
	 */
	private static Maze loadMaze(JsonObject maze) {
		Player player = loadPlayer(maze.getJsonObject("player"));
		int rows = maze.getInt("rows");
		int cols = maze.getInt("cols");
		Tile[][] tiles = new Tile[rows][cols];

		//Load in the tiles for the map
		JsonArray jsonArray = maze.getJsonArray("tiles");

		int index = 0;
		for (int row = 0; row < rows; row++) {
			for (int col = 0; col < cols; col++) {
				tiles[row][col] = loadTile(jsonArray.getJsonObject(index++));
			}
		}


        //Update the amount of treasure in the map.
		JsonObject treasureData = maze.getJsonObject("treasureData");
		int totalInLevel = treasureData.getInt("totalInLevel");
		int totalCollected = treasureData.getInt("totalCollected");
		Treasure.setTreasureCountersUponLoad(totalInLevel, totalCollected);

		//Load in the Crates
		JsonArray crateArray = maze.getJsonArray("crates");
		ArrayList<Crate> crateArrayList = new ArrayList<>();

		if (crateArray != null) {
			int size = crateArray.size();
			for (int i = 0; i < size; i++){
				crateArrayList.add(loadCrate(crateArray.getJsonObject(i)));
			}
		}


		//Load in the skeletons
		JsonArray skeletonArray = maze.getJsonArray("enemies");
		ArrayList<Skeleton> skeletonArrayList = new ArrayList<>();

		if (skeletonArray != null){
		    int size = skeletonArray.size();
		    for (int i = 0; i < size; i++){
		        skeletonArrayList.add(loadSkeleton(skeletonArray.getJsonObject(i)));
            }
        }

		return new Maze(tiles, player, crateArrayList, skeletonArrayList);
	}

    /**
     * Load a saved skeleton object from json.
     * @param jsonObject the raw json object from file.
     * @return a Java form skeleton.
     */
    private static Skeleton loadSkeleton(JsonObject jsonObject) {
	    Coordinate skeletonCoordinate = loadCoordinate(jsonObject.getJsonObject("coordinate"));
	    Direction dir = Direction.valueOf(jsonObject.getString("direction"));
	    return new Skeleton(skeletonCoordinate, dir);
    }

    /**
	 * Extracts the Level Json Object from a Json Object.
	 * @param objectPlusSaveName the raw object to remove level from
	 * @return just the level in object form
	 */
	private static JsonObject extractLevel(JsonObject objectPlusSaveName){
		return objectPlusSaveName.getJsonObject("Level");
	}

}