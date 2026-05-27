package game.interfaces;

/**
 * Componente aggiornabile ogni frame del game loop.
 * Qualsiasi oggetto che evolve nel tempo (punteggio, power-up, hazard)
 * implementa questa interfaccia, permettendo al Controller di aggiornarli
 * tutti con una singola forEach senza conoscerne il tipo concreto.
 */

public interface Updatable {
    void update(long currentTimeMs);
}