package com.bozidar.rpg.model;

import java.util.List;

public record Monster(
        String id,
        String name,
        CharacterStats stats,
        List<Move> moves
) {
}
