package com.bozidar.rpg.service;

import com.bozidar.rpg.dto.BattleReward;
import com.bozidar.rpg.dto.CombatantHpView;
import com.bozidar.rpg.dto.PlayerTurnResponse;
import com.bozidar.rpg.dto.TurnMove;
import com.bozidar.rpg.exception.BattleNotFoundException;
import com.bozidar.rpg.exception.InvalidMoveException;
import com.bozidar.rpg.model.ActiveStatusEffect;
import com.bozidar.rpg.model.BattleState;
import com.bozidar.rpg.model.BattleStatus;
import com.bozidar.rpg.model.CharacterStats;
import com.bozidar.rpg.model.CombatantState;
import com.bozidar.rpg.model.EffectApplication;
import com.bozidar.rpg.model.EffectTarget;
import com.bozidar.rpg.model.EncounterState;
import com.bozidar.rpg.model.EncounterStatus;
import com.bozidar.rpg.model.HeroState;
import com.bozidar.rpg.model.Monster;
import com.bozidar.rpg.model.Move;
import com.bozidar.rpg.model.MoveResult;
import com.bozidar.rpg.model.RunState;
import com.bozidar.rpg.repository.InMemoryBattleRepository;
import com.bozidar.rpg.repository.InMemoryRunRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class BattleService {

    private final BattleCalculatorService calculator;
    private final MonsterAiService monsterAi;
    private final InMemoryBattleRepository battleRepository;
    private final InMemoryRunRepository runRepository;
    private final RunConfigService runConfigService;
    private final RewardService rewardService;

    public BattleService(BattleCalculatorService calculator,
                         MonsterAiService monsterAi,
                         InMemoryBattleRepository battleRepository,
                         InMemoryRunRepository runRepository,
                         RunConfigService runConfigService,
                         RewardService rewardService) {
        this.calculator = calculator;
        this.monsterAi = monsterAi;
        this.battleRepository = battleRepository;
        this.runRepository = runRepository;
        this.runConfigService = runConfigService;
        this.rewardService = rewardService;
    }

    public BattleState startBattle(RunState run, String monsterId) {
        EncounterState encounter = run.getEncounters().stream()
                .filter(e -> e.getMonsterId().equals(monsterId))
                .findFirst()
                .orElseThrow(() -> new InvalidMoveException(
                        "Monster is not part of this run: " + monsterId
                ));

        if (encounter.getStatus() == EncounterStatus.LOCKED) {
            throw new InvalidMoveException(
                    "Encounter is locked. Beat the previous encounter first."
            );
        }

        Monster monsterTemplate = runConfigService.getMonsters().stream()
                .filter(m -> m.id().equals(monsterId))
                .findFirst()
                .orElseThrow(() -> new InvalidMoveException("Unknown monster: " + monsterId));

        HeroState heroState = run.getHero();
        CharacterStats heroStats = heroState.getBaseStats();
        CombatantState hero = new CombatantState(
                "hero",
                heroState.getName(),
                heroStats.maxHealth(),
                heroStats.maxHealth(),
                heroStats,
                run.getEquippedMoves()
        );

        CombatantState monster = new CombatantState(
                monsterTemplate.id(),
                monsterTemplate.name(),
                monsterTemplate.stats().maxHealth(),
                monsterTemplate.stats().maxHealth(),
                monsterTemplate.stats(),
                monsterTemplate.moves()
        );

        BattleState battle = new BattleState(
                UUID.randomUUID().toString(),
                run.getRunId(),
                hero,
                monster
        );
        battle.appendLog("Battle started against %s.".formatted(monster.getName()));

        return battleRepository.save(battle);
    }

    public PlayerTurnResponse processPlayerTurn(String battleId, String moveId) {
        BattleState battle = battleRepository.findById(battleId);
        if (battle == null) {
            throw new BattleNotFoundException(battleId);
        }
        if (battle.getStatus() != BattleStatus.IN_PROGRESS) {
            throw new InvalidMoveException("Battle is no longer in progress.");
        }

        Move playerMove = battle.getHero().getMoves().stream()
                .filter(m -> m.id().equals(moveId))
                .findFirst()
                .orElseThrow(() -> new InvalidMoveException(
                        "Move is not equipped: " + moveId
                ));

        MoveResult playerResult = calculator.resolveMove(
                playerMove, battle.getHero(), battle.getMonster()
        );
        applyResult(playerResult, battle.getHero(), battle.getMonster());
        battle.appendLog(playerResult.message());

        Move monsterMove = null;
        MoveResult monsterResult = null;

        if (battle.getHero().isDefeated()) {
            battle.setStatus(BattleStatus.HERO_LOST);
        } else if (battle.getMonster().isDefeated()) {
            battle.setStatus(BattleStatus.HERO_WON);
        } else {
            monsterMove = monsterAi.chooseNextMove(battle.getMonster(), battle.getHero());
            monsterResult = calculator.resolveMove(
                    monsterMove, battle.getMonster(), battle.getHero()
            );
            applyResult(monsterResult, battle.getMonster(), battle.getHero());
            battle.appendLog(monsterResult.message());

            if (battle.getMonster().isDefeated()) {
                battle.setStatus(BattleStatus.HERO_WON);
            } else if (battle.getHero().isDefeated()) {
                battle.setStatus(BattleStatus.HERO_LOST);
            }
        }

        if (battle.getStatus() == BattleStatus.IN_PROGRESS) {
            battle.getHero().tickEffects();
            battle.getMonster().tickEffects();
        }
        battle.incrementTurn();

        BattleReward reward = null;
        if (battle.getStatus() == BattleStatus.HERO_WON) {
            RunState run = runRepository.findById(battle.getRunId());
            if (run != null) {
                reward = rewardService.applyVictoryRewards(run, battle.getMonster().getId());
                runRepository.save(run);
            }
        }

        return buildResponse(battle, playerMove, playerResult, monsterMove, monsterResult, reward);
    }

    private void applyResult(MoveResult result, CombatantState caster, CombatantState target) {
        if (result.damageToTarget() > 0) {
            target.takeDamage(result.damageToTarget());
        }
        if (result.healToCaster() > 0) {
            caster.heal(result.healToCaster());
        }
        if (result.selfDamageToCaster() > 0) {
            caster.takeDamage(result.selfDamageToCaster());
        }
        for (EffectApplication effect : result.effects()) {
            ActiveStatusEffect ase = new ActiveStatusEffect(
                    effect.type(), effect.magnitude(), effect.durationTurns()
            );
            if (effect.target() == EffectTarget.SELF) {
                caster.addEffect(ase);
            } else {
                target.addEffect(ase);
            }
        }
    }

    private PlayerTurnResponse buildResponse(BattleState battle,
                                              Move playerMove,
                                              MoveResult playerResult,
                                              Move monsterMove,
                                              MoveResult monsterResult,
                                              BattleReward reward) {
        TurnMove playerTurn = new TurnMove(
                playerMove.id(),
                playerMove.name(),
                playerResult.message()
        );

        TurnMove monsterTurn = null;
        if (monsterMove != null && monsterResult != null) {
            monsterTurn = new TurnMove(
                    monsterMove.id(),
                    monsterMove.name(),
                    monsterResult.message()
            );
        }

        return new PlayerTurnResponse(
                battle.getBattleId(),
                playerTurn,
                monsterTurn,
                CombatantHpView.from(battle.getHero()),
                CombatantHpView.from(battle.getMonster()),
                battle.getStatus(),
                battle.getBattleLog(),
                battle.getTurnNumber(),
                reward
        );
    }
}
