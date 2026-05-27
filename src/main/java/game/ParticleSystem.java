package game;

import game.interfaces.ParticleEmitter;
import game.interfaces.Updatable;
import game.model.Particle;
import game.model.Particle.ParticleType;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Sistema di particelle che gestisce zampilli di lava e polvere sotto il player.
 * Implementa Updatable (aggiornato ogni frame dal Controller) e ParticleEmitter
 * (riceve richieste di emissione da Lava e Player senza accoppiamento diretto).
 * Ogni particella è un record immutabile — lo stato evolve per sostituzione.
 */
public class ParticleSystem implements Updatable, ParticleEmitter {

    private final List<Particle> particles = new ArrayList<>();
    private final Canvas         canvas;
    private final Random         rng = new Random();

    public ParticleSystem(double width, double height) {
        canvas = new Canvas(width, height);
    }

    @Override
    public void emitLavaSpark(double x, double y) {
        for (int i = 0; i < 5; i++) {
            particles.add(new Particle(
                    x + rng.nextDouble(-6, 6),
                    y + rng.nextDouble(-6, 6),
                    rng.nextDouble(-1.2, 1.2),
                    rng.nextDouble(-3.5, -1.5),
                    1.0, 1.0,
                    ParticleType.LAVA_SPARK
            ));
        }
    }

    @Override
    public void emitDust(double x, double y) {
        for (int i = 0; i < 3; i++) {
            particles.add(new Particle(
                    x + rng.nextDouble(-6, 6),
                    y + rng.nextDouble(-2, 2),
                    rng.nextDouble(-1.0, 1.0),
                    rng.nextDouble(-0.3, 0.3),
                    0.5, 0.5,
                    ParticleType.DUST
            ));
        }
    }

    @Override
    public void update(long currentTimeMs) {
        particles.removeIf(Particle::isDead);
        particles.replaceAll(Particle::move);
        render();
    }

    private void render() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        for (Particle p : particles) {
            if (p.type() == ParticleType.LAVA_SPARK)
                gc.setFill(Color.rgb(255, rng.nextInt(60, 120), 0, p.alpha()));
            else
                gc.setFill(Color.rgb(110, 75, 35, p.alpha()));
            gc.fillRect(p.x(), p.y(), 3, 3);
        }
    }

    public Canvas getCanvas() { return canvas; }
}