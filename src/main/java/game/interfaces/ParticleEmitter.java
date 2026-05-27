package game.interfaces;

/**
 * Oggetto capace di ricevere emissioni di particelle.
 * Lava e Player dipendono da questa interfaccia, non da ParticleSystem,
 * mantenendo il DIP: l'implementazione concreta è iniettata dall'esterno.
 */
public interface ParticleEmitter {
    void emitLavaSpark(double x, double y);
    void emitDust(double x, double y);
}