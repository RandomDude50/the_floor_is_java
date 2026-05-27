package game.interfaces;

import game.model.Position;

/**
 * Oggetto che occupa spazio fisico sulla mappa e può essere "toccato".
 * La collisione è definita come sovrapposizione tra la posizione passata
 * e il corpo dell'oggetto, dati un centro e un raggio.
 * Base di Collectible: ogni power-up è per definizione collidable.
 */

public interface Collidable {
    boolean collidesWith(Position position, double radius);
}