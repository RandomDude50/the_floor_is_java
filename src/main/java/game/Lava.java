package game;

import game.interfaces.HazardMap;
import game.interfaces.LavaClearer;
import game.interfaces.ParticleEmitter;
import game.model.Position;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import java.util.Random;

/**
 * Implementazione concreta di AbstractTileHazard.
 *
 * Stage della griglia:
 *   0 = sicuro (verde prato)
 *   1 = arancione — lava nascente
 *   2 = rosso     — lava calda
 *   3 = bordeaux  — lava piena, letale (lethalStage() = 3)
 *
 * Espansione (spread):
 *   - ogni UPDATE_INTERVAL ms, ogni tile > 0 può avanzare di stage (probabilità
 *     crescente con difficulty) ed espandersi ai 4 vicini ortogonali
 *   - ogni CHAZZE_INTERVAL ms (random 5–8s) nasce una nuova chiazza casuale
 *   - difficulty aumenta linearmente col tempo (max 3.0x dopo 4 minuti)
 *   - difficultyPenalty è accumulabile: ogni reset lava aumenta la velocità base
 */
public class Lava extends AbstractTileHazard implements HazardMap, LavaClearer {

    private final Canvas        canvas;
    private final int           width, height;
    private final WritableImage frameImage;
    private final Random        random;
    private final int[][]       grassVariant;
    private ParticleEmitter     particleEmitter = null;

    private long   lastUpdateTime    = 0;
    private long   gameStartTime     = 0;
    private long   lastNewChazzeTime = 0;
    private long   nextChazzeInterval;
    private int    currentWave       = 0;
    private double difficultyPenalty = 1.0;

    private static final long UPDATE_INTERVAL = 2000;
    private static final int  TILE_SIZE       = 8;

    private static final int[] STAGE_ARGB = {
            0xFF7EC850,   // 0 = verde prato
            0xFFFFAA00,   // 1 = arancione
            0xFFDD3300,   // 2 = rosso
            0xFF880000    // 3 = bordeaux (letale)
    };

    private static final int[] GRASS_VARIANTS = {
            0xFF7EC850,
            0xFF82CC52,
            0xFF78C44C,
            0xFF86D056,
            0xFF7AC84E
    };

    public Lava(double width, double height) {
        super((int) width, (int) height, TILE_SIZE);
        this.width        = (int) width;
        this.height       = (int) height;
        this.canvas       = new Canvas(width, height);
        this.frameImage   = new WritableImage(this.width, this.height);
        this.random       = new Random(System.currentTimeMillis());
        this.grassVariant = initGrassVariants();
        generateNextChazzeInterval();
    }

    public void setParticleEmitter(ParticleEmitter emitter) {
        this.particleEmitter = emitter;
    }

    private int[][] initGrassVariants() {
        int[][] g = new int[tilesX][tilesY];
        for (int ty = 0; ty < tilesY; ty++)
            for (int tx = 0; tx < tilesX; tx++)
                g[tx][ty] = random.nextInt(GRASS_VARIANTS.length);
        return g;
    }

    // — AbstractTileHazard —

    @Override
    protected int lethalStage() { return 3; }

    @Override
    protected void spread(long currentTimeMs) {
        currentWave++;
        long   elapsed    = (currentTimeMs - gameStartTime) / 1000;
        double difficulty = Math.min(1.0 + (elapsed / 120.0) * 0.5, 3.0);
        difficulty *= difficultyPenalty;

        if (currentTimeMs - lastNewChazzeTime >= nextChazzeInterval) {
            createNewChazze();
            lastNewChazzeTime = currentTimeMs;
            generateNextChazzeInterval();
        }

        int[][] snapshot = snapshot();

        for (int ty = 0; ty < tilesY; ty++) {
            for (int tx = 0; tx < tilesX; tx++) {
                if (snapshot[tx][ty] > 0) {
                    advanceStage(tx, ty, difficulty);
                    expandToNeighbor(tx,     ty - 1, snapshot, difficulty);
                    expandToNeighbor(tx,     ty + 1, snapshot, difficulty);
                    expandToNeighbor(tx - 1, ty,     snapshot, difficulty);
                    expandToNeighbor(tx + 1, ty,     snapshot, difficulty);
                }
            }
        }
    }

