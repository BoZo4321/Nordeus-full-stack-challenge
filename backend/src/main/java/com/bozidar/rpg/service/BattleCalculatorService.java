package com.bozidar.rpg.service;

import com.bozidar.rpg.model.CombatantState;
import com.bozidar.rpg.model.EffectApplication;
import com.bozidar.rpg.model.EffectTarget;
import com.bozidar.rpg.model.Move;
import com.bozidar.rpg.model.MoveResult;
import com.bozidar.rpg.model.MoveType;
import com.bozidar.rpg.model.RuntimeStats;
import com.bozidar.rpg.model.StatusEffectType;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class BattleCalculatorService {

    public MoveResult resolveMove(Move move, CombatantState caster, CombatantState target) {
        RuntimeStats casterStats = caster.currentStats();
        RuntimeStats targetStats = target.currentStats();

        int damageToTarget = 0;
        int healToCaster = 0;
        int selfDamageToCaster = 0;
        List<EffectApplication> effects = new ArrayList<>();
        String message;

        switch (move.effect()) {
            case DAMAGE -> {
                damageToTarget = computeDamage(move, casterStats, targetStats);
                message = "%s used %s and dealt %d damage to %s.".formatted(
                        caster.getName(), move.name(), damageToTarget, target.getName()
                );
            }
            case HEAL -> {
                healToCaster = computeHeal(move, casterStats);
                message = "%s used %s and recovered %d HP.".formatted(
                        caster.getName(), move.name(), healToCaster
                );
            }
            case BUFF_ATTACK -> {
                effects.add(new EffectApplication(
                        EffectTarget.SELF, StatusEffectType.ATTACK_UP,
                        move.baseValue(), move.durationTurns()
                ));
                message = "%s used %s and raised their Attack for %d turns.".formatted(
                        caster.getName(), move.name(), move.durationTurns()
                );
            }
            case BUFF_DEFENSE -> {
                effects.add(new EffectApplication(
                        EffectTarget.SELF, StatusEffectType.DEFENSE_UP,
                        move.baseValue(), move.durationTurns()
                ));
                message = "%s used %s and raised their Defense for %d turns.".formatted(
                        caster.getName(), move.name(), move.durationTurns()
                );
            }
            case BUFF_MAGIC -> {
                effects.add(new EffectApplication(
                        EffectTarget.SELF, StatusEffectType.MAGIC_UP,
                        move.baseValue(), move.durationTurns()
                ));
                message = "%s used %s and raised their Magic for %d turns.".formatted(
                        caster.getName(), move.name(), move.durationTurns()
                );
            }
            case DEBUFF_ATTACK -> {
                effects.add(new EffectApplication(
                        EffectTarget.ENEMY, StatusEffectType.ATTACK_DOWN,
                        move.baseValue(), move.durationTurns()
                ));
                if (move.type() == MoveType.SUPPORT) {
                    message = "%s used %s and lowered %s's Attack for %d turns.".formatted(
                            caster.getName(), move.name(), target.getName(), move.durationTurns()
                    );
                } else {
                    damageToTarget = computeDamage(move, casterStats, targetStats);
                    message = "%s used %s, dealing %d damage and lowering %s's Attack for %d turns.".formatted(
                            caster.getName(), move.name(), damageToTarget,
                            target.getName(), move.durationTurns()
                    );
                }
            }
            case DEBUFF_DEFENSE -> {
                effects.add(new EffectApplication(
                        EffectTarget.ENEMY, StatusEffectType.DEFENSE_DOWN,
                        move.baseValue(), move.durationTurns()
                ));
                if (move.type() == MoveType.SUPPORT) {
                    message = "%s used %s and lowered %s's Defense for %d turns.".formatted(
                            caster.getName(), move.name(), target.getName(), move.durationTurns()
                    );
                } else {
                    damageToTarget = computeDamage(move, casterStats, targetStats);
                    message = "%s used %s, dealing %d damage and lowering %s's Defense for %d turns.".formatted(
                            caster.getName(), move.name(), damageToTarget,
                            target.getName(), move.durationTurns()
                    );
                }
            }
            case DEBUFF_MAGIC -> {
                effects.add(new EffectApplication(
                        EffectTarget.ENEMY, StatusEffectType.MAGIC_DOWN,
                        move.baseValue(), move.durationTurns()
                ));
                if (move.type() == MoveType.SUPPORT) {
                    message = "%s used %s and lowered %s's Magic for %d turns.".formatted(
                            caster.getName(), move.name(), target.getName(), move.durationTurns()
                    );
                } else {
                    damageToTarget = computeDamage(move, casterStats, targetStats);
                    message = "%s used %s, dealing %d damage and lowering %s's Magic for %d turns.".formatted(
                            caster.getName(), move.name(), damageToTarget,
                            target.getName(), move.durationTurns()
                    );
                }
            }
            case DRAIN_LIFE -> {
                damageToTarget = Math.max(1, move.baseValue() + casterStats.getMagic());
                healToCaster = damageToTarget;
                message = "%s used %s, dealing %d damage to %s and recovering %d HP.".formatted(
                        caster.getName(), move.name(), damageToTarget,
                        target.getName(), healToCaster
                );
            }
            case SELF_DAMAGE_BUFF_MAGIC -> {
                selfDamageToCaster = move.baseValue();
                effects.add(new EffectApplication(
                        EffectTarget.SELF, StatusEffectType.MAGIC_UP,
                        move.baseValue(), move.durationTurns()
                ));
                message = "%s used %s, sacrificing %d HP to raise their Magic for %d turns.".formatted(
                        caster.getName(), move.name(), selfDamageToCaster, move.durationTurns()
                );
            }
            default -> {
                message = "%s used %s.".formatted(caster.getName(), move.name());
            }
        }

        return new MoveResult(message, damageToTarget, healToCaster, selfDamageToCaster, effects);
    }

    private int computeDamage(Move move, RuntimeStats casterStats, RuntimeStats targetStats) {
        return switch (move.type()) {
            case PHYSICAL -> Math.max(1, move.baseValue() + casterStats.getAttack() - targetStats.getDefense());
            case MAGIC -> Math.max(1, move.baseValue() + casterStats.getMagic());
            case SUPPORT -> Math.max(1, move.baseValue());
        };
    }

    private int computeHeal(Move move, RuntimeStats casterStats) {
        return Math.max(0, move.baseValue() + casterStats.getMagic());
    }
}
