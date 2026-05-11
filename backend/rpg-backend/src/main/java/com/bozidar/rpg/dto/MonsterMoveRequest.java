package com.bozidar.rpg.dto;

import com.bozidar.rpg.model.Move;

import java.util.List;

public record MonsterMoveRequest(
        String monsterId,
        int heroCurrentHealth,
        int monsterCurrentHealth,
        List<Move> availableMonsterMoves
) {
}
