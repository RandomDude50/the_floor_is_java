package game.interfaces;

import game.model.Position;

/**
 * Mappa di pericolo che occupa l'intera area di gioco.
 * Espone solo tre domande al Controller: "questo punto è pericoloso?",
 * "aggiornati" e "dammi il tuo nodo visivo". Tutto il resto (tile, stage,
 * algoritmo di espansione) è nascosto nelle implementazioni concrete.
 * Domani "Lava" potrebbe essere sostituita da "Ice" o "Acid" senza toccare Controller.
 */

public interface HazardMap {
    boolean           isHazardous(Position pos);     // stage letale (3)
    boolean           isAnyHazard(Position pos);     // qualsiasi stage > 0
    void              update(long currentTimeMs, Position trackedPosition);
    javafx.scene.Node getNode();
    void              setGameStartTime(long startTime);
}