package net.runelite.client.plugins.hoseaplugins.api.spells;

/*
 * Copyright (c) 2017, Adam <Adam@sigterm.info>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import net.runelite.api.widgets.InterfaceID;
import net.runelite.api.widgets.WidgetInfo;

/**
 * Utility class mapping widget IDs to global constants.
 * <p>
 * The constants defined directly under the {@link net.runelite.api.widgets.WidgetID} class are
 * Widget group IDs. All child IDs are defined in sub-classes relating
 * to their group.
 * <p>
 * For a more direct group-child widget mapping, use the
 * {@link WidgetInfo} enum class.
 */
public final class WidgetID
{
    public static final int SPELLBOOK_GROUP_ID = InterfaceID.SPELLBOOK;
    static class StandardSpellBook
    {
        static final int LUMBRIDGE_HOME_TELEPORT = 7;
        static final int KOUREND_HOME_TELEPORT = 4;
        static final int CATHERBY_HOME_TELEPORT = 5;
    }

    static class AncientSpellBook
    {
        static final int EDGEVILLE_HOME_TELEPORT = 103;
    }

    static class LunarSpellBook
    {
        static final int LUNAR_HOME_TELEPORT = 104;
        static final int FERTILE_SOIL = 129;
    }

    static class ArceuusSpellBook
    {
        static final int ARCEUUS_HOME_TELEPORT = 148;
    }

    static class SpellBook
    {
        static final int FILTERED_SPELLS_BOUNDS = 3;
        static final int TOOLTIP = 190;

        // NORMAL SPELLS
        static final int LUMBRIDGE_HOME_TELEPORT = 7;
        static final int WIND_STRIKE = 8;
        static final int CONFUSE = 9;
        static final int ENCHANT_CROSSBOW_BOLT = 10;
        static final int WATER_STRIKE = 11;

        static final int JEWELLERY_ENCHANTMENTS = 12;
        static final int LVL_1_ENCHANT = 13;
        static final int LVL_2_ENCHANT = 24;
        static final int LVL_3_ENCHANT = 37;
        static final int LVL_4_ENCHANT = 46;
        static final int LVL_5_ENCHANT = 61;
        static final int LVL_6_ENCHANT = 74;
        static final int LVL_7_ENCHANT = 77;

        static final int EARTH_STRIKE = 14;
        static final int WEAKEN = 15;
        static final int FIRE_STRIKE = 16;
        static final int BONES_TO_BANANAS = 17;
        static final int WIND_BOLT = 18;
        static final int CURSE = 19;
        static final int BIND = 20;
        static final int LOW_LEVEL_ALCHEMY = 21;
        static final int WATER_BOLT = 22;
        static final int VARROCK_TELEPORT = 23;
        static final int EARTH_BOLT = 25;
        static final int LUMBRIDGE_TELEPORT = 26;
        static final int TELEKINETIC_GRAB = 27;
        static final int FIRE_BOLT = 28;
        static final int FALADOR_TELEPORT = 29;
        static final int CRUMBLE_UNDEAD = 30;
        static final int TELEPORT_TO_HOUSE = 31;
        static final int WIND_BLAST = 32;
        static final int SUPERHEAT_ITEM = 33;
        static final int CAMELOT_TELEPORT = 34;
        static final int WATER_BLAST = 35;
        static final int KOUREND_CASTLE_TELEPORT = 36;
        static final int IBAN_BLAST = 38;
        static final int SNARE = 39;
        static final int MAGIC_DART = 40;
        static final int ARDOUGNE_TELEPORT = 41;
        static final int EARTH_BLAST = 42;
        static final int CIVITAS_ILLA_FORTIS_TELEPORT = 43;
        static final int HIGH_LEVEL_ALCHEMY = 44;
        static final int CHARGE_WATER_ORB = 45;
        static final int WATCHTOWER_TELEPORT = 47;
        static final int FIRE_BLAST = 48;
        static final int CHARGE_EARTH_ORB = 49;
        static final int BONES_TO_PEACHES = 50;
        static final int SARADOMIN_STRIKE = 51;
        static final int CLAWS_OF_GUTHIX = 52;
        static final int FLAMES_OF_ZAMORAK = 53;
        static final int TROLLHEIM_TELEPORT = 54;
        static final int WIND_WAVE = 55;
        static final int CHARGE_FIRE_ORB = 56;
        static final int TELEPORT_TO_APE_ATOLL = 57;
        static final int WATER_WAVE = 58;
        static final int CHARGE_AIR_ORB = 59;
        static final int VULNERABILITY = 60;
        static final int EARTH_WAVE = 62;
        static final int ENFEEBLE = 63;
        static final int TELEOTHER_LUMBRIDGE = 64;
        static final int FIRE_WAVE = 65;
        static final int ENTANGLE = 66;
        static final int STUN = 67;
        static final int CHARGE = 68;
        static final int WIND_SURGE = 69;
        static final int TELEOTHER_FALADOR = 70;
        static final int WATER_SURGE = 71;
        static final int TELE_BLOCK = 72;
        static final int BOUNTY_TARGET_TELEPORT = 73;
        static final int TELEOTHER_CAMELOT = 75;
        static final int EARTH_SURGE = 76;
        static final int FIRE_SURGE = 78;

