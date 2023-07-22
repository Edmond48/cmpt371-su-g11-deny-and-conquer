package com.vng_eleven.deny_and_conquer;

import com.vng_eleven.deny_and_conquer.game_logic.Board;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public class MainApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(MainController.class.getResource("game-view.fxml"));

        Board board = Board.getInstance();
        GridPane gridPane = board.getGridPane();

        VBox root = loader.load();
        HBox hbox = new HBox();
        hbox.setAlignment(Pos.CENTER);

        root.getChildren().add(hbox);
        hbox.getChildren().add(gridPane);


        Scene scene = new Scene(root, 400, 400);

        stage.setTitle("Main game");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}