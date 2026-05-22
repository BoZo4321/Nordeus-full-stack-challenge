package com.bozidar.rpg.repository;

import com.bozidar.rpg.model.BattleState;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryBattleRepository {

    private final Map<String, BattleState> battles = new ConcurrentHashMap<>();

    public BattleState save(BattleState battle) {
        battles.put(battle.getBattleId(), battle);
        return battle;
    }

    public BattleState findById(String battleId) {
        return battles.get(battleId);
    }

    public boolean exists(String battleId) {
        return battles.containsKey(battleId);
    }

    public void delete(String battleId) {
        battles.remove(battleId);
    }
}
