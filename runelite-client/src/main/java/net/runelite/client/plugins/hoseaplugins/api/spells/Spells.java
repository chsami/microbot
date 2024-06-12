package net.runelite.client.plugins.hoseaplugins.api.spells;

import net.runelite.api.Client;
import net.runelite.api.Varbits;
import net.runelite.client.RuneLite;
public class Spells
{
    static Client client = RuneLite.getInjector().getInstance(Client.class);

    public static boolean onCorrectSpellbook(String spellName)
    {
        int bookId = client.getVarbitValue(Varbits.SPELLBOOK);
        String spellbook = spellName.substring(0, spellName.indexOf('.'));
        if (spellbook.equalsIgnoreCase("Standard"))
        {
            return bookId == 0;
        }
        if (spellbook.equalsIgnoreCase("Ancient"))
        {
            return bookId == 1;
        }
        if (spellbook.equalsIgnoreCase("Lunar"))
        {
            return bookId == 2;
        }
        if (spellbook.equalsIgnoreCase("Necromancy"))
        {
            return bookId == 3;
        }
        return false;
    }


    public static int spellLevelForName(String spellName)
    {
        String spellbook = spellName.substring(0, spellName.indexOf('.'));
        String spell = spellName.substring(spellbook.length() + 1);

        if (spellbook.equalsIgnoreCase("Standard"))
        {
            StandardSpell standardSpell = standardSpellForName(spell);
            if (standardSpell != null)
            {
                return standardSpell.getLevel();
            }
        }

        if (spellbook.equalsIgnoreCase("Ancient"))
        {
            AncientSpell ancientSpell = ancientSpellForName(spell);
            if (ancientSpell != null)
            {
                return ancientSpell.getLevel();
            }
        }

        if (spellbook.equalsIgnoreCase("Lunar"))
        {
            LunarSpell lunarSpell = lunarSpellForName(spell);
            if (lunarSpell != null)
            {
                return lunarSpell.getLevel();
            }
        }

        if (spellbook.equalsIgnoreCase("Necromancy"))
        {
            NecromancySpell necromancySpell = necromancySpellForName(spell);
            if (necromancySpell != null)
            {
                return necromancySpell.getLevel();
            }
        }

        return -1;
    }

    public static WidgetInfo spellInfoForName(String spellName)
    {
        String spellbook = spellName.substring(0, spellName.indexOf('.'));
        String spell = spellName.substring(spellbook.length() + 1);

        if (spellbook.equalsIgnoreCase("Standard"))
        {
            StandardSpell standardSpell = standardSpellForName(spell);
            if (standardSpell != null)
            {
                return standardSpell.getWidget();
            }
        }

        if (spellbook.equalsIgnoreCase("Ancient"))
        {
            AncientSpell ancientSpell = ancientSpellForName(spell);
            if (ancientSpell != null)
            {
                return ancientSpell.getWidget();
            }
        }

        if (spellbook.equalsIgnoreCase("Lunar"))
        {
            LunarSpell lunarSpell = lunarSpellForName(spell);
            if (lunarSpell != null)
            {
                return lunarSpell.getWidget();
            }
        }

        if (spellbook.equalsIgnoreCase("Necromancy"))
        {
            NecromancySpell necromancySpell = necromancySpellForName(spell);
            if (necromancySpell != null)
            {
                return necromancySpell.getWidget();
            }
        }

        return null;
    }

    public static StandardSpell standardSpellForName(String spell)
    {
        for (StandardSpell standardSpell : StandardSpell.values())
        {
            if (standardSpell.name().replaceAll("_", " ").equalsIgnoreCase(spell.replaceAll("_", " ")))
            {
                return standardSpell;
            }
        }

        return null;
    }

    public static AncientSpell ancientSpellForName(String spell)
    {
        for (AncientSpell ancientSpell : AncientSpell.values())
        {
            if (ancientSpell.name().replaceAll("_", " ").equalsIgnoreCase(spell.replaceAll("_", " ")))
            {
                return ancientSpell;
            }
        }

        return null;
    }

    public static LunarSpell lunarSpellForName(String spell)
    {
        for (LunarSpell lunarSpell : LunarSpell.values())
        {
            if (lunarSpell.name().replaceAll("_", " ").equalsIgnoreCase(spell.replaceAll("_", " ")))
            {
                return lunarSpell;
            }
        }

        return null;
    }

