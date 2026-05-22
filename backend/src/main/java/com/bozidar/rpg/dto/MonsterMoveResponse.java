package com.bozidar.rpg.dto;

import com.bozidar.rpg.model.Move;

public record MonsterMoveResponse(
        String monsterId,
        Move selectedMove
) {
}
