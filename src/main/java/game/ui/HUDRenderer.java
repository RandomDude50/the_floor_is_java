package game.ui;

import game.interfaces.GameEventListener;
import game.model.ScoreSnapshot;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

public class HUDRenderer implements GameEventListener {

    private final Text scoreText;
    private final Text timerText;
    private final Text highScoreText;
    private final Text livesText;
    private final Text powerUpText;

    public HUDRenderer(Pane root, double gw, int initialHighScore) {
        // Score box
        root.getChildren().add(UIFactory.darkBox(300, 90, gw - 320, 10, GameColors.SCORE));
        scoreText     = UIFactory.boldText("Score: 0",                   20, GameColors.SCORE,      gw - 310, 38);
        timerText     = UIFactory.boldText("Time: 0:00",                 20, GameColors.TIMER,      gw - 310, 63);
        highScoreText = UIFactory.boldText("Best: " + initialHighScore,  18, GameColors.HIGH_SCORE, gw - 310, 90);
        root.getChildren().addAll(scoreText, timerText, highScoreText);

        // Lives box — usa testo semplice invece di emoji miste
        root.getChildren().add(UIFactory.darkBox(210, 45, gw - 225, 108, GameColors.LIVES));
        livesText   = UIFactory.boldText("♥ ♥ ♥", 28, GameColors.LIVES, gw - 215, 140);
        powerUpText = UIFactory.boldText("",       18, GameColors.POWER_UP_HUD, 20, 40);
        root.getChildren().addAll(livesText, powerUpText);
    }

    @Override
    public void onScoreUpdated(ScoreSnapshot s, int highScore, boolean newRecord) {
        scoreText.setText("Score: " + s.score());
        timerText.setText("Time: "  + s.formattedTime());
        highScoreText.setText("Best: " + highScore + (newRecord ? " ★" : ""));
        highScoreText.setFill(newRecord ? GameColors.HIGH_SCORE_NEW : GameColors.HIGH_SCORE);
    }

    @Override
    public void onLivesChanged(int lives) {
        // ♥ per vita rimasta, ♡ per vita persa — caratteri Unicode standard, nessun quadratino
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 3; i++)
            sb.append(i < lives ? "♥ " : "♡ ");
        livesText.setText(sb.toString().trim());
    }

    @Override
    public void onPowerUpStatus(String label) { powerUpText.setText(label); }
}