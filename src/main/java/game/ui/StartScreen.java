package game.ui;

import javafx.geometry.Pos;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

public class StartScreen {

    private final Rectangle overlay;
    private final VBox      panel;

    public StartScreen(Pane root, double gw, double gh) {
        overlay = new Rectangle(gw, gh);
        overlay.setFill(Color.web("#000000", 0.82));

        Text title = new Text("THE FLOOR IS JAVA!");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 54));
        title.setFill(Color.web("#FF4400"));
        title.setStroke(Color.web("#880000"));
        title.setStrokeWidth(2);
        title.setTextAlignment(TextAlignment.CENTER);

        Rectangle sep = new Rectangle(460, 3);
        sep.setFill(Color.web("#FF4400", 0.8));

        Text sub      = styledText("Survive the spreading lava!", 26, "#FFDD00");
        Text controls = styledText("Move with   W  A  S  D", 20, "#AAAAAA");
        Text esc      = styledText("ESC = Pause     R = Restart", 18, "#888888");
        Text hint     = styledText("Press SPACE to start", 34, "#00FF88");

        panel = new VBox(16);
        panel.setAlignment(Pos.CENTER);
        panel.setPrefWidth(gw);      // larghezza intera → CENTER funziona
        panel.setLayoutX(0);
        panel.setLayoutY(gh / 2 - 190);
        panel.getChildren().addAll(title, sep, sub, controls, esc, hint);

        root.getChildren().addAll(overlay, panel);
    }

    public void hide(Pane root) {
        root.getChildren().removeAll(overlay, panel);
    }

    private Text styledText(String s, int size, String hex) {
        Text t = new Text(s);
        t.setFont(Font.font("Arial", FontWeight.BOLD, size));
        t.setFill(Color.web(hex));
        t.setTextAlignment(TextAlignment.CENTER);
        return t;
    }
}