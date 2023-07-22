package com.vng_eleven.deny_and_conquer.game_logic;

import javafx.scene.layout.GridPane;

public class Board {
    static final int DIMENSION = 8;
    static private final Board board = new Board();

    GridPane gp;
    Cell[][] cells;

    private Board() {
        cells = new Cell[DIMENSION][DIMENSION];
        gp = new GridPane();

        for (int i = 0; i < DIMENSION; i++) {
            for (int j = 0; j < DIMENSION; j++) {

                cells[i][j] = new Cell();

                gp.add(cells[i][j].getCanvas(), i, j);
            }
        }
    }

    public GridPane getGridPane() {
        return this.gp;
    }

    public static Board getInstance() {
        return board;
    }
}
