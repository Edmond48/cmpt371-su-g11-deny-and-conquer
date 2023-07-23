package com.vng_eleven.deny_and_conquer;

import com.vng_eleven.deny_and_conquer.game_logic.Board;
import com.vng_eleven.deny_and_conquer.server_client.Server;
import com.vng_eleven.deny_and_conquer.server_client.TokenMessage;
import javafx.application.Application;
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

import java.io.ObjectInputStream;
import java.net.Socket;

public class MainApplication extends Application {
    Scene mainScene;
    Board board;
    Socket socket;

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

        host.setOnMouseClicked(event -> showHostWaitingRoom());

        join.setOnMouseClicked(event -> showClientConnectRoom());

        Parent root = createCentredFrame(host, join);
        host.setStyle("-fx-margin: 10px");
        join.setStyle("-fx-margin: 10px");
        this.mainScene.setRoot(root);
    }

    private void showGame() {
        GridPane gridPane = board.getGridPane();

        Parent root = createCentredFrame(gridPane);

        this.mainScene.setRoot(root);
        board.start();
    }

    private void showHostWaitingRoom() {
        Text text = new Text();
        Button startNow = new Button("Start now");
        Text status = new Text();

        Parent root = createCentredFrame(text, startNow, status);

        // start hosting server on this device
        Server server = Server.getInstance();
        server.start();
        String address = server.getServerIPAddress();

        text.setText("Waiting for players to join. " + "IP address of server is: " + address);
        startNow.setOnMouseClicked(event -> {
            boolean success = server.stopWaitingForPlayers();
            if (!success) {
                status.setText("Not enough players!");
            }
        });

        // connect self to server
        try {
            setUpBoardConnection(address);
            System.out.println("Client connected to server!");
        }
        catch (Exception e) {
            System.err.println("Client could not connect to server");
            e.printStackTrace();
        }
        this.mainScene.setRoot(root);

        waitForServerToStartGame();
    }

    private void showClientWaitingRoom() {
        Text text = new Text("Connected successfully, waiting for other players");
        Parent root = createCentredFrame(text);

        this.mainScene.setRoot(root);
        waitForServerToStartGame();
    }

    private void showClientConnectRoom() {
        Text text = new Text();
        text.setText("Please enter the host's IP address");

        TextField addressField = new TextField();

        Button connectBtn = new Button("Connect");
        String address = addressField.getText();
        connectBtn.setOnMouseClicked(event -> {
            try {
                setUpBoardConnection(address);
                System.out.println("Client connected to server!");
                showClientWaitingRoom();
            }
            catch (Exception e) {
                System.err.println("Client could not connect to server");
                e.printStackTrace();
            }
        });

        Parent root = createCentredFrame(text, addressField, connectBtn);

        this.mainScene.setRoot(root);
    }

    ////////////////////////////////////////////////////////
    // main
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

    private void setUpBoardConnection(String address) {
        this.board = new Board(address);
        this.socket = board.getSocket();
    }

    private void waitForServerToStartGame() {
        WaitStartMessage wsm = new WaitStartMessage();
        wsm.start();
    }

    private class WaitStartMessage extends Thread {
        @Override
        public void run() {
            try {
                TokenMessage message = board.receiveMessage();
                if (message.isStartGameMessage()) {
                    board.setPenColor(message.getColor());
                    showGame();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}