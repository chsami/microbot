package net.runelite.client.plugins.hoseaplugins.lucidgauntlet.resource;

import lombok.AccessLevel;
import lombok.Getter;
import net.runelite.api.ItemID;

import java.util.Collections;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

enum Resource
{
    TELEPORT_CRYSTAL("Teleport crystal", ItemID.TELEPORT_CRYSTAL, null, false),
    CORRUPTED_TELEPORT_CRYSTAL("Corrupted teleport crystal", ItemID.CORRUPTED_TELEPORT_CRYSTAL, null, true),

    WEAPON_FRAME("Weapon frame", ItemID.WEAPON_FRAME_23871, null, false),
    CORRUPTED_WEAPON_FRAME("Weapon frame", ItemID.WEAPON_FRAME, null, true),

    CRYSTALLINE_BOWSTRING("Crystalline bowstring", ItemID.CRYSTALLINE_BOWSTRING, null, false),
    CORRUPTED_BOWSTRING("Corrupted bowstring", ItemID.CORRUPTED_BOWSTRING, null, true),

    CRYSTAL_SPIKE("Crystal spike", ItemID.CRYSTAL_SPIKE, null, false),
    CORRUPTED_SPIKE("Corrupted spike", ItemID.CORRUPTED_SPIKE, null, true),

    CRYSTAL_ORB("Crystal orb", ItemID.CRYSTAL_ORB, null, false),
    CORRUPTED_ORB("Corrupted orb", ItemID.CORRUPTED_ORB, null, true),

    RAW_PADDLEFISH("Raw paddlefish", ItemID.RAW_PADDLEFISH, "You manage to catch a fish\\.", false),

    CRYSTAL_SHARDS("Crystal shards", ItemID.CRYSTAL_SHARDS, "You find (\\d+) crystal shards\\.", false),
    CORRUPTED_SHARDS("Corrupted shards", ItemID.CORRUPTED_SHARDS, "You find (\\d+) corrupted shards\\.", true),

    CRYSTAL_ORE("Crystal ore", ItemID.CRYSTAL_ORE, "You manage to mine some ore\\.", false),
    CORRUPTED_ORE("Corrupted ore", ItemID.CORRUPTED_ORE, "You manage to mine some ore\\.", true),

    PHREN_BARK("Phren bark", ItemID.PHREN_BARK_23878, "You get some bark\\.", false),
    CORRUPTED_PHREN_BARK("Phren bark", ItemID.PHREN_BARK, "You get some bark\\.", true),

    LINUM_TIRINUM("Linum tirinum", ItemID.LINUM_TIRINUM_23876, "You pick some fibre from the plant\\.", false),
    CORRUPTED_LINUM_TIRINUM("Linum tirinum", ItemID.LINUM_TIRINUM, "You pick some fibre from the plant\\.", true),

    GRYM_LEAF("Grym leaf", ItemID.GRYM_LEAF_23875, "You pick a herb from the roots\\.", false),
    CORRUPTED_GRYM_LEAF("Grym leaf", ItemID.GRYM_LEAF, "You pick a herb from the roots\\.", true),
    ;

    private final String name;

    @Getter(AccessLevel.PACKAGE)
    private final Pattern pattern;

    @Getter(AccessLevel.PACKAGE)
    private final int itemId;

    @Getter(AccessLevel.PACKAGE)
    private final boolean corrupted;

    Resource(final String name, final int itemId, final String pattern, final boolean corrupted)
    {
        this.name = name;
        this.itemId = itemId;
        this.corrupted = corrupted;
        this.pattern = pattern != null ? Pattern.compile(pattern) : null;
    }

    static Resource fromName(final String name, final boolean corrupted)
    {
        for (final Resource resource : values())
        {
            if (resource.name.equals(name) && (corrupted == resource.corrupted || resource == RAW_PADDLEFISH))
            {
                return resource;
            }
        }

        return null;
    }

    static Map<Resource, Integer> fromPattern(final String pattern, final boolean corrupted)
    {
        for (final Resource resource : Resource.values())
        {
            if (resource.pattern == null ||
                    (corrupted != resource.corrupted && resource != Resource.RAW_PADDLEFISH))
            {
                continue;
            }

            final Matcher matcher = resource.pattern.matcher(pattern);

            if (!matcher.matches())
            {
                continue;
            }

            final int itemCount = matcher.groupCount() == 1 ? Integer.parseInt(matcher.group(1)) : 1;

            return Collections.singletonMap(resource, itemCount);
        }

        return null;
    }
}
