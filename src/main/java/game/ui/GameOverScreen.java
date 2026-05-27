package game.ui;

import game.interfaces.GameEventListener;
import game.model.ScoreSnapshot;
import javafx.geometry.Pos;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

public class GameOverScreen implements GameEventListener {

    private final Pane   root;
    private final double gw, gh;

    public GameOverScreen(Pane root, double gw, double gh) {
        this.root = root;
        this.gw   = gw;
        this.gh   = gh;
    }

    @Override
    public void onGameOver(ScoreSnapshot s, int highScore, boolean newRecord) {
        Rectangle overlay = new Rectangle(gw, gh);
        overlay.setFill(Color.web("#000000", 0.78));
        root.getChildren().add(overlay);

        Text title = new Text("GAME OVER!");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 72));
        title.setFill(Color.web("#FF2222"));
        title.setStroke(Color.web("#880000"));
        title.setStrokeWidth(2);
        title.setTextAlignment(TextAlignment.CENTER);

        Rectangle sep = new Rectangle(440, 3);
        sep.setFill(Color.web("#FF4400", 0.8));

        Text score   = styledText("Final Score: " + s.score(), 46, "#FFFF00");
        Text time    = styledText("Time: " + s.formattedTime(), 34, "#00FF00");
        Text best    = newRecord
                ? styledText("★ NEW HIGH SCORE! ★",  30, "#FFD700")
                : styledText("Best: " + highScore,    26, "#FFD700");
        Text restart = styledText("Press R to restart", 28, "#00FF88");

        VBox panel = new VBox(18);
        panel.setAlignment(Pos.CENTER);
        panel.setPrefWidth(gw);   // larghezza piena → tutto centrato
        panel.setLayoutX(0);
        panel.setLayoutY(gh / 2 - 200);
        panel.getChildren().addAll(title, sep, score, time, best, restart);

        root.getChildren().add(panel);
    }

    private Text styledText(String s, int size, String hex) {
        Text t = new Text(s);
        t.setFont(Font.font("Arial", FontWeight.BOLD, size));
        t.setFill(Color.web(hex));
        t.setTextAlignment(TextAlignment.CENTER);
        return t;
    }
}