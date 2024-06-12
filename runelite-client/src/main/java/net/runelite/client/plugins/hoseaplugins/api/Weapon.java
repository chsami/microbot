package net.runelite.client.plugins.hoseaplugins.api;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.runelite.api.ItemID;
import java.util.Arrays;
import java.util.List;

@Getter
@RequiredArgsConstructor
public enum Weapon
{
    NOTHING(List.of(-1), WeaponType.OTHER),

    // Ranged Weapons
    BOW_OF_FAERDHINEN(List.of(
        ItemID.BOW_OF_FAERDHINEN,
        ItemID.BOW_OF_FAERDHINEN_27187,
        ItemID.BOW_OF_FAERDHINEN_C,
        ItemID.BOW_OF_FAERDHINEN_C_25869,
        ItemID.BOW_OF_FAERDHINEN_C_25884,
        ItemID.BOW_OF_FAERDHINEN_C_25886,
        ItemID.BOW_OF_FAERDHINEN_C_25888,
        ItemID.BOW_OF_FAERDHINEN_C_25890,
        ItemID.BOW_OF_FAERDHINEN_C_25892,
        ItemID.BOW_OF_FAERDHINEN_C_25894,
        ItemID.BOW_OF_FAERDHINEN_C_25896
    ), WeaponType.RANGED),
    TWISTED_BOW(List.of(
            ItemID.TWISTED_BOW
    ), WeaponType.RANGED),
    DRAGON_CROSSBOW(List.of(
            ItemID.DRAGON_CROSSBOW,
            ItemID.DRAGON_CROSSBOW_CR
    ), WeaponType.RANGED),
    RUNE_CROSSBOW(List.of(
            ItemID.RUNE_CROSSBOW,
            ItemID.RUNE_CROSSBOW_OR,
            ItemID.RUNE_CROSSBOW_23601
    ), WeaponType.RANGED),
    TOXIC_BLOWPIPE(List.of(
            ItemID.TOXIC_BLOWPIPE,
            ItemID.BLAZING_BLOWPIPE
    ), WeaponType.RANGED),
    ARMADYL_CROSSBOW(List.of(
            ItemID.ARMADYL_CROSSBOW,
            ItemID.ARMADYL_CROSSBOW_23611
    ), WeaponType.RANGED),
    DRAGON_HUNTER_CROSSBOW(List.of(
            ItemID.DRAGON_HUNTER_CROSSBOW,
            ItemID.DRAGON_HUNTER_CROSSBOW_B,
            ItemID.DRAGON_HUNTER_CROSSBOW_T
    ), WeaponType.RANGED),
    DARK_BOW(List.of(
            ItemID.DARK_BOW,
            ItemID.DARK_BOW_12765,
            ItemID.DARK_BOW_12766,
            ItemID.DARK_BOW_12767,
            ItemID.DARK_BOW_12768,
            ItemID.DARK_BOW_20408
    ), WeaponType.RANGED),
    HEAVY_BALLISTA(List.of(
            ItemID.HEAVY_BALLISTA,
            ItemID.HEAVY_BALLISTA_OR,
            ItemID.HEAVY_BALLISTA_23630
    ), WeaponType.RANGED),
    LIGHT_BALLISTA(List.of(
            ItemID.LIGHT_BALLISTA,
            ItemID.LIGHT_BALLISTA_27188
    ), WeaponType.RANGED),
    ZARYTE_CROSSBOW(List.of(
            ItemID.ZARYTE_CROSSBOW,
            ItemID.ZARYTE_CROSSBOW_27186
    ), WeaponType.RANGED),
    MORRIGANS_JAVELIN(List.of(
            ItemID.MORRIGANS_JAVELIN,
            ItemID.MORRIGANS_JAVELIN_BH,
            ItemID.MORRIGANS_JAVELIN_23619,
            ItemID.MORRIGANS_JAVELIN_BHINACTIVE
    ), WeaponType.RANGED),
    MORRIGANS_THROWNAXE(List.of(
            ItemID.MORRIGANS_THROWING_AXE,
            ItemID.MORRIGANS_THROWING_AXE_BH,
            ItemID.MORRIGANS_THROWING_AXE_BHINACTIVE
    ), WeaponType.RANGED),
    DRAGON_KNIVES(List.of(
            ItemID.DRAGON_KNIFE,
            ItemID.DRAGON_KNIFE_22812,
            ItemID.DRAGON_KNIFE_22814,
            ItemID.DRAGON_KNIFE_27157,
            ItemID.DRAGON_KNIFEP,
            ItemID.DRAGON_KNIFEP_22808,
            ItemID.DRAGON_KNIFEP_22810
    ), WeaponType.RANGED),
    RUNE_KNIVES(List.of(
            ItemID.RUNE_KNIFE,
            ItemID.RUNE_KNIFEP_5660,
            ItemID.RUNE_KNIFEP_5667
    ), WeaponType.RANGED),
    ECLIPSE_ATLATL(List.of(
            ItemID.ECLIPSE_ATLATL
    ), WeaponType.RANGED),
    NORMAL_SHORTBOW(List.of(
            ItemID.SHORTBOW,
            ItemID.OAK_SHORTBOW,
            ItemID.WILLOW_SHORTBOW,
            ItemID.MAPLE_SHORTBOW,
            ItemID.YEW_SHORTBOW,
            ItemID.YEW_SHORTBOW_20401
    ), WeaponType.RANGED),
    MAGIC_SHORTBOW(List.of(
            ItemID.MAGIC_SHORTBOW,
            ItemID.MAGIC_SHORTBOW_20558,
            ItemID.MAGIC_SHORTBOW_I
    ), WeaponType.RANGED),
    HUNTER_CROSSBOW(List.of(
            ItemID.HUNTERS_CROSSBOW
    ), WeaponType.RANGED),
    HUNTER_SUNLIGHT_CROSSBOW(List.of(
            ItemID.HUNTERS_SUNLIGHT_CROSSBOW
    ), WeaponType.RANGED),
    CRAWS_BOW(List.of(
            ItemID.CRAWS_BOW
    ), WeaponType.RANGED),
    WEBWEAVER(List.of(
            ItemID.WEBWEAVER_BOW
    ), WeaponType.RANGED),
    CRYSTAL_BOW(List.of(
            ItemID.CRYSTAL_BOW,
            ItemID.CRYSTAL_BOW_24123
    ), WeaponType.RANGED),
    VENATOR_BOW(List.of(
            ItemID.VENATOR_BOW,
            ItemID.VENATOR_BOW_UNCHARGED
    ), WeaponType.RANGED),
    KARILS_CROSSBOW(List.of(
            ItemID.KARILS_CROSSBOW,
            ItemID.KARILS_CROSSBOW_100,
            ItemID.KARILS_CROSSBOW_75,
            ItemID.KARILS_CROSSBOW_50,
            ItemID.KARILS_CROSSBOW_25
    ), WeaponType.RANGED),
    DRAGON_THROWNAXE(List.of(
            ItemID.DRAGON_THROWNAXE,
            ItemID.DRAGON_THROWNAXE_21207
    ), WeaponType.RANGED),
    CHINCHOMPA(List.of(
            ItemID.CHINCHOMPA,
            ItemID.CHINCHOMPA_10033,
            ItemID.RED_CHINCHOMPA,
            ItemID.RED_CHINCHOMPA_10034,
            ItemID.BLACK_CHINCHOMPA
    ), WeaponType.RANGED),
    TOKTZ_XIL_UL(List.of(
            ItemID.TOKTZXILUL
    ), WeaponType.RANGED),
    TONALZTICS_OF_RALOS(List.of(
            ItemID.TONALZTICS_OF_RALOS
    ), WeaponType.RANGED),

