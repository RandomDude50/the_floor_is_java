package game;

import game.interfaces.PowerUpSupplier;
import java.util.Random;

public final class PowerUpFactory {

    // Solo speed e shield — ClearLava ha il suo spawn separato
    private static final java.util.List<PowerUpSupplier> REGULAR_TYPES =
            java.util.List.of(SpeedPowerUp::new, ShieldPowerUp::new);

    private PowerUpFactory() {}

    public static AbstractPowerUp randomRegular(double x, double y, Random rng) {
        return REGULAR_TYPES.get(rng.nextInt(REGULAR_TYPES.size())).create(x, y);
    }
}