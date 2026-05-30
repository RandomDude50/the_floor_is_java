package game;

import game.config.GameConfig;
import game.interfaces.Movable;
import game.model.Position;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.Objects;

public class Player implements Movable {

    private static final double WIDTH           = 30;
    private static final double HEIGHT          = 52;
    private static final double RADIUS          = WIDTH / 2;
    private static final int    ANIM_TICKS      = 8;
    private static final double MOONWALK_BONUS  = 1.10; // +10% velocità

    private static final Image[] FRAMES = {
            loadImage("/player_frame_0.png"),
            loadImage("/player_frame_1.png"),
            loadImage("/player_frame_2.png")
    };

    private Position position;
    private double   speed;
    private final double baseSpeed;
    private final double speedMultiplier;
    private final long   invincibilityMs;
    private final long   speedBoostMs;

    private double  movementModifier = 1.0; // reset ogni frame
    private boolean moonwalking      = false;
    private boolean moving           = false;
    private int     animTick         = 0;
    private int     frameIdx         = 0;

    private boolean invincible      = false;
    private long    invincibleUntil = 0;
    private boolean speedBoosted    = false;
    private long    speedBoostUntil = 0;
    private int     blinkTick       = 0;

    private game.interfaces.ParticleEmitter particleEmitter = null;

    private final ImageView imageView;

    public Player(Position start, GameConfig cfg) {
        this.position        = start;
        this.baseSpeed       = cfg.playerSpeed();
        this.speed           = baseSpeed;
        this.speedMultiplier = cfg.speedMultiplier();
        this.invincibilityMs = cfg.invincibilityMs();
        this.speedBoostMs    = cfg.speedBoostMs();

        imageView = new ImageView(FRAMES[0]);
        imageView.setFitWidth(WIDTH);
        imageView.setFitHeight(HEIGHT);
        imageView.setX(position.x() - WIDTH  / 2);
        imageView.setY(position.y() - HEIGHT / 2);
    }

    private static Image loadImage(String path) {
        return new Image(
                Objects.requireNonNull(
                        Player.class.getResourceAsStream(path),
                        "Resource not found: " + path
                ),
                WIDTH, HEIGHT, false, true
        );
    }

    public void setParticleEmitter(game.interfaces.ParticleEmitter e) { particleEmitter = e; }

    // — Movable —

    @Override public void setMovementModifier(double m) { movementModifier = m; }

    @Override
    public void moveUp() {
        position = new Position(position.x(), position.y() - speed * movementModifier);
        moving = true;
    }

    @Override
    public void moveDown() {
        position = new Position(position.x(), position.y() + speed * movementModifier);
        moving = true;
    }

    @Override
    public void moveLeft() {
        // Moonwalk: +10% velocità quando si cammina a sinistra
        moonwalking = true;
        position = new Position(position.x() - speed * movementModifier * MOONWALK_BONUS, position.y());
        moving = true;
    }

    @Override
    public void moveRight() {
        position = new Position(position.x() + speed * movementModifier, position.y());
        moving = true;
    }

    @Override
    public void activateInvincibility() {
        invincible      = true;
        invincibleUntil = System.currentTimeMillis() + invincibilityMs;
        blinkTick       = 0;
    }

    @Override
    public void activateSpeedBoost() {
        speedBoosted    = true;
        speedBoostUntil = System.currentTimeMillis() + speedBoostMs;
        speed           = baseSpeed * speedMultiplier;
    }

    @Override public void activateShield() { activateInvincibility(); }

    @Override
    public void update() {
        long now = System.currentTimeMillis();

        if (invincible && now >= invincibleUntil) {
            invincible = false;
            imageView.setOpacity(1.0);
        }
        if (speedBoosted && now >= speedBoostUntil) {
            speedBoosted = false;
            speed        = baseSpeed;
        }
        if (invincible) imageView.setOpacity((++blinkTick / 5) % 2 == 0 ? 1.0 : 0.2);

        if (moving) {
            animTick++;
            if (animTick >= ANIM_TICKS) { animTick = 0; frameIdx = (frameIdx + 1) % FRAMES.length; }
            if (particleEmitter != null)
                particleEmitter.emitDust(position.x(), position.y() + HEIGHT / 2 - 8);
        } else {
            frameIdx = 0; animTick = 0;
        }
        imageView.setImage(FRAMES[frameIdx]);
        imageView.setX(position.x() - WIDTH  / 2);
        imageView.setY(position.y() - HEIGHT / 2);

        // reset stato frame
        moving           = false;
        moonwalking      = false;
        movementModifier = 1.0;
    }

    @Override public Position getPosition()             { return position; }
    @Override public void     setPosition(Position pos) { this.position = pos; }
    @Override public double   getRadius()               { return RADIUS; }
    @Override public boolean  isInvincible()            { return invincible; }
    @Override public boolean  isSpeedBoosted()          { return speedBoosted; }
    @Override public boolean  isMoonwalking()           { return moonwalking; }
    @Override public Node     getNode()                 { return imageView; }

    public double getX() { return position.x(); }
    public double getY() { return position.y(); }
}