    // Magic Weapons
    KODAI_WAND(List.of(
            ItemID.KODAI_WAND,
            ItemID.KODAI_WAND_23626
    ), WeaponType.MAGIC),
    ELDRITCH_NIGHTMARE_STAFF(List.of(
            ItemID.ELDRITCH_NIGHTMARE_STAFF
    ), WeaponType.MAGIC),
    HARMONIZED_NIGHTMARE_STAFF(List.of(
            ItemID.HARMONISED_NIGHTMARE_STAFF
    ), WeaponType.MAGIC),
    VOLATILE_NIGHTMARE_STAFF(List.of(
            ItemID.VOLATILE_NIGHTMARE_STAFF,
            ItemID.VOLATILE_NIGHTMARE_STAFF_25517
    ), WeaponType.MAGIC),
    NIGHTMARE_STAFF(List.of(
            ItemID.NIGHTMARE_STAFF
    ), WeaponType.MAGIC),
    ANCIENT_SCEPTRE(List.of(
            ItemID.ANCIENT_SCEPTRE,
            ItemID.ANCIENT_SCEPTRE_L
    ), WeaponType.MAGIC),
    BLOOD_ANCIENT_SCEPTRE(List.of(
            ItemID.BLOOD_ANCIENT_SCEPTRE,
            ItemID.BLOOD_ANCIENT_SCEPTRE_L,
            ItemID.BLOOD_ANCIENT_SCEPTRE_28260
    ), WeaponType.MAGIC),
    ICE_ANCIENT_SCEPTRE(List.of(
            ItemID.ICE_ANCIENT_SCEPTRE,
            ItemID.ICE_ANCIENT_SCEPTRE_L,
            ItemID.ICE_ANCIENT_SCEPTRE_28262
    ), WeaponType.MAGIC),
    ANCIENT_STAFF(List.of(
            ItemID.ANCIENT_STAFF,
            ItemID.ANCIENT_STAFF_20431
    ), WeaponType.MAGIC),
    MASTER_WAND(List.of(
            ItemID.MASTER_WAND,
            ItemID.MASTER_WAND_20560
    ), WeaponType.MAGIC),
    TOXIC_TRIDENT(List.of(
            ItemID.TRIDENT_OF_THE_SWAMP,
            ItemID.TRIDENT_OF_THE_SWAMP_E
    ), WeaponType.MAGIC),
    TRIDENT(List.of(
            ItemID.TRIDENT_OF_THE_SEAS,
            ItemID.TRIDENT_OF_THE_SEAS_FULL,
            ItemID.TRIDENT_OF_THE_SEAS_E
    ), WeaponType.MAGIC),
    WARPED_SCEPTRE(List.of(
            ItemID.WARPED_SCEPTRE
    ), WeaponType.MAGIC),
    STAFF_OF_THE_DEAD(List.of(
            ItemID.STAFF_OF_THE_DEAD,
            ItemID.STAFF_OF_THE_DEAD_23613
    ), WeaponType.MAGIC),
    TOXIC_STAFF_OF_THE_DEAD(List.of(
            ItemID.TOXIC_STAFF_OF_THE_DEAD
    ), WeaponType.MAGIC),
    AHRIMS_STAFF(List.of(
            ItemID.AHRIMS_STAFF,
            ItemID.AHRIMS_STAFF_23653,
            ItemID.AHRIMS_STAFF_100,
            ItemID.AHRIMS_STAFF_75,
            ItemID.AHRIMS_STAFF_50,
            ItemID.AHRIMS_STAFF_25
    ), WeaponType.MAGIC),
    SANGUINESTI_STAFF(List.of(
            ItemID.SANGUINESTI_STAFF,
            ItemID.HOLY_SANGUINESTI_STAFF
    ), WeaponType.MAGIC),
    THAMMARONS_SCEPTRE(List.of(
            ItemID.THAMMARONS_SCEPTRE,
            ItemID.THAMMARONS_SCEPTRE_A,
            ItemID.THAMMARONS_SCEPTRE_AU,
            ItemID.THAMMARONS_SCEPTRE_U
    ), WeaponType.MAGIC),
    ACCURSED_SCEPTRE(List.of(
            ItemID.ACCURSED_SCEPTRE,
            ItemID.ACCURSED_SCEPTRE_AU,
            ItemID.ACCURSED_SCEPTRE_A,
            ItemID.ACCURSED_SCEPTRE_U
    ), WeaponType.MAGIC),
    ZURIELS_STAFF(List.of(
            ItemID.ZURIELS_STAFF,
            ItemID.ZURIELS_STAFF_BH,
            ItemID.ZURIELS_STAFF_BHINACTIVE,
            ItemID.ZURIELS_STAFF_23617
    ), WeaponType.MAGIC),
    TOKTZMEJTAL(List.of(
            ItemID.TOKTZMEJTAL
    ), WeaponType.MAGIC),
    GUTHIX_STAFF(List.of(
            ItemID.GUTHIX_STAFF
    ), WeaponType.MAGIC),
    SARA_STAFF(List.of(
            ItemID.SARADOMIN_STAFF
    ), WeaponType.MAGIC),
    ZAMMY_STAFF(List.of(
            ItemID.ZAMORAK_STAFF
    ), WeaponType.MAGIC),
    TUMEKENS_SHADOW(List.of(
            ItemID.TUMEKENS_SHADOW
    ), WeaponType.MAGIC),
    STAFF_OF_LIGHT(List.of(
            ItemID.STAFF_OF_LIGHT
    ), WeaponType.MAGIC),
    BLUE_MOON_STAFF(List.of(
            ItemID.BLUE_MOON_SPEAR
    ), WeaponType.MAGIC),