        // ANCIENT SPELLS
        static final int ICE_RUSH = 79;
        static final int ICE_BLITZ = 80;
        static final int ICE_BURST = 81;
        static final int ICE_BARRAGE = 82;
        static final int BLOOD_RUSH = 83;
        static final int BLOOD_BLITZ = 84;
        static final int BLOOD_BURST = 85;
        static final int BLOOD_BARRAGE = 86;
        static final int SMOKE_RUSH = 87;
        static final int SMOKE_BLITZ = 88;
        static final int SMOKE_BURST = 89;
        static final int SMOKE_BARRAGE = 90;
        static final int SHADOW_RUSH = 91;
        static final int SHADOW_BLITZ = 92;
        static final int SHADOW_BURST = 93;
        static final int SHADOW_BARRAGE = 94;
        static final int PADDEWWA_TELEPORT = 95;
        static final int SENNTISTEN_TELEPORT = 96;
        static final int KHARYRLL_TELEPORT = 97;
        static final int LASSAR_TELEPORT = 98;
        static final int DAREEYAK_TELEPORT = 99;
        static final int CARRALLANGER_TELEPORT = 100;
        static final int ANNAKARL_TELEPORT = 101;
        static final int GHORROCK_TELEPORT = 102;
        static final int EDGEVILLE_HOME_TELEPORT = 103;

