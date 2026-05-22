package com.bozidar.rpg.service;

import com.bozidar.rpg.dto.BattleReward;
import com.bozidar.rpg.model.CharacterStats;
import com.bozidar.rpg.model.EncounterState;
import com.bozidar.rpg.model.EncounterStatus;
import com.bozidar.rpg.model.HeroState;
import com.bozidar.rpg.model.Monster;
import com.bozidar.rpg.model.Move;
import com.bozidar.rpg.model.MoveEffect;
import com.bozidar.rpg.model.MoveType;
import com.bozidar.rpg.model.RunState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RewardServiceTest {

    @Mock
    private RunConfigService runConfigService;

    @InjectMocks
    private RewardService rewardService;

    private static final Move RUSTY_BLADE = new Move("rusty_blade", "Rusty Blade",
            MoveType.PHYSICAL, MoveEffect.DAMAGE, 10, 0, "desc");
    private static final Move FRENZY = new Move("frenzy", "Frenzy",
            MoveType.SUPPORT, MoveEffect.BUFF_ATTACK, 7, 2, "desc");

    private static final Monster GOBLIN = new Monster(
            "goblin_warrior", "Goblin Warrior",
            new CharacterStats(70, 12, 5, 4),
            List.of(RUSTY_BLADE, FRENZY)
    );

    private RunState buildRun(int level, int xp) {
        HeroState hero = new HeroState("Knight", level, xp,
                new CharacterStats(100, 14, 8, 10));
        EncounterState enc0 = new EncounterState(0, "goblin_warrior", "Goblin Warrior", EncounterStatus.AVAILABLE);
        EncounterState enc1 = new EncounterState(1, "giant_spider", "Giant Spider", EncounterStatus.LOCKED);
        Move slash = new Move("slash", "Slash", MoveType.PHYSICAL, MoveEffect.DAMAGE, 12, 0, "desc");
        return new RunState("run-1", hero, List.of(slash), List.of(enc0, enc1));
    }

    @Test
    void awardsCorrectXp() {
        when(runConfigService.getMonsters()).thenReturn(List.of(GOBLIN));
        RunState run = buildRun(1, 0);

        BattleReward reward = rewardService.applyVictoryRewards(run, "goblin_warrior");

        assertEquals(RewardService.XP_PER_BATTLE, reward.xpAwarded());
        assertEquals(RewardService.XP_PER_BATTLE, run.getHero().getXp());
    }

    @Test
    void doesNotLevelUpBelowThreshold() {
        when(runConfigService.getMonsters()).thenReturn(List.of(GOBLIN));
        RunState run = buildRun(1, 0);

        BattleReward reward = rewardService.applyVictoryRewards(run, "goblin_warrior");

        assertFalse(reward.leveledUp());
        assertEquals(1, run.getHero().getLevel());
    }

    @Test
    void levelsUpWhenXpReachesThreshold() {
        when(runConfigService.getMonsters()).thenReturn(List.of(GOBLIN));
        // start at 60 xp; +40 = 100 → level up
        RunState run = buildRun(1, 60);

        BattleReward reward = rewardService.applyVictoryRewards(run, "goblin_warrior");

        assertTrue(reward.leveledUp());
        assertEquals(2, run.getHero().getLevel());
        assertEquals(0, run.getHero().getXp());

        CharacterStats stats = run.getHero().getBaseStats();
        assertEquals(115, stats.maxHealth());
        assertEquals(17, stats.attack());
        assertEquals(10, stats.defense());
        assertEquals(12, stats.magic());
    }

    @Test
    void learnsNewMoveFromDefeatedMonster() {
        when(runConfigService.getMonsters()).thenReturn(List.of(GOBLIN));
        RunState run = buildRun(1, 0);

        BattleReward reward = rewardService.applyVictoryRewards(run, "goblin_warrior");

        assertNotNull(reward.learnedMove());
        // hero's run learnedMoves should now contain the new move
        assertTrue(run.getLearnedMoves().stream()
                .anyMatch(m -> m.id().equals(reward.learnedMove().id())));
    }

    @Test
    void doesNotLearnAlreadyKnownMove() {
        when(runConfigService.getMonsters()).thenReturn(List.of(GOBLIN));
        // hero already knows both goblin moves
        HeroState hero = new HeroState("Knight", 1, 0,
                new CharacterStats(100, 14, 8, 10));
        EncounterState enc0 = new EncounterState(0, "goblin_warrior", "Goblin Warrior", EncounterStatus.AVAILABLE);
        RunState run = new RunState("run-1", hero,
                List.of(RUSTY_BLADE, FRENZY), List.of(enc0));

        BattleReward reward = rewardService.applyVictoryRewards(run, "goblin_warrior");

        assertNull(reward.learnedMove());
    }

    @Test
    void marksEncounterCompletedAndUnlocksNext() {
        when(runConfigService.getMonsters()).thenReturn(List.of(GOBLIN));
        RunState run = buildRun(1, 0);

        rewardService.applyVictoryRewards(run, "goblin_warrior");

        assertEquals(EncounterStatus.COMPLETED,
                run.getEncounters().get(0).getStatus());
        assertEquals(EncounterStatus.AVAILABLE,
                run.getEncounters().get(1).getStatus());
    }
}
