package nz.ac.vuw.ecs.swen225.a3.maze.Tests;

import nz.ac.vuw.ecs.swen225.a3.maze.*;
import org.junit.jupiter.api.*;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Josh
 *         <p>
 *         Class which contains most tests that test classes in the maze
 *         directory
 */
public class MazeTest {

	private Tile[][] tiles;
	private Player player;
	private Maze maze;

	/*
	* Create a new maze before each test
	* */
	@BeforeEach
	public void setUp() {
		tiles = new Tile[9][9];
		for (int row = 0; row < tiles.length; row++) {
			for (int col = 0; col < tiles[0].length; col++) {
				tiles[row][col] = new Tile(new Coordinate(row, col), Tile.TileType.FLOOR);
			}
		}
		player = new Player(new Coordinate(3, 3));
		maze = new Maze(tiles, player);
	}

	/*
	* Reset the Treasure class after each test as it contains
	* static methods that will change it's state
	* */
	@AfterEach
	public void tearDown() {
		Treasure.reset();
	}

	/**
	 * Testing basic player moves
	 */
	@Test
	public void testMovePlayerValid() {

		// Player starts facing down
		assertEquals(new Coordinate(4, 3), player.getNextPos());
		assertEquals(new Coordinate(2, 3), player.getPrevPos());

		assertTrue(maze.movePlayer(Direction.DOWN));

		assertEquals(Direction.DOWN, player.getLastDirection());
		assertEquals(Direction.DOWN, player.getDirection());
		assertEquals(new Coordinate(4, 3), player.getCoordinate());
		assertEquals(new Coordinate(3, 3), player.getPrevPos());

		assertTrue(maze.movePlayer(Direction.RIGHT));
		assertEquals(new Coordinate(4, 4), player.getCoordinate());
		assertEquals(new Coordinate(4, 3), player.getPrevPos());

		assertTrue(maze.movePlayer(Direction.UP));
		assertEquals(new Coordinate(3, 4), player.getCoordinate());
		assertEquals(new Coordinate(4, 4), player.getPrevPos());

		assertTrue(maze.movePlayer(Direction.LEFT));
		assertEquals(new Coordinate(3, 3), player.getCoordinate());
		assertEquals(new Coordinate(3, 4), player.getPrevPos());
	}

	/**
	 * Testing moving the player off the tiles
	 */
	@Test
	public void testMovePlayerNotValid() {
		assertTrue(maze.movePlayer(Direction.LEFT));
		assertTrue(maze.movePlayer(Direction.LEFT));
		assertTrue(maze.movePlayer(Direction.LEFT));
		assertFalse(maze.movePlayer(Direction.LEFT));
	}

	/**
	 * Test to see if player correctly unlocks a key door
	 */
	@Test
	public void testUnlockKeyDoor() {

		Key key = new Key(BasicColor.YELLOW);
		tiles[3][4].setEntity(key);

		// Collect key
		assertTrue(maze.movePlayer(Direction.RIGHT));

		// Unlock door with same color as key
		tiles[3][5].setEntity(new KeyDoor(BasicColor.YELLOW));
		assertTrue(maze.movePlayer(Direction.RIGHT));

		// Make sure Chap keeps the key after unlocking the door
		assertTrue(player.getInventory().contains(key));
	}

	/**
	 * Test to see if player fails to unlock a key door when player does not have
	 * any keys
	 */
	@Test
	public void testUnlockDoorNotValid01() {

		tiles[3][4].setEntity(new KeyDoor(BasicColor.RED));
		assertFalse(maze.movePlayer(Direction.RIGHT));
	}

	/**
	 * Test to see if player fails to enter key door when they have the wrong
	 * colored key
	 */
	@Test
	public void testUnlockDoorNotValid02() {

		Key key = new Key(BasicColor.RED);
		tiles[3][4].setEntity(key);

		// Collect key
		assertTrue(maze.movePlayer(Direction.RIGHT));

		// Unlock door with WRONG colored key
		tiles[3][5].setEntity(new KeyDoor(BasicColor.YELLOW));
		assertFalse(maze.movePlayer(Direction.RIGHT));

		// Make sure Chap still contains key
		assertTrue(player.getInventory().contains(key));
	}

	/**
	 * Test to see if player unlocks the treasure door after collecting all the
	 * treasure
	 */
	@Test
	public void testUnlockTreasureDoor() {

		TreasureDoor treasureDoor = new TreasureDoor();
		tiles[3][4].setEntity(new Treasure());
		tiles[3][5].setEntity(treasureDoor);

		// Collect treasure
		assertTrue(maze.movePlayer(Direction.RIGHT));

		assertTrue(Treasure.allCollected());
		assertTrue(maze.movePlayer(Direction.RIGHT));
	}

	/**
	 * Test to see if player does NOT unlock the treasure door
	 */
	@Test
	public void testNotUnlockTreasureDoor() {

		Treasure treasure = new Treasure();
		tiles[7][3].setEntity(treasure);

		tiles[3][5].setEntity(new TreasureDoor());
		assertTrue(maze.movePlayer(Direction.RIGHT));
		// Player cannot enter door
		assertFalse(maze.movePlayer(Direction.RIGHT));
	}

