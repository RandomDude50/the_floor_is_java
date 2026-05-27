package game.model;

public record Particle(
        double x, double y,
        double vx, double vy,
        double life, double maxLife,
        ParticleType type
) {
    public enum ParticleType { LAVA_SPARK, DUST }

    public Particle move() {
        return new Particle(
                x + vx, y + vy,
                vx, vy * 0.88,
                life - 0.04, maxLife,
                type
        );
    }

    public boolean isDead() { return life <= 0; }

    public double alpha() { return Math.max(0, life / maxLife); }
}