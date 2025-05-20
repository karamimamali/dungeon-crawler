package locations;

import java.awt.Point;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Random;
import java.util.Scanner;

import tile.Empty;
import tile.Gold;
import tile.GoldDoor;
import tile.Stairs;
import tile.Start;
import tile.Tile;
import tile.Wall;
import tile.character.Enemy;

/**
 * Creates from a file and stores a HashMap of the floor, as Points and Tiles.
 *
 * @version 2.1
 * @author tp275
 */
public class Floor {

    // the Point the player starts at on the floor
    private Point startPos;
    // sets difficulty of the floor: affects the layout loaded and enemies
    private final int difficulty;
    // contains all x,y Points of the floor with their corresponding tiles
    private final HashMap<Point, Tile> floorPlan = new HashMap<>();
    // a String representation of the current floorPlan
    private String[] floorPlanStringList;
    // ID of the floor, helpful as higher floors have lower IDs
    private final int id;
    // a handy Random for use within the class
    private final Random random = new Random();

    /**
     * Stores parameters and creates the floor plan from resource file
     *
     * @param difficulty - The floor's difficulty level
     * @param id - The floor's unique ID
     */
    public Floor(int difficulty, int id) {
        this.difficulty = difficulty;
        this.id = id;
        this.createFloorPlan();
    }

    /**
     * Populates floorPlan with all x,y Points of the floor with their corresponding Tiles
     */
    private void createFloorPlan() {
        try {
            Scanner reader = findFloorplanFile();
            int rows = reader.nextInt();
            int cols = reader.nextInt();

            // read the rest of the file as one token
            reader.useDelimiter("\\Z");
            this.floorPlanStringList = reader.next().trim().split("\\n");

            // loop through every tile of the floor
            for (int y = 0; y < cols; y++) {
                for (int x = 0; x < rows; x++) {
                    char tileChar = this.floorPlanStringList[x].charAt(y);
                    if (tileChar == 's') { // note startPos if start tile
                        this.startPos = new Point(x, y);
                    }
                    Point point = new Point(x, y);
                    floorPlan.put(point, convertCharToTile(tileChar, point));
                }
            }
            reader.close();

        } catch (Exception e) {
            System.out.println("Error: could not read floor plan file");
            e.printStackTrace();
        }
    }

    /**
     * Returns a Scanner reading from the floor plan resource file
     * chosen randomly from a certain amount of files at the specified difficulty level
     *
     * @return Scanner reading the floor plan resource
     * @throws Exception if the resource is not found
     */
    private Scanner findFloorplanFile() throws Exception {
        int numberOfFloorplans = 3; // Default number of floorplan options per difficulty
        // Special case for final dungeon (difficulty 4)
        if (this.difficulty == 4) {
            numberOfFloorplans = 3; // We've created 3 layouts for the final dungeon
        }

        String filename = "/floorplan" + this.difficulty + "-" + (random.nextInt(numberOfFloorplans) + 1) + ".txt";
        InputStream inputStream = getClass().getResourceAsStream(filename);

        if (inputStream == null) {
            throw new Exception("Resource file not found: " + filename);
        }

        return new Scanner(inputStream);
    }

    /**
     * Returns a fully initialised tile that corresponds with the tileString parameter
     *
     * @param tileString - A char from the text representation of the floor
     * @param point - The Point on the floor that the tileString char is at
     * @return A fully initialised tile that corresponds with the tileString parameter
     */
    private Tile convertCharToTile(char tileString, Point point) {
        switch (tileString) {
            case '-':
                return new Wall();
            case 's':
                this.startPos = point;
                return new Start();
            case 'x':
                return new Stairs();
            case 'o':
                return new Empty();
            case 'e':
                return new Enemy(this.difficulty + 1);
            case 'g':
                return new Gold(this.difficulty + 1);
            case 'd':
                // Gold door with cost based on difficulty
                return new GoldDoor((this.difficulty + 1) * 2);
        }
        return null;
    }

