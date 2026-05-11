package com.bozidar.rpg.model;

import java.util.List;

public record Hero(
        String name,
        int level,
        int xp,
        CharacterStats stats,
        List<Move> defaultMoves
) {
}
