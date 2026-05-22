package com.bozidar.rpg.model;

public class HeroState {
    private final String name;
    private int level;
    private int xp;
    private CharacterStats baseStats;

    public HeroState(String name, int level, int xp, CharacterStats baseStats) {
        this.name = name;
        this.level = level;
        this.xp = xp;
        this.baseStats = baseStats;
    }

    public String getName() {
        return name;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getXp() {
        return xp;
    }

    public void setXp(int xp) {
        this.xp = xp;
    }

    public CharacterStats getBaseStats() {
        return baseStats;
    }

    public void setBaseStats(CharacterStats baseStats) {
        this.baseStats = baseStats;
    }
}
