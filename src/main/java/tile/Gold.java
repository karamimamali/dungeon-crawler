package tile;

/**
 * A Gold tile for the player to discover and be happy about.
 * Has a certain value that can be varied with eg. difficulty
 *
 * @version 1.1
 * @author karamimamali
 */
public class Gold extends tile.Tile {

    // the value/amount of gold in this Gold object
    private final int value;
    // whether this gold has been collected already
    private boolean collected = false;

    /**
     * Sets name, description and specified value of the Gold tile
     *
     * @param value The value/amount of gold in this Gold object
     */
    public Gold(int value) {
        setName("Gold");
        setDescription("You found some gold!");
        this.value = value;
    }

    /**
     * Returns the value of this Gold object
     *
     * @return The value of this Gold object
     */
    public int getValue() {
        return this.value;
    }

    /**
     * Marks this gold as collected
     */
    public void collect() {
        this.collected = true;
        // Change description to indicate gold has been collected
        setDescription("You've already collected the gold from here.");
    }

    /**
     * Checks if this gold has already been collected
     *
     * @return true if gold has been collected, false otherwise
     */
    public boolean isCollected() {
        return this.collected;
    }
}