package game.effects;

import game.interfaces.LavaClearer;
import game.interfaces.Movable;
import game.interfaces.PowerUpEffect;

/**
 * Effetto leggendario: azzera tutte le chiazze di lava esistenti
 * e applica un moltiplicatore permanente alla velocità di espansione.
 * Il malus è accumulabile: ogni raccolta aumenta ulteriormente la difficoltà.
 */
public class ClearLavaEffect implements PowerUpEffect {

    private final LavaClearer lava;

    public ClearLavaEffect(LavaClearer lava) {
        this.lava = lava;
    }

    @Override
    public void applyTo(Movable target) {
        lava.clearAndAccelerateLava();
    }

    @Override
    public String statusLabel() { return "💧 LAVA CLEARED!"; }
}