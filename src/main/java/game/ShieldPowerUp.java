package game;

import game.effects.ShieldEffect;
import game.interfaces.PowerUpEffect;
import game.ui.GameColors;
import javafx.scene.paint.Color;

public class ShieldPowerUp extends AbstractPowerUp {
    public ShieldPowerUp(double x, double y) { super(x, y); }
    @Override protected String        imagePath() { return "/shield_powerup.png"; }
    @Override public    PowerUpEffect effect()    { return new ShieldEffect(); }
}