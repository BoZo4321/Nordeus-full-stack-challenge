package com.bozidar.rpg.dto;

import jakarta.validation.constraints.NotBlank;

public record StartBattleRequest(
        @NotBlank String monsterId
) {
}
