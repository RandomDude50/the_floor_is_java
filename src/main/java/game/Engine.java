package game;

import game.config.GameConfig;
import game.interfaces.GameLoop;
import game.interfaces.Movable;
import javafx.animation.AnimationTimer;

public class Engine implements GameLoop {

    private final Movable        entity;
    private final InputManager   inputManager;
    private final MovementSystem movementSystem;
    private AnimationTimer       gameLoop;

    public Engine(Movable entity, GameConfig config, InputManager inputManager) {
        this.entity         = entity;
        this.inputManager   = inputManager;
        this.movementSystem = new MovementSystem(config.gameWidth(), config.gameHeight());
    }

    @Override
    public void start() {
        gameLoop = new AnimationTimer() {
            @Override public void handle(long now) {
                movementSystem.handleInput(entity, inputManager);
                movementSystem.clamp(entity);
                entity.update();
            }
        };
        gameLoop.start();
    }

    @Override
    public void stop() { if (gameLoop != null) gameLoop.stop(); }
}