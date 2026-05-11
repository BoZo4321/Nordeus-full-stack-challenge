package com.bozidar.rpg.dto;

import com.bozidar.rpg.model.Move;

import java.util.List;

public record EquipMovesResponse(
        List<Move> equippedMoves,
        List<Move> learnedMoves
) {
}
