package com.bozidar.rpg.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BattleState {
    private final String battleId;
    private final String runId;
    private final CombatantState hero;
    private final CombatantState monster;
    private BattleStatus status;
    private final List<String> battleLog;
    private int turnNumber;

    public BattleState(String battleId, String runId, CombatantState hero, CombatantState monster) {
        this.battleId = battleId;
        this.runId = runId;
        this.hero = hero;
        this.monster = monster;
        this.status = BattleStatus.IN_PROGRESS;
        this.battleLog = new ArrayList<>();
        this.turnNumber = 0;
    }

    public void appendLog(String message) {
        battleLog.add(message);
    }

    public void incrementTurn() {
        turnNumber++;
    }

    public String getBattleId() {
        return battleId;
    }

    public String getRunId() {
        return runId;
    }

    public CombatantState getHero() {
        return hero;
    }

    public CombatantState getMonster() {
        return monster;
    }

    public BattleStatus getStatus() {
        return status;
    }

    public void setStatus(BattleStatus status) {
        this.status = status;
    }

    public List<String> getBattleLog() {
        return Collections.unmodifiableList(battleLog);
    }

    public int getTurnNumber() {
        return turnNumber;
    }
}
