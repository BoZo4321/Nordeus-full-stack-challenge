package com.bozidar.rpg.service;

import com.bozidar.rpg.model.CharacterStats;
import com.bozidar.rpg.model.Hero;
import com.bozidar.rpg.model.Monster;
import com.bozidar.rpg.model.Move;
import com.bozidar.rpg.model.MoveEffect;
import com.bozidar.rpg.model.MoveType;
import com.bozidar.rpg.model.RunConfig;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RunConfigService {

    public RunConfig getRunConfig() {
        Hero hero = createHero();

        List<Monster> monsters = List.of(
                createGoblinWarrior(),
                createGiantSpider(),
                createGoblinMage(),
                createWitch(),
                createDragon()
        );

        return new RunConfig(hero, monsters);
    }

    public Hero getHeroTemplate() {
        return createHero();
    }

    public List<Monster> getMonsters() {
        return List.of(
                createGoblinWarrior(),
                createGiantSpider(),
                createGoblinMage(),
                createWitch(),
                createDragon()
        );
    }

    private Hero createHero() {
        List<Move> defaultMoves = List.of(
                new Move(
                        "slash",
                        "Slash",
                        MoveType.PHYSICAL,
                        MoveEffect.DAMAGE,
                        12,
                        0,
                        "Deals moderate physical damage. Scales with Attack and is reduced by Defense."
                ),
                new Move(
                        "shield_up",
                        "Shield Up",
                        MoveType.SUPPORT,
                        MoveEffect.BUFF_DEFENSE,
                        8,
                        2,
                        "Raises the hero's Defense for two turns."
                ),
                new Move(
                        "battle_cry",
                        "Battle Cry",
                        MoveType.SUPPORT,
                        MoveEffect.BUFF_ATTACK,
                        8,
                        2,
                        "Raises the hero's Attack for two turns."
                ),
                new Move(
                        "second_wind",
                        "Second Wind",
                        MoveType.MAGIC,
                        MoveEffect.HEAL,
                        10,
                        0,
                        "Heals the hero. Scales with Magic."
                )
        );

        return new Hero(
                "Knight",
                1,
                0,
                new CharacterStats(100, 14, 8, 10),
                defaultMoves
        );
    }

    private Monster createGoblinWarrior() {
        return new Monster(
                "goblin_warrior",
                "Goblin Warrior",
                new CharacterStats(70, 12, 5, 4),
                List.of(
                        new Move(
                                "rusty_blade",
                                "Rusty Blade",
                                MoveType.PHYSICAL,
                                MoveEffect.DAMAGE,
                                10,
                                0,
                                "Deals moderate physical damage."
                        ),
                        new Move(
                                "dirty_kick",
                                "Dirty Kick",
                                MoveType.PHYSICAL,
                                MoveEffect.DEBUFF_DEFENSE,
                                7,
                                2,
                                "Deals light physical damage and lowers the target's Defense."
                        ),
                        new Move(
                                "frenzy",
                                "Frenzy",
                                MoveType.SUPPORT,
                                MoveEffect.BUFF_ATTACK,
                                7,
                                2,
                                "Raises the user's Attack for two turns."
                        ),
                        new Move(
                                "headbutt",
                                "Headbutt",
                                MoveType.PHYSICAL,
                                MoveEffect.DAMAGE,
                                15,
                                0,
                                "Deals heavy physical damage."
                        )
                )
        );
    }

    private Monster createGiantSpider() {
        return new Monster(
                "giant_spider",
                "Giant Spider",
                new CharacterStats(85, 14, 7, 5),
                List.of(
                        new Move(
                                "bite",
                                "Bite",
                                MoveType.PHYSICAL,
                                MoveEffect.DAMAGE,
                                11,
                                0,
                                "Deals moderate physical damage."
                        ),
                        new Move(
                                "web_throw",
                                "Web Throw",
                                MoveType.PHYSICAL,
                                MoveEffect.DEBUFF_DEFENSE,
                                8,
                                2,
                                "Deals light physical damage and lowers the target's Defense."
                        ),
                        new Move(
                                "pounce",
                                "Pounce",
                                MoveType.PHYSICAL,
                                MoveEffect.DAMAGE,
                                17,
                                0,
                                "Deals heavy physical damage."
                        ),
                        new Move(
                                "skitter",
                                "Skitter",
                                MoveType.SUPPORT,
                                MoveEffect.BUFF_DEFENSE,
                                8,
                                2,
                                "Raises the user's Defense for two turns."
                        )
                )
        );
    }

    private Monster createGoblinMage() {
        return new Monster(
                "goblin_mage",
                "Goblin Mage",
                new CharacterStats(80, 7, 5, 15),
                List.of(
                        new Move(
                                "firebolt",
                                "Firebolt",
                                MoveType.MAGIC,
                                MoveEffect.DAMAGE,
                                13,
                                0,
                                "Deals moderate magic damage."
                        ),
                        new Move(
                                "arcane_surge",
                                "Arcane Surge",
                                MoveType.SUPPORT,
                                MoveEffect.BUFF_MAGIC,
                                8,
                                2,
                                "Raises the user's Magic for two turns."
                        ),
                        new Move(
                                "mana_drain",
                                "Mana Drain",
                                MoveType.MAGIC,
                                MoveEffect.DEBUFF_MAGIC,
                                8,
                                2,
                                "Deals light magic damage and lowers the target's Magic."
                        ),
                        new Move(
                                "hex_shield",
                                "Hex Shield",
                                MoveType.SUPPORT,
                                MoveEffect.BUFF_DEFENSE,
                                8,
                                2,
                                "Raises the user's Defense for two turns."
                        )
                )
        );
    }

    private Monster createWitch() {
        return new Monster(
                "witch",
                "Witch",
                new CharacterStats(95, 8, 6, 18),
                List.of(
                        new Move(
                                "shadow_bolt",
                                "Shadow Bolt",
                                MoveType.MAGIC,
                                MoveEffect.DAMAGE,
                                16,
                                0,
                                "Deals heavy magic damage."
                        ),
                        new Move(
                                "drain_life",
                                "Drain Life",
                                MoveType.MAGIC,
                                MoveEffect.DRAIN_LIFE,
                                9,
                                0,
                                "Deals light magic damage and heals the user for the same amount."
                        ),
                        new Move(
                                "curse",
                                "Curse",
                                MoveType.SUPPORT,
                                MoveEffect.DEBUFF_ATTACK,
                                8,
                                2,
                                "Lowers the target's Attack for two turns."
                        ),
                        new Move(
                                "dark_pact",
                                "Dark Pact",
                                MoveType.SUPPORT,
                                MoveEffect.SELF_DAMAGE_BUFF_MAGIC,
                                10,
                                2,
                                "Raises the user's Magic at the cost of some HP."
                        )
                )
        );
    }

    private Monster createDragon() {
        return new Monster(
                "dragon",
                "Dragon",
                new CharacterStats(130, 18, 12, 20),
                List.of(
                        new Move(
                                "flame_breath",
                                "Flame Breath",
                                MoveType.MAGIC,
                                MoveEffect.DAMAGE,
                                20,
                                0,
                                "Deals heavy magic damage."
                        ),
                        new Move(
                                "claw_swipe",
                                "Claw Swipe",
                                MoveType.PHYSICAL,
                                MoveEffect.DAMAGE,
                                16,
                                0,
                                "Deals moderate physical damage."
                        ),
                        new Move(
                                "intimidate",
                                "Intimidate",
                                MoveType.SUPPORT,
                                MoveEffect.DEBUFF_ATTACK,
                                8,
                                2,
                                "Lowers the target's Attack for two turns."
                        ),
                        new Move(
                                "dragon_scales",
                                "Dragon Scales",
                                MoveType.SUPPORT,
                                MoveEffect.BUFF_DEFENSE,
                                10,
                                2,
                                "Raises the user's Defense for two turns."
                        )
                )
        );
    }
}
