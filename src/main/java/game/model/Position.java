package game.model;

public record Position(double x, double y) {

    public Position clampedWithin(double minX, double maxX, double minY, double maxY) {
        return new Position(
                Math.max(minX, Math.min(x, maxX)),
                Math.max(minY, Math.min(y, maxY))
        );
    }

    public double distanceTo(Position other) {
        double dx = x - other.x();
        double dy = y - other.y();
        return Math.sqrt(dx * dx + dy * dy);
    }
}