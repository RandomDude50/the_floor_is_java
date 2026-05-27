package game;

import game.interfaces.Collidable;
import game.interfaces.PowerUpEffect;
import game.model.Position;
import javafx.scene.effect.Glow;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public abstract class PowerUp implements Collidable {

    private static final double RADIUS = 10;
    private final Circle  circle;
    private boolean collected = false;

    protected PowerUp(double x, double y) {
        circle = new Circle(RADIUS);
        circle.setCenterX(x);
        circle.setCenterY(y);
        circle.setFill(color());
        circle.setStroke(Color.WHITE);
        circle.setStrokeWidth(2);
        circle.setEffect(new Glow(0.8));
    }

    protected abstract Color         color();
    public    abstract PowerUpEffect effect();

    @Override
    public boolean collidesWith(Position pos, double radius) {
        // FIX: usa Position.distanceTo invece di calcolo inline
        return new Position(circle.getCenterX(), circle.getCenterY())
                .distanceTo(pos) < (RADIUS + radius);
    }

    public void    collect()     { collected = true; }
    public boolean isCollected() { return collected; }
    public Circle  getCircle()   { return circle; }
}