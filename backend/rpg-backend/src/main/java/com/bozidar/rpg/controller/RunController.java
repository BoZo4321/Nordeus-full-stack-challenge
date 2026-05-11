package com.bozidar.rpg.controller;

import com.bozidar.rpg.dto.EquipMovesRequest;
import com.bozidar.rpg.dto.EquipMovesResponse;
import com.bozidar.rpg.dto.StartBattleRequest;
import com.bozidar.rpg.dto.StartBattleResponse;
import com.bozidar.rpg.dto.StartRunResponse;
import com.bozidar.rpg.model.BattleState;
import com.bozidar.rpg.model.RunConfig;
import com.bozidar.rpg.model.RunState;
import com.bozidar.rpg.service.BattleService;
import com.bozidar.rpg.service.MoveManagementService;
import com.bozidar.rpg.service.RunConfigService;
import com.bozidar.rpg.service.RunSessionService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class RunController {

    private final RunConfigService runConfigService;
    private final RunSessionService runSessionService;
    private final BattleService battleService;
    private final MoveManagementService moveManagementService;

    public RunController(RunConfigService runConfigService,
                         RunSessionService runSessionService,
                         BattleService battleService,
                         MoveManagementService moveManagementService) {
        this.runConfigService = runConfigService;
        this.runSessionService = runSessionService;
        this.battleService = battleService;
        this.moveManagementService = moveManagementService;
    }

    @GetMapping("/run-config")
    public RunConfig getRunConfig() {
        return runConfigService.getRunConfig();
    }

    @PostMapping("/runs")
    public StartRunResponse startNewRun() {
        RunState run = runSessionService.startNewRun();
        return StartRunResponse.from(run);
    }

    @GetMapping("/runs/{runId}")
    public StartRunResponse getRun(@PathVariable String runId) {
        RunState run = runSessionService.findById(runId);
        return StartRunResponse.from(run);
    }

    @PostMapping("/runs/{runId}/battles")
    public StartBattleResponse startBattle(@PathVariable String runId,
                                            @Valid @RequestBody StartBattleRequest request) {
        RunState run = runSessionService.findById(runId);
        BattleState battle = battleService.startBattle(run, request.monsterId());
        return StartBattleResponse.from(battle);
    }

    @PutMapping("/runs/{runId}/hero/moves")
    public EquipMovesResponse equipMoves(@PathVariable String runId,
                                          @Valid @RequestBody EquipMovesRequest request) {
        RunState run = runSessionService.findById(runId);
        moveManagementService.updateEquippedMoves(run, request.moveIds());
        runSessionService.save(run);
        return new EquipMovesResponse(run.getEquippedMoves(), run.getLearnedMoves());
    }
}
