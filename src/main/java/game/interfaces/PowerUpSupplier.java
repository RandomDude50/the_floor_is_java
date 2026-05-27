package game.interfaces;

import game.AbstractPowerUp;

/**
 * Factory funzionale per istanziare un AbstractPowerUp da coordinate (x, y).
 * Usata da PowerUpFactory come tipo della lista TYPES: ogni costruttore
 * SpeedPowerUp::new / ShieldPowerUp::new è un'istanza valida di questa interfaccia.
 * Aggiungere un nuovo power-up = aggiungere un method reference alla lista, nient'altro.
 */

@FunctionalInterface
public interface PowerUpSupplier {
    AbstractPowerUp create(double x, double y);
}