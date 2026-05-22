package com.bozidar.rpg.model;

import java.util.List;

public record MoveResult(
        String message,
        int damageToTarget,
        int healToCaster,
        int selfDamageToCaster,
        List<EffectApplication> effects
) {
}
