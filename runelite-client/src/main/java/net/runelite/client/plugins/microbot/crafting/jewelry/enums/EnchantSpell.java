package net.runelite.client.plugins.microbot.crafting.jewelry.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.runelite.api.ItemID;
import net.runelite.client.plugins.skillcalculator.skills.MagicAction;

import java.util.Map;
import java.util.Set;

@Getter
@RequiredArgsConstructor
public enum EnchantSpell {
    LEVEL_1(MagicAction.ENCHANT_SAPPHIRE_JEWELLERY, Set.of(Map.of(ItemID.WATER_RUNE, 1), Map.of(ItemID.COSMIC_RUNE, 1))),
    LEVEL_2(MagicAction.ENCHANT_EMERALD_JEWELLERY, Set.of(Map.of(ItemID.AIR_RUNE, 3), Map.of(ItemID.COSMIC_RUNE, 1))),
    LEVEL_3(MagicAction.ENCHANT_RUBY_JEWELLERY, Set.of(Map.of(ItemID.FIRE_RUNE, 5), Map.of(ItemID.COSMIC_RUNE, 1))),
    LEVEL_4(MagicAction.ENCHANT_DIAMOND_JEWELLERY, Set.of(Map.of(ItemID.EARTH_RUNE, 10), Map.of(ItemID.COSMIC_RUNE, 1))),
    LEVEL_5(MagicAction.ENCHANT_DRAGONSTONE_JEWELLERY, Set.of(Map.of(ItemID.WATER_RUNE, 15), Map.of(ItemID.EARTH_RUNE, 15), Map.of(ItemID.COSMIC_RUNE, 1))),
    LEVEL_6(MagicAction.ENCHANT_ONYX_JEWELLERY, Set.of(Map.of(ItemID.FIRE_RUNE, 20), Map.of(ItemID.EARTH_RUNE, 20), Map.of(ItemID.COSMIC_RUNE, 1))),
    LEVEL_7(MagicAction.ENCHANT_ZENYTE_JEWELLERY, Set.of(Map.of(ItemID.SOUL_RUNE, 20), Map.of(ItemID.BLOOD_RUNE, 20), Map.of(ItemID.COSMIC_RUNE, 1)));
    
    private final MagicAction magicAction;
    private final Set<Map<Integer, Integer>> requiredRunes;
}
