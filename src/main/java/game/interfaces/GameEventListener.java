package game.interfaces;

import game.model.ScoreSnapshot;

/**
 * Bus di eventi del gioco (Observer pattern con default methods).
 * I listener implementano solo gli eventi che li interessano; il resto
 * è gestito dai default no-op. Controller notifica tutti i listener con
 * fire(Consumer<GameEventListener>), senza sapere chi ascolta cosa.
 * Aggiungere un nuovo evento = un nuovo default method, zero breaking changes.
 */

public interface GameEventListener {
    default void onScoreUpdated(ScoreSnapshot s, int highScore, boolean newRecord) {}
    default void onLivesChanged(int remainingLives) {}
    default void onPowerUpStatus(String statusLabel) {}
    default void onGameOver(ScoreSnapshot s, int highScore, boolean newRecord) {}
}