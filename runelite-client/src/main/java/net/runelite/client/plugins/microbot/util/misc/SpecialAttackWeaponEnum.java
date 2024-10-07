package net.runelite.client.plugins.microbot.util.misc;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SpecialAttackWeaponEnum {
    DRAGON_DAGGER("dragon dagger", 250, false),
    DRAGON_SCIMITAR("dragon scimitar", 550, false),
    DRAGON_LONGSWORD("dragon longsword", 250, false),
    DRAGON_MACE("dragon mace", 250, false),
    DRAGON_BATTLEAXE("dragon battleaxe", 1000, false),
    DRAGON_WARHAMMER("dragon warhammer", 500, false),
    DRAGON_HALBERD("dragon halberd", 300, true),
    DRAGON_SPEAR("dragon spear", 250, true),
    DRAGON_2H_SWORD("dragon 2h sword", 600, true),
    DRAGON_PICKAXE("dragon pickaxe", 1000, false),
    INFERNAL_PICKAXE("infernal pickaxe", 1000, false),
    THIRD_AGE_PICKAXE("3rd age pickaxe", 1000, false),
    DRAGON_AXE("dragon axe", 1000, false),
    INFERNAL_AXE("infernal axe", 1000, false),
    THIRRD_AGE_AXE("3rd age axe", 1000, false),
    DRAGON_HARPOON("dragon harpoon", 1000, false),
    INFERNAL_HARPOON("infernal harpoon", 1000, false),
    DRAGON_CLAWS("dragon claws", 500, true),
    DRAGON_THROWNAXE("dragon thrownaxe", 250, false),
    DRAGON_SWORD("dragon sword", 400, false),
    ARMADYL_GODSWORD("armadyl godsword", 500, true),
    BANDOS_GODSWORD("bandos godsword", 500, true),
    SARADOMIN_GODSWORD("saradomin godsword", 500, true),
    ZAMORAK_GODSWORD("zamorak godsword", 500, true),
    ABYSSAL_WHIP("abyssal whip", 500, false),
    ABYSSAL_TENTACLE("abyssal tentacle", 500, false),
    ABYSSAL_DAGGER("abyssal dagger", 500, false),
    ABYSSAL_BLUDGEON("abyssal bludgeon", 500, true),
    BARRELCHEST_ANCHOR("barrelchest anchor", 500, true),
    GRANITE_HAMMER("granite hammer", 600, false),
    GRANITE_MAUL("granite maul", 500, true),
    RUNE_CLAWS("rune claws", 250, true),
    EXCALIBUR("excalibur", 1000, false),
    DARKLIGHT("darklight", 500, false),
    ARCLIGHT("arclight", 500, false),
    BONE_DAGGER("bone dagger", 750, false),
    BRINE_SABRE("brine sabre", 750, false),
    ANCIENT_MACE("ancient mace", 1000, true),
    SARADOMIN_SWORD("saradomin sword", 1000, true),
    SARADOMINS_BLESSED_SWORD("saradomin's blessed sword", 650, true),
    ZAMORAKIAN_SPEAR("zamorakian spear", 250, true),
    ZAMORAKIAN_HASTA("zamorakian hasta", 250, true),
    CRYSTAL_HALBERD("crystal halberd", 300, true),
    DINHS_BULWARK("dinh's bulwark", 500, true),
    ARMADYL_CROSSBOW("armadyl crossbow", 400, false),
    DARK_BOW("dark bow", 550, true),
    DRAGON_CROSSBOW("dragon crossbow", 600, false),
    MAGIC_SHORTBOW("magic shortbow", 550, true),
    MAGIC_SHORTBOW_I("magic shortbow (i)", 500, true),
    MAGIC_LONGBOW("magic longbow", 350, true),
    MAGIC_COMP_BOW("magic comp bow", 350, true),
    RUNE_THROWNAXE("rune thrownaxe", 100, false),
    DORGESHUUN_CROSSBOW("dorgeshuun crossbow", 750, false),
    SEERCULL("seercull", 1000, true),
    TOXIC_BLOWPIPE("toxic blowpipe", 500, true),
    LIGHT_BALLISTA("light ballista", 650, true),
    HEAVY_BALLISTA("heavy ballista", 650, true),
    STAFF_OF_THE_DEAD("staff of the dead", 1000, false),
    TOXIC_STAFF_OF_THE_DEAD("toxic staff of the dead", 1000, false),
    STAFF_OF_LIGHT("staff of light", 1000, false),
    ROD_OF_IVANDIS("rod of ivandis", 100, false),
    IVANDIS_FLAIL("ivandis flail", 100, false),
    DAWNBRINGER("dawnbringer", 350, false);



        private final String name;
        private final int energyRequired;
        private final boolean is2H;

    @Override
    public String toString() {
        return name;
    }
}
