package com.vng_eleven.deny_and_conquer.game_logic;

import com.vng_eleven.deny_and_conquer.server_client.Server;
import com.vng_eleven.deny_and_conquer.server_client.TokenMessage;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Board extends Thread {
    public static final int DIMENSION = 8;

    GridPane gp;
    Cell[][] cells;

    Socket socket;
    ObjectInputStream is;
    ObjectOutputStream os;

    int intPenColor = 0;
    Color penColor = Color.BLACK;

    public Board(String IpAddress) {
        cells = new Cell[DIMENSION][DIMENSION];
        gp = new GridPane();

        for (int i = 0; i < DIMENSION; i++) {
            for (int j = 0; j < DIMENSION; j++) {

                cells[i][j] = new Cell(this);

                gp.add(cells[i][j].getCanvas(), i, j);
            }
        }
        try {
            this.socket = new Socket(IpAddress, Server.DEFAULT_PORT);
            // need to create output stream first before input
            this.os = new ObjectOutputStream(socket.getOutputStream());
            os.flush();
            this.is = new ObjectInputStream(socket.getInputStream());
        }
        catch (Exception e) {
            e.printStackTrace();
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

    public synchronized void sendMessage(TokenMessage msg) {
        try {
            os.writeObject(msg);
            os.flush();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public synchronized TokenMessage receiveMessage() {
        try {
            return (TokenMessage) is.readObject();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Color intToColor(int x) {
        int r = (x>>16)&0xFF;
        int g = (x>>8)&0xFF;
        int b = (x)&0xFF;
        return new Color(r/255.0, g/255.0, b/255.0, 1);
    }
}
