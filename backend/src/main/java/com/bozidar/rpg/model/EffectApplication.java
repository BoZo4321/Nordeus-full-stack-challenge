package com.bozidar.rpg.model;

public record EffectApplication(
        EffectTarget target,
        StatusEffectType type,
        int magnitude,
        int durationTurns
) {
}
