package game.interfaces;

/**
 * Ciclo di aggiornamento del motore di gioco.
 * Astrae l'AnimationTimer di JavaFX: Engine ne è l'unica implementazione ora,
 * ma l'interfaccia permette di sostituirlo con un loop basato su Thread,
 * ScheduledExecutorService o qualsiasi altro meccanismo senza toccare Controller.
 */

public interface GameLoop {
    void start();
    void stop();
}