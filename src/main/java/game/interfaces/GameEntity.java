package game.interfaces;

import game.model.Position;

/**
 * Entità base del mondo di gioco: ha una posizione e si aggiorna ogni frame.
 * È il livello più alto della gerarchia degli oggetti "vivi" sulla mappa.
 * Movable la estende per aggiungere capacità di movimento controllato;
 * in futuro potrebbe essere estesa anche da entità non controllabili (NPC, nemici).
 */

public interface GameEntity {
    Position getPosition();
    void     update();
}