package com.vng_eleven.deny_and_conquer.game_logic;

import com.vng_eleven.deny_and_conquer.server_client.Server;
import javafx.scene.layout.GridPane;

import java.net.Socket;

public class Board {
    public static final int DIMENSION = 8;

    GridPane gp;
    Cell[][] cells;
    Socket socket;

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
            System.out.println("Client connected to server!");
        }
        catch (Exception e) {
            System.err.println("Client could not connect to server");
            e.printStackTrace();
        }
    }

    public GridPane getGridPane() {
        return this.gp;
    }
}