    public static NecromancySpell necromancySpellForName(String spell)
    {
        for (NecromancySpell necromancySpell : NecromancySpell.values())
        {
            if (necromancySpell.name().replaceAll("_", " ").equalsIgnoreCase(spell.replaceAll("_", " ")))
            {
                return necromancySpell;
            }
        }

        return null;
    }

    public enum StandardSpell
    {
        HOME_TELEPORT(
                0,
                WidgetInfo.SPELL_LUMBRIDGE_HOME_TELEPORT
        ),
        VARROCK_TELEPORT(
                25,
                WidgetInfo.SPELL_VARROCK_TELEPORT
        ),
        LUMBRIDGE_TELEPORT(
                31,
                WidgetInfo.SPELL_LUMBRIDGE_TELEPORT
        ),
        FALADOR_TELEPORT(
                37,
                WidgetInfo.SPELL_FALADOR_TELEPORT
        ),
        TELEPORT_TO_HOUSE(
                40,
                WidgetInfo.SPELL_TELEPORT_TO_HOUSE
        ),
        CAMELOT_TELEPORT(
                45,
                WidgetInfo.SPELL_CAMELOT_TELEPORT
        ),
        TELEPORT_TO_KOUREND(
                48,
                WidgetInfo.SPELL_KOUREND_HOME_TELEPORT
        ),
        ARDOUGNE_TELEPORT(
                51,
                WidgetInfo.SPELL_ARDOUGNE_TELEPORT
        ),
        CIVITAS_ILLA_FORTIS_TELEPORT(
                54,
                WidgetInfo.SPELL_CIVITAS_ILLA_FORTIS_TELEPORT
        ),
        WATCHTOWER_TELEPORT(
                58,
                WidgetInfo.SPELL_WATCHTOWER_TELEPORT
        ),
        TROLLHEIM_TELEPORT(
                61,
                WidgetInfo.SPELL_TROLLHEIM_TELEPORT
        ),
        TELEPORT_TO_APE_ATOLL(
                64,
                WidgetInfo.SPELL_TELEPORT_TO_APE_ATOLL
        ),
        TELEOTHER_LUMBRIDGE(
                74,
                WidgetInfo.SPELL_TELEOTHER_LUMBRIDGE
        ),
        TELEOTHER_FALADOR(
                82,
                WidgetInfo.SPELL_TELEOTHER_FALADOR
        ),
        TELEPORT_TO_BOUNTY_TARGET(
                85,
                WidgetInfo.SPELL_BOUNTY_TARGET_TELEPORT
        ),
        TELEOTHER_CAMELOT(
                90,
                WidgetInfo.SPELL_TELEOTHER_CAMELOT
        ),

        // Strike spells
        WIND_STRIKE(
                1,
                WidgetInfo.SPELL_WIND_STRIKE
        ),
        WATER_STRIKE(
                5,
                WidgetInfo.SPELL_WATER_STRIKE
        ),
        EARTH_STRIKE(
                9,
                WidgetInfo.SPELL_EARTH_STRIKE
        ),
        FIRE_STRIKE(
                13,
                WidgetInfo.SPELL_FIRE_STRIKE
        ),

        // Bolt spells
        WIND_BOLT(
                17,
                WidgetInfo.SPELL_WIND_BOLT
        ),
        WATER_BOLT(
                23,
                WidgetInfo.SPELL_WATER_BOLT
        ),
        EARTH_BOLT(
                29,
                WidgetInfo.SPELL_EARTH_BOLT
        ),
        FIRE_BOLT(
                35,
                WidgetInfo.SPELL_FIRE_BOLT
        ),

        // Blast spells
        WIND_BLAST(
                41,
                WidgetInfo.SPELL_WIND_BLAST
        ),
        WATER_BLAST(
                47,
                WidgetInfo.SPELL_WATER_BLAST
        ),
        EARTH_BLAST(
                53,
                WidgetInfo.SPELL_EARTH_BLAST
        ),
        FIRE_BLAST(
                59,
                WidgetInfo.SPELL_FIRE_BLAST
        ),

