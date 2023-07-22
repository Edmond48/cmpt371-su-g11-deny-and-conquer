package com.vng_eleven.deny_and_conquer;

import com.vng_eleven.deny_and_conquer.game_logic.Board;
import com.vng_eleven.deny_and_conquer.server_client.Server;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.InetAddress;
import java.nio.channels.ScatteringByteChannel;

public class MainApplication extends Application {
    Scene mainScene;
    Board board;

    @Override
    public void start(Stage stage) {

        this.mainScene = new Scene(createCentredFrame(), 500, 500);
        showMenu();

        stage.setTitle("Deny 'n' Conquer");
        stage.setScene(mainScene);
        stage.show();
    }

    private void showMenu() {
        Button host = new Button("Host game");
        Button join = new Button("Join game");

        host.setOnMouseClicked(event -> {
            showHostWaitingRoom();
        });

        join.setOnMouseClicked(event -> {
            showClientWaitingRoom();
        });

        Parent root = createCentredFrame(host, join);
        host.setStyle("-fx-margin: 10px");
        join.setStyle("-fx-margin: 10px");
        this.mainScene.setRoot(root);
    }

    private void showGame(String IpAddress) {
        this.board = new Board(IpAddress);
        GridPane gridPane = board.getGridPane();

        Parent root = createCentredFrame(gridPane);

        this.mainScene.setRoot(root);
    }

    private void showHostWaitingRoom() {
        Text text = new Text();

        Parent root = createCentredFrame(text);
        Server server = Server.getInstance();
        server.start();
        String address = server.getServerIPAddress();
        text.setText("Waiting for players to join. " + "IP address of server is: " + address);

        this.mainScene.setRoot(root);
    }

    private void showClientWaitingRoom() {
        Text text = new Text();
        text.setText("Please enter the host's IP address");

        TextField addressField = new TextField();

        Button connectBtn = new Button("Connect");
        connectBtn.setOnMouseClicked(event -> {
            showGame(addressField.getText());
        });

        Parent root = createCentredFrame(text, addressField, connectBtn);

        this.mainScene.setRoot(root);
    }

    public static void main(String[] args) {
        launch();
    }

    ////////////////////////////////////////////////////////
    // helper
    private Parent createCentredFrame(Node... nodes) {
        HBox root = new HBox();
        root.setAlignment(Pos.CENTER);
        root.setPrefWidth(500);
        root.setPrefHeight(500);

        VBox inner = new VBox();
        inner.setAlignment(Pos.CENTER);

        for (Node node : nodes) {
            inner.getChildren().add(node);
        }
        root.getChildren().add(inner);
        return root;
    }
}