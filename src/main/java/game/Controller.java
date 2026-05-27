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

    private final Pane          root;
    private final GameConfig    config;
    private final ScoreRepository scoreRepo;

    private Movable         player;
    private HazardMap       hazardMap;
    private GameLoop        engine;
    private ScoreTracker    scoreTracker;
    private LivesManager    livesManager;
    private PowerUpManager  powerUpManager;
    private ParticleSystem  particles;

    private List<Updatable>          updatables;
    private List<GameEventListener>  listeners;

    private AnimationTimer gameLoopTimer;

    // stati del gioco
    private enum GameState { START, PLAYING, PAUSED, GAME_OVER }
    private GameState state = GameState.START;

    private int        highScore;
    private PauseScreen pauseScreen;

    public Controller(Pane root, GameConfig config, ScoreRepository scoreRepo) {
        this.root      = root;
        this.config    = config;
        this.scoreRepo = scoreRepo;
        this.highScore = scoreRepo.load();

        setupKeyHandlers();
        buildGame();
        showStartScreen();
    }

    // ------------------------------------------------------------------ //
    //  Setup
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
        powerUpManager = new PowerUpManager(root, config, player, (LavaClearer) hazardMap, hazardMap);

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
            input.keyPressed(e.getCode().getCode());
        });
        root.setOnKeyReleased(e -> input.keyReleased(e.getCode().getCode()));
        root.setFocusTraversable(true);
        root.requestFocus();
    }

    private void setupKeyHandlers() {
        // handler globale sempre attivo (non dipende dall'InputManager)
    }

    private void handleGlobalKeys(KeyCode code) {
        switch (state) {
            case START -> {
                if (code == KeyCode.SPACE) startPlaying();
            }
            case PLAYING -> {
                if (code == KeyCode.ESCAPE) pause();
                if (code == KeyCode.R)      restart();
            }
            case PAUSED -> {
                if (code == KeyCode.ESCAPE) resume();
                if (code == KeyCode.R)      restart();
            }
            case GAME_OVER -> {
                if (code == KeyCode.R)      restart();
            }
        }
    }

    // ------------------------------------------------------------------ //
    //  Transizioni di stato
    // ------------------------------------------------------------------ //

    private void showStartScreen() {
        state = GameState.START;
        new StartScreen(root, config.gameWidth(), config.gameHeight());
    }

    private void startPlaying() {
        // rimuove gli ultimi 2 nodi aggiunti da StartScreen (overlay + panel)
        int size = root.getChildren().size();
        if (size >= 2) root.getChildren().remove(size - 2, size);
        state = GameState.PLAYING;

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
        pauseScreen.show();
    }

    private void resume() {
        state = GameState.PLAYING;
        pauseScreen.hide();
        engine.start();
        startGameLoop();
    }

    private void restart() {
        state = GameState.PLAYING;
        if (gameLoopTimer != null) gameLoopTimer.stop();
        engine.stop();
        buildGame();
        startPlaying();
    }

    // ------------------------------------------------------------------ //
    //  Game loop
    // ------------------------------------------------------------------ //

    public void start() {
        // il gioco parte dalla start screen, non automaticamente
    }

    private void startGameLoop() {
        gameLoopTimer = new AnimationTimer() {
            @Override public void handle(long ignored) {
                if (state != GameState.PLAYING) return;
                long currentTime = System.currentTimeMillis();

                hazardMap.update(currentTime, player.getPosition());
                updatables.forEach(u -> u.update(currentTime));

                ScoreSnapshot snap   = scoreTracker.snapshot();
                boolean       newRec = snap.score() > highScore;
                if (newRec) highScore = snap.score();

                fire(l -> l.onScoreUpdated(snap, highScore, newRec));
                fire(l -> l.onPowerUpStatus(powerUpManager.activeStatusLabel()));

                if (!player.isInvincible() && hazardMap.isHazardous(player.getPosition()))
                    loseLife();
            }
        };
        gameLoopTimer.start();
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