        /**
         * @TODO Fix incorrect values
         */
        // LUNAR SPELLS
        static final int LUNAR_HOME_TELEPORT = 104;
        static final int BAKE_PIE = 105;
        static final int CURE_PLANT = 106;
        static final int MONSTER_EXAMINE = 107;
        static final int NPC_CONTACT = 108;
        static final int CURE_OTHER = 109;
        static final int HUMIDIFY = 110;
        static final int MOONCLAN_TELEPORT = 111;
        static final int TELE_GROUP_MOONCLAN = 112;
        static final int CURE_ME = 113;
        static final int HUNTER_KIT = 114;
        static final int WATERBIRTH_TELEPORT = 115;
        static final int TELE_GROUP_WATERBIRTH = 116;
        static final int CURE_GROUP = 117;
        static final int STAT_SPY = 118;
        static final int BARBARIAN_TELEPORT = 119;
        static final int TELE_GROUP_BARBARIAN = 120;
        static final int SUPERGLASS_MAKE = 121;
        static final int TAN_LEATHER = 122;
        static final int KHAZARD_TELEPORT = 123;
        static final int TELE_GROUP_KHAZARD = 124;
        static final int DREAM = 125;
        static final int STRING_JEWELLERY = 126;
        static final int STAT_RESTORE_POT_SHARE = 127;
        static final int MAGIC_IMBUE = 128;
        static final int FERTILE_SOIL = 129;
        static final int BOOST_POTION_SHARE = 130;
        static final int FISHING_GUILD_TELEPORT = 131;
        static final int TELE_GROUP_FISHING_GUILD = 132;
        static final int PLANK_MAKE = 133;
        static final int CATHERBY_TELEPORT = 134;
        static final int TELE_GROUP_CATHERBY = 135;
        static final int RECHARGE_DRAGONSTONE = 136;
        static final int ICE_PLATEAU_TELEPORT = 137;
        static final int TELE_GROUP_ICE_PLATEAU = 138;
        static final int ENERGY_TRANSFER = 139;
        static final int HEAL_OTHER = 140;
        static final int VENGEANCE_OTHER = 141;
        static final int VENGEANCE = 142;
        static final int HEAL_GROUP = 143;
        static final int SPELLBOOK_SWAP = 144;
        static final int GEOMANCY = 145;
        static final int SPIN_FLAX = 146;
        static final int OURANIA_TELEPORT = 147;

        // ARCEUUS SPELLS
        static final int ARCEUUS_HOME_TELEPORT = 148;
        static final int BASIC_REANIMATION = 149;
        static final int ARCEUUS_LIBRARY_TELEPORT = 150;
        static final int ADEPT_REANIMATION = 151;
        static final int EXPERT_REANIMATION = 152;
        static final int MASTER_REANIMATION = 153;
        static final int DRAYNOR_MANOR_TELEPORT = 154;
        static final int MIND_ALTAR_TELEPORT = 156;
        static final int RESPAWN_TELEPORT = 157;
        static final int SALVE_GRAVEYARD_TELEPORT = 158;
        static final int FENKENSTRAINS_CASTLE_TELEPORT = 159;
        static final int WEST_ARDOUGNE_TELEPORT = 160;
        static final int HARMONY_ISLAND_TELEPORT = 161;
        static final int CEMETERY_TELEPORT = 162;
        static final int RESURRECT_CROPS = 163;
        static final int BARROWS_TELEPORT = 164;
        static final int APE_ATOLL_TELEPORT = 165;
        static final int BATTLEFRONT_TELEPORT = 166;
        static final int INFERIOR_DEMONBANE = 167;
        static final int SUPERIOR_DEMONBANE = 168;
        static final int DARK_DEMONBANE = 169;
        static final int MARK_OF_DARKNESS = 170;
        static final int GHOSTLY_GRASP = 171;
        static final int SKELETAL_GRASP = 172;
        static final int UNDEAD_GRASP = 173;
        static final int WARD_OF_ARCEUUS = 174;
        static final int LESSER_CORRUPTION = 175;
        static final int GREATER_CORRUPTION = 176;
        static final int DEMONIC_OFFERING = 177;
        static final int SINISTER_OFFERING = 178;
        static final int DEGRIME = 179;
        static final int SHADOW_VEIL = 180;
        static final int VILE_VIGOUR = 181;
        static final int DARK_LURE = 182;
        static final int DEATH_CHARGE = 183;
        static final int RESURRECT_LESSER_GHOST = 184;
        static final int RESURRECT_LESSER_SKELETON = 185;
        static final int RESURRECT_LESSER_ZOMBIE = 186;
        static final int RESURRECT_SUPERIOR_GHOST = 187;
        static final int RESURRECT_SUPERIOR_SKELETON = 188;
        static final int RESURRECT_SUPERIOR_ZOMBIE = 189;
        static final int RESURRECT_GREATER_GHOST = 190;
        static final int RESURRECT_GREATER_SKELETON = 191;
        static final int RESURRECT_GREATER_ZOMBIE = 192;
    }
}
