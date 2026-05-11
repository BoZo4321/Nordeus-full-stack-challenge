package com.bozidar.rpg.service;

import com.bozidar.rpg.dto.MonsterMoveRequest;
import com.bozidar.rpg.dto.MonsterMoveResponse;
import com.bozidar.rpg.model.CombatantState;
import com.bozidar.rpg.model.Move;
import com.bozidar.rpg.model.MoveEffect;
import com.bozidar.rpg.model.StatusEffectType;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
public class MonsterAiService {

    private final Random random = new Random();

    public MonsterMoveResponse chooseNextMove(MonsterMoveRequest request) {
        List<Move> availableMoves = request.availableMonsterMoves();
        if (availableMoves == null || availableMoves.isEmpty()) {
            throw new IllegalArgumentException("Monster must have at least one available move.");
        }
        Move selected = availableMoves.get(random.nextInt(availableMoves.size()));
        return new MonsterMoveResponse(request.monsterId(), selected);
    }

    public Move chooseNextMove(CombatantState monster, CombatantState hero) {
        List<Move> moves = monster.getMoves();
        if (moves == null || moves.isEmpty()) {
            throw new IllegalArgumentException("Monster must have at least one move available.");
        }

        // Priority 1: heal when below 30% HP
        double hpPercent = (double) monster.getCurrentHealth() / monster.getMaxHealth();
        if (hpPercent < 0.30) {
            Optional<Move> healMove = moves.stream()
                    .filter(m -> m.effect() == MoveEffect.HEAL || m.effect() == MoveEffect.DRAIN_LIFE)
                    .findFirst();
            if (healMove.isPresent()) {
                return healMove.get();
            }
        }

        // Priority 2: debuff hero's Attack if hero has an attack buff active
        boolean heroHasAttackBuff = hero.getActiveEffects().stream()
                .anyMatch(e -> e.getType() == StatusEffectType.ATTACK_UP);
        if (heroHasAttackBuff) {
            Optional<Move> debuffMove = moves.stream()
                    .filter(m -> m.effect() == MoveEffect.DEBUFF_ATTACK)
                    .findFirst();
            if (debuffMove.isPresent()) {
                return debuffMove.get();
            }
        }

        // Priority 3: buff self when no buff is active yet
        boolean monsterHasAnyBuff = monster.getActiveEffects().stream()
                .anyMatch(e -> e.getType() == StatusEffectType.ATTACK_UP
                        || e.getType() == StatusEffectType.DEFENSE_UP
                        || e.getType() == StatusEffectType.MAGIC_UP);
        if (!monsterHasAnyBuff) {
            Optional<Move> buffMove = moves.stream()
                    .filter(m -> m.effect() == MoveEffect.BUFF_ATTACK
                            || m.effect() == MoveEffect.BUFF_DEFENSE
                            || m.effect() == MoveEffect.BUFF_MAGIC
                            || m.effect() == MoveEffect.SELF_DAMAGE_BUFF_MAGIC)
                    .findFirst();
            if (buffMove.isPresent()) {
                return buffMove.get();
            }
        }

        // Priority 4: highest base-value damage move
        Optional<Move> bestDamage = moves.stream()
                .filter(m -> m.effect() == MoveEffect.DAMAGE
                        || m.effect() == MoveEffect.DRAIN_LIFE
                        || m.effect() == MoveEffect.DEBUFF_ATTACK
                        || m.effect() == MoveEffect.DEBUFF_DEFENSE
                        || m.effect() == MoveEffect.DEBUFF_MAGIC)
                .max(Comparator.comparingInt(Move::baseValue));
        if (bestDamage.isPresent()) {
            return bestDamage.get();
        }

        // Fallback: random
        return moves.get(random.nextInt(moves.size()));
    }
}

