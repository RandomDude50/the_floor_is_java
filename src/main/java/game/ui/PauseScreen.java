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

public class PauseScreen {

    private final Rectangle overlay;
    private final VBox       panel;
    private boolean          visible = false;

    public PauseScreen(Pane root, double gw, double gh) {
        overlay = new Rectangle(gw, gh);
        overlay.setFill(Color.web("#000000", 0.65));
        overlay.setVisible(false);

        Text title = new Text("⏸ PAUSED");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 72));
        title.setFill(Color.WHITE);
        title.setStroke(Color.web("#444444"));
        title.setStrokeWidth(2);
        title.setTextAlignment(TextAlignment.CENTER);

        Rectangle sep = new Rectangle(340, 3);
        sep.setFill(Color.web("#FFFFFF", 0.4));

        Text resume  = styledText("ESC  —  Resume", 30, "#00FF88");
        Text restart = styledText("R  —  Restart",  26, "#FFDD00");

        panel = new VBox(22);
        panel.setAlignment(Pos.CENTER);
        panel.setPrefWidth(gw);
        panel.setLayoutX(0);
        panel.setLayoutY(gh / 2 - 150);
        panel.setVisible(false);
        panel.getChildren().addAll(title, sep, resume, restart);

        root.getChildren().addAll(overlay, panel);
    }

    public void show() { visible = true;  overlay.setVisible(true);  panel.setVisible(true); }
    public void hide() { visible = false; overlay.setVisible(false); panel.setVisible(false); }
    public boolean isVisible() { return visible; }

    private Text styledText(String s, int size, String hex) {
        Text t = new Text(s);
        t.setFont(Font.font("Arial", FontWeight.BOLD, size));
        t.setFill(Color.web(hex));
        t.setTextAlignment(TextAlignment.CENTER);
        return t;
    }
}