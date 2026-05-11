package com.bozidar.rpg.dto;

import com.bozidar.rpg.model.ActiveStatusEffect;
import com.bozidar.rpg.model.StatusEffectType;

public record EffectView(
        StatusEffectType type,
        int magnitude,
        int turnsRemaining
) {
    public static EffectView from(ActiveStatusEffect effect) {
        return new EffectView(effect.getType(), effect.getMagnitude(), effect.getTurnsRemaining());
    }
}
