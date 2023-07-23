package com.vng_eleven.deny_and_conquer.game_logic;

import com.vng_eleven.deny_and_conquer.server_client.Server;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;

import java.net.Socket;

public class Board {
    public static final int DIMENSION = 8;

    GridPane gp;
    Cell[][] cells;
    Socket socket;

    Color penColor = Color.BLACK;

    public Board(Socket socket) {
        cells = new Cell[DIMENSION][DIMENSION];
        gp = new GridPane();

        for (int i = 0; i < DIMENSION; i++) {
            for (int j = 0; j < DIMENSION; j++) {

                cells[i][j] = new Cell(this);

                gp.add(cells[i][j].getCanvas(), i, j);
            }
        }
        this.socket = socket;
    }

    public Color getPenColor() {
        return penColor;
    }

    public void setPenColor(Color penColor) {
        this.penColor = penColor;
    }

    public GridPane getGridPane() {
        return this.gp;
    }

    public static Color intToColor(int x) {
        int r = (x>>16)&0xFF;
        int g = (x>>8)&0xFF;
        int b = (x)&0xFF;
        return new Color(r/255.0, g/255.0, b/255.0, 1);
    }
}
