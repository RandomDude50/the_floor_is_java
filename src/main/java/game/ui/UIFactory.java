package game.ui;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

public final class UIFactory {
    private UIFactory() {}

    public static Text boldText(String s, int size, Color fill, double x, double y) {
        Text t = new Text(s);
        t.setFont(Font.font("Arial", FontWeight.BOLD, size));
        t.setFill(fill);
        t.setX(x);
        t.setY(y);
        return t;
    }

    // FIX: aggiunto per MainApp header (nessuna posizione fissa)
    public static Text inlineText(String s, int size, Color fill) {
        return boldText(s, size, fill, 0, 0);
    }

    public static Text strokedText(String s, int size, Color fill,
                                   Color stroke, double sw, double x, double y) {
        Text t = boldText(s, size, fill, x, y);
        t.setStroke(stroke);
        t.setStrokeWidth(sw);
        return t;
    }

    public static Rectangle darkBox(double w, double h, double x, double y, Color stroke) {
        Rectangle r = new Rectangle(w, h);
        r.setFill(Color.web("#000000", 0.7));
        r.setStroke(stroke);
        r.setStrokeWidth(2);
        r.setX(x);
        r.setY(y);
        return r;
    }

    public static Rectangle fullOverlay(double w, double h) {
        Rectangle r = new Rectangle(w, h);
        r.setFill(Color.web("#000000", 0.6));
        return r;
    }
}