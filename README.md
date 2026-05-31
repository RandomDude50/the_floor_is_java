
# 🌋 The Floor is Java! 🌋

<br>

## Project Mission

**The Floor is Java!** is more than a game — it is an architectural exercise.

The primary goal was to build a system so thoroughly abstracted that:

> *"Given only the interfaces, abstract classes, and records — with zero concrete implementations — an AI model could faithfully recreate the entire game in Python, Kotlin, C#, or any other OOP language."*

Every design decision was made with this standard in mind. The result is a codebase where **Controller never imports a single concrete class**, where **adding a new power-up requires exactly one new file and one line**, and where **swapping the lava mechanic for ice or acid requires zero changes outside the Lava class itself**.

This is not accidental. It is the result of a rigorous, iterative application of SOLID principles, DRY, and Java's full abstraction toolkit — interfaces, abstract classes, records, functional interfaces, and lambdas.

---

<br>

## Game Overview

### Objective
Survive as long as possible on a map that is progressively consumed by lava. The longer you survive — and the more cleverly you move — the higher your score.

### Controls
| Key | Action |
|-----|--------|
| `W A S D` | Move |
| `SPACE` | Start game |
| `ESC` | Pause / Resume |
| `R` | Restart *(only while paused or after game over)* |

<br>

### Mechanics & Features
| Feature | Description |
|---------|-------------|
| 🌋 **Spreading Lava** | Lava expands organically tile-by-tile from random patches, with difficulty increasing over time |
| 💀 **3 Lives** | Each time lava touches the player, a life is lost |
| 🛡️ **Respawn Shield** | 3 seconds of blinking invincibility after each death |
| ⚡ **Speed Power-up** | +60% movement speed for 5 seconds |
| 🛡️ **Shield Power-up** | 3 seconds of on-demand invincibility |
| 💧 **Clear Lava Potion** | *Legendary* — clears all lava, but each use permanently reduces the spawn interval of new patches |
| 🌙 **Moonwalk Bonus** | Walking left without flipping the sprite = Moonwalk! Grants **+10% speed** and **+20% score rate** while active |
| ↗️ **Diagonal Penalty** | Moving diagonally (two keys simultaneously) applies a **−10% speed** penalty |
| 🏆 **Persistent High Score** | Saved locally to `~/.lava_highscore.txt` between sessions |
| ✨ **Particle Effects** | Lava sparks on tile ignition, dust clouds under the player's feet |
| ⏸️ **Pause / Resume** | Full pause — timer, lava, and power-ups all freeze |

<br>

### Difficulty Curve
- Lava spreads faster every 2 minutes (capped at 3× base speed)
- New lava patches spawn every **4–7 seconds** randomly
- The *Clear Lava* legendary potion spawns every **80–100 seconds**
- Each *Clear Lava* potion collected **reduces the patch spawn interval by 1 second** (min 1 second) — lava keeps regenerating faster and faster
- Power-ups and potions **never spawn on lava** — always on safe ground

---

<br>

## Architecture

### The Core Philosophy: Abstraction as Documentation

The architecture follows a strict **three-layer pattern** inspired by the Java Collections Framework:

```
Interface          →   defines the contract ("what")
Abstract Class     →   implements shared structure ("how, partially")
Concrete Class     →   provides the full implementation ("how, completely")
```

This pattern makes the codebase **self-documenting**: reading only the interfaces and abstract classes is sufficient to understand the entire system — no concrete implementation needs to be read.

<br>

### Package Structure

