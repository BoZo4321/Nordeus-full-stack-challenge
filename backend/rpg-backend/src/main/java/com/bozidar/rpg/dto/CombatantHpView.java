package com.bozidar.rpg.dto;

import com.bozidar.rpg.model.CombatantState;
import com.bozidar.rpg.model.Move;

import java.util.List;

public record CombatantHpView(
        String id,
        String name,
        int currentHealth,
        int maxHealth,
        List<Move> moves,
        List<EffectView> activeEffects
) {
    public static CombatantHpView from(CombatantState state) {
        List<EffectView> effects = state.getActiveEffects().stream()
                .map(EffectView::from)
                .toList();
        return new CombatantHpView(
                state.getId(),
                state.getName(),
                state.getCurrentHealth(),
                state.getMaxHealth(),
                state.getMoves(),
                effects
        );
    }
}