    // — HazardMap —

    @Override
    public void update(long currentTimeMs, Position trackedPosition) {
        if (currentTimeMs - lastUpdateTime >= UPDATE_INTERVAL) {
            spread(currentTimeMs);
            lastUpdateTime = currentTimeMs;
        }
        render();
    }

    @Override
    public boolean isAnyHazard(Position pos) {
        int tx = (int) (pos.x() / tileSize);
        int ty = (int) (pos.y() / tileSize);
        if (tx < 0 || tx >= tilesX || ty < 0 || ty >= tilesY) return false;
        return grid[tx][ty] > 0;
    }

    @Override
    public Node getNode() { return canvas; }

    @Override
    public void setGameStartTime(long startTime) {
        this.gameStartTime     = startTime;
        this.lastNewChazzeTime = startTime;
    }

    // — LavaClearer —

    @Override
    public void clearAndAccelerateLava() {
        // Azzera tutta la griglia
        for (int ty = 0; ty < tilesY; ty++)
            for (int tx = 0; tx < tilesX; tx++)
                grid[tx][ty] = 0;

        // Malus accumulabile sulla velocità di espansione
        difficultyPenalty *= 1.4;

        long now = System.currentTimeMillis();

        // Resetta gameStartTime → difficulty riparte da 1.0 (organica come inizio)
        // difficultyPenalty mantiene il malus accumulato
        gameStartTime = now;

        // Resetta i timer delle chiazze → prima chiazza dopo 5-8s come a inizio partita
        lastNewChazzeTime = now;
        generateNextChazzeInterval();

        // Resetta lastUpdateTime → spread() non parte immediatamente
        // ma aspetta il primo UPDATE_INTERVAL come all'avvio
        lastUpdateTime = now;
    }

    // — Logica interna —

    private int[][] snapshot() {
        int[][] snap = new int[tilesX][tilesY];
        for (int ty = 0; ty < tilesY; ty++)
            for (int tx = 0; tx < tilesX; tx++)
                snap[tx][ty] = grid[tx][ty];
        return snap;
    }

    private void advanceStage(int tx, int ty, double difficulty) {
        if (grid[tx][ty] == 1 && random.nextDouble() < 0.3 * difficulty) {
            grid[tx][ty] = 2;
        } else if (grid[tx][ty] == 2 && random.nextDouble() < 0.4 * difficulty) {
            grid[tx][ty] = 3;
            if (particleEmitter != null) {
                particleEmitter.emitLavaSpark(
                        tx * tileSize + tileSize / 2.0,
                        ty * tileSize + tileSize / 2.0
                );
            }
        }
    }

    private void createNewChazze() {
        int rx = random.nextInt(tilesX);
        int ry = random.nextInt(tilesY);
        if (grid[rx][ry] == 0) grid[rx][ry] = 1;
    }

    private void expandToNeighbor(int tx, int ty, int[][] snap, double difficulty) {
        if (tx < 0 || tx >= tilesX || ty < 0 || ty >= tilesY) return;
        if (snap[tx][ty] == 0 && random.nextDouble() < 0.5 * difficulty)
            grid[tx][ty] = 1;
    }

    private void generateNextChazzeInterval() {
        nextChazzeInterval = 5000 + random.nextInt(3000); // 5–8 secondi
    }

    private void render() {
        PixelWriter pw = frameImage.getPixelWriter();
        for (int sy = 0; sy < height; sy++) {
            int ty = sy / tileSize;
            for (int sx = 0; sx < width; sx++) {
                int tx    = sx / tileSize;
                int stage = (tx < tilesX && ty < tilesY) ? grid[tx][ty] : 0;
                int argb  = (stage == 0)
                        ? GRASS_VARIANTS[grassVariant[tx][ty]]
                        : STAGE_ARGB[stage];
                pw.setArgb(sx, sy, argb);
            }
        }
        canvas.getGraphicsContext2D().drawImage(frameImage, 0, 0);
    }
}