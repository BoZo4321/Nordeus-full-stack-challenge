package com.bozidar.rpg.repository;

import com.bozidar.rpg.model.EncounterStatus;
import com.bozidar.rpg.model.Move;

import java.util.List;

public class RunStateSnapshot {
    public String runId;
    public String heroName;
    public int heroLevel;
    public int heroXp;
    public int heroMaxHealth;
    public int heroAttack;
    public int heroDefense;
    public int heroMagic;
    public List<Move> learnedMoves;
    public List<Move> equippedMoves;
    public int currentEncounterIndex;
    public List<EncounterSnapshot> encounters;

    public static class EncounterSnapshot {
        public int index;
        public String monsterId;
        public String monsterName;
        public EncounterStatus status;
    }
}
