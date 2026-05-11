package com.bozidar.rpg.model;

public class EncounterState {
    private final int index;
    private final String monsterId;
    private final String monsterName;
    private EncounterStatus status;

    public EncounterState(int index, String monsterId, String monsterName, EncounterStatus status) {
        this.index = index;
        this.monsterId = monsterId;
        this.monsterName = monsterName;
        this.status = status;
    }

    public int getIndex() {
        return index;
    }

    public String getMonsterId() {
        return monsterId;
    }

    public String getMonsterName() {
        return monsterName;
    }

    public EncounterStatus getStatus() {
        return status;
    }

    public void setStatus(EncounterStatus status) {
        this.status = status;
    }
}
