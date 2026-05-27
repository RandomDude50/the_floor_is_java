package game.repository;

import game.interfaces.ScoreRepository;
import java.nio.file.*;

public class FileScoreRepository implements ScoreRepository {

    private static final Path FILE =
            Paths.get(System.getProperty("user.home"), ".lava_highscore.txt");

    @Override
    public int load() {
        try {
            if (Files.exists(FILE))
                return Integer.parseInt(Files.readString(FILE).trim());
        } catch (Exception ignored) {}
        return 0;
    }

    @Override
    public void save(int score) {
        try {
            if (score > load()) Files.writeString(FILE, String.valueOf(score));
        } catch (Exception ignored) {}
    }

    @Override
    public boolean isNewRecord(int score) { return score > load(); }
}