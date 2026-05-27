package game;

import game.interfaces.ScoreFormula;
import game.interfaces.Updatable;
import game.model.ScoreSnapshot;

public class ScoreTracker implements Updatable {

    private final ScoreFormula formula;
    private long gameStartTime;
    private long elapsedSeconds;

    public ScoreTracker(ScoreFormula formula) { this.formula = formula; }
    public ScoreTracker()                     { this(ScoreFormula.DEFAULT); }

    public void start(long startTime) { this.gameStartTime = startTime; }

    @Override
    public void update(long currentTimeMs) {
        elapsedSeconds = (currentTimeMs - gameStartTime) / 1000;
    }

    public ScoreSnapshot snapshot() {
        return ScoreSnapshot.of(elapsedSeconds, formula);
    }
}