package game;

import game.interfaces.Collectible;
import game.interfaces.PowerUpEffect;
import game.model.Position;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.Objects;

/**
 * Power-up raccoglibile con rappresentazione visiva tramite ImageView.
 * Gestisce stato (collected), collisione per distanza euclidea e ciclo di vita
 * del nodo JavaFX. Le sottoclassi devono solo dichiarare imagePath() (risorsa PNG)
 * e effect() (cosa succede al Movable che lo raccoglie). Il boilerplate di
 * caricamento immagine, dimensionamento e posizionamento è centralizzato qui
 * per evitare duplicazione tra SpeedPowerUp, ShieldPowerUp e futuri tipi.
 */

public abstract class AbstractPowerUp implements Collectible {

    private static final double SIZE = 32;
    private final ImageView imageView;
    private boolean collected = false;

    protected AbstractPowerUp(double x, double y) {
        Image img = new Image(
                Objects.requireNonNull(getClass().getResourceAsStream(imagePath())), SIZE, SIZE, true, true);
        imageView = new ImageView(img);
        imageView.setX(x - SIZE / 2);
        imageView.setY(y - SIZE / 2);
    }

    protected abstract String imagePath(); // es. "/speed_powerup.png"
    public    abstract PowerUpEffect effect();

    @Override
    public boolean collidesWith(Position pos, double radius) {
        double cx = imageView.getX() + SIZE / 2;
        double cy = imageView.getY() + SIZE / 2;
        return new Position(cx, cy).distanceTo(pos) < (SIZE / 2 + radius);
    }

    @Override public Node    getNode()     { return imageView; }
    @Override public void    collect()     { collected = true; }
    @Override public boolean isCollected() { return collected; }
}