        // Wave spells
        WIND_WAVE(
                62,
                WidgetInfo.SPELL_WIND_WAVE
        ),
        WATER_WAVE(
                65,
                WidgetInfo.SPELL_WATER_WAVE
        ),
        EARTH_WAVE(
                70,
                WidgetInfo.SPELL_EARTH_WAVE
        ),
        FIRE_WAVE(
                75,
                WidgetInfo.SPELL_FIRE_WAVE
        ),

        // Surge spells
        WIND_SURGE(
                81,
                WidgetInfo.SPELL_WIND_SURGE
        ),
        WATER_SURGE(
                85,
                WidgetInfo.SPELL_WATER_SURGE
        ),
        EARTH_SURGE(
                90,
                WidgetInfo.SPELL_EARTH_SURGE
        ),
        FIRE_SURGE(
                95,
                WidgetInfo.SPELL_FIRE_SURGE
        ),

        // God spells
        SARADOMIN_STRIKE(
                60,
                WidgetInfo.SPELL_SARADOMIN_STRIKE
        ),
        CLAWS_OF_GUTHIX(
                60,
                WidgetInfo.SPELL_CLAWS_OF_GUTHIX
        ),
        FLAMES_OF_ZAMORAK(
                60,
                WidgetInfo.SPELL_FLAMES_OF_ZAMORAK
        ),

        // Other combat spells
        CRUMBLE_UNDEAD(
                39,
                WidgetInfo.SPELL_CRUMBLE_UNDEAD
        ),
        IBAN_BLAST(
                50,
                WidgetInfo.SPELL_IBAN_BLAST
        ),
        MAGIC_DART(
                50,
                WidgetInfo.SPELL_MAGIC_DART
        ),

        // Curse spells
        CONFUSE(
                3,
                WidgetInfo.SPELL_CONFUSE
        ),
        WEAKEN(
                11,
                WidgetInfo.SPELL_WEAKEN
        ),
        CURSE(
                19,
                WidgetInfo.SPELL_CURSE
        ),
        BIND(
                20,
                WidgetInfo.SPELL_BIND
        ),
        SNARE(
                50,
                WidgetInfo.SPELL_SNARE
        ),
        VULNERABILITY(
                66,
                WidgetInfo.SPELL_VULNERABILITY
        ),
        ENFEEBLE(
                73,
                WidgetInfo.SPELL_ENFEEBLE
        ),
        ENTANGLE(
                79,
                WidgetInfo.SPELL_ENTANGLE
        ),
        STUN(
                80,
                WidgetInfo.SPELL_STUN
        ),
        TELE_BLOCK(
                85,
                WidgetInfo.SPELL_TELE_BLOCK
        ),

        // Support spells
        CHARGE(
                80,
                WidgetInfo.SPELL_CHARGE
        ),

        // Utility spells
        BONES_TO_BANANAS(
                15,
                WidgetInfo.SPELL_BONES_TO_BANANAS
        ),
        LOW_LEVEL_ALCHEMY(
                21,
                WidgetInfo.SPELL_LOW_LEVEL_ALCHEMY
        ),
        SUPERHEAT_ITEM(
                43,
                WidgetInfo.SPELL_SUPERHEAT_ITEM
        ),
        HIGH_LEVEL_ALCHEMY(
                55,
                WidgetInfo.SPELL_HIGH_LEVEL_ALCHEMY
        ),
        BONES_TO_PEACHES(
                60,
                WidgetInfo.SPELL_BONES_TO_PEACHES
        ),

        // Enchantment spells
        LVL_1_ENCHANT(
                7,
                WidgetInfo.SPELL_LVL_1_ENCHANT
        ),
        LVL_2_ENCHANT(
                27,
                WidgetInfo.SPELL_LVL_2_ENCHANT
        ),
        LVL_3_ENCHANT(
                49,
                WidgetInfo.SPELL_LVL_3_ENCHANT
        ),
        CHARGE_WATER_ORB(
                56,
                WidgetInfo.SPELL_CHARGE_WATER_ORB
        ),
        LVL_4_ENCHANT(
                57,
                WidgetInfo.SPELL_LVL_4_ENCHANT
        ),
        CHARGE_EARTH_ORB(
                60,
                WidgetInfo.SPELL_CHARGE_EARTH_ORB
        ),
        CHARGE_FIRE_ORB(
                63,
                WidgetInfo.SPELL_CHARGE_FIRE_ORB
        ),
        CHARGE_AIR_ORB(
                66,
                WidgetInfo.SPELL_CHARGE_AIR_ORB
        ),
        LVL_5_ENCHANT(
                68,
                WidgetInfo.SPELL_LVL_5_ENCHANT
        ),
        LVL_6_ENCHANT(
                87,
                WidgetInfo.SPELL_LVL_6_ENCHANT
        ),
        LVL_7_ENCHANT(
                93,
                WidgetInfo.SPELL_LVL_7_ENCHANT
        ),

