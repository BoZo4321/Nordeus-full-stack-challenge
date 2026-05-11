package com.bozidar.rpg.service;

import com.bozidar.rpg.exception.RunNotFoundException;
import com.bozidar.rpg.model.EncounterState;
import com.bozidar.rpg.model.EncounterStatus;
import com.bozidar.rpg.model.Hero;
import com.bozidar.rpg.model.HeroState;
import com.bozidar.rpg.model.Monster;
import com.bozidar.rpg.model.RunState;
import com.bozidar.rpg.repository.InMemoryRunRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class RunSessionService {

    private final RunConfigService runConfigService;
    private final InMemoryRunRepository runRepository;

    public RunSessionService(RunConfigService runConfigService,
                             InMemoryRunRepository runRepository) {
        this.runConfigService = runConfigService;
        this.runRepository = runRepository;
    }

    public RunState startNewRun() {
        Hero heroTemplate = runConfigService.getHeroTemplate();
        List<Monster> monsters = runConfigService.getMonsters();

        List<EncounterState> encounters = new ArrayList<>();
        for (int i = 0; i < monsters.size(); i++) {
            Monster m = monsters.get(i);
            EncounterStatus status = (i == 0) ? EncounterStatus.AVAILABLE : EncounterStatus.LOCKED;
            encounters.add(new EncounterState(i, m.id(), m.name(), status));
        }

        HeroState heroState = new HeroState(
                heroTemplate.name(),
                heroTemplate.level(),
                heroTemplate.xp(),
                heroTemplate.stats()
        );

        RunState run = new RunState(
                UUID.randomUUID().toString(),
                heroState,
                heroTemplate.defaultMoves(),
                encounters
        );

        return runRepository.save(run);
    }

    public RunState findById(String runId) {
        RunState run = runRepository.findById(runId);
        if (run == null) {
            throw new RunNotFoundException(runId);
        }
        return run;
    }

    public RunState save(RunState run) {
        return runRepository.save(run);
    }
}
