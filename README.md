# 🌋 The Floor is Lava!

> **A 2D survival game engineered with Java 21 + JavaFX 21, designed as a showcase of modern Object-Oriented design principles.**
> Developed with AI-assisted coding tools (Claude by Anthropic) for the *Programmazione ad Oggetti* course — DIEF, Università di Modena e Reggio Emilia.

---

## 📸 Screenshots

| Gameplay | Game Over |
|----------|-----------|
| ![gameplay](docs/gameplay.png) | ![gameover](docs/gameover.png) |

---

## 🎯 Project Mission

**The Floor is Lava!** is more than a game — it is an architectural exercise.

The primary goal was to build a system so thoroughly abstracted that:

> *"Given only the interfaces, abstract classes, and records — with zero concrete implementations — an AI model could faithfully recreate the entire game in Python, Kotlin, C#, or any other OOP language."*

Every design decision was made with this standard in mind. The result is a codebase where **Controller never imports a single concrete class**, where **adding a new power-up requires exactly one new file and one line**, and where **swapping the lava mechanic for ice or acid requires zero changes outside the Lava class itself**.

This is not accidental. It is the result of a rigorous, iterative application of SOLID principles, DRY, and Java's full abstraction toolkit — interfaces, abstract classes, records, functional interfaces, and lambdas.

---

## 🕹️ Game Overview

### Objective
Survive as long as possible on a map that is progressively consumed by lava. The longer you survive, the higher your score.

### Controls
| Key | Action |
|-----|--------|
| `W A S D` | Move |
| `SPACE` | Start game |
| `ESC` | Pause / Resume |
| `R` | Restart |

### Mechanics
| Feature | Description |
|---------|-------------|
| 🌋 **Spreading Lava** | Lava expands organically tile-by-tile, with increasing difficulty over time |
| 💀 **3 Lives** | Each time lava touches the player, a life is lost |
| 🛡️ **Respawn Shield** | 3 seconds of blinking invincibility after each death |
| ⚡ **Speed Power-up** | +60% movement speed for 5 seconds |
| 🛡️ **Shield Power-up** | 3 seconds of on-demand invincibility |
| 💧 **Clear Lava Potion** | *Legendary* — clears all lava, but permanently accelerates future expansion (stackable penalty) |
| 🏆 **Persistent High Score** | Saved locally to `~/.lava_highscore.txt` between sessions |
| ✨ **Particle Effects** | Lava sparks and dust clouds under the player's feet |

### Difficulty Curve
- Lava spreads faster every 2 minutes (capped at 3× base speed)
- New lava patches spawn every 5–8 seconds randomly
- Each *Clear Lava* potion collected multiplies future difficulty by ×1.4 (stackable)
- Power-ups cannot spawn on lava tiles — they always appear on safe ground

---

## 🏗️ Architecture

### The Core Philosophy: Abstraction as Documentation

The architecture follows a strict **three-layer pattern** inspired by the Java Collections Framework:

```
Interface          →   defines the contract ("what")
Abstract Class     →   implements shared structure ("how, partially")
Concrete Class     →   provides the full implementation ("how, completely")
```

This pattern makes the codebase **self-documenting**: reading only the interfaces and abstract classes is sufficient to understand the entire system.

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
│   ├── LavaClearer.java        # optional extension for clearable hazards
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
├── Player.java                 # implements Movable — sprite animation
├── Lava.java                   # extends AbstractTileHazard, implements HazardMap + LavaClearer
├── ParticleSystem.java         # implements Updatable + ParticleEmitter
├── InputManager.java           # keyboard state array
├── MovementSystem.java         # declarative key→action Map
├── Engine.java                 # implements GameLoop
│
├── ScoreTracker.java           # implements Updatable — uses ScoreFormula
├── LivesManager.java           # lives counter
├── PowerUpManager.java         # implements Updatable — spawn, collect, status
│
├── Controller.java             # orchestrator — depends ONLY on interfaces
└── MainApp.java                # JavaFX entry point — wires all dependencies

src/main/resources/
├── pirate_frame_0.png          # player idle
├── pirate_frame_1.png          # player walk frame 1
├── pirate_frame_2.png          # player walk frame 2
├── speed_powerup.png
├── shield_powerup.png
├── clear_lava_powerup.png
└── Item_Lava_Bucket.png
```

---

## 🧱 SOLID Principles in Practice

### Single Responsibility Principle
`Controller` was deliberately kept as a **pure orchestrator** — it contains zero rendering code, zero score formatting, zero UI logic. Each of those concerns lives in its own class:

| Class | Single Responsibility |
|-------|-----------------------|
| `ScoreTracker` | Compute score from elapsed time |
| `LivesManager` | Track remaining lives |
| `PowerUpManager` | Spawn, detect, and apply power-ups |
| `HUDRenderer` | Render the heads-up display |
| `GameOverScreen` | Render the game-over overlay |
| `MovementSystem` | Translate keyboard input to movement |
| `ParticleSystem` | Manage and render particles |

### Open/Closed Principle
**Adding a new power-up** requires exactly:
1. One new class extending `AbstractPowerUp`
2. One new class implementing `PowerUpEffect`
3. One line added to `PowerUpFactory`

Zero modifications to `PowerUpManager`, `Controller`, `HUDRenderer`, or any other existing class.

### Liskov Substitution Principle
`SpeedPowerUp`, `ShieldPowerUp`, and `ClearLavaPowerUp` are all used exclusively as `Collectible` throughout the system. No `instanceof` check exists anywhere outside the factory.

### Interface Segregation Principle
`LavaClearer` is deliberately **separate** from `HazardMap`. Not all hazard maps need to support clearing — this allows future `IceMap` or `AcidMap` implementations to exist without implementing `clearAndAccelerateLava()`.

`GameEventListener` uses **default methods** — listeners implement only the events they care about. `HUDRenderer` handles score, lives, and power-up status. `GameOverScreen` handles only `onGameOver`. Neither knows the other exists.

### Dependency Inversion Principle
`Controller`'s field declarations:
```java
private final Movable    player;    // not Player
private final HazardMap  hazardMap; // not Lava
private final GameLoop   engine;    // not Engine
```
`MainApp` is the **only** class that mentions concrete types — it is the single wiring point of the entire application.

---

## ⚙️ Functional Programming

The codebase embraces Java's functional features throughout:

```java
// Declarative key→action map — adding a new key is one line
private static final Map<Integer, Consumer<Movable>> KEY_ACTIONS = Map.of(
    87, Movable::moveUp,
    65, Movable::moveLeft,
    83, Movable::moveDown,
    68, Movable::moveRight
);

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

