package net.runelite.client.plugins.hoseaplugins.api.spells;

/**
 * Taken from marcojacobsNL
 * https://github.com/marcojacobsNL/runelite-plugins/blob/master/src/main/java/com/koffee/KoffeeUtils/Runes.java
 */

import com.google.common.collect.ImmutableMap;
import lombok.Getter;

import java.util.Map;

import static net.runelite.api.ItemID.*;

public enum Runes {
    AIR(1, AIR_RUNE),
    WATER(2, WATER_RUNE),
    EARTH(3, EARTH_RUNE),
    FIRE(4, FIRE_RUNE),
    MIND(5, MIND_RUNE),
    CHAOS(6, CHAOS_RUNE),
    DEATH(7, DEATH_RUNE),
    BLOOD(8, BLOOD_RUNE),
    COSMIC(9, COSMIC_RUNE),
    NATURE(10, NATURE_RUNE),
    LAW(11, LAW_RUNE),
    BODY(12, BODY_RUNE),
    SOUL(13, SOUL_RUNE),
    ASTRAL(14, ASTRAL_RUNE),
    MIST(15, MIST_RUNE),
    MUD(16, MUD_RUNE),
    DUST(17, DUST_RUNE),
    LAVA(18, LAVA_RUNE),
    STEAM(19, STEAM_RUNE),
    SMOKE(20, SMOKE_RUNE),
    WRATH(21, WRATH_RUNE);

    private static final Map<Integer, Integer> runes;

    static {
        ImmutableMap.Builder<Integer, Integer> builder = new ImmutableMap.Builder<>();
        for (Runes rune : values()) {
            builder.put(rune.getItemId(), rune.getId());
        }
        runes = builder.build();
    }

    @Getter
    private final int id;
    @Getter
    private final int itemId;


    Runes(final int id, final int itemId) {
        this.id = id;
        this.itemId = itemId;
    }

    public static int getVarbitIndexForItemId(int itemId) {
        return runes.get(itemId);
    }

    public String getName() {
        String name = this.name();
        name = name.charAt(0) + name.substring(1).toLowerCase();
        return name;
    }
}