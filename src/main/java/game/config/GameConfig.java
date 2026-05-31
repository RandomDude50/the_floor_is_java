package game.config;

import game.model.Position;

public record GameConfig(
        double gameWidth,
        double gameHeight,
        int    initialLives,
        double playerSpeed,
        double speedMultiplier,
        long   invincibilityMs,
        long   speedBoostMs,
        long   powerUpSpawnMs
) {
    public static final GameConfig DEFAULT = new GameConfig(
            1252, 640, 3, 3.0, 1.6, 3_000, 5_000, 10_000
    );

    public Position spawnPosition() {
        return new Position(gameWidth / 2, gameHeight / 2);
    }
}