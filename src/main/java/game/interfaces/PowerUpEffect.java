package game.interfaces;

import game.Player;

/**
 * Effetto applicato a un Movable al momento della raccolta di un power-up.
 * È un'interfaccia funzionale: ogni effetto è esprimibile come lambda.
 * Dipende da Movable (non da Player) per massima portabilità — qualsiasi
 * entità controllabile può ricevere un effetto senza cast o instanceof.
 */

public interface PowerUpEffect {
    void   applyTo(Movable target);   // Movable invece di Player
    String statusLabel();
}