    // MELEE WEAPONS
    VOIDWAKER(List.of(
            ItemID.VOIDWAKER,
            ItemID.VOIDWAKER_27869
    ), WeaponType.MAGIC),
    STAFF_OF_BALANCE(List.of(
            ItemID.STAFF_OF_BALANCE
    ), WeaponType.MAGIC),
    DRAGON_DAGGER(List.of(
            ItemID.DRAGON_DAGGER,
            ItemID.DRAGON_DAGGER_20407,
            ItemID.DRAGON_DAGGER_CR,
            ItemID.DRAGON_DAGGER_PCR,
            ItemID.DRAGON_DAGGER_PCR_28023,
            ItemID.DRAGON_DAGGER_PCR_28025,
            ItemID.DRAGON_DAGGERP_5680,
            ItemID.DRAGON_DAGGERP_5698
    ), WeaponType.MELEE),
    DHAROKS_AXE(List.of(
            ItemID.DHAROKS_GREATAXE,
            ItemID.DHAROKS_GREATAXE_25516,
            ItemID.DHAROKS_GREATAXE_100,
            ItemID.DHAROKS_GREATAXE_75,
            ItemID.DHAROKS_GREATAXE_50,
            ItemID.DHAROKS_GREATAXE_25
    ), WeaponType.MELEE),
    VERACS_FLAIL(List.of(
            ItemID.VERACS_FLAIL,
            ItemID.VERACS_FLAIL_27189,
            ItemID.VERACS_FLAIL_100,
            ItemID.VERACS_FLAIL_75,
            ItemID.VERACS_FLAIL_50,
            ItemID.VERACS_FLAIL_25
    ), WeaponType.MELEE),
    VESTAS_LONGSWORD(List.of(
            ItemID.VESTAS_LONGSWORD,
            ItemID.VESTAS_LONGSWORD_23615,
            ItemID.VESTAS_LONGSWORD_BH,
            ItemID.VESTAS_LONGSWORD_BHINACTIVE
    ), WeaponType.MELEE),
    STATIUS_WARHAMMER(List.of(
            ItemID.STATIUSS_WARHAMMER,
            ItemID.STATIUSS_WARHAMMER_23620,
            ItemID.STATIUSS_WARHAMMER_BH,
            ItemID.STATIUSS_WARHAMMER_BHINACTIVE
    ), WeaponType.MELEE),
    GODSWORD(List.of(
            ItemID.ARMADYL_GODSWORD,
            ItemID.ARMADYL_GODSWORD_20593,
            ItemID.ARMADYL_GODSWORD_22665,
            ItemID.ARMADYL_GODSWORD_OR,
            ItemID.ANCIENT_GODSWORD,
            ItemID.ANCIENT_GODSWORD_27184,
            ItemID.BANDOS_GODSWORD,
            ItemID.BANDOS_GODSWORD_20782,
            ItemID.BANDOS_GODSWORD_OR,
            ItemID.BANDOS_GODSWORD_21060,
            ItemID.SARADOMIN_GODSWORD,
            ItemID.SARADOMIN_GODSWORD_OR,
            ItemID.ZAMORAK_GODSWORD,
            ItemID.ZAMORAK_GODSWORD_OR
    ), WeaponType.MELEE),
    ELDER_MAUL(List.of(
            ItemID.ELDER_MAUL,
            ItemID.ELDER_MAUL_21205,
            ItemID.ELDER_MAUL_OR
    ), WeaponType.MELEE),
    DRAGON_CLAWS(List.of(
            ItemID.DRAGON_CLAWS,
            ItemID.DRAGON_CLAWS_20784,
            ItemID.DRAGON_CLAWS_CR,
            ItemID.DRAGON_CLAWS_OR
    ), WeaponType.MELEE),
    DRAGON_SCIMITAR(List.of(
            ItemID.DRAGON_SCIMITAR,
            ItemID.DRAGON_SCIMITAR_20406,
            ItemID.DRAGON_SCIMITAR_CR,
            ItemID.DRAGON_SCIMITAR_OR
    ), WeaponType.MELEE),
    GRANITE_MAUL(List.of(
            ItemID.GRANITE_MAUL,
            ItemID.GRANITE_MAUL_12848,
            ItemID.GRANITE_MAUL_20557,
            ItemID.GRANITE_MAUL_24227,
            ItemID.GRANITE_MAUL_24225
    ), WeaponType.MELEE),
    OSMUMTENS_FANG(List.of(
            ItemID.OSMUMTENS_FANG,
            ItemID.OSMUMTENS_FANG_OR
    ), WeaponType.MELEE),
    SCYTHE(List.of(
            ItemID.SCYTHE_OF_VITUR,
            ItemID.SCYTHE_OF_VITUR_22664,
            ItemID.HOLY_SCYTHE_OF_VITUR_UNCHARGED,
            ItemID.SANGUINE_SCYTHE_OF_VITUR
    ), WeaponType.MELEE),
    ZOMBIE_AXE(List.of(
            ItemID.ZOMBIE_AXE
    ), WeaponType.MELEE),
    BULWARK(List.of(
            ItemID.DINHS_BULWARK,
            ItemID.DINHS_BLAZING_BULWARK
    ), WeaponType.MELEE),
    DRAGON_WARHAMMER(List.of(
            ItemID.DRAGON_WARHAMMER,
            ItemID.DRAGON_WARHAMMER_20785,
            ItemID.DRAGON_WARHAMMER_OR,
            ItemID.DRAGON_WARHAMMER_CR
    ), WeaponType.MELEE),
    WHIP(List.of(
            ItemID.ABYSSAL_WHIP,
            ItemID.ABYSSAL_WHIP_4178,
            ItemID.ABYSSAL_WHIP_20405
    ), WeaponType.MELEE),
    TENT_WHIP(List.of(
            ItemID.ABYSSAL_TENTACLE,
            ItemID.ABYSSAL_TENTACLE_OR
    ), WeaponType.MELEE),
    ANCIENT_MACE(List.of(
            ItemID.ANCIENT_MACE
    ), WeaponType.MELEE),
    BARRELCHEST_ANCHOR(List.of(
            ItemID.BARRELCHEST_ANCHOR,
            ItemID.BARRELCHEST_ANCHOR_10888,
            ItemID.BARRELCHEST_ANCHOR_BH
    ), WeaponType.MELEE),
    ABYSSAL_BLUDGEON(List.of(
            ItemID.ABYSSAL_BLUDGEON
    ), WeaponType.MELEE),
    DRAGON_SPEAR(List.of(
            ItemID.DRAGON_SPEAR,
            ItemID.DRAGON_SPEAR_CR,
            ItemID.DRAGON_SPEAR_PCR,
            ItemID.DRAGON_SPEAR_PCR_28045,
            ItemID.DRAGON_SPEAR_PCR_28047,
            ItemID.DRAGON_SPEARP_5730,
            ItemID.DRAGON_SPEARP_5716
    ), WeaponType.MELEE),
    DRAGON_MACE(List.of(
            ItemID.DRAGON_MACE,
            ItemID.DRAGON_MACE_BH,
            ItemID.DRAGON_MACE_CR
    ), WeaponType.MELEE),
    DRAGON_SWORD(List.of(
            ItemID.DRAGON_SWORD,
            ItemID.DRAGON_SWORD_21206,
            ItemID.DRAGON_SWORD_CR
    ), WeaponType.MELEE),
    DUAL_MACUAHUITL(List.of(
            ItemID.DUAL_MACUAHUITL
    ), WeaponType.MELEE),
    SARADOMIN_SWORD(List.of(
            ItemID.SARADOMIN_SWORD,
            ItemID.SARADOMINS_BLESSED_SWORD
    ), WeaponType.MELEE),
    ABYSSAL_DAGGER(List.of(
            ItemID.ABYSSAL_DAGGER,
            ItemID.ABYSSAL_DAGGER_BH,
            ItemID.ABYSSAL_DAGGER_BHP,
            ItemID.ABYSSAL_DAGGER_P,
            ItemID.ABYSSAL_DAGGER_P_13269,
            ItemID.ABYSSAL_DAGGER_P_13271,
            ItemID.ABYSSAL_DAGGER_BHP_27865,
            ItemID.ABYSSAL_DAGGER_BHP_27867
    ), WeaponType.MELEE),
    VESTAS_SPEAR(List.of(
            ItemID.VESTAS_SPEAR,
            ItemID.VESTAS_SPEAR_BH,
            ItemID.VESTAS_SPEAR_BHINACTIVE
    ), WeaponType.MELEE),
    ZAMMY_SPEAR(List.of(
            ItemID.ZAMORAKIAN_SPEAR
    ), WeaponType.MELEE),
    ZAMMY_HASTA(List.of(
        ItemID.ZAMORAKIAN_HASTA
    ), WeaponType.MELEE),
    VIGGORAS_CHAINMACE(List.of(
        ItemID.VIGGORAS_CHAINMACE
    ), WeaponType.MELEE),
    URSINE_CHAINMACE(List.of(
            ItemID.URSINE_CHAINMACE
    ), WeaponType.MELEE),
    SOULREAPER_AXE(List.of(
            ItemID.SOULREAPER_AXE
    ), WeaponType.MELEE);


    final List<Integer> ids;
    final WeaponType weaponType;

    public static Weapon getWeaponForId(int id)
    {
        return Arrays.stream(Weapon.values()).filter(wep -> wep.getIds().stream().anyMatch(i -> i == id)).findFirst().orElse(NOTHING);
    }
}

