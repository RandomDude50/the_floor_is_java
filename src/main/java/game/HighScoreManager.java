package game;

import java.io.*;
import java.nio.file.*;

/**
 * Gestisce il salvataggio e la lettura dell'high score su file locale.
 * Il file viene salvato nella home dell'utente: ~/.lava_highscore.txt
 */
public class HighScoreManager {

    private static final Path SCORE_FILE =
            Paths.get(System.getProperty("user.home"), ".lava_highscore.txt");

    public static int load() {
        try {
            if (Files.exists(SCORE_FILE)) {
                String content = Files.readString(SCORE_FILE).trim();
                return Integer.parseInt(content);
            }
        } catch (Exception ignored) {}
        return 0;
    }

    public static void save(int score) {
        try {
            int current = load();
            if (score > current) {
                Files.writeString(SCORE_FILE, String.valueOf(score));
            }
        } catch (Exception ignored) {}
    }

    public static boolean isNewRecord(int score) {
        return score > load();
    }
}