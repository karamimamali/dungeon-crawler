# Djeneric Dungeon Crawler

A Java-based dungeon crawler game with a graphical user interface featuring multiple dungeons, combat mechanics, inventory, and progression systems.

## Project Overview

This project implements a classic roguelike dungeon crawler where players navigate through multiple dungeons, battle enemies, collect gold, and progress through increasingly difficult levels. The game features a modern, customized UI with animations, sound effects, and visual feedback.

## Features

- **Multiple Dungeons**: Explore 4 different dungeons with increasing difficulty
- **Floor System**: Each dungeon has multiple floors to explore
- **Combat System**: Turn-based combat with enemies scaled by difficulty
- **Gold Collection**: Find gold throughout the dungeon to spend on opening doors
- **Character Progression**: Gain XP from combat to level up and increase stats
- **Interactive Map**: Real-time visual representation of the dungeon layout
- **Sound Effects**: Various sound effects for movement, combat, and interactions
- **Animations**: Player and environment animations
- **Statistics Tracking**: Real-time display of player stats (HP, XP, gold, etc.)

## Getting Started

### Prerequisites

- Java Development Kit (JDK) 8 or higher
- Git (optional)

### Installation

1. Clone the repository (or download the source code):
   ```bash
   git clone https://github.com/yourusername/djeneric-dungeon-crawler.git
   ```

2. Navigate to the project directory:
   ```bash
   cd djeneric-dungeon-crawler
   ```

3. Compile the code:
   ```bash
   javac -d bin src/main/java/gui/Main.java
   ```

4. Run the game:
   ```bash
   java -cp bin gui.Main
   ```

## How to Play

- Use the arrow buttons (↑, ↓, ←, →) to move your character through the dungeon
- Battle enemies automatically when moving onto their tile
- Collect gold (G) to open gold doors (D)
- Find stairs (X) to proceed to the next floor or dungeon
- Complete all dungeons to win the game

### Map Legend

- **P**: Player
- **-**: Wall (cannot be traversed)
- **o**: Empty tile
- **e**: Enemy
- **g**: Gold
- **d**: Gold Door (closed)
- **D**: Gold Door (open)
- **x**: Stairs
- **s**: Start

## Project Structure

```
src/
├── main/
│   ├── java/
│   │   ├── gui/
│   │   │   └── Main.java              # Main game class and UI
│   │   ├── locations/
│   │   │   ├── Dungeon.java           # Dungeon implementation
│   │   │   ├── Floor.java             # Floor implementation
│   │   │   └── Map.java               # Game world map
│   │   └── tile/
│   │       ├── character/
│   │       │   ├── Battle.java        # Combat system
│   │       │   ├── Character.java     # Base character class
│   │       │   ├── Enemy.java         # Enemy implementation
│   │       │   ├── Player.java        # Player implementation
│   │       │   └── PlayerStats.java   # Player statistics
│   │       ├── Empty.java             # Empty tile
│   │       ├── Gold.java              # Gold tile
│   │       ├── GoldDoor.java          # Door that requires gold
│   │       ├── Stairs.java            # Stairs to next level
│   │       ├── Start.java             # Start position
│   │       ├── Tile.java              # Base tile class
│   │       └── Wall.java              # Wall tile
│   └── res/
│       ├── floorplan*.txt             # Floor layouts
│       └── music/                     # Sound files
```

## Design Patterns Used

- **Model-View-Controller (MVC)**: Separation of game logic, UI, and control
- **Inheritance**: Tile hierarchy for different game elements
- **Composition**: Player contains PlayerStats
- **Factory Method**: Creation of tiles in the Floor class
- **Singleton**: Map class manages all dungeons

## Contributing

This project is open for contributions. Please feel free to fork the repository, make changes, and submit pull requests.

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Acknowledgments

- Developed by karamimamali with UI enhancements by tp275
- Background music: "Fight for the Future" (track #336841)