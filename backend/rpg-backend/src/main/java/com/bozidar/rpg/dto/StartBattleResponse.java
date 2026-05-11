package com.bozidar.rpg.dto;

import com.bozidar.rpg.model.BattleState;
import com.bozidar.rpg.model.BattleStatus;

import java.util.List;

public record StartBattleResponse(
        String battleId,
        String runId,
        CombatantHpView hero,
        CombatantHpView monster,
        BattleStatus battleStatus,
        List<String> battleLog,
        int turnNumber
) {
    public static StartBattleResponse from(BattleState battle) {
        return new StartBattleResponse(
                battle.getBattleId(),
                battle.getRunId(),
                CombatantHpView.from(battle.getHero()),
                CombatantHpView.from(battle.getMonster()),
                battle.getStatus(),
                battle.getBattleLog(),
                battle.getTurnNumber()
        );
    }
}
