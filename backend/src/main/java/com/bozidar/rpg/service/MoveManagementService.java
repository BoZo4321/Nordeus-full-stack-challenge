package com.bozidar.rpg.service;

import com.bozidar.rpg.exception.InvalidMoveException;
import com.bozidar.rpg.model.Move;
import com.bozidar.rpg.model.RunState;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class MoveManagementService {

    private static final int MAX_EQUIPPED = 4;

    public void updateEquippedMoves(RunState run, List<String> moveIds) {
        if (moveIds.isEmpty()) {
            throw new InvalidMoveException("Must equip at least 1 move.");
        }
        if (moveIds.size() > MAX_EQUIPPED) {
            throw new InvalidMoveException(
                    "Cannot equip more than " + MAX_EQUIPPED + " moves."
            );
        }

        Map<String, Move> learnedByid = run.getLearnedMoves().stream()
                .collect(Collectors.toMap(Move::id, m -> m));

        for (String moveId : moveIds) {
            if (!learnedByid.containsKey(moveId)) {
                throw new InvalidMoveException("Move not learned: " + moveId);
            }
        }

        List<Move> equipped = moveIds.stream()
                .map(learnedByid::get)
                .toList();

        run.setEquippedMoves(equipped);
    }
}
