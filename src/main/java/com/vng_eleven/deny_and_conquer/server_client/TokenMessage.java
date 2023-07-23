package com.vng_eleven.deny_and_conquer.server_client;

import java.io.Serializable;

public class TokenMessage implements Serializable {
    public enum Token {
        START_GAME,
        ATTEMPT,
        OCCUPY,
        RELEASE
    }

    Token token;
    int color;

    public TokenMessage(Token token, int color) {
        this.token = token;
        this.color = color;
    }

    public Token getToken() {
        return this.token;
    }
    public int getColor() {
        return color;
    }

    public boolean isStartGameMessage() {
        return this.token == Token.START_GAME;
    }

    @Override
    public String toString() {
        return "[" + token.toString() + ", color:" + color + "]";
    }
}
