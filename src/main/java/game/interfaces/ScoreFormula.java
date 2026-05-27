package game.interfaces;

/**
 * Strategy pattern per il calcolo del punteggio (interfaccia funzionale).
 * La formula di default vale 60 punti/secondo + 60 punti/minuto completo.
 * Passarla a ScoreTracker come dipendenza permette di cambiarla a runtime
 * (es. difficoltà alta = formula esponenziale) senza modificare nulla altrove.
 */

@FunctionalInterface
public interface ScoreFormula {
    int compute(long elapsedSeconds);

    ScoreFormula DEFAULT = elapsed -> (int)(elapsed * 60 + (elapsed / 60) * 60);
}