// Particle state evolution without mutation
public Particle move() {
    return new Particle(x + vx, y + vy, vx, vy * 0.88, life - 0.04, maxLife, type);
}
```

---

## 🌍 Cross-Language Portability

This is the project's most distinctive engineering achievement.

The **entire abstract footprint** — all interfaces, abstract classes, and records — is sufficient to reconstruct the game in any OOP language, with the assistance of an AI model, **without reading a single line of concrete implementation**.

### Why this works

| Layer | Translates to |
|-------|--------------|
| `interface` | `interface` (Java, Kotlin, C#), `Protocol` (Swift), `ABC` (Python) |
| `abstract class` | identical in Kotlin, C#; `ABC` in Python |
| `record` | `data class` (Kotlin), `record struct` (C#), `@dataclass` (Python) |
| `@FunctionalInterface` | lambda / callable in any language |
| `default` methods | extension functions (Kotlin), mixin pattern (Python) |

### What an AI needs to translate this project

1. The 14 interface files
2. The 2 abstract class files  
3. The 3 record files
4. The `Controller` class (already depends only on interfaces)

**Result**: a faithful, idiomatic reimplementation in the target language — with zero ambiguity about behavior, contracts, or data flow.

---

## 📊 Logic Line Count

| Area | Lines |
|------|-------|
| Lava spreading algorithm (`Lava.java`) | ~90 |
| Player state machine + animation | ~80 |
| PowerUp hierarchy (3 concrete + 2 abstract + factory) | ~70 |
| Particle system | ~60 |
| Controller + game loop | ~55 |
| Records + interfaces (14 files) | ~60 |
| Movement / Input / Engine | ~40 |
| Score / Lives / PowerUpManager | ~50 |
| Repository | ~20 |
| **Total** | **~525*** |

> *Slightly above the 500-line guideline due to the particle system and legendary power-up additions made during polish phase. GUI components (`HUDRenderer`, `GameOverScreen`, `StartScreen`, `PauseScreen`, `UIFactory`, `GameColors`, `MainApp`) excluded per project guidelines.

---

## 🔧 Build & Run

### Requirements
- Java 21+
- Maven 3.8+

### Run
```bash
mvn clean javafx:run
```

### Project was tested on
- Windows 11 with JDK 21 (Temurin)
- IntelliJ IDEA 2026.1 with bundled Maven 3

---

## 🤖 AI-Assisted Development

This project was developed using **Claude (Anthropic)** as an AI coding assistant across the entire development lifecycle:

| Phase | AI Contribution |
|-------|----------------|
| Initial design | Game concept, class structure, SOLID application plan |
| Iterative refactoring | SRP extraction, DIP injection, ISP with default methods |
| Bug fixing | Z-index ordering, dimension initialization, interface mismatches |
| Progressive abstraction | Interface + abstract class layering, `Movable`/`HazardMap`/`GameLoop` |
| Functional patterns | `Map<KeyCode, Consumer<Movable>>`, `Optional`-based spawning, particle records |
| Visual polish | Sprite animation, particle effects, organic lava growth |
| Documentation | JavaDoc, class-level comments, this README |

All architectural decisions were validated against course material on interfaces, abstract classes, and SOLID principles. The AI was used as a collaborative tool — every suggestion was reviewed, understood, and deliberately accepted or rejected.

---

## 📚 References

- [learn-java-javafx — course examples](https://github.com/nbicocchi/learn-java-javafx/tree/main/code)
- [learn-java-core](https://github.com/nbicocchi/learn-java-core)
- [Baeldung — Interface vs Abstract Class](https://www.baeldung.com/java-interface-vs-abstract-class)
- [Baeldung — SOLID Principles](https://www.baeldung.com/solid-principles)
- [Baeldung — Default Methods](https://www.baeldung.com/java-static-default-methods)
- [Refactoring Guru — Strategy Pattern](https://refactoring.guru/design-patterns/strategy)
- [Refactoring Guru — Observer Pattern](https://refactoring.guru/design-patterns/observer)

---

<div align="center">

**Built with Java 21 · JavaFX 21 · Maven · Claude AI**

*Programmazione ad Oggetti — DIEF, Università di Modena e Reggio Emilia*

</div>