    /**
     * Returns the floor's ID number
     *
     * @return The floor's ID number
     */
    public int getID() {
        return this.id;
    }

    /**
     * Returns the floor's difficulty level
     *
     * @return The floor's difficulty level
     */
    public int getDifficulty() {
        return this.difficulty;
    }

    /**
     * Checks that the tile at the given point is not a wall or outside the bounds of the floor.
     * If it's a closed gold door and the player has enough gold, it will automatically open the door.
     *
     * @param point - The location to check
     * @param player - The Player object (needed to check gold for doors)
     * @return True if floor location contains a usable tile for the player, false otherwise
     */
    public boolean checkValidPlayerLocation(Point point, tile.character.Player player) {
        if (!this.floorPlan.containsKey(point)) {
            return false;
        }

        Tile tile = this.floorPlan.get(point);

        // Cannot walk on walls
        if (tile.getName().equals("Wall")) {
            return false;
        }

        // Check if it's a gold door
        if (tile instanceof GoldDoor) {
            GoldDoor door = (GoldDoor) tile;

            // If door is already open, allow passage
            if (door.isOpen()) {
                return true;
            }

            // If player has enough gold, automatically open the door and allow passage
            int playerGold = player.getGold();
            int doorCost = door.getCost();

            if (playerGold >= doorCost) {
                // Player has enough gold, open the door
                player.getStats().spendGold(doorCost);
                door.open();
                // Return true to allow passage
                return true;
            } else {
                // Not enough gold, cannot pass
                return false;
            }
        }

        return true;
    }

    /**
     * Overloaded method for backward compatibility
     *
     * @param point - The location to check
     * @return True if floor location contains a usable tile for the player, false otherwise
     * @deprecated Use checkValidPlayerLocation(Point, Player) instead
     */
    public boolean checkValidPlayerLocation(Point point) {
        if (!this.floorPlan.containsKey(point)) {
            return false;
        }

        Tile tile = this.floorPlan.get(point);

        // Cannot walk on walls
        if (tile.getName().equals("Wall")) {
            return false;
        }

        // Cannot walk through closed gold doors
        if (tile instanceof GoldDoor && !((GoldDoor) tile).isOpen()) {
            return false;
        }

        return true;
    }

    /**
     * Returns a string of the character representation of the floor plan, with the player's location shown on it
     *
     * @param playerLocation - The Point of the player's current location on the floor. A P will be printed on the map at this location.
     * @return A string of the character representation of the floor plan, with the player's location shown on it
     */
    public String getFloorMap(Point playerLocation) {
        StringBuilder fpString = new StringBuilder();
        for (int i = 0; i < this.floorPlanStringList.length; i++) {
            StringBuilder floorRow = new StringBuilder(this.floorPlanStringList[i]);
            if (i == playerLocation.x) {
                floorRow.setCharAt(playerLocation.y, 'P');
            }
            // Update map display to show open doors
            for (int y = 0; y < floorRow.length(); y++) {
                Point p = new Point(i, y);
                if (floorPlan.containsKey(p) && floorPlan.get(p) instanceof GoldDoor) {
                    GoldDoor door = (GoldDoor) floorPlan.get(p);
                    if (door.isOpen()) {
                        floorRow.setCharAt(y, 'D'); // 'D' for open door
                    }
                }
            }
            fpString.append(floorRow).append('\n');
        }
        return fpString.toString();
    }

    /**
     * Returns the Tile in floorPlan corresponding to the given point.
     * If no match, returns null
     *
     * @param point - The Point to match
     * @return The corresponding Tile, or null if no match
     */
    public Tile getTileByPoint(Point point) {
        return this.floorPlan.get(point);
    }

    /**
     * Returns the Point corresponding to the location of the start tile on this floor
     *
     * @return The Point corresponding to the location of the start tile on this floor
     */
    public Point getStartPos() {
        return this.startPos;
    }
}