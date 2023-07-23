package com.vng_eleven.deny_and_conquer;

import com.vng_eleven.deny_and_conquer.client.Board;
import com.vng_eleven.deny_and_conquer.client.Result;
import com.vng_eleven.deny_and_conquer.server.Server;
import com.vng_eleven.deny_and_conquer.server.TokenMessage;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.net.Socket;
import java.util.Comparator;
import java.util.List;

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

////////////////////////////////////////////////////////////////////////////////////
// Phases of the game with UI + logic

    ////////////////////////////////////////////////
    // Start menu
    private void showMenu() {
        Button host = new Button("Host game");
        Button join = new Button("Join game");

        host.setOnMouseClicked(event -> showHostWaitingRoom());

        join.setOnMouseClicked(event -> showClientConnectRoom());

        Parent root = createCentredFrame(host, join);

        this.mainScene.setRoot(root);
    }

    ////////////////////////////////////////////////
    // Host and client waiting rooms
    private void showHostWaitingRoom() {
        Text text1 = new Text("Waiting for players to join...");
        Text text2 = new Text();
        Button startNow = new Button("Start now");
        Text status = new Text();

        Parent root = createCentredFrame(text1, text2, startNow, status);

        // start hosting server on this device
        Server server = Server.getInstance();
        server.start();
        String address = server.getServerIPAddress();
        text2.setText("IP address of server is: " + address);

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

    ////////////////////////////////////////////////
    // Main game
    private void showGame() {

        Node penColorInfo = createInfoText();
        GridPane gridPane = board.getGridPane();

        Parent root = createCentredFrame(penColorInfo, gridPane);

        this.mainScene.setRoot(root);

        Task<Board> runGame = new Task() {
            @Override
            public Board call() {
                board.start();
                try {
                    board.join();
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
                return board;
            }
        };
        runGame.setOnSucceeded(event -> {
            // when game is finished
            showEndScreen();
        });

        new Thread(runGame).start();
    }
    private Node createInfoText() {
        HBox hbox = new HBox();
        hbox.setAlignment(Pos.CENTER);

        Text penColorInfo = new Text();
        penColorInfo.setText("Your pen color is ");

        Canvas canvas = getColorSquare(board.getPenColor());

        hbox.getChildren().addAll(penColorInfo, canvas);

        return hbox;
    }

    ////////////////////////////////////////////////
    // End of game
    private void showEndScreen() {
        Node penColorInfo = createInfoText();
        List<Result> results = board.getResults();
        results.sort(Comparator.comparingInt(Result::getRank));

        VBox table = new VBox();
        table.setSpacing(3);

        for (int i = 0; i < results.size(); i++) {
            Result result = results.get(i);
            HBox row = new HBox();
            row.setSpacing(3);

            row.getChildren().addAll(
                    new Text("" + result.getRank()),
                    getColorSquare(result.getColor()),
                    new Text("Score: " + result.getScore()));
            table.getChildren().add(row);
        }

        Parent root = createCentredFrame(penColorInfo, table);
        this.mainScene.setRoot(root);
    }
    private Canvas getColorSquare(Color color) {
        Canvas canvas = new Canvas(25, 25);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setFill(color);
        gc.fillRect(0,0, canvas.getWidth(), canvas.getHeight());
        return canvas;
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
        inner.setSpacing(5);

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