package com.bozidar.rpg.exception;

public class BattleNotFoundException extends GameException {

    public BattleNotFoundException(String battleId) {
        super("Battle not found: " + battleId);
    }
}