	/**
	 * Test to see if player fails to walk on a Fire Tile
	 */
	@Test
	public void testWalkFireBlockNotValid() {
		tiles[3][4] = new Tile(new Coordinate(3, 4), Tile.TileType.FIRE);

		// If Player dies and level is restarted. This does not count as a move
		assertFalse(maze.movePlayer(Direction.RIGHT));
		assertTrue(maze.isResetLevel());
		// At this point, player has died and level will be restarted. It is too
		// difficult to test here
	}

	/**
	 * Test to see if player collects firBoots and walks on fire block
	 */
	@Test
	public void testWalkFireBlockValid() {
		tiles[3][4].setEntity(new FireBoots());
		tiles[3][5] = new Tile(new Coordinate(3, 5), Tile.TileType.FIRE);
		assertTrue(maze.movePlayer(Direction.RIGHT));
		assertTrue(player.isInInventory(new FireBoots()));
		assertTrue(maze.movePlayer(Direction.RIGHT));
	}

	/**
	 * Test to see if player walks on an ice block with ice boots
	 */
	@Test
	public void testIceBlockWalking() {
		tiles[3][4].setEntity(new IceBoots());
		tiles[3][5] = new Tile(new Coordinate(3, 5), Tile.TileType.ICE);
		tiles[3][6] = new Tile(new Coordinate(3, 6), Tile.TileType.ICE);
		assertTrue(maze.movePlayer(Direction.RIGHT));
		assertTrue(player.isInInventory(new IceBoots()));
		assertTrue(maze.movePlayer(Direction.RIGHT));
		assertTrue(maze.movePlayer(Direction.RIGHT));
		assertTrue(maze.movePlayer(Direction.RIGHT));
		assertEquals(new Coordinate(3, 7), player.getCoordinate());
	}

	/**
	 * Test to see if player does not have ice boots and slides on ice onto a floor block
	 */
	@Test
	public void testIceBlockSlidingOntoFloor() {
		tiles[3][4] = new Tile(new Coordinate(3, 4), Tile.TileType.ICE);
		tiles[3][5] = new Tile(new Coordinate(3, 5), Tile.TileType.ICE);
		tiles[3][6] = new Tile(new Coordinate(3, 6), Tile.TileType.ICE);
		assertTrue(maze.movePlayer(Direction.RIGHT));
		// Player slides to the next non-ice block
		assertEquals(new Coordinate(3, 7), player.getCoordinate());
	}

	/**
	 * Test to see if player does not have ice boots and slides on ice and next to a wall
	 */
	@Test
	public void testIceBlockSlidingOntoWall() {
		tiles[3][4] = new Tile(new Coordinate(3, 4), Tile.TileType.ICE);
		tiles[3][5] = new Tile(new Coordinate(3, 5), Tile.TileType.ICE);
		tiles[3][6] = new Tile(new Coordinate(3, 6), Tile.TileType.ICE);
		tiles[3][7] = new Tile(new Coordinate(3, 7), Tile.TileType.WALL);
		assertTrue(maze.movePlayer(Direction.RIGHT));
		// Player slides to the next non-ice block
		assertEquals(new Coordinate(3, 6), player.getCoordinate());
	}

	/**
	 * Test to see if player does not have ice boots and slides on ice into a fire block
	 */
	@Test
	public void testIceBlockSlidingOntoFire() {
		tiles[3][4] = new Tile(new Coordinate(3, 4), Tile.TileType.ICE);
		tiles[3][5] = new Tile(new Coordinate(3, 5), Tile.TileType.ICE);
		tiles[3][6] = new Tile(new Coordinate(3, 6), Tile.TileType.ICE);
		tiles[3][7] = new Tile(new Coordinate(3, 7), Tile.TileType.FIRE);
		assertTrue(maze.movePlayer(Direction.RIGHT));
		// Player should now be standing on last ice block
		assertEquals(new Coordinate(3, 6), player.getCoordinate());
		assertFalse(maze.movePlayer(Direction.RIGHT));
		// Player should be dead from the fire
		assertTrue(maze.isResetLevel());
	}

	/**
	 * Test to see if player does not have ice boots and slides on ice onto a hint tile
	 */
	@Test
	public void testIceBlockSlidingOntoHint() {
		tiles[3][4] = new Tile(new Coordinate(3, 4), Tile.TileType.ICE);
		tiles[3][5] = new Tile(new Coordinate(3, 5), Tile.TileType.ICE);
		tiles[3][6] = new Tile(new Coordinate(3, 6), Tile.TileType.ICE);
		tiles[3][7] = new Tile(new Coordinate(3, 7), Tile.TileType.HINT);
		assertTrue(maze.movePlayer(Direction.RIGHT));
		// Player should now be standing on hint tile
		HintTile hintTile = (HintTile) tiles[3][7];
		assertEquals(hintTile.getMessage(), maze.getHintMessage());
		assertEquals(new Coordinate(3, 7), player.getCoordinate());
	}

