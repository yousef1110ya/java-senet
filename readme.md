# Senet - Ancient Egyptian Board Game

A Java implementation of **Senet**, one of the oldest known board games from ancient Egypt. This project features a complete game engine with support for both human and AI players, special cell effects, and full game rule enforcement.

## 📋 Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Project Structure](#project-structure)
- [Requirements](#requirements)
- [Installation](#installation)
- [How to Run](#how-to-run)
- [Game Rules](#game-rules)
- [Special Cells](#special-cells)
- [Architecture](#architecture)
- [Development](#development)

## 🎮 Overview

Senet is an ancient Egyptian board game that dates back to around 3100 BCE. This implementation recreates the classic game with:

- A 30-cell board with alternating starting positions
- 7 pieces per player (14 pieces total)
- Stick throwing mechanism (similar to dice)
- Special cells with unique effects
- Turn-based gameplay with rule enforcement
- Support for human and AI players

## ✨ Features

- **Complete Game Engine**: Full implementation of Senet game mechanics
- **Dual Player Support**: Play against another human or an AI opponent
- **Special Cell Effects**: 6 special cells with unique gameplay mechanics
- **Rule Enforcement**: Comprehensive rule engine validating all moves
- **AI Strategy**: Bot player with intelligent move selection
- **Game State Management**: Robust state tracking and validation
- **Move Validation**: Legal move checking and special effect resolution

## 📁 Project Structure

```
java-senet/
├── src/
│   ├── main/java/com/example/algo/
│   │   ├── App.java                    # Main game loop and entry point
│   │   ├── move/                       # Move execution logic
│   │   │   ├── Move.java
│   │   │   └── MovePiece.java
│   │   ├── player/                     # Player management
│   │   │   └── Player.java
│   │   ├── rules/                      # Game rules and validation
│   │   │   └── RuleEngine.java
│   │   ├── setup/                      # Game initialization
│   │   │   ├── BoardFactory.java
│   │   │   └── GameInitializer.java
│   │   ├── state/                      # Game state and board
│   │   │   ├── Cell.java
│   │   │   ├── CellEffect.java
│   │   │   ├── GameState.java
│   │   │   ├── NormalCell.java
│   │   │   ├── Piece.java
│   │   │   ├── SpecialCell.java
│   │   │   └── effect/                 # Special cell effects
│   │   │       ├── HappinessEffect.java
│   │   │       ├── HorusEffect.java
│   │   │       ├── ReAtoumEffect.java
│   │   │       ├── RebirthEffect.java
│   │   │       ├── ThreeTruthsEffect.java
│   │   │       └── WaterEffect.java
│   │   ├── strategy/                   # Player strategies
│   │   │   ├── MoveStrategy.java
│   │   │   ├── ai/
│   │   │   │   └── BotStrategy.java
│   │   │   └── human/
│   │   │       └── HumanStrategy.java
│   │   └── util/                       # Utility classes
│   │       ├── GeneralUtil.java
│   │       ├── RandomProvider.java
│   │       └── StickThrow.java
│   └── test/java/com/example/algo/
│       └── AppTest.java
├── pom.xml                             # Maven configuration
└── README.md                           # This file
```

## 🔧 Requirements

- **Java**: JDK 8 or higher
- **Maven**: 3.6.0 or higher (for building and running)
- **JUnit**: 3.8.1 (for testing, included via Maven)

## 📦 Installation

1. **Clone or download** this repository:
   ```bash
   git clone <repository-url>
   cd java-senet
   ```

2. **Verify Java installation**:
   ```bash
   java -version
   ```

3. **Verify Maven installation**:
   ```bash
   mvn -version
   ```

## 🚀 How to Run

### Using Maven

Run the game using Maven's exec plugin:

```bash
mvn exec:java
```

### Using Java directly

1. **Compile the project**:
   ```bash
   mvn compile
   ```

2. **Run the main class**:
   ```bash
   java -cp target/classes com.example.algo.App
   ```

### Running Tests

```bash
mvn test
```

## 🎲 Game Rules

### Basic Mechanics

1. **Board**: 30 cells numbered 0-29 (cells 1-30 in game terms)
2. **Pieces**: Each player starts with 7 pieces
3. **Starting Positions**: Pieces alternate on cells 1-14 at the start
4. **Movement**: Players throw sticks to determine movement (values 1-5)
5. **Objective**: Move all pieces off the board (beyond cell 30)

### Movement Rules

- **Forward Movement**: Pieces move forward by the stick value
- **No Backward Movement**: Pieces cannot move backward
- **Swapping**: If a piece lands on an opponent's piece, they swap positions
- **Blocking**: Cannot land on your own pieces
- **Exit Rule**: Pieces can only exit the board from cells 26-30
- **Cell 25 Rule**: Cannot jump from before cell 25 to after cell 25 (must land exactly on 26)

### Turn Flow

1. Player throws sticks (1-5)
2. Player selects a piece to move
3. Move is validated by rule engine
4. Move is executed
5. Special cell effects are applied if applicable
6. Game checks for win condition
7. Turn switches to next player

## 🏛️ Special Cells

The board contains 6 special cells with unique effects:

| Cell | Name | Effect |
|------|------|--------|
| **15** | House of Rebirth | Pieces landing here are sent back to the start |
| **26** | House of Happiness | Safe zone - pieces cannot be swapped here |
| **27** | House of Water | Pieces landing here must return to Rebirth (cell 15) |
| **28** | House of Three Truths | If player rolls 3 on next turn and moves this piece, it's removed; otherwise sent to Rebirth |
| **29** | House of Re-Atoum | If player rolls 2 on next turn and moves this piece, it's removed; otherwise sent to Rebirth |
| **30** | House of Horus | If player moves this piece on next turn, it's removed; otherwise sent to Rebirth |

### Special Effect Resolution

When a piece lands on Three Truths, Re-Atoum, or Horus cells, the effect is **pending** until the player's next turn. On the next turn:
- The player must roll sticks
- If the condition is met (specific roll or move), the piece is **removed from the game**
- If the condition is not met, the piece is sent to **Rebirth** (cell 15)

## 🏗️ Architecture

### Design Patterns

- **Strategy Pattern**: `MoveStrategy` interface allows different player types (Human, AI)
- **Factory Pattern**: `BoardFactory` creates the game board with special cells
- **State Pattern**: `GameState` manages the complete game state
- **Command Pattern**: `Move` and `MovePiece` encapsulate move execution

### Key Components

- **GameState**: Central state management with board, pieces, and players
- **RuleEngine**: Validates all moves according to game rules
- **Cell Effects**: Polymorphic effect system for special cells
- **Move Execution**: Moves are validated, executed, and effects applied

### Game Flow

```
App.main()
  ↓
GameInitializer.createNewGame()
  ↓
Game Loop:
  ├─→ Current Player throws sticks
  ├─→ Strategy chooses move
  ├─→ RuleEngine validates move
  ├─→ Move executes
  ├─→ Special effects apply
  ├─→ Check win condition
  └─→ Switch players
```

## 💻 Development

### Building the Project

```bash
mvn clean compile
```

### Creating a JAR

```bash
mvn clean package
```

The JAR will be created in `target/algo-0.0.1-SNAPSHOT.jar`

### Code Structure Notes

- **Package**: `com.example.algo`
- **Main Class**: `com.example.algo.App`
- **Board Size**: 30 cells (0-indexed array)
- **Pieces per Player**: 7
- **Total Pieces**: 14

### Adding New Features

- **New Special Cells**: Extend `CellEffect` and add to `BoardFactory`
- **New Strategies**: Implement `MoveStrategy` interface
- **New Rules**: Extend `RuleEngine.isLegal()` method

## 📝 Notes

- The game uses a 0-indexed array internally, but displays cells as 1-30
- Pieces start with position -1 (off-board) before initial placement
- The stick throwing mechanism uses bit counting to simulate ancient Egyptian throwing sticks
- Game ends when a player has no pieces remaining on the board

## 🎯 Future Enhancements

Potential improvements for the project:

- [ ] GUI implementation
- [ ] Network multiplayer support
- [ ] Advanced AI with minimax algorithm
- [ ] Game history and replay
- [ ] Configuration file for custom rules
- [ ] Statistics tracking

---

**Enjoy playing Senet!** 🎮
