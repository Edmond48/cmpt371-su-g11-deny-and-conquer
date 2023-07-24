package com.vng_eleven.deny_and_conquer.client;

import com.vng_eleven.deny_and_conquer.server.Server;
import com.vng_eleven.deny_and_conquer.server.TokenMessage;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Board extends Thread {
    public static final int DEFAULT_DIMENSION = 8;

    // ui
    GridPane gp;
    int dimension;
    int intPenColor = 0;
    Color penColor = Color.BLACK;
    // cells
    Cell[][] cells;

    // networking
    Socket socket;
    ObjectInputStream is;
    ObjectOutputStream os;

    // result for later
    List<Result> results;

    public Board(String IpAddress) {
        dimension = DEFAULT_DIMENSION;

        gp = new GridPane();
        results = new ArrayList<>();

        try {
            this.socket = new Socket(IpAddress, Server.DEFAULT_PORT);
            // need to create output stream first before input
            this.os = new ObjectOutputStream(socket.getOutputStream());
            os.flush();
            this.is = new ObjectInputStream(socket.getInputStream());

            TokenMessage sizeMsg = receiveMessage();
            assert sizeMsg.getToken() == TokenMessage.Token.SIZE;
            this.dimension = sizeMsg.getColor();
            System.out.println(dimension);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        cells = new Cell[dimension][dimension];
        for (int i = 0; i < dimension; i++) {
            for (int j = 0; j < dimension; j++) {

                cells[i][j] = new Cell(this, i, j);

                gp.add(cells[i][j].getCanvas(), i, j);
            }
        }
    }

    // process server messages
    @Override
    public void run() {
        boolean isListening = true;
        while (isListening) {
            TokenMessage msg = receiveMessage();
            if (msg.isNull()) {
                continue;
            }

            if (msg.isResultMessage()) {
                // row = score; column = rank
                results.add(new Result(msg.getColor(), msg.getRow(), msg.getCol()));
            }
            if (msg.isEndGameMessage()) {
                sendMessage(new TokenMessage(TokenMessage.Token.END_GAME, -1, -1, -1));
                isListening = false;
            }
            else {
                processOperation(msg);
            }
        }

        // end all connections
        try {
            is.close();
            os.close();
            socket.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void processOperation(TokenMessage msg) {
        TokenMessage.Token token = msg.getToken();
        int color = msg.getColor();
        int row = msg.getRow();
        int col = msg.getCol();

        switch (token) {
            case ATTEMPT:
                cells[row][col].attempt(color);
                break;
            case OCCUPY:
                cells[row][col].occupy(color);
                break;
            case RELEASE:
                cells[row][col].release();
                break;
        }
    }

    public Color getPenColor() {
        return penColor;
    }
    public int getIntPenColor() {
        return intPenColor;
    }

    public void setPenColor(int intPenColor) {
        this.intPenColor = intPenColor;
        this.penColor = intToColor(intPenColor);
    }

    public GridPane getGridPane() {
        return this.gp;
    }

    public Socket getSocket() {
        return socket;
    }

    public List<Result> getResults() {
        return this.results;
    }

    public void sendMessage(TokenMessage msg) {
        try {
            os.writeObject(msg);
            os.flush();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public TokenMessage receiveMessage() {
        try {
            return (TokenMessage) is.readObject();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return TokenMessage.nullInstance();
    }

    public static Color intToColor(int x) {
        int r = (x>>16)&0xFF;
        int g = (x>>8)&0xFF;
        int b = (x)&0xFF;
        return new Color(r/255.0, g/255.0, b/255.0, 1);
    }
}
