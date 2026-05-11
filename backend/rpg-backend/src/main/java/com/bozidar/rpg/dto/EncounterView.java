package com.bozidar.rpg.dto;

import com.bozidar.rpg.model.EncounterState;
import com.bozidar.rpg.model.EncounterStatus;

public record EncounterView(
        int index,
        String monsterId,
        String monsterName,
        EncounterStatus status
) {
    public static EncounterView from(EncounterState e) {
        return new EncounterView(e.getIndex(), e.getMonsterId(), e.getMonsterName(), e.getStatus());
    }
}
