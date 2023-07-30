package com.vng_eleven.deny_and_conquer.server;

import java.io.Serializable;

public class TokenMessage implements Serializable {
    public enum Token {
        // macro game communications
        SIZE,       // server sends this so all clients have the same board size
        START_GAME,
        RESULT,     // announce the results
        END_GAME,

        // cell operations
        ATTEMPT,    // temporarily lock a cell
        OCCUPY,     // permanently lock a cell
        RELEASE,    // unlock a temporary cell

        // null instance
        NULL
    }

    Token token;
    int color;
    int row;
    int col;

    public TokenMessage(Token token, int color, int row, int col) {
        this.token = token;
        this.color = color;
        this.row = row;
        this.col = col;
    }

    public Token getToken() {
        return this.token;
    }
    public int getColor() {
        return color;
    }
    public int getRow() {
        return this.row;
    }
    public int getCol() {
        return this.col;
    }

    public boolean isStartGameMessage() {
        return this.token == Token.START_GAME;
    }
    public boolean isEndGameMessage() {
        return this.token == Token.END_GAME;
    }
    public boolean isResultMessage() {
        return this.token == Token.RESULT;
    }

    public static TokenMessage nullInstance() {
        return new TokenMessage(Token.NULL, 0, -1, -1);
    }
    public boolean isNull() {
        return this.token == Token.NULL;
    }

    // for debugging
    @Override
    public String toString() {
        return "[" + token.toString() + ", color:" + color + "(" + row + ", " + col + ")]";
    }
}
