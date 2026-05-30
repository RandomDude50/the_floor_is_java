package game;

import game.interfaces.ScoreFormula;
import game.interfaces.Updatable;
import game.model.ScoreSnapshot;

public class ScoreTracker implements Updatable {

    private final ScoreFormula formula;
    private long    gameStartTime  = 0;
    private long    elapsedSeconds = 0;
    private long    totalPausedMs  = 0;
    private long    pauseStartTime = 0;
    private long    moonwalkMs     = 0;
    private long    lastUpdateMs   = 0;
    private boolean moonwalkActive = false;

    public ScoreTracker(ScoreFormula formula) { this.formula = formula; }
    public ScoreTracker()                     { this(ScoreFormula.DEFAULT); }

    public void start(long startTime) {
        gameStartTime  = startTime;
        lastUpdateMs   = startTime;
        totalPausedMs  = 0;
        pauseStartTime = 0;
        moonwalkMs     = 0;
        elapsedSeconds = 0;
    }

    /** Chiamato quando il gioco viene messo in pausa. */
    public void onPause(long currentTimeMs) {
        pauseStartTime = currentTimeMs;
    }

    /** Chiamato quando il gioco viene ripreso dalla pausa. */
    public void onResume(long currentTimeMs) {
        if (pauseStartTime > 0) {
            totalPausedMs += currentTimeMs - pauseStartTime;
            pauseStartTime = 0;
        }
        lastUpdateMs = currentTimeMs; // evita spike nel moonwalk accumulator
    }

    public void setMoonwalkActive(boolean active) { moonwalkActive = active; }

    @Override
    public void update(long currentTimeMs) {
        long effective = currentTimeMs - gameStartTime - totalPausedMs;
        elapsedSeconds = effective / 1000;

        // Accumula moonwalk time (cap 200ms per evitare spike)
        if (moonwalkActive && lastUpdateMs > 0) {
            long delta = currentTimeMs - lastUpdateMs;
            if (delta < 200) moonwalkMs += delta;
        }
        lastUpdateMs = currentTimeMs;
    }

    public ScoreSnapshot snapshot() {
        int  base          = formula.compute(elapsedSeconds);
        long moonwalkSecs  = moonwalkMs / 1000;
        int  moonwalkBonus = (int)(moonwalkSecs * 60 * 0.20); // +20% del rate base
        return new ScoreSnapshot(base + moonwalkBonus,
                String.format("%d:%02d", elapsedSeconds / 60, elapsedSeconds % 60));
    }
}