package com.bozidar.rpg.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RunState {
    private final String runId;
    private final HeroState hero;
    private final List<Move> learnedMoves;
    private final List<Move> equippedMoves;
    private final List<EncounterState> encounters;
    private int currentEncounterIndex;

    public RunState(String runId,
                    HeroState hero,
                    List<Move> defaultMoves,
                    List<EncounterState> encounters) {
        this.runId = runId;
        this.hero = hero;
        this.learnedMoves = new ArrayList<>(defaultMoves);
        this.equippedMoves = new ArrayList<>(defaultMoves);
        this.encounters = new ArrayList<>(encounters);
        this.currentEncounterIndex = 0;
    }

    public String getRunId() {
        return runId;
    }

    public HeroState getHero() {
        return hero;
    }

    public List<Move> getLearnedMoves() {
        return Collections.unmodifiableList(learnedMoves);
    }

    public void addLearnedMove(Move move) {
        learnedMoves.add(move);
    }

    public List<Move> getEquippedMoves() {
        return Collections.unmodifiableList(equippedMoves);
    }

    public void setEquippedMoves(List<Move> moves) {
        equippedMoves.clear();
        equippedMoves.addAll(moves);
    }

    public List<EncounterState> getEncounters() {
        return Collections.unmodifiableList(encounters);
    }

    public int getCurrentEncounterIndex() {
        return currentEncounterIndex;
    }

    public void setCurrentEncounterIndex(int currentEncounterIndex) {
        this.currentEncounterIndex = currentEncounterIndex;
    }
}
