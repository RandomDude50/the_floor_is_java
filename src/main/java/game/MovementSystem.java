package game;

import game.interfaces.Movable;

import java.util.Map;
import java.util.function.Consumer;

public class MovementSystem {

    private final double mapWidth, mapHeight;

    /**
     * Mappa dichiarativa tasto → azione sul Movable.
     * Aggiungere un nuovo tasto = una riga, zero modifiche al resto.
     */

    private static final Map<Integer, Consumer<Movable>> KEY_ACTIONS = Map.of(
            87, Movable::moveUp,
            65, Movable::moveLeft,
            83, Movable::moveDown,
            68, Movable::moveRight
    );

    public MovementSystem(double mapWidth, double mapHeight) {
        this.mapWidth  = mapWidth;
        this.mapHeight = mapHeight;
    }

    public void handleInput(Movable entity, InputManager input) {
        boolean h = input.isPressed(65) || input.isPressed(68);
        boolean v = input.isPressed(87) || input.isPressed(83);

        // Malus diagonale: -10% velocità quando si preme sia orizzontale che verticale
        entity.setMovementModifier(h && v ? 0.9 : 1.0);

        KEY_ACTIONS.forEach((key, action) -> {
            if (input.isPressed(key)) action.accept(entity);
        });
    }

    public void clamp(Movable entity) {
        double r = entity.getRadius();
        entity.setPosition(
                entity.getPosition().clampedWithin(r, mapWidth - r, r, mapHeight - r)
        );
    }
}
