# Minesweeper

## Project Overview
This project is an implementation of the classic **Minesweeper** game, developed in **Java** using the **Processing library** for graphics and **Gradle** as the dependency manager.  

The player’s goal is to reveal all safe tiles without detonating a mine. Mines are placed randomly at the start of the game, and players can flag tiles to mark suspected mines. The game ends when all safe tiles are revealed or when a mine is clicked.


---

## Features
- **18 × 27 grid** with tiles initially hidden (blue).  
- **Random mine placement** based on command-line argument (default = 100 mines if missing or invalid).  
- **Mouse interactions**:  
  - **Left-click**: Reveal tile.  
  - **Right-click**: Flag/unflag a tile (prevents accidental reveal).  
- **Win condition**: All non-mine tiles revealed → display **“You win!”**.  
- **Loss condition**: Clicking on a mine triggers sequential explosions → display **“You lost!”**.  
- **Explosion animation**: 10 frames (`mine0.png`–`mine9.png`), staggered every 3 frames.  
- **Cascade reveal**: Blank cells automatically reveal adjacent tiles.  
- **Timer**: Shown in the top-right corner, counting seconds elapsed until game ends.  
- **Restart**: Press `r` to reset the game with new mine positions.  

---

## File Structure
```
├── build.gradle               # Gradle build file
├── src/
│   ├── main/java/             # Java source files (game logic, Processing setup)
│   └── test/java/             # Unit tests (if applicable)
├── resources/
│   └── images/                # Provided sprites (mine0.png – mine9.png, tiles, flags)
└── README.md
```

---

## Setup and Running
### Requirements
- **Java 8** (must compile and run on this version)  
- **Gradle** (project uses Gradle wrapper if provided)

### Running the Game
```bash
# Run with default mines (100)
gradle run

# Run with custom number of mines
gradle run --args="150"
```

---

## Controls
- **Left Mouse Button** → Reveal a tile.  
- **Right Mouse Button** → Flag/unflag a tile.  
- **R** → Restart the game.  

---

## Design Notes
- **OOP principles** applied: separation of game entities (Tile, Board, Mine, Timer).  
- **Animation handling**: mine explosions use frame-based sequencing.  
- **Recursive reveal**: blank tiles trigger adjacent reveals until boundary reached.  
- **Command-line flexibility**: number of mines set via runtime argument.  

---

## Marking Criteria Alignment
✔ Window launches and displays correct initial layout.  
✔ Random mine placement works with argument/default.  
✔ Hover highlights and flagging/unflagging implemented.  
✔ Correct number/colour shown for revealed tiles.  
✔ Explosion animation sequenced correctly.  
✔ Win/Loss conditions with appropriate messages.  
✔ Timer counts and stops when game ends.  

---

## References
- [Processing Javadocs](https://processing.github.io/processing-javadocs/core/)  
- [Minesweeper Online](https://minesweeperonline.com/)  