        // Other spells
        TELEKINETIC_GRAB(
                31,
                WidgetInfo.SPELL_TELEKINETIC_GRAB
        ),
        ;

        private final int level;
        private final WidgetInfo widgetInfo;

        StandardSpell(int level, WidgetInfo widgetInfo)
        {
            this.level = level;
            this.widgetInfo = widgetInfo;
        }

        public int getLevel()
        {
            return level;
        }

        public WidgetInfo getWidget()
        {
            return widgetInfo;
        }
    }

    public enum AncientSpell
    {
        // Teleport spells
        EDGEVILLE_HOME_TELEPORT(
                0,
                WidgetInfo.SPELL_EDGEVILLE_HOME_TELEPORT
        ),
        PADDEWWA_TELEPORT(
                54,
                WidgetInfo.SPELL_PADDEWWA_TELEPORT
        ),
        SENNTISTEN_TELEPORT(
                60,
                WidgetInfo.SPELL_SENNTISTEN_TELEPORT
        ),
        KHARYRLL_TELEPORT(
                66,
                WidgetInfo.SPELL_KHARYRLL_TELEPORT
        ),
        LASSAR_TELEPORT(
                72,
                WidgetInfo.SPELL_LASSAR_TELEPORT
        ),
        DAREEYAK_TELEPORT(
                78,
                WidgetInfo.SPELL_DAREEYAK_TELEPORT
        ),
        CARRALLANGER_TELEPORT(
                84,
                WidgetInfo.SPELL_CARRALLANGER_TELEPORT
        ),
        BOUNTY_TARGET_TELEPORT(
                85,
                WidgetInfo.SPELL_BOUNTY_TARGET_TELEPORT
        ),
        ANNAKARL_TELEPORT(
                90,
                WidgetInfo.SPELL_ANNAKARL_TELEPORT
        ),
        GHORROCK_TELEPORT(
                96,
                WidgetInfo.SPELL_GHORROCK_TELEPORT
        ),

        // Rush Spells
        SMOKE_RUSH(
                50,
                WidgetInfo.SPELL_SMOKE_RUSH
        ),
        SHADOW_RUSH(
                52,
                WidgetInfo.SPELL_SHADOW_RUSH
        ),
        BLOOD_RUSH(
                56,
                WidgetInfo.SPELL_BLOOD_RUSH
        ),
        ICE_RUSH(
                58,
                WidgetInfo.SPELL_ICE_RUSH
        ),

        // Burst Spells
        SMOKE_BURST(
                62,
                WidgetInfo.SPELL_SMOKE_BURST
        ),
        SHADOW_BURST(
                64,
                WidgetInfo.SPELL_SHADOW_BURST
        ),
        BLOOD_BURST(
                68,
                WidgetInfo.SPELL_BLOOD_BURST
        ),
        ICE_BURST(
                70,
                WidgetInfo.SPELL_ICE_BURST
        ),

        // Blitz Spells
        SMOKE_BLITZ(
                74,
                WidgetInfo.SPELL_SMOKE_BLITZ
        ),
        SHADOW_BLITZ(
                76,
                WidgetInfo.SPELL_SHADOW_BLITZ
        ),
        BLOOD_BLITZ(
                80,
                WidgetInfo.SPELL_BLOOD_BLITZ
        ),
        ICE_BLITZ(
                82,
                WidgetInfo.SPELL_ICE_BLITZ
        ),

        // Barrage Spells
        SMOKE_BARRAGE(
                86,
                WidgetInfo.SPELL_SMOKE_BARRAGE
        ),
        SHADOW_BARRAGE(
                88,
                WidgetInfo.SPELL_SHADOW_BARRAGE
        ),
        BLOOD_BARRAGE(
                92,
                WidgetInfo.SPELL_BLOOD_BARRAGE
        ),
        ICE_BARRAGE(
                94,
                WidgetInfo.SPELL_ICE_BARRAGE
        );

