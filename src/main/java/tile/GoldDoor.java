package tile;

/**
 * A GoldDoor tile that requires the player to spend gold to open
 * and pass through it.
 *
 * @version 1.0
 * @author tp275
 */
public class GoldDoor extends tile.Tile {

    // the cost to open this door
    private final int cost;
    // whether this door has been opened
    private boolean isOpen = false;

    /**
     * Sets the name, description and cost of the GoldDoor tile
     *
     * @param cost The amount of gold required to open this door
     */
    public GoldDoor(int cost) {
        setName("Gold Door");
        this.cost = cost;
        setDescription("A heavy door with " + cost + " gold coins embossed on it. You need " + cost + " gold to open it.");
    }

    /**
     * Returns the cost to open this door
     *
     * @return The amount of gold required to open this door
     */
    public int getCost() {
        return this.cost;
    }

    /**
     * Marks this door as open
     */
    public void open() {
        this.isOpen = true;
        setDescription("An opened door that cost you " + cost + " gold.");
    }

    /**
     * Checks if this door is open
     *
     * @return true if door is open, false if it's still closed
     */
    public boolean isOpen() {
        return this.isOpen;
    }
}