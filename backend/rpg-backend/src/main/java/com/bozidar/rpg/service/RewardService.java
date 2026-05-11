package com.bozidar.rpg.service;

import com.bozidar.rpg.dto.BattleReward;
import com.bozidar.rpg.model.CharacterStats;
import com.bozidar.rpg.model.EncounterStatus;
import com.bozidar.rpg.model.HeroState;
import com.bozidar.rpg.model.Move;
import com.bozidar.rpg.model.RunState;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class RewardService {

    static final int XP_PER_BATTLE = 40;
    static final int XP_TO_LEVEL_UP = 100;
    private static final int LEVEL_UP_MAX_HEALTH = 15;
    private static final int LEVEL_UP_ATTACK = 3;
    private static final int LEVEL_UP_DEFENSE = 2;
    private static final int LEVEL_UP_MAGIC = 2;

    private final RunConfigService runConfigService;
    private final Random random = new Random();

    public RewardService(RunConfigService runConfigService) {
        this.runConfigService = runConfigService;
    }

    public BattleReward applyVictoryRewards(RunState run, String defeatedMonsterId) {
        HeroState hero = run.getHero();
        hero.setXp(hero.getXp() + XP_PER_BATTLE);

        boolean leveledUp = false;
        if (hero.getXp() >= XP_TO_LEVEL_UP) {
            hero.setXp(hero.getXp() - XP_TO_LEVEL_UP);
            hero.setLevel(hero.getLevel() + 1);
            CharacterStats old = hero.getBaseStats();
            hero.setBaseStats(new CharacterStats(
                    old.maxHealth() + LEVEL_UP_MAX_HEALTH,
                    old.attack() + LEVEL_UP_ATTACK,
                    old.defense() + LEVEL_UP_DEFENSE,
                    old.magic() + LEVEL_UP_MAGIC
            ));
            leveledUp = true;
        }

        Move learnedMove = pickNewMoveFromMonster(run, defeatedMonsterId);
        if (learnedMove != null) {
            run.addLearnedMove(learnedMove);
        }

        run.getEncounters().stream()
                .filter(e -> e.getMonsterId().equals(defeatedMonsterId))
                .findFirst()
                .ifPresent(e -> {
                    e.setStatus(EncounterStatus.COMPLETED);
                    int nextIndex = e.getIndex() + 1;
                    if (nextIndex < run.getEncounters().size()) {
                        run.getEncounters().get(nextIndex).setStatus(EncounterStatus.AVAILABLE);
                    }
                });

        return new BattleReward(XP_PER_BATTLE, leveledUp, learnedMove);
    }

    private Move pickNewMoveFromMonster(RunState run, String monsterId) {
        List<Move> monsterMoves = runConfigService.getMonsters().stream()
                .filter(m -> m.id().equals(monsterId))
                .findFirst()
                .map(m -> m.moves())
                .orElse(List.of());

        Set<String> learnedIds = run.getLearnedMoves().stream()
                .map(Move::id)
                .collect(Collectors.toSet());

        List<Move> candidates = monsterMoves.stream()
                .filter(m -> !learnedIds.contains(m.id()))
                .toList();

        if (candidates.isEmpty()) {
            return null;
        }
        return candidates.get(random.nextInt(candidates.size()));
    }
}
