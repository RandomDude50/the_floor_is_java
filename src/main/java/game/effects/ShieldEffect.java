package game.effects;

import game.interfaces.Movable;
import game.interfaces.PowerUpEffect;

public class ShieldEffect implements PowerUpEffect {
    @Override public void   applyTo(Movable m) { m.activateShield(); }
    @Override public String statusLabel()       { return "🛡 SHIELD ACTIVE!"; }
}