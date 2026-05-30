package game;

import game.config.GameConfig;
import game.interfaces.Collectible;
import game.interfaces.HazardMap;
import game.interfaces.LavaClearer;
import game.interfaces.Movable;
import game.interfaces.Updatable;
import game.model.Position;
import javafx.scene.layout.Pane;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

public class PowerUpManager implements Updatable {

    private final List<Collectible> active = new ArrayList<>();
    private final Pane              root;
    private final Movable           target;
    private final LavaClearer       lava;
    private final HazardMap         hazardMap;
    private final double            gw, gh;
    private final long              spawnInterval;
    private final Random            rng = new Random();
    private long                    lastSpawn;
    private long                    lastClearSpawn;
    private long                    nextClearInterval;

    private static final int MAX_SPAWN_ATTEMPTS = 20;

    public PowerUpManager(Pane root, GameConfig cfg, Movable target,
                          LavaClearer lava, HazardMap hazardMap) {
        this.root          = root;
        this.target        = target;
        this.lava          = lava;
        this.hazardMap     = hazardMap;
        this.gw            = cfg.gameWidth();
        this.gh            = cfg.gameHeight();
        this.spawnInterval = cfg.powerUpSpawnMs();
    }

    public void init(long startTime) {
        lastSpawn      = startTime;
        lastClearSpawn = startTime;
        nextClearInterval = generateClearInterval();
    }

    @Override
    public void update(long currentTimeMs) {
        if (currentTimeMs - lastSpawn >= spawnInterval) {
            spawnRegular();
            lastSpawn = currentTimeMs;
        }
        if (currentTimeMs - lastClearSpawn >= nextClearInterval) {
            spawnClearLava();
            lastClearSpawn    = currentTimeMs;
            nextClearInterval = generateClearInterval();
        }
        collectHits();
    }

    private long generateClearInterval() {
        return 75_000 + rng.nextInt(20_001); // 75–95 secondi
    }

    private void spawnRegular() {
        safPosition()
                .map(pos -> PowerUpFactory.randomRegular(pos.x(), pos.y(), rng))
                .ifPresent(this::addToScene);
    }

    private void spawnClearLava() {
        safPosition()
                .map(pos -> new ClearLavaPowerUp(pos.x(), pos.y(), lava))
                .ifPresent(this::addToScene);
    }

    /**
     * Cerca una posizione sicura (non sulla lava) con MAX_SPAWN_ATTEMPTS tentativi.
     * Ritorna Optional.empty() se non trova una posizione libera.
     */
    private Optional<Position> safPosition() {
        double m = 50;
        for (int i = 0; i < MAX_SPAWN_ATTEMPTS; i++) {
            Position candidate = new Position(
                    m + rng.nextDouble() * (gw - m * 2),
                    m + rng.nextDouble() * (gh - m * 2)
            );
            // FIX: controlla anche stage 1 e 2, non solo il letale
            if (!hazardMap.isAnyHazard(candidate)) return Optional.of(candidate);
        }
        return Optional.empty();
    }

    private void addToScene(Collectible pu) {
        active.add(pu);
        root.getChildren().add(pu.getNode());
    }

    private void collectHits() {
        active.removeIf(pu -> {
            if (!pu.collidesWith(target.getPosition(), target.getRadius())) return false;
            pu.effect().applyTo(target);
            root.getChildren().remove(pu.getNode());
            return true;
        });
    }

    public String activeStatusLabel() {
        boolean sp = target.isSpeedBoosted();
        boolean sh = target.isInvincible();
        if (sp && sh) return "⚡ SPEED  🛡 SHIELD";
        if (sp)       return "⚡ SPEED BOOST!";
        if (sh)       return "🛡 SHIELD ACTIVE!";
        return "";
    }
}