        private final int level;
        private final WidgetInfo widgetInfo;

        AncientSpell(int level, WidgetInfo widgetInfo)
        {
            this.level = level;
            this.widgetInfo = widgetInfo;
        }

        public int getLevel()
        {
            return level;
        }

        public WidgetInfo getWidget()
        {
            return widgetInfo;
        }

    }

    public enum LunarSpell
    {
        // Teleport spells
        LUNAR_HOME_TELEPORT(
                0,
                WidgetInfo.SPELL_LUNAR_HOME_TELEPORT
        ),
        MOONCLAN_TELEPORT(
                69,
                WidgetInfo.SPELL_MOONCLAN_TELEPORT
        ),
        TELE_GROUP_MOONCLAN(
                70,
                WidgetInfo.SPELL_TELE_GROUP_MOONCLAN
        ),
        OURANIA_TELEPORT(
                71,
                WidgetInfo.SPELL_OURANIA_TELEPORT
        ),
        WATERBIRTH_TELEPORT(
                72,
                WidgetInfo.SPELL_WATERBIRTH_TELEPORT
        ),
        TELE_GROUP_WATERBIRTH(
                73,
                WidgetInfo.SPELL_TELE_GROUP_WATERBIRTH
        ),
        BARBARIAN_TELEPORT(
                75,
                WidgetInfo.SPELL_BARBARIAN_TELEPORT
        ),
        TELE_GROUP_BARBARIAN(
                76,
                WidgetInfo.SPELL_TELE_GROUP_BARBARIAN
        ),
        KHAZARD_TELEPORT(
                78,
                WidgetInfo.SPELL_KHAZARD_TELEPORT
        ),
        TELE_GROUP_KHAZARD(
                79,
                WidgetInfo.SPELL_TELE_GROUP_KHAZARD
        ),
        FISHING_GUILD_TELEPORT(
                85,
                WidgetInfo.SPELL_FISHING_GUILD_TELEPORT
        ),
        TELE_GROUP_FISHING_GUILD(
                86,
                WidgetInfo.SPELL_TELE_GROUP_FISHING_GUILD
        ),
        CATHERBY_TELEPORT(
                87,
                WidgetInfo.SPELL_CATHERBY_TELEPORT
        ),
        TELE_GROUP_CATHERBY(
                88,
                WidgetInfo.SPELL_TELE_GROUP_CATHERBY
        ),
        ICE_PLATEAU_TELEPORT(
                89,
                WidgetInfo.SPELL_ICE_PLATEAU_TELEPORT
        ),
        TELE_GROUP_ICE_PLATEAU(
                90,
                WidgetInfo.SPELL_TELE_GROUP_ICE_PLATEAU
        ),

        // Combat spells
        MONSTER_EXAMINE(
                66,
                WidgetInfo.SPELL_MONSTER_EXAMINE
        ),
        CURE_OTHER(
                66,
                WidgetInfo.SPELL_CURE_OTHER
        ),
        CURE_ME(
                66,
                WidgetInfo.SPELL_CURE_ME
        ),
        CURE_GROUP(
                66,
                WidgetInfo.SPELL_CURE_GROUP
        ),
        STAT_SPY(
                66,
                WidgetInfo.SPELL_STAT_SPY
        ),
        DREAM(
                66,
                WidgetInfo.SPELL_DREAM
        ),
        STAT_RESTORE_POT_SHARE(
                66,
                WidgetInfo.SPELL_STAT_RESTORE_POT_SHARE
        ),
        BOOST_POTION_SHARE(
                66,
                WidgetInfo.SPELL_BOOST_POTION_SHARE
        ),
        ENERGY_TRANSFER(
                66,
                WidgetInfo.SPELL_ENERGY_TRANSFER
        ),
        HEAL_OTHER(
                66,
                WidgetInfo.SPELL_HEAL_OTHER
        ),
        VENGEANCE_OTHER(
                66,
                WidgetInfo.SPELL_VENGEANCE_OTHER
        ),
        VENGEANCE(
                66,
                WidgetInfo.SPELL_VENGEANCE
        ),
        HEAL_GROUP(
                66,
                WidgetInfo.SPELL_HEAL_GROUP
        ),

