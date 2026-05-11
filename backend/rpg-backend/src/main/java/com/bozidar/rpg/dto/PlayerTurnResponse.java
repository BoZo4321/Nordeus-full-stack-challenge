package com.bozidar.rpg.dto;

import com.bozidar.rpg.model.BattleStatus;

import java.util.List;

public record PlayerTurnResponse(
        String battleId,
        TurnMove playerMove,
        TurnMove monsterMove,
        CombatantHpView hero,
        CombatantHpView monster,
        BattleStatus battleStatus,
        List<String> battleLog,
        int turnNumber,
        BattleReward reward
) {
}
