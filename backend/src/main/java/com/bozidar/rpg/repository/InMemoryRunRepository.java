package com.bozidar.rpg.repository;

import com.bozidar.rpg.model.CharacterStats;
import com.bozidar.rpg.model.EncounterState;
import com.bozidar.rpg.model.HeroState;
import com.bozidar.rpg.model.RunState;
import org.springframework.stereotype.Repository;
import tools.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;

@Repository
public class InMemoryRunRepository {

    private final RunJpaRepository jpaRepository;
    private final ObjectMapper objectMapper;

    public InMemoryRunRepository(RunJpaRepository jpaRepository, ObjectMapper objectMapper) {
        this.jpaRepository = jpaRepository;
        this.objectMapper = objectMapper;
    }

    public RunState save(RunState run) {
        try {
            String json = objectMapper.writeValueAsString(toSnapshot(run));
            RunEntity entity = jpaRepository.findById(run.getRunId())
                    .orElse(new RunEntity(run.getRunId(), json));
            entity.setStateJson(json);
            jpaRepository.save(entity);
            return run;
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize RunState: " + e.getMessage(), e);
        }
    }

    public RunState findById(String runId) {
        return jpaRepository.findById(runId)
                .map(entity -> {
                    try {
                        RunStateSnapshot snap = objectMapper.readValue(
                                entity.getStateJson(), RunStateSnapshot.class);
                        return fromSnapshot(snap);
                    } catch (Exception e) {
                        throw new RuntimeException("Failed to deserialize RunState: " + e.getMessage(), e);
                    }
                })
                .orElse(null);
    }

    public boolean exists(String runId) {
        return jpaRepository.existsById(runId);
    }

    public void delete(String runId) {
        jpaRepository.deleteById(runId);
    }

    private RunStateSnapshot toSnapshot(RunState run) {
        RunStateSnapshot snap = new RunStateSnapshot();
        snap.runId = run.getRunId();
        snap.heroName = run.getHero().getName();
        snap.heroLevel = run.getHero().getLevel();
        snap.heroXp = run.getHero().getXp();
        snap.heroMaxHealth = run.getHero().getBaseStats().maxHealth();
        snap.heroAttack = run.getHero().getBaseStats().attack();
        snap.heroDefense = run.getHero().getBaseStats().defense();
        snap.heroMagic = run.getHero().getBaseStats().magic();
        snap.learnedMoves = new ArrayList<>(run.getLearnedMoves());
        snap.equippedMoves = new ArrayList<>(run.getEquippedMoves());
        snap.currentEncounterIndex = run.getCurrentEncounterIndex();
        snap.encounters = run.getEncounters().stream()
                .map(e -> {
                    RunStateSnapshot.EncounterSnapshot es = new RunStateSnapshot.EncounterSnapshot();
                    es.index = e.getIndex();
                    es.monsterId = e.getMonsterId();
                    es.monsterName = e.getMonsterName();
                    es.status = e.getStatus();
                    return es;
                })
                .toList();
        return snap;
    }

    private RunState fromSnapshot(RunStateSnapshot snap) {
        HeroState hero = new HeroState(
                snap.heroName, snap.heroLevel, snap.heroXp,
                new CharacterStats(snap.heroMaxHealth, snap.heroAttack,
                        snap.heroDefense, snap.heroMagic)
        );
        List<EncounterState> encounters = snap.encounters.stream()
                .map(e -> new EncounterState(e.index, e.monsterId, e.monsterName, e.status))
                .toList();
        RunState run = new RunState(snap.runId, hero, snap.learnedMoves, encounters);
        run.setEquippedMoves(snap.equippedMoves);
        run.setCurrentEncounterIndex(snap.currentEncounterIndex);
        return run;
    }
}