        // Utility spells
        BAKE_PIE(
                66,
                WidgetInfo.SPELL_BAKE_PIE
        ),
        GEOMANCY(
                66,
                WidgetInfo.SPELL_GEOMANCY
        ),
        CURE_PLANT(
                66,
                WidgetInfo.SPELL_CURE_PLANT
        ),
        NPC_CONTACT(
                66,
                WidgetInfo.SPELL_NPC_CONTACT
        ),
        HUMIDIFY(
                66,
                WidgetInfo.SPELL_HUMIDIFY
        ),
        HUNTER_KIT(
                66,
                WidgetInfo.SPELL_HUNTER_KIT
        ),
        SPIN_FLAX(
                66,
                WidgetInfo.SPELL_SPIN_FLAX
        ),
        SUPERGLASS_MAKE(
                66,
                WidgetInfo.SPELL_SUPERGLASS_MAKE
        ),
        TAN_LEATHER(
                66,
                WidgetInfo.SPELL_TAN_LEATHER
        ),
        STRING_JEWELLERY(
                66,
                WidgetInfo.SPELL_STRING_JEWELLERY
        ),
        MAGIC_IMBUE(
                66,
                WidgetInfo.SPELL_MAGIC_IMBUE
        ),
        FERTILE_SOIL(
                66,
                WidgetInfo.SPELL_FERTILE_SOIL
        ),
        PLANK_MAKE(
                66,
                WidgetInfo.SPELL_PLANK_MAKE
        ),
        RECHARGE_DRAGONSTONE(
                66,
                WidgetInfo.SPELL_RECHARGE_DRAGONSTONE
        ),
        SPELLBOOK_SWAP(
                66,
                WidgetInfo.SPELL_SPELLBOOK_SWAP
        ),
        ;

        private final int level;
        private final WidgetInfo widgetInfo;

        LunarSpell(int level, WidgetInfo widgetInfo)
        {
            this.level = level;
            this.widgetInfo = widgetInfo;
        }

        public int getLevel()
        {
            return level;
        }

        public WidgetInfo getWidget()
        {
            return widgetInfo;
        }

    }

    public enum NecromancySpell
    {
        // Teleport spells
        ARCEUUS_HOME_TELEPORT(
                1,
                WidgetInfo.SPELL_ARCEUUS_HOME_TELEPORT
        ),
        ARCEUUS_LIBRARY_TELEPORT(
                6,
                WidgetInfo.SPELL_ARCEUUS_LIBRARY_TELEPORT
        ),
        DRAYNOR_MANOR_TELEPORT(
                17,
                WidgetInfo.SPELL_DRAYNOR_MANOR_TELEPORT
        ),
        BATTLEFRONT_TELEPORT(
                23,
                WidgetInfo.SPELL_BATTLEFRONT_TELEPORT
        ),
        MIND_ALTAR_TELEPORT(
                28,
                WidgetInfo.SPELL_MIND_ALTAR_TELEPORT
        ),
        RESPAWN_TELEPORT(
                34,
                WidgetInfo.SPELL_RESPAWN_TELEPORT
        ),
        SALVE_GRAVEYARD_TELEPORT(
                40,
                WidgetInfo.SPELL_SALVE_GRAVEYARD_TELEPORT
        ),
        FENKENSTRAINS_CASTLE_TELEPORT(
                48,
                WidgetInfo.SPELL_FENKENSTRAINS_CASTLE_TELEPORT
        ),
        WEST_ARDOUGNE_TELEPORT(
                61,
                WidgetInfo.SPELL_WEST_ARDOUGNE_TELEPORT
        ),
        HARMONY_ISLAND_TELEPORT(
                65,
                WidgetInfo.SPELL_HARMONY_ISLAND_TELEPORT
        ),
        CEMETERY_TELEPORT(
                71,
                WidgetInfo.SPELL_CEMETERY_TELEPORT
        ),
        BARROWS_TELEPORT(
                83,
                WidgetInfo.SPELL_BARROWS_TELEPORT
        ),
        APE_ATOLL_TELEPORT(
                90,
                WidgetInfo.SPELL_APE_ATOLL_TELEPORT
        ),

