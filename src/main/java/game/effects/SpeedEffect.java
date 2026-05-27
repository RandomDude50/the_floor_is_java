package game.effects;

import game.interfaces.Movable;
import game.interfaces.PowerUpEffect;

public class SpeedEffect implements PowerUpEffect {
    @Override public void   applyTo(Movable m) { m.activateSpeedBoost(); }
    @Override public String statusLabel()       { return "⚡ SPEED BOOST!"; }
}