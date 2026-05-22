package com.bozidar.rpg.model;

public class ActiveStatusEffect {
    private final StatusEffectType type;
    private final int magnitude;
    private int turnsRemaining;

    public ActiveStatusEffect(StatusEffectType type, int magnitude, int turnsRemaining) {
        this.type = type;
        this.magnitude = magnitude;
        this.turnsRemaining = turnsRemaining;
    }

    public StatusEffectType getType() {
        return type;
    }

    public int getMagnitude() {
        return magnitude;
    }

    public int getTurnsRemaining() {
        return turnsRemaining;
    }

    public void decrementTurns() {
        if (turnsRemaining > 0) {
            turnsRemaining--;
        }
    }

    public boolean isExpired() {
        return turnsRemaining <= 0;
    }
}
