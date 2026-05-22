package com.bozidar.rpg.controller;

import com.bozidar.rpg.dto.MonsterMoveRequest;
import com.bozidar.rpg.dto.MonsterMoveResponse;
import com.bozidar.rpg.service.MonsterAiService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MonsterMoveController {
    private final MonsterAiService monsterAiService;

    public MonsterMoveController(MonsterAiService monsterAiService) {
        this.monsterAiService = monsterAiService;
    }

    @PostMapping("/api/monster/next-move")
    public MonsterMoveResponse getNextMonsterMove(@RequestBody MonsterMoveRequest request) {
        return monsterAiService.chooseNextMove(request);
    }
}
