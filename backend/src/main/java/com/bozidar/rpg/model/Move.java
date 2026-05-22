package com.bozidar.rpg.model;

public record Move(
        String id,
        String name,
        MoveType type,
        MoveEffect effect,
        int baseValue,
        int durationTurns,
        String description
) {
}
