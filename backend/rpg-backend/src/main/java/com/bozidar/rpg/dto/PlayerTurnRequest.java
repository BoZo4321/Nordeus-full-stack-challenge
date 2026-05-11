package com.bozidar.rpg.dto;

import jakarta.validation.constraints.NotBlank;

public record PlayerTurnRequest(
        @NotBlank String moveId
) {
}
