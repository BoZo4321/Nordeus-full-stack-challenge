package com.bozidar.rpg.model;

import java.util.List;

public record RunConfig(
        Hero hero,
        List<Monster> monsters
) {
}
