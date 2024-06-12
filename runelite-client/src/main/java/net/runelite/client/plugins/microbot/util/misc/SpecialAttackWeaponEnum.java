package net.runelite.client.plugins.microbot.util.misc;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.runelite.client.plugins.microbot.util.equipment.Rs2Equipment;

@Getter
@RequiredArgsConstructor
public enum SpecialAttackWeaponEnum {
    DRAGON_DAGGER("dragon dagger", 250, false),
    GRANITE_MAUL("granite maul", 600, true),
    DRAGON_WARHAMMER("dragon warhammer", 500, false),
    DRAGON_SWORD("dragon sword", 250, false),
    ANCIENT_GODSWORD("ancient godsword", 500, true),
    ABYSSAL_DAGGER("abyssal dagger", 250, false),
    BANDOS_GODSWORD("bandos godsword", 500, true),
    DRAGON_MACE("dragon mace", 250, false),
    ZAMORAK_GODSWORD("zamorak godsword", 500, true),
    ARMADYL_GODSWORD("armadyl godsword", 500, true),
    ARMADYL_CROSSBOW("armadyl crossbow", 500, false),
    DRAGON_CROSSBOW("dragon crossbow", 600, false),
    SARADOMIN_GODSWORD("saradomin godsword", 500, true),
    HEAVY_BALLISTA("heavy ballista", 650, true),
    DRAGON_CLAWS("dragon claws", 550, true);

    private final String name;
    private final int energyRequired;
    private final boolean is2H;

    @Override
    public String toString() {
        return name;
    }
}