```
src/main/java/game/
│
├── config/                     ← Immutable configuration
│   └── GameConfig.java         # record — single source of all game parameters
│
├── model/                      ← Pure data, no side effects
│   ├── Position.java           # record — geometry (clamp, distance)
│   └── ScoreSnapshot.java      # record — score + formatted time
│
├── interfaces/                 ← The "footprint" — stable contracts
│   ├── Updatable.java          # update(long ms)
│   ├── Collidable.java         # collidesWith(Position, radius)
│   ├── Collectible.java        # extends Collidable — power-up contract
│   ├── GameEntity.java         # getPosition() + update()
│   ├── Movable.java            # extends GameEntity — full player contract
│   ├── HazardMap.java          # lava/hazard abstraction
│   ├── LavaClearer.java        # optional extension for clearable hazards (ISP)
│   ├── GameLoop.java           # start() / stop()
│   ├── ParticleEmitter.java    # emitLavaSpark() + emitDust()
│   ├── ScoreRepository.java    # load / save / isNewRecord
│   ├── PowerUpEffect.java      # applyTo(Movable) — functional interface
│   ├── PowerUpSupplier.java    # factory functional interface
│   ├── ScoreFormula.java       # strategy pattern — functional interface
│   └── GameEventListener.java  # observer pattern with default methods
│
├── effects/                    ← Pure strategy implementations
│   ├── SpeedEffect.java
│   ├── ShieldEffect.java
│   └── ClearLavaEffect.java
│
├── repository/                 ← I/O isolated behind interface
│   └── FileScoreRepository.java
│
├── ui/                         ← Rendering only, zero game logic
│   ├── GameColors.java         # all Color constants in one place
│   ├── UIFactory.java          # JavaFX boilerplate factory
│   ├── HUDRenderer.java        # implements GameEventListener
│   ├── GameOverScreen.java     # implements GameEventListener
│   ├── StartScreen.java
│   └── PauseScreen.java
│
├── AbstractPowerUp.java        # abstract — ImageView, collision, lifecycle
├── AbstractTileHazard.java     # abstract — grid structure, isHazardous()
│
├── SpeedPowerUp.java           # concrete
├── ShieldPowerUp.java          # concrete
├── ClearLavaPowerUp.java       # concrete — legendary
├── PowerUpFactory.java         # OCP factory
│
├── Player.java                 # implements Movable — sprite animation, moonwalk, diagonal
├── Lava.java                   # extends AbstractTileHazard, implements HazardMap + LavaClearer
├── ParticleSystem.java         # implements Updatable + ParticleEmitter
├── InputManager.java           # keyboard state array
├── MovementSystem.java         # declarative key→action Map + diagonal detection
├── Engine.java                 # implements GameLoop
│
├── ScoreTracker.java           # implements Updatable — ScoreFormula + moonwalk bonus + pause support
├── LivesManager.java           # lives counter
├── PowerUpManager.java         # implements Updatable — spawn, collect, status
│
├── Controller.java             # orchestrator — depends ONLY on interfaces + records
└── MainApp.java                # JavaFX entry point — wires all concrete dependencies

src/main/resources/
├── pirate_frame_0.png          # player idle
├── pirate_frame_1.png          # player walk frame 1
├── pirate_frame_2.png          # player walk frame 2
├── speed_powerup.png
├── shield_powerup.png
├── clear_lava_powerup.png
└── Item_Lava_Bucket.png        # header decoration
```

---

<br>

## SOLID Principles in Practice

### Single Responsibility Principle
`Controller` was deliberately kept as a **pure orchestrator** — it contains zero rendering code, zero score formatting, zero UI logic. Each of those concerns lives in its own class:

| Class | Single Responsibility |
|-------|-----------------------|
| `ScoreTracker` | Compute score from elapsed time, moonwalk bonus, pause tracking |
| `LivesManager` | Track remaining lives |
| `PowerUpManager` | Spawn, detect collision, and apply power-ups |
| `HUDRenderer` | Render the heads-up display |
| `GameOverScreen` | Render the game-over overlay |
| `MovementSystem` | Translate keyboard input to movement + diagonal penalty |
| `ParticleSystem` | Manage and render particles |

<br>

### Open/Closed Principle
**Adding a new power-up** requires exactly:
1. One new class extending `AbstractPowerUp`
2. One new class implementing `PowerUpEffect`
3. One line added to `PowerUpFactory`

Zero modifications to `PowerUpManager`, `Controller`, `HUDRenderer`, or any other existing class.

<br>

### Liskov Substitution Principle
`SpeedPowerUp`, `ShieldPowerUp`, and `ClearLavaPowerUp` are all used exclusively as `Collectible` throughout the system. No `instanceof` check exists anywhere outside the factory.

<br>

### Interface Segregation Principle
`LavaClearer` is deliberately **separate** from `HazardMap`. Not all hazard maps need to support clearing — this allows future `IceMap` or `AcidMap` implementations to exist without implementing `clearAndAccelerateLava()`.

`GameEventListener` uses **default methods** — listeners implement only the events they care about. `HUDRenderer` handles score, lives, and power-up status. `GameOverScreen` handles only `onGameOver`. Neither knows the other exists.

<br>

### Dependency Inversion Principle
`Controller`'s field declarations use only interface types:
```java
private Movable    player;    // not Player
private HazardMap  hazardMap; // not Lava
private GameLoop   engine;    // not Engine
```

`MainApp` is the **only** class that mentions concrete types — it is the single wiring point of the entire application.

---

<br>

## Functional Programming

The codebase embraces Java's functional features throughout:

```java
// Declarative key→action map — adding a new key is one line
private static final Map<Integer, Consumer<Movable>> KEY_ACTIONS = Map.of(
    87, Movable::moveUp,
    65, Movable::moveLeft,
    83, Movable::moveDown,
    68, Movable::moveRight
);

// Diagonal penalty detection — pure functional check
boolean diagonal = (input.isPressed(65) || input.isPressed(68))
                && (input.isPressed(87) || input.isPressed(83));
entity.setMovementModifier(diagonal ? 0.9 : 1.0);

// Functional pipeline for power-up spawning
private void spawn() {
    safePosition()
        .map(pos -> PowerUpFactory.randomRegular(pos.x(), pos.y(), rng))
        .ifPresent(this::addToScene);
}

// Strategy pattern as lambda — ScoreFormula is a @FunctionalInterface
ScoreFormula DEFAULT = elapsed -> (int)(elapsed * 60 + (elapsed / 60) * 60);

// Observer pattern — fire events to all listeners with a single line
private void fire(Consumer<GameEventListener> event) { listeners.forEach(event); }

// Particle state evolution without mutation — record returns new instance
public Particle move() {
    return new Particle(x + vx, y + vy, vx, vy * 0.88, life - 0.04, maxLife, type);
}
```

---

<br>

## Gameplay Mechanics Detail

### Moonwalk Bonus
The player sprite naturally faces right at all times. When the player moves **left**, the sprite slides backwards — an involuntary moonwalk. This visual quirk was deliberately turned into a bonus mechanic:
- **+10% speed** while moving left
- **+20% score rate** for all time spent moonwalking (tracked by `ScoreTracker`)
- Displayed in the HUD as `MOONWALK!` alongside active power-ups

<br>

### Diagonal Penalty
Moving in two directions simultaneously (e.g., `W + D`) applies a **−10% speed modifier**, set by `MovementSystem` via `Movable.setMovementModifier(0.9)`. Combined with the moonwalk bonus when moving left-diagonally, the net effect is approximately neutral speed.

<br>

### Clear Lava Potion — Stackable Penalty
Each legendary potion collected:
1. Instantly clears the entire lava grid
2. Reduces `chazzeIntervalBase` by 1 second (min 1 second) — new patches regenerate more frequently
3. Resets `gameStartTime` so lava difficulty restarts from scratch — same organic growth as game start

This makes hoarding the potion increasingly risky: maps regenerate faster and faster each time.

---

<br>

## Cross-Language Portability

This is the project's most distinctive engineering achievement.

The **entire abstract footprint** — all interfaces, abstract classes, and records — is sufficient to reconstruct the game in any OOP language, with the assistance of an AI model, **without reading a single line of concrete implementation**.

<br>

### Why this works

| Layer | Translates to |
|-------|--------------|
| `interface` | `interface` (Java, Kotlin, C#), `Protocol` (Swift), `ABC` (Python) |
| `abstract class` | identical in Kotlin, C#; `ABC` in Python |
| `record` | `data class` (Kotlin), `record struct` (C#), `@dataclass` (Python) |
| `@FunctionalInterface` | lambda / callable in any language |
| `default` methods | extension functions (Kotlin), mixin pattern (Python) |

<br>

### What an AI needs to translate this project

1. The 14 interface files
2. The 2 abstract class files
3. The 3 record files
4. The `Controller` class (already depends only on interfaces)

**Result**: a faithful, idiomatic reimplementation in the target language — with zero ambiguity about behavior, contracts, or data flow.

---

<br>

## Logic Line Count

| Area | Lines |
|------|-------|
| Lava spreading algorithm (`Lava.java`) | ~90 |
| Player state machine + animation + moonwalk/diagonal | ~90 |
| PowerUp hierarchy (3 concrete + 2 abstract + factory) | ~70 |
| Particle system | ~60 |
| Controller + game loop + state machine | ~60 |
| Records + interfaces (14 files) | ~65 |
| MovementSystem + InputManager + Engine | ~45 |
| ScoreTracker + LivesManager + PowerUpManager | ~55 |
| Repository | ~20 |
| **Total** | **~555*** |

> *GUI components (`HUDRenderer`, `GameOverScreen`, `StartScreen`, `PauseScreen`, `UIFactory`, `GameColors`, `MainApp`) and `pom.xml` excluded per project guidelines.

---

<br>

## Build & Run

### Requirements
- Java 21+
- Maven 3.8+

### Run
```bash
mvn clean javafx:run
```

### Tested on
- Windows 11, JDK 21 (Temurin), IntelliJ IDEA 2026.1

---

<br>

## References

- [learn-java-javafx — course examples](https://github.com/nbicocchi/learn-java-javafx/tree/main/code)
- [learn-java-core](https://github.com/nbicocchi/learn-java-core)
- [Baeldung — Interface vs Abstract Class](https://www.baeldung.com/java-interface-vs-abstract-class)
- [Baeldung — SOLID Principles](https://www.baeldung.com/solid-principles)
- [Baeldung — Default Methods](https://www.baeldung.com/java-static-default-methods)
- [Refactoring Guru — Strategy Pattern](https://refactoring.guru/design-patterns/strategy)
- [Refactoring Guru — Observer Pattern](https://refactoring.guru/design-patterns/observer)

