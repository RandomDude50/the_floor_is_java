package game.interfaces;

/**
 * Contratto per qualsiasi HazardMap che supporta la pulizia forzata
 * con conseguente aumento permanente della velocità di espansione.
 * Separato da HazardMap per ISP: non tutte le mappe devono supportarlo.
 */
public interface LavaClearer {
    void clearAndAccelerateLava();
}