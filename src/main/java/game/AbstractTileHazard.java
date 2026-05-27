package game;

import game.interfaces.HazardMap;
import game.model.Position;

/**
 *  * HazardMap basata su griglia di tile con stage discreti (0 = sicuro, N = letale).
 *  * Fornisce: struttura dati grid[][], calcolo tilesX/Y, implementazione di isHazardous().
 *  * Delega alle sottoclassi: spread() (come si espande il pericolo nel tempo)
 *  * e lethalStage() (da quale stage il tile uccide il player).
 *  * Lava è l'unica sottoclasse ora; "Ice" o "Acid" potrebbero estenderla senza
 *  * toccare Controller, MovementSystem o qualsiasi altra classe del sistema.
 *
 *
 * Struttura dati:
 *   grid[tx][ty] = stage corrente del tile (tx, ty), da 0 (sicuro) a N (letale)
 *   tileSize     = lato in pixel di ogni tile (es. 8px)
 *   tilesX/Y     = numero di tile orizzontali/verticali che coprono lo schermo
 *
 * Contratto per le sottoclassi:
 *   - spread()      : definisce come e quando i tile avanzano di stage
 *   - lethalStage() : soglia a partire dalla quale un tile è letale per il player
 */

public abstract class AbstractTileHazard implements HazardMap {

    protected final int[][] grid;
    protected final int     tileSize;
    protected final int     tilesX;
    protected final int     tilesY;

    protected AbstractTileHazard(int width, int height, int tileSize) {
        this.tileSize = tileSize;
        this.tilesX   = (int) Math.ceil((double) width  / tileSize);
        this.tilesY   = (int) Math.ceil((double) height / tileSize);
        this.grid     = new int[tilesX][tilesY];
    }

    /** Logica di espansione — implementata dalla sottoclasse (es. Lava). */
    protected abstract void spread(long currentTimeMs);

    /** Stage minimo considerato letale (es. 3 su 0–3). */
    protected abstract int lethalStage();

    @Override
    public boolean isHazardous(Position pos) {
        int tx = (int) (pos.x() / tileSize);
        int ty = (int) (pos.y() / tileSize);
        if (tx < 0 || tx >= tilesX || ty < 0 || ty >= tilesY) return false;
        return grid[tx][ty] >= lethalStage();
    }
}