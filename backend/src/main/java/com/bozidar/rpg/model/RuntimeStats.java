package com.bozidar.rpg.model;

import java.util.List;

public class RuntimeStats {
    private final int attack;
    private final int defense;
    private final int magic;

    public RuntimeStats(int attack, int defense, int magic) {
        this.attack = attack;
        this.defense = defense;
        this.magic = magic;
    }

    public static RuntimeStats from(CharacterStats base, List<ActiveStatusEffect> effects) {
        int attack = base.attack();
        int defense = base.defense();
        int magic = base.magic();

        for (ActiveStatusEffect effect : effects) {
            switch (effect.getType()) {
                case ATTACK_UP -> attack += effect.getMagnitude();
                case ATTACK_DOWN -> attack -= effect.getMagnitude();
                case DEFENSE_UP -> defense += effect.getMagnitude();
                case DEFENSE_DOWN -> defense -= effect.getMagnitude();
                case MAGIC_UP -> magic += effect.getMagnitude();
                case MAGIC_DOWN -> magic -= effect.getMagnitude();
            }
        }

        return new RuntimeStats(
                Math.max(0, attack),
                Math.max(0, defense),
                Math.max(0, magic)
        );
    }

    public int getAttack() {
        return attack;
    }

    public int getDefense() {
        return defense;
    }

    public int getMagic() {
        return magic;
    }
}
