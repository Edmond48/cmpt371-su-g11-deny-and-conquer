package com.vng_eleven.deny_and_conquer.client;

import javafx.scene.paint.Color;

// object used to better handle the RESULT message on the client side
public class Result {
    Color color;
    int score;
    int rank;

    public Result(int hexColor, int score, int rank) {
        this.color = Board.intToColor(hexColor);
        this.score = score;
        this.rank = rank + 1;
    }

    public Color getColor() {
        return color;
    }

    public int getScore() {
        return score;
    }

    public int getRank() {
        return rank;
    }
}
