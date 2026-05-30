package game;

import game.config.GameConfig;
import game.interfaces.ScoreRepository;
import game.repository.FileScoreRepository;
import game.ui.GameColors;
import game.ui.UIFactory;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class MainApp extends Application {

    private Controller controller;

    @Override
    public void start(Stage stage) {
        GameConfig config   = GameConfig.DEFAULT;
        Pane       gamePane = new Pane();
        gamePane.setPrefSize(config.gameWidth(), config.gameHeight());

        BorderPane frame = new BorderPane(gamePane);
        frame.setStyle(
                "-fx-background-color: linear-gradient(to bottom, #2d5a1b, #4a8c2a, #2d5a1b);" +
                        "-fx-border-color: #8B4513; -fx-border-width: 6;" +
                        "-fx-border-radius: 8; -fx-background-radius: 8;"
        );
        frame.setPadding(new Insets(8));
        frame.setTop(buildHeader());

        // DIP: unico punto di wiring delle dipendenze concrete
        ScoreRepository repo = new FileScoreRepository();
        controller = new Controller(gamePane, config, repo);

        stage.setTitle("The Floor is Java!");
        stage.setScene(new Scene(frame, 1280, 720));
        stage.setResizable(false);
        stage.show();
        controller.start();
    }

    private HBox buildHeader() {
        var title = UIFactory.inlineText("  THE FLOOR IS JAVA!  ", 20, GameColors.GAME_OVER_TITLE);
        title.setStroke(GameColors.LIVES);
        title.setStrokeWidth(1);

        HBox header = new HBox(10);
        header.setPadding(new Insets(4, 12, 4, 12));
        header.setStyle("-fx-background-color: rgba(0,0,0,0.55); -fx-background-radius: 6;");
        header.setAlignment(Pos.CENTER_LEFT);

        try {
            var img = new Image(
                    getClass().getResourceAsStream("/Item_Lava_Bucket.png"), 32, 32, true, true);
            header.getChildren().addAll(new ImageView(img), title, new ImageView(img));
        } catch (Exception e) {
            header.getChildren().add(title); // FIX: fallback silenzioso
        }
        return header;
    }

    @Override public void stop()             { if (controller != null) controller.stop(); }
    public static void   main(String[] args) { launch(); }
}