	/**
	 * Test to see if player does not have ice boots and slides on ice into the goal
	 */
	@Test
	public void testIceBlockSlidingOntoGoal() {
		tiles[3][4] = new Tile(new Coordinate(3, 4), Tile.TileType.ICE);
		tiles[3][5] = new Tile(new Coordinate(3, 5), Tile.TileType.ICE);
		tiles[3][6] = new Tile(new Coordinate(3, 6), Tile.TileType.ICE);
		tiles[3][7] = new Tile(new Coordinate(3, 7), Tile.TileType.GOAL);
		assertTrue(maze.movePlayer(Direction.RIGHT));
		assertEquals(new Coordinate(3, 7), player.getCoordinate());
		assertTrue(maze.isGoalReached());
	}

	/**
	 * Test to see if player does not have ice boots and slides into a floor tile with a key
	 */
	@Test
	public void testIceBlockSlidingOntoKey() {
		tiles[3][4] = new Tile(new Coordinate(3, 4), Tile.TileType.ICE);
		tiles[3][5] = new Tile(new Coordinate(3, 5), Tile.TileType.ICE);
		tiles[3][6] = new Tile(new Coordinate(3, 6), Tile.TileType.ICE);
		tiles[3][7] = new Tile(new Coordinate(3, 7), Tile.TileType.FLOOR);
		Key key = new Key(BasicColor.YELLOW);
		tiles[3][7].setEntity(key);
		assertTrue(maze.movePlayer(Direction.RIGHT));
		assertEquals(new Coordinate(3, 7), player.getCoordinate());
		assertTrue(player.isInInventory(key));
		assertTrue(player.getInventory().contains(key));
	}

	/**
	 * Test to see if player does not have ice boots and slides into a floor tile with treasure
	 */
	@Test
	public void testIceBlockSlidingOntoTreasure() {
		tiles[3][4] = new Tile(new Coordinate(3, 4), Tile.TileType.ICE);
		tiles[3][5] = new Tile(new Coordinate(3, 5), Tile.TileType.ICE);
		tiles[3][6] = new Tile(new Coordinate(3, 6), Tile.TileType.ICE);
		tiles[3][7] = new Tile(new Coordinate(3, 7), Tile.TileType.FLOOR);
		Treasure treasure = new Treasure();
		tiles[3][7].setEntity(treasure);
		assertTrue(maze.movePlayer(Direction.RIGHT));
		assertEquals(new Coordinate(3, 7), player.getCoordinate());
		assertTrue(Treasure.allCollected());
	}

	/**
	 * Test to see if player does not have ice boots and slides into a floor tile with treasure
	 */
	@Test
	public void testIceBlockSlidingIntoLockedTreasureDoor() {
		tiles[3][4] = new Tile(new Coordinate(3, 4), Tile.TileType.ICE);
		tiles[3][5] = new Tile(new Coordinate(3, 5), Tile.TileType.ICE);
		tiles[3][6] = new Tile(new Coordinate(3, 6), Tile.TileType.ICE);
		tiles[3][7] = new Tile(new Coordinate(3, 7), Tile.TileType.FLOOR);
		TreasureDoor treasureDoor = new TreasureDoor();
		tiles[3][7].setEntity(treasureDoor);
		Treasure treasure = new Treasure();
		tiles[3][8].setEntity(treasure);
		assertTrue(maze.movePlayer(Direction.RIGHT));
		assertEquals(new Coordinate(3, 6), player.getCoordinate());
		assertFalse(maze.movePlayer(Direction.RIGHT));
	}

	/**
	 * Test to see if player does not have ice boots and slides into a floor tile with treasure
	 */
	@Test
	public void testIceBlockSlidingIntoLockedKeyDoor() {
		tiles[3][4] = new Tile(new Coordinate(3, 4), Tile.TileType.ICE);
		tiles[3][5] = new Tile(new Coordinate(3, 5), Tile.TileType.ICE);
		tiles[3][6] = new Tile(new Coordinate(3, 6), Tile.TileType.ICE);
		tiles[3][7] = new Tile(new Coordinate(3, 7), Tile.TileType.FLOOR);
		KeyDoor keyDoor = new KeyDoor(BasicColor.YELLOW);
		tiles[3][7].setEntity(keyDoor);
		assertTrue(maze.movePlayer(Direction.RIGHT));
		assertEquals(new Coordinate(3, 6), player.getCoordinate());
		assertFalse(maze.movePlayer(Direction.RIGHT));
	}

	// -----------------------------//
	// -------GENERAL TESTS --------//
	// -----------------------------//

	@Test
	void extensionsTest() {
		Player p = new Player(new Coordinate(1, 2));
		Player p2 = new Player(new Coordinate(3, 1));
		// Not sure what was trying to be done in this line, but it doesn't compile.
		// Please tell me your intentions
//		assertTrue(p.addToInventory(p2));
	}

}
