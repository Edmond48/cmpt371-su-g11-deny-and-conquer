package com.vng_eleven.deny_and_conquer.server_client;

import java.io.Serializable;

public class TokenMessage implements Serializable {
    public enum Token {
        START_GAME,
        ATTEMPT,
        OCCUPY,
        RELEASE,
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

    public static TokenMessage nullInstance() {
        return new TokenMessage(Token.NULL, 0, -1, -1);
    }
    public boolean isNull() {
        return this.token == Token.NULL;
    }

    @Override
    public String toString() {
        return "[" + token.toString() + ", color:" + color + "(" + row + ", " + col + ")]";
    }
}
