package game.model;

import game.interfaces.ScoreFormula;

public record ScoreSnapshot(int score, String formattedTime) {

    public static ScoreSnapshot of(long elapsedSeconds) {
        return of(elapsedSeconds, ScoreFormula.DEFAULT);
    }

    public static ScoreSnapshot of(long elapsedSeconds, ScoreFormula formula) {
        return new ScoreSnapshot(
                formula.compute(elapsedSeconds),
                String.format("%d:%02d", elapsedSeconds / 60, elapsedSeconds % 60)
        );
    }

    /** Funzionale: ritorna lo snapshot col punteggio maggiore. */
    public ScoreSnapshot max(ScoreSnapshot other) {
        return this.score >= other.score ? this : other;
    }

    public static final ScoreSnapshot ZERO = ScoreSnapshot.of(0);
}