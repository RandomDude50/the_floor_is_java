package game.interfaces;

/**
 * Power-up raccoglibile sulla mappa. Estende Collidable aggiungendo:
 * - effect()      : l'effetto da applicare al Movable che lo raccoglie
 * - getNode()     : il nodo JavaFX da aggiungere/rimuovere dalla scena
 * - collect()     : marca l'oggetto come già raccolto
 * Separare Collectible da Collidable permette di avere ostacoli non raccoglibili
 * in futuro (es. muri) senza modificare questa gerarchia.
 */

public interface Collectible extends Collidable {
    PowerUpEffect          effect();
    javafx.scene.Node      getNode();
    boolean                isCollected();
    void                   collect();
}