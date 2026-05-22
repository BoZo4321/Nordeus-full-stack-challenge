using System;

[Serializable]
public class PlayerTurnRequest
{
    public string moveId;
}

[Serializable]
public class CharacterStats
{
    public int maxHealth;
    public int attack;
    public int defense;
    public int magic;
}

[Serializable]
public class Move
{
    public string id;
    public string name;
    public string type;
    public string effect;
    public int baseValue;
    public int durationTurns;
    public string description;
}

[Serializable]
public class EffectView
{
    public string type;
    public int magnitude;
    public int turnsRemaining;
}

[Serializable]
public class CombatantHpView
{
    public string id;
    public string name;
    public int currentHealth;
    public int maxHealth;
    public Move[] moves;
    public EffectView[] activeEffects;
}

[Serializable]
public class EncounterView
{
    public int index;
    public string monsterId;
    public string monsterName;
    public string status;
}

[Serializable]
public class HeroSummaryView
{
    public string name;
    public int level;
    public int xp;
    public CharacterStats stats;
    public Move[] equippedMoves;
    public Move[] learnedMoves;
}

[Serializable]
public class StartRunResponse
{
    public string runId;
    public HeroSummaryView hero;
    public EncounterView[] encounters;
}

[Serializable]
public class StartBattleResponse
{
    public string battleId;
    public string runId;
    public CombatantHpView hero;
    public CombatantHpView monster;
    public string battleStatus;
    public string[] battleLog;
    public int turnNumber;
}

[Serializable]
public class TurnMove
{
    public string moveId;
    public string moveName;
    public string message;
}

[Serializable]
public class BattleReward
{
    public int xpAwarded;
    public bool leveledUp;
    public Move learnedMove;
}

[Serializable]
public class PlayerTurnResponse
{
    public string battleId;
    public TurnMove playerMove;
    public TurnMove monsterMove;
    public CombatantHpView hero;
    public CombatantHpView monster;
    public string battleStatus;
    public string[] battleLog;
    public int turnNumber;
    public BattleReward reward;
}

[Serializable]
public class EquipMovesResponse
{
    public Move[] equippedMoves;
    public Move[] learnedMoves;
}

[Serializable]
public class StartBattleRequest
{
    public string monsterId;
}

[Serializable]
public class EquipMovesRequest
{
    public string[] moveIds;
}
