package game;

import game.config.GameConfig;
import game.interfaces.*;
import game.model.ScoreSnapshot;
import game.ui.GameOverScreen;
import game.ui.HUDRenderer;
import game.ui.PauseScreen;
import game.ui.StartScreen;
import javafx.animation.AnimationTimer;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;

import java.util.List;
import java.util.function.Consumer;

public class Controller {

    private final Pane            root;
    private final GameConfig      config;
    private final ScoreRepository scoreRepo;

    private Movable        player;
    private HazardMap      hazardMap;
    private GameLoop       engine;
    private ScoreTracker   scoreTracker;
    private LivesManager   livesManager;
    private PowerUpManager powerUpManager;
    private ParticleSystem particles;
    private PauseScreen    pauseScreen;
    private StartScreen    startScreen;

    private List<Updatable>         updatables;
    private List<GameEventListener> listeners;

    private AnimationTimer gameLoopTimer;

    private enum GameState { START, PLAYING, PAUSED, GAME_OVER }
    private GameState state = GameState.START;

    private int     highScore;
    private boolean sessionRecord = false; // rimane true una volta battuto il record

    public Controller(Pane root, GameConfig config, ScoreRepository scoreRepo) {
        this.root      = root;
        this.config    = config;
        this.scoreRepo = scoreRepo;
        this.highScore = scoreRepo.load();
        buildGame();
        setupGlobalKeyHandlers();
    }

    // ------------------------------------------------------------------ //
    //  Costruzione del gioco
    // ------------------------------------------------------------------ //

    private void buildGame() {
        root.getChildren().clear();

        InputManager input = new InputManager();

        player    = new Player(config.spawnPosition(), config);
        hazardMap = new Lava(config.gameWidth(), config.gameHeight());
        particles = new ParticleSystem(config.gameWidth(), config.gameHeight());

        ((Lava)   hazardMap).setParticleEmitter(particles);
        ((Player) player)   .setParticleEmitter(particles);

        engine         = new Engine(player, config, input);
        scoreTracker   = new ScoreTracker();
        livesManager   = new LivesManager(config.initialLives());
        powerUpManager = new PowerUpManager(root, config, player,
                (LavaClearer) hazardMap, hazardMap);

        updatables = List.of(scoreTracker, powerUpManager, particles);

        // z-order: lava → particelle → player → HUD → pausa
        root.getChildren().add(hazardMap.getNode());
        root.getChildren().add(particles.getCanvas());
        root.getChildren().add(player.getNode());

        listeners = List.of(
                new HUDRenderer(root, config.gameWidth(), highScore),
                new GameOverScreen(root, config.gameWidth(), config.gameHeight())
        );

        pauseScreen = new PauseScreen(root, config.gameWidth(), config.gameHeight());

        root.setOnKeyPressed(e -> {
            handleGlobalKeys(e.getCode());
            if (state == GameState.PLAYING) input.keyPressed(e.getCode().getCode());
        });
        root.setOnKeyReleased(e -> input.keyReleased(e.getCode().getCode()));
        root.setFocusTraversable(true);
        root.requestFocus();
    }

    private void setupGlobalKeyHandlers() { /* gestito in buildGame */ }

    private void handleGlobalKeys(KeyCode code) {
        switch (state) {
            case START     -> { if (code == KeyCode.SPACE)  startPlaying(); }
            // FIX: R bloccato durante PLAYING — solo in pausa o game over
            case PLAYING   -> { if (code == KeyCode.ESCAPE) pause(); }
            case PAUSED    -> {
                if (code == KeyCode.ESCAPE) resume();
                if (code == KeyCode.R)      restart();
            }
            case GAME_OVER -> { if (code == KeyCode.R)      restart(); }
        }
    }

    // ------------------------------------------------------------------ //
    //  Transizioni di stato
    // ------------------------------------------------------------------ //

    public void start() {
        state       = GameState.START;
        startScreen = new StartScreen(root, config.gameWidth(), config.gameHeight());
    }

    private void startPlaying() {
        if (startScreen != null) { startScreen.hide(root); startScreen = null; }
        state         = GameState.PLAYING;
        sessionRecord = false;
        beginGameSession();
    }

    private void beginGameSession() {
        long now = System.currentTimeMillis();
        engine.start();
        scoreTracker.start(now);
        hazardMap.setGameStartTime(now);
        powerUpManager.init(now);
        startGameLoop();
    }

    private void pause() {
        state = GameState.PAUSED;
        engine.stop();
        if (gameLoopTimer != null) gameLoopTimer.stop();
        scoreTracker.onPause(System.currentTimeMillis()); // FIX: ferma il timer
        pauseScreen.show();
    }

    private void resume() {
        state = GameState.PLAYING;
        pauseScreen.hide();
        scoreTracker.onResume(System.currentTimeMillis()); // FIX: riprende il timer
        engine.start();
        startGameLoop();
    }

    private void restart() {
        if (gameLoopTimer != null) gameLoopTimer.stop();
        engine.stop();
        state         = GameState.PLAYING;
        sessionRecord = false;
        buildGame();
        beginGameSession(); // FIX: non chiama startPlaying() — non rimuove nodi StartScreen
    }

    // ------------------------------------------------------------------ //
    //  Game loop
    // ------------------------------------------------------------------ //

    private void startGameLoop() {
        gameLoopTimer = new AnimationTimer() {
            @Override public void handle(long ignored) {
                if (state != GameState.PLAYING) return;
                long now = System.currentTimeMillis();

                hazardMap.update(now, player.getPosition());
                updatables.forEach(u -> u.update(now));

                // Moonwalk: notifica ScoreTracker
                scoreTracker.setMoonwalkActive(player.isMoonwalking());

                ScoreSnapshot snap = scoreTracker.snapshot();

                // FIX: sessionRecord rimane true per tutta la partita una volta battuto
                if (snap.score() > highScore) {
                    highScore     = snap.score();
                    sessionRecord = true;
                }

                fire(l -> l.onScoreUpdated(snap, highScore, sessionRecord));
                fire(l -> l.onPowerUpStatus(buildStatusLabel()));

                if (!player.isInvincible() && hazardMap.isHazardous(player.getPosition()))
                    loseLife();
            }
        };
        gameLoopTimer.start();
    }

    private String buildStatusLabel() {
        String powerUp   = powerUpManager.activeStatusLabel();
        String moonwalk  = player.isMoonwalking() ? "MOONWALK!" : "";
        if (powerUp.isEmpty()) return moonwalk;
        if (moonwalk.isEmpty()) return powerUp;
        return powerUp + "  " + moonwalk;
    }

    private void loseLife() {
        livesManager.loseLife();
        fire(l -> l.onLivesChanged(livesManager.getLives()));

        if (livesManager.isGameOver()) {
            state = GameState.GAME_OVER;
            ScoreSnapshot snap   = scoreTracker.snapshot();
            boolean       newRec = scoreRepo.isNewRecord(snap.score());
            scoreRepo.save(snap.score());
            fire(l -> l.onGameOver(snap, highScore, newRec));
            engine.stop();
            if (gameLoopTimer != null) gameLoopTimer.stop();
        } else {
            player.setPosition(config.spawnPosition());
            player.activateInvincibility();
        }
    }

    private void fire(Consumer<GameEventListener> event) { listeners.forEach(event); }

    public void stop() {
        engine.stop();
        if (gameLoopTimer != null) gameLoopTimer.stop();
    }
}