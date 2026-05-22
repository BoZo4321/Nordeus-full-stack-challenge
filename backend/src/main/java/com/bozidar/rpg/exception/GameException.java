package com.bozidar.rpg.exception;

public abstract class GameException extends RuntimeException {

    protected GameException(String message) {
        super(message);
    }
}
