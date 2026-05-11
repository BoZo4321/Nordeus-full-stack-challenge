package com.bozidar.rpg.service;

import com.bozidar.rpg.model.ActiveStatusEffect;
import com.bozidar.rpg.model.CharacterStats;
import com.bozidar.rpg.model.CombatantState;
import com.bozidar.rpg.model.Move;
import com.bozidar.rpg.model.MoveEffect;
import com.bozidar.rpg.model.MoveResult;
import com.bozidar.rpg.model.MoveType;
import com.bozidar.rpg.model.StatusEffectType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BattleCalculatorServiceTest {

    private BattleCalculatorService calculator;

    @BeforeEach
    void setUp() {
        calculator = new BattleCalculatorService();
    }

    private CombatantState combatant(int attack, int defense, int magic) {
        return new CombatantState("id", "Name", 100, 100,
                new CharacterStats(100, attack, defense, magic), List.of());
    }

    private Move move(MoveType type, MoveEffect effect, int baseValue) {
        return new Move("id", "Move", type, effect, baseValue, 0, "desc");
    }

    private Move move(MoveType type, MoveEffect effect, int baseValue, int duration) {
        return new Move("id", "Move", type, effect, baseValue, duration, "desc");
    }

    // --- Physical damage ---

    @Test
    void physicalDamage_usesAttackMinusDefense() {
        // base 10 + attack 14 - defense 5 = 19
        MoveResult result = calculator.resolveMove(
                move(MoveType.PHYSICAL, MoveEffect.DAMAGE, 10),
                combatant(14, 0, 0),
                combatant(0, 5, 0)
        );
        assertEquals(19, result.damageToTarget());
    }

    @Test
    void physicalDamage_floorIsOne() {
        // base 1 + attack 0 - defense 100 → would be -99, floor to 1
        MoveResult result = calculator.resolveMove(
                move(MoveType.PHYSICAL, MoveEffect.DAMAGE, 1),
                combatant(0, 0, 0),
                combatant(0, 100, 0)
        );
        assertEquals(1, result.damageToTarget());
    }

    @Test
    void physicalDamage_withAttackBuff() {
        CombatantState caster = combatant(10, 0, 0);
        caster.addEffect(new ActiveStatusEffect(StatusEffectType.ATTACK_UP, 8, 2));
        // base 5 + (attack 10 + buff 8) - defense 3 = 20
        MoveResult result = calculator.resolveMove(
                move(MoveType.PHYSICAL, MoveEffect.DAMAGE, 5),
                caster,
                combatant(0, 3, 0)
        );
        assertEquals(20, result.damageToTarget());
    }

    // --- Magic damage ---

    @Test
    void magicDamage_bypassesDefense() {
        // base 13 + magic 15 = 28, regardless of target defense
        MoveResult result = calculator.resolveMove(
                move(MoveType.MAGIC, MoveEffect.DAMAGE, 13),
                combatant(0, 0, 15),
                combatant(0, 50, 0)
        );
        assertEquals(28, result.damageToTarget());
        assertEquals(0, result.healToCaster());
    }

    @Test
    void magicDamage_floorIsOne() {
        MoveResult result = calculator.resolveMove(
                move(MoveType.MAGIC, MoveEffect.DAMAGE, 1),
                combatant(0, 0, 0),
                combatant(0, 0, 0)
        );
        assertEquals(1, result.damageToTarget());
    }

    // --- Healing ---

    @Test
    void heal_scalesWithMagic() {
        // base 10 + magic 12 = 22
        MoveResult result = calculator.resolveMove(
                move(MoveType.MAGIC, MoveEffect.HEAL, 10),
                combatant(0, 0, 12),
                combatant(0, 0, 0)
        );
        assertEquals(22, result.healToCaster());
        assertEquals(0, result.damageToTarget());
    }

    // --- Drain life ---

    @Test
    void drainLife_damagesAndHealsForSameAmount() {
        // base 9 + magic 18 = 27
        MoveResult result = calculator.resolveMove(
                move(MoveType.MAGIC, MoveEffect.DRAIN_LIFE, 9),
                combatant(0, 0, 18),
                combatant(0, 0, 0)
        );
        assertEquals(27, result.damageToTarget());
        assertEquals(27, result.healToCaster());
    }

    // --- Buff / debuff effects ---

    @Test
    void buffAttack_producesEffectApplicationWithNoDirectDamage() {
        MoveResult result = calculator.resolveMove(
                move(MoveType.SUPPORT, MoveEffect.BUFF_ATTACK, 8, 2),
                combatant(14, 0, 0),
                combatant(0, 0, 0)
        );
        assertEquals(0, result.damageToTarget());
        assertEquals(1, result.effects().size());
        assertEquals(StatusEffectType.ATTACK_UP, result.effects().get(0).type());
        assertEquals(8, result.effects().get(0).magnitude());
    }

    @Test
    void debuffDefense_physicalType_dealsPhysicalDamageAndAppliesEffect() {
        // physical: base 7 + attack 12 - defense 5 = 14
        MoveResult result = calculator.resolveMove(
                move(MoveType.PHYSICAL, MoveEffect.DEBUFF_DEFENSE, 7, 2),
                combatant(12, 0, 0),
                combatant(0, 5, 0)
        );
        assertEquals(14, result.damageToTarget());
        assertEquals(1, result.effects().size());
        assertEquals(StatusEffectType.DEFENSE_DOWN, result.effects().get(0).type());
    }

    @Test
    void selfDamageBuffMagic_dealsSelfDamageAndAddsEffect() {
        MoveResult result = calculator.resolveMove(
                move(MoveType.SUPPORT, MoveEffect.SELF_DAMAGE_BUFF_MAGIC, 10, 2),
                combatant(0, 0, 0),
                combatant(0, 0, 0)
        );
        assertEquals(0, result.damageToTarget());
        assertEquals(10, result.selfDamageToCaster());
        assertEquals(1, result.effects().size());
        assertEquals(StatusEffectType.MAGIC_UP, result.effects().get(0).type());
    }
}
