package com.bozidar.rpg.exception;

public class RunNotFoundException extends GameException {

    public RunNotFoundException(String runId) {
        super("Run not found: " + runId);
    }
}
