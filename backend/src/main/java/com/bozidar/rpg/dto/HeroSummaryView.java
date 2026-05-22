package com.bozidar.rpg.dto;

import com.bozidar.rpg.model.CharacterStats;
import com.bozidar.rpg.model.HeroState;
import com.bozidar.rpg.model.Move;
import com.bozidar.rpg.model.RunState;

import java.util.List;

public record HeroSummaryView(
        String name,
        int level,
        int xp,
        CharacterStats stats,
        List<Move> equippedMoves,
        List<Move> learnedMoves
) {
    public static HeroSummaryView from(RunState run) {
        HeroState hero = run.getHero();
        CharacterStats stats = hero.getBaseStats();
        return new HeroSummaryView(
                hero.getName(),
                hero.getLevel(),
                hero.getXp(),
                stats,
                run.getEquippedMoves(),
                run.getLearnedMoves()
        );
    }
}
