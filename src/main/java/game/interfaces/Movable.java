package game.interfaces;

import game.model.Position;

/**
 * Entità controllabile dal giocatore: si muove nelle 4 direzioni, ha un raggio
 * fisico per collisioni e gestisce stati temporanei (invincibilità, speed boost).
 * Controller e MovementSystem dipendono solo da questa interfaccia, mai da Player:
 * sostituire il personaggio giocabile non richiede alcuna modifica all'orchestrazione.
 */

public interface Movable extends GameEntity {
    void   moveUp();
    void   moveDown();
    void   moveLeft();
    void   moveRight();
    void   setPosition(Position pos);
    double getRadius();
    boolean isInvincible();
    boolean isSpeedBoosted();
    void   activateInvincibility();
    void   activateSpeedBoost();
    void   activateShield();
    javafx.scene.Node getNode();
}