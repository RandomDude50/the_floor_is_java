package game;

import game.effects.ClearLavaEffect;
import game.interfaces.LavaClearer;
import game.interfaces.PowerUpEffect;
import game.ui.GameColors;
import javafx.scene.paint.Color;

/**
 * Power-up leggendario (raro): elimina tutta la lava esistente
 * ma aumenta permanentemente la velocità di espansione futura.
 */
public class ClearLavaPowerUp extends AbstractPowerUp {

    private final LavaClearer lava;

    public ClearLavaPowerUp(double x, double y, LavaClearer lava) {
        super(x, y);
        this.lava = lava;
    }

    @Override protected String        imagePath() { return "/Item_Lava_Bucket.png"; }
    @Override public    PowerUpEffect effect()    { return new ClearLavaEffect(lava); }
}