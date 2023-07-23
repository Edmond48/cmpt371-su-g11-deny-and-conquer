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

    public TokenMessage nullInstance() {
        return new TokenMessage(Token.NULL, 0);
    }
    public boolean isNull() {
        return this.token == Token.NULL;
    }

    @Override
    public String toString() {
        return "[" + token.toString() + ", color:" + color + "]";
    }
}
