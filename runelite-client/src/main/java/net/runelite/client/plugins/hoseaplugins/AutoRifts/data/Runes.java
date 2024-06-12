package net.runelite.client.plugins.hoseaplugins.AutoRifts.data;

/**
 * Taken from marcojacobsNL
 * https://github.com/marcojacobsNL/runelite-plugins/blob/master/src/main/java/com/koffee/KoffeeUtils/Runes.java
 */

import com.google.common.collect.ImmutableMap;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.awt.image.BufferedImage;
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

    private static final Map<Integer, Runes> runes;

    static {
        ImmutableMap.Builder<Integer, Runes> builder = new ImmutableMap.Builder<>();
        for (Runes rune : values()) {
            builder.put(rune.getId(), rune);
        }
        runes = builder.build();
    }

    @Getter(AccessLevel.PACKAGE)
    private final int id;
    @Getter(AccessLevel.PACKAGE)
    private final int itemId;
    @Getter(AccessLevel.PACKAGE)
    @Setter(AccessLevel.PACKAGE)
    private BufferedImage image;

    Runes(final int id, final int itemId) {
        this.id = id;
        this.itemId = itemId;
    }

    public static Runes getRuneVarbit(int runeID) {
        return runes.get(runeID);
    }

    public String getName() {
        String name = this.name();
        name = name.substring(0, 1) + name.substring(1).toLowerCase();
        return name;
    }
}