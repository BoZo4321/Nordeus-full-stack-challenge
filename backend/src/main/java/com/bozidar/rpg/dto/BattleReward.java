package com.bozidar.rpg.dto;

import com.bozidar.rpg.model.Move;

public record BattleReward(
        int xpAwarded,
        boolean leveledUp,
        Move learnedMove
) {
}
