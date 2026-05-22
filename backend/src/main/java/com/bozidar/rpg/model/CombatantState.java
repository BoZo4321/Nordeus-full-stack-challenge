package com.bozidar.rpg.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CombatantState {
    private final String id;
    private final String name;
    private int currentHealth;
    private final int maxHealth;
    private final CharacterStats baseStats;
    private final List<Move> moves;
    private final List<ActiveStatusEffect> activeEffects;

    public CombatantState(String id,
                          String name,
                          int currentHealth,
                          int maxHealth,
                          CharacterStats baseStats,
                          List<Move> moves) {
        this.id = id;
        this.name = name;
        this.currentHealth = currentHealth;
        this.maxHealth = maxHealth;
        this.baseStats = baseStats;
        this.moves = List.copyOf(moves);
        this.activeEffects = new ArrayList<>();
    }

    public RuntimeStats currentStats() {
        return RuntimeStats.from(baseStats, activeEffects);
    }

    public void takeDamage(int amount) {
        currentHealth = Math.max(0, currentHealth - Math.max(0, amount));
    }

    public void heal(int amount) {
        currentHealth = Math.min(maxHealth, currentHealth + Math.max(0, amount));
    }

    public boolean isDefeated() {
        return currentHealth <= 0;
    }

    public void addEffect(ActiveStatusEffect effect) {
        activeEffects.add(effect);
    }

    public void tickEffects() {
        for (ActiveStatusEffect effect : activeEffects) {
            effect.decrementTurns();
        }
        activeEffects.removeIf(ActiveStatusEffect::isExpired);
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getCurrentHealth() {
        return currentHealth;
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    public CharacterStats getBaseStats() {
        return baseStats;
    }

    public List<Move> getMoves() {
        return moves;
    }

    public List<ActiveStatusEffect> getActiveEffects() {
        return Collections.unmodifiableList(activeEffects);
    }
}
