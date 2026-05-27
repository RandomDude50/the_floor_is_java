package game;

import game.effects.SpeedEffect;
import game.interfaces.PowerUpEffect;
import game.ui.GameColors;
import javafx.scene.paint.Color;

public class SpeedPowerUp extends AbstractPowerUp {
    public SpeedPowerUp(double x, double y) { super(x, y); }
    @Override protected String        imagePath() { return "/speed_powerup.png"; }
    @Override public    PowerUpEffect effect()    { return new SpeedEffect(); }
}