package net.runelite.client.plugins.hoseaplugins.spines;

import lombok.Getter;

@Getter
public enum SpineSkill {

    ATTACK(1, 1),
    STRENGTH(2, 1),
    DEFENCE(3, 1),
    PRAYER(4, 1),
    HITPOINTS(3, 2),
    MAGIC(2, 2),
    RANGED(1, 2);

    private final int index;
    private final int page;

    SpineSkill(int index, int page) {
        this.index = index;
        this.page = page;
    }

}