        // Combat spells
        GHOSTLY_GRASP(
                35,
                WidgetInfo.SPELL_GHOSTLY_GRASP
        ),
        SKELETAL_GRASP(
                56,
                WidgetInfo.SPELL_SKELETAL_GRASP
        ),
        UNDEAD_GRASP(
                79,
                WidgetInfo.SPELL_UNDEAD_GRASP
        ),
        INFERIOR_DEMONBANE(
                44,
                WidgetInfo.SPELL_INFERIOR_DEMONBANE
        ),
        SUPERIOR_DEMONBANE(
                62,
                WidgetInfo.SPELL_SUPERIOR_DEMONBANE
        ),
        DARK_DEMONBANE(
                82,
                WidgetInfo.SPELL_DARK_DEMONBANE
        ),
        LESSER_CORRUPTION(
                64,
                WidgetInfo.SPELL_LESSER_CORRUPTION
        ),
        GREATER_CORRUPTION(
                85,
                WidgetInfo.SPELL_GREATER_CORRUPTION
        ),
        RESURRECT_LESSER_GHOST(
                38,
                WidgetInfo.SPELL_RESURRECT_LESSER_GHOST
        ),
        RESURRECT_LESSER_SKELETON(
                38,
                WidgetInfo.SPELL_RESURRECT_LESSER_SKELETON
        ),
        RESURRECT_LESSER_ZOMBIE(
                38,
                WidgetInfo.SPELL_RESURRECT_LESSER_ZOMBIE
        ),
        RESURRECT_SUPERIOR_GHOST(
                57,
                WidgetInfo.SPELL_RESURRECT_SUPERIOR_GHOST
        ),
        RESURRECT_SUPERIOR_SKELETON(
                57,
                WidgetInfo.SPELL_RESURRECT_SUPERIOR_SKELETON
        ),
        RESURRECT_SUPERIOR_ZOMBIE(
                57,
                WidgetInfo.SPELL_RESURRECT_SUPERIOR_ZOMBIE
        ),
        RESURRECT_GREATER_GHOST(
                76,
                WidgetInfo.SPELL_RESURRECT_GREATER_GHOST
        ),
        RESURRECT_GREATER_SKELETON(
                76,
                WidgetInfo.SPELL_RESURRECT_GREATER_SKELETON
        ),
        RESURRECT_GREATER_ZOMBIE(
                76,
                WidgetInfo.SPELL_RESURRECT_GREATER_ZOMBIE
        ),
        DARK_LURE(
                50,
                WidgetInfo.SPELL_DARK_LURE
        ),
        MARK_OF_DARKNESS(
                59,
                WidgetInfo.SPELL_MARK_OF_DARKNESS
        ),
        WARD_OF_ARCEUUS(
                73,
                WidgetInfo.SPELL_WARD_OF_ARCEUUS
        ),

        // Utility spells
        BASIC_REANIMATION(
                16,
                WidgetInfo.SPELL_BASIC_REANIMATION
        ),
        ADEPT_REANIMATION(
                41,
                WidgetInfo.SPELL_ADEPT_REANIMATION
        ),
        EXPERT_REANIMATION(
                72,
                WidgetInfo.SPELL_EXPERT_REANIMATION
        ),
        MASTER_REANIMATION(
                90,
                WidgetInfo.SPELL_MASTER_REANIMATION
        ),
        DEMONIC_OFFERING(
                84,
                WidgetInfo.SPELL_DEMONIC_OFFERING
        ),
        SINISTER_OFFERING(
                92,
                WidgetInfo.SPELL_SINISTER_OFFERING
        ),
        SHADOW_VEIL(
                47,
                WidgetInfo.SPELL_SHADOW_VEIL
        ),
        VILE_VIGOUR(
                66,
                WidgetInfo.SPELL_VILE_VIGOUR
        ),
        DEGRIME(
                70,
                WidgetInfo.SPELL_DEGRIME
        ),
        RESURRECT_CROPS(
                78,
                WidgetInfo.SPELL_RESURRECT_CROPS
        ),
        DEATH_CHARGE(
                80,
                WidgetInfo.SPELL_DEATH_CHARGE
        ),
        ;

        private final int level;
        private final WidgetInfo widgetInfo;

        NecromancySpell(int level, WidgetInfo widgetInfo)
        {
            this.level = level;
            this.widgetInfo = widgetInfo;
        }

        public int getLevel()
        {
            return level;
        }

        public WidgetInfo getWidget()
        {
            return widgetInfo;
        }
    }
}
