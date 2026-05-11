package com.bozidar.rpg.controller;

import com.bozidar.rpg.dto.PlayerTurnRequest;
import com.bozidar.rpg.dto.PlayerTurnResponse;
import com.bozidar.rpg.service.BattleService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/battles")
public class BattleController {

    private final BattleService battleService;

    public BattleController(BattleService battleService) {
        this.battleService = battleService;
    }

    @PostMapping("/{battleId}/turns")
    public PlayerTurnResponse playTurn(@PathVariable String battleId,
                                        @Valid @RequestBody PlayerTurnRequest request) {
        return battleService.processPlayerTurn(battleId, request.moveId());
    }
}
