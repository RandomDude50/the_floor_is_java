package game.interfaces;

/**
 * Persistenza del punteggio massimo (DIP: dipendenza invertita sull'I/O).
 * Controller non sa se il record è salvato su file, database o memoria.
 * FileScoreRepository è l'implementazione concreta wired in MainApp;
 * in test si può iniettare un'implementazione in-memory senza modificare nulla.
 */

public interface ScoreRepository {
    int     load();
    void    save(int score);
    boolean isNewRecord(int score);
}