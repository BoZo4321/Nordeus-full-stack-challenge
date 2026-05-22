package com.bozidar.rpg.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;

public record EquipMovesRequest(
        @NotEmpty(message = "Must equip at least one move.")
        @Size(max = 4, message = "Cannot equip more than 4 moves.")
        List<String> moveIds
) {
}
