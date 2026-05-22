package com.bozidar.rpg.service;

import com.bozidar.rpg.model.ActiveStatusEffect;
import com.bozidar.rpg.model.CharacterStats;
import com.bozidar.rpg.model.CombatantState;
import com.bozidar.rpg.model.Move;
import com.bozidar.rpg.model.MoveEffect;
import com.bozidar.rpg.model.MoveType;
import com.bozidar.rpg.model.StatusEffectType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MonsterAiServiceTest {

    private MonsterAiService ai;

    @BeforeEach
    void setUp() {
        ai = new MonsterAiService();
    }

    private static final Move DAMAGE_WEAK = new Move("dmg_weak", "Weak", MoveType.PHYSICAL, MoveEffect.DAMAGE, 5, 0, "d");
    private static final Move DAMAGE_STRONG = new Move("dmg_strong", "Strong", MoveType.PHYSICAL, MoveEffect.DAMAGE, 15, 0, "d");
    private static final Move DRAIN = new Move("drain", "Drain", MoveType.MAGIC, MoveEffect.DRAIN_LIFE, 9, 0, "d");
    private static final Move HEAL = new Move("heal", "Heal", MoveType.MAGIC, MoveEffect.HEAL, 10, 0, "d");
    private static final Move BUFF = new Move("buff", "Buff", MoveType.SUPPORT, MoveEffect.BUFF_ATTACK, 8, 2, "d");
    private static final Move DEBUFF_ATK = new Move("debuff", "Debuff", MoveType.SUPPORT, MoveEffect.DEBUFF_ATTACK, 8, 2, "d");

    private CombatantState monster(int currentHp, int maxHp, List<Move> moves) {
        return new CombatantState("m", "Monster", currentHp, maxHp,
                new CharacterStats(maxHp, 10, 5, 8), moves);
    }

    private CombatantState hero(List<Move> moves) {
        return new CombatantState("h", "Hero", 100, 100,
                new CharacterStats(100, 14, 8, 10), moves);
    }

    @Test
    void priority1_usesHealWhenLowHp() {
        CombatantState monster = monster(20, 100, List.of(DAMAGE_STRONG, DRAIN));
        // 20 / 100 = 20% < 30% → should pick drain
        Move chosen = ai.chooseNextMove(monster, hero(List.of()));
        assertEquals(DRAIN.id(), chosen.id());
    }

    @Test
    void priority1_doesNotHealWhenHpIsNotLow() {
        CombatantState monster = monster(50, 100, List.of(DAMAGE_STRONG, DRAIN));
        // 50% > 30% → should NOT pick drain for priority 1
        // no other priorities kick in (no hero buff, no buff to apply)
        // falls through to priority 4 (best damage)
        Move chosen = ai.chooseNextMove(monster, hero(List.of()));
        assertEquals(DAMAGE_STRONG.id(), chosen.id());
    }

    @Test
    void priority2_deBuffsHeroAttackWhenHeroHasAttackBuff() {
        CombatantState monster = monster(80, 100, List.of(DAMAGE_STRONG, DEBUFF_ATK, BUFF));
        CombatantState heroWithBuff = hero(List.of());
        heroWithBuff.addEffect(new ActiveStatusEffect(StatusEffectType.ATTACK_UP, 8, 2));

        Move chosen = ai.chooseNextMove(monster, heroWithBuff);
        assertEquals(DEBUFF_ATK.id(), chosen.id());
    }

    @Test
    void priority3_buffsSelfWhenNoBuff() {
        CombatantState monster = monster(80, 100, List.of(DAMAGE_STRONG, BUFF));
        // monster has no active buffs → picks buff move over damage
        Move chosen = ai.chooseNextMove(monster, hero(List.of()));
        assertEquals(BUFF.id(), chosen.id());
    }

    @Test
    void priority3_skipsBuff_whenMonsterAlreadyBuffed() {
        CombatantState monster = monster(80, 100, List.of(DAMAGE_STRONG, BUFF));
        monster.addEffect(new ActiveStatusEffect(StatusEffectType.ATTACK_UP, 8, 2));
        // monster already has a buff → falls to priority 4 (best damage)
        Move chosen = ai.chooseNextMove(monster, hero(List.of()));
        assertEquals(DAMAGE_STRONG.id(), chosen.id());
    }

    @Test
    void priority4_picksBestDamageMove() {
        CombatantState monster = monster(80, 100, List.of(DAMAGE_WEAK, DAMAGE_STRONG));
        // no heal needed, no hero buff, monster has no buff moves
        Move chosen = ai.chooseNextMove(monster, hero(List.of()));
        assertEquals(DAMAGE_STRONG.id(), chosen.id());
    }

    @Test
    void throwsWhenMonsterHasNoMoves() {
        CombatantState monster = monster(80, 100, List.of());
        org.junit.jupiter.api.Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> ai.chooseNextMove(monster, hero(List.of()))
        );
    }
}
