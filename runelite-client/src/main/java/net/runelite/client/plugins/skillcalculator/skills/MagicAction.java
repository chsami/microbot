/*
 * Copyright (c) 2021, Jordan Atwood <nightfirecat@protonmail.com>
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
package net.runelite.client.plugins.skillcalculator.skills;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.runelite.api.MenuAction;
import net.runelite.api.SpriteID;
import net.runelite.client.game.ItemManager;

@AllArgsConstructor
@Getter
public enum MagicAction implements SkillAction
{
	WIND_STRIKE("Wind Strike", 1, 5.5f, SpriteID.SPELL_WIND_STRIKE, false, 14286855, MenuAction.WIDGET_TARGET),
	CONFUSE("Confuse", 3, 13, SpriteID.SPELL_CONFUSE, false,14286856, MenuAction.WIDGET_TARGET),
	ENCHANT_OPAL_BOLT("Enchant Opal Bolt", 4, 9, SpriteID.SPELL_ENCHANT_CROSSBOW_BOLT, true, 14286857, MenuAction.CC_OP),
	WATER_STRIKE("Water Strike", 5, 7.5f, SpriteID.SPELL_WATER_STRIKE, false, 14286858, MenuAction.WIDGET_TARGET),
	ARCEUUS_LIBRARY_TELEPORT("Arceuus Library Teleport", 6, 10, SpriteID.SPELL_ARCEUUS_LIBRARY_TELEPORT, true, -1, null),
	ENCHANT_SAPPHIRE_JEWELLERY("Enchant Sapphire Jewellery", 7, 17.5f, SpriteID.SPELL_LVL_1_ENCHANT, false, 14286859, null),
	ENCHANT_SAPPHIRE_BOLT("Enchant Sapphire Bolt", 7, 17.5f, SpriteID.SPELL_ENCHANT_CROSSBOW_BOLT, true, -1, null),
	EARTH_STRIKE("Earth Strike", 9, 9.5f, SpriteID.SPELL_EARTH_STRIKE, false, 14286860, MenuAction.WIDGET_TARGET),
	WEAKEN("Weaken", 11, 21, SpriteID.SPELL_WEAKEN, false, 14286861, MenuAction.WIDGET_TARGET),
	FIRE_STRIKE("Fire Strike", 13, 11.5f, SpriteID.SPELL_FIRE_STRIKE, false, 14286862, MenuAction.WIDGET_TARGET),
	ENCHANT_JADE_BOLT("Enchant Jade Bolt", 14, 19, SpriteID.SPELL_ENCHANT_CROSSBOW_BOLT, true, -1, null),
	BONES_TO_BANANAS("Bones To Bananas", 15, 25, SpriteID.SPELL_BONES_TO_BANANAS, false, 14286863, MenuAction.CC_OP),
	BASIC_REANIMATION("Basic Reanimation", 16, 32, SpriteID.SPELL_BASIC_REANIMATION, true, -1, null),
	DRAYNOR_MANOR_TELEPORT("Draynor Manor Teleport", 17, 16, SpriteID.SPELL_DRAYNOR_MANOR_TELEPORT, true, -1, null),
	WIND_BOLT("Wind Bolt", 17, 13.5f, SpriteID.SPELL_WIND_BOLT, false, 14286864, MenuAction.WIDGET_TARGET),
	CURSE("Curse", 19, 29, SpriteID.SPELL_CURSE, false, 14286865, MenuAction.WIDGET_TARGET),
	BIND("Bind", 20, 30, SpriteID.SPELL_BIND, false, 14286866, MenuAction.WIDGET_TARGET),
	LOW_LEVEL_ALCHEMY("Low Level Alchemy", 21, 31, SpriteID.SPELL_LOW_LEVEL_ALCHEMY, false, 14286867, MenuAction.WIDGET_TARGET),
	WATER_BOLT("Water Bolt", 23, 16.5f, SpriteID.SPELL_WATER_BOLT, false, 14286868, MenuAction.WIDGET_TARGET),
	ENCHANT_PEARL_BOLT("Enchant Pearl Bolt", 24, 29, SpriteID.SPELL_ENCHANT_CROSSBOW_BOLT, true, -1, null),
	VARROCK_TELEPORT("Varrock Teleport", 25, 35, SpriteID.SPELL_VARROCK_TELEPORT, false, 14286869, null),
	ENCHANT_EMERALD_JEWELLERY("Enchant Emerald Jewellery", 27, 37, SpriteID.SPELL_LVL_2_ENCHANT, false, 14286870, null),
	ENCHANT_EMERALD_BOLT("Enchant Emerald Bolt", 27, 37, SpriteID.SPELL_ENCHANT_CROSSBOW_BOLT, true, -1, null),
	MIND_ALTAR_TELEPORT("Mind Altar Teleport", 28, 22, SpriteID.SPELL_MIND_ALTAR_TELEPORT, true, -1, null),
	ENCHANT_TOPAZ_BOLT("Enchant Topaz Bolt", 29, 33, SpriteID.SPELL_ENCHANT_CROSSBOW_BOLT, true, -1, null),
	EARTH_BOLT("Earth Bolt", 29, 19.5f, SpriteID.SPELL_EARTH_BOLT, false, 14286871, MenuAction.WIDGET_TARGET),
	LUMBRIDGE_TELEPORT("Lumbridge Teleport", 31, 41, SpriteID.SPELL_LUMBRIDGE_TELEPORT, false, 14286872, null),
	TELEKINETIC_GRAB("Telekinetic Grab", 33, 43, SpriteID.SPELL_TELEKINETIC_GRAB, false, 14286873, MenuAction.WIDGET_TARGET),
	RESPAWN_TELEPORT("Respawn Teleport", 34, 27, SpriteID.SPELL_RESPAWN_TELEPORT, true, -1, null),
	FIRE_BOLT("Fire Bolt", 35, 22.5f, SpriteID.SPELL_FIRE_BOLT, false, 14286874, MenuAction.WIDGET_TARGET),
	GHOSTLY_GRASP("Ghostly Grasp", 35, 22.5f, SpriteID.SPELL_GHOSTLY_GRASP, true, -1, null),
	FALADOR_TELEPORT("Falador Teleport", 37, 48, SpriteID.SPELL_FALADOR_TELEPORT, false, 14286875, null),
	RESURRECT_LESSER_THRALL("Resurrect Lesser Thrall", 38, 55, SpriteID.SPELL_RESURRECT_LESSER_GHOST, true, -1, null),
	CRUMBLE_UNDEAD("Crumble Undead", 39, 24.5f, SpriteID.SPELL_CRUMBLE_UNDEAD, false, 14286876, MenuAction.WIDGET_TARGET),
	SALVE_GRAVEYARD_TELEPORT("Salve Graveyard Teleport", 40, 30, SpriteID.SPELL_SALVE_GRAVEYARD_TELEPORT, true, -1, null),
	TELEPORT_TO_HOUSE("Teleport To House", 40, 30, SpriteID.SPELL_TELEPORT_TO_HOUSE, true, 14286877, null),
	ADEPT_REANIMATION("Adept Reanimation", 41, 80, SpriteID.SPELL_ADEPT_REANIMATION, true, -1, null),
	WIND_BLAST("Wind Blast", 41, 25.5f, SpriteID.SPELL_WIND_BLAST, false, 14286878, MenuAction.WIDGET_TARGET),
	SUPERHEAT_ITEM("Superheat Item", 43, 53, SpriteID.SPELL_SUPERHEAT_ITEM, false, 14286879, MenuAction.CC_OP),
	INFERIOR_DEMONBANE("Inferior Demonbane", 44, 27, SpriteID.SPELL_INFERIOR_DEMONBANE, true, -1, null),
	CAMELOT_TELEPORT("Camelot Teleport", 45, 55.5f, SpriteID.SPELL_CAMELOT_TELEPORT, true, 14286880, null),
	WATER_BLAST("Water Blast", 47, 28.5f, SpriteID.SPELL_WATER_BLAST, false, 14286881, MenuAction.WIDGET_TARGET),
	SHADOW_VEIL("Shadow Veil", 47, 58, SpriteID.SPELL_SHADOW_VEIL, true, -1, null),
	FENKENSTRAINS_CASTLE_TELEPORT("Fenkenstrain's Castle Teleport", 48, 50, SpriteID.SPELL_FENKENSTRAINS_CASTLE_TELEPORT, true, -1, null),
	ENCHANT_RUBY_JEWELLERY("Enchant Ruby Jewellery", 49, 59, SpriteID.SPELL_LVL_3_ENCHANT, false, 14286882, null),
	ENCHANT_RUBY_BOLT("Enchant Ruby Bolt", 49, 59, SpriteID.SPELL_ENCHANT_CROSSBOW_BOLT, true, -1, null),
	IBAN_BLAST("Iban Blast", 50, 30, SpriteID.SPELL_IBAN_BLAST, true, 14286883, MenuAction.WIDGET_TARGET),
	SMOKE_RUSH("Smoke Rush", 50, 30, SpriteID.SPELL_SMOKE_RUSH, true, 14286932, MenuAction.WIDGET_TARGET),
	MAGIC_DART("Magic Dart", 50, 30, SpriteID.SPELL_MAGIC_DART, true, 1428685, MenuAction.WIDGET_TARGET),
	SNARE("Snare", 50, 60, SpriteID.SPELL_SNARE, false, 14286884, MenuAction.WIDGET_TARGET),
	DARK_LURE("Dark Lure", 50, 60, SpriteID.SPELL_DARK_LURE, true, -1, null),
	ARDOUGNE_TELEPORT("Ardougne Teleport", 51, 61, SpriteID.SPELL_ARDOUGNE_TELEPORT, true, 14286886, null),
	SHADOW_RUSH("Shadow Rush", 52, 31, SpriteID.SPELL_SHADOW_RUSH, true, 14286936, MenuAction.WIDGET_TARGET),
	EARTH_BLAST("Earth Blast", 53, 31.5f, SpriteID.SPELL_EARTH_BLAST, false, 14286887, MenuAction.WIDGET_TARGET),
	PADDEWWA_TELEPORT("Paddewwa Teleport", 54, 64, SpriteID.SPELL_PADDEWWA_TELEPORT, true, 14286940, null),
	HIGH_LEVEL_ALCHEMY("High Level Alchemy", 55, 65, SpriteID.SPELL_HIGH_LEVEL_ALCHEMY, false, 14286888, MenuAction.WIDGET_TARGET),
	CHARGE_WATER_ORB("Charge Water Orb", 56, 66, SpriteID.SPELL_CHARGE_WATER_ORB, true, 14286889, MenuAction.WIDGET_TARGET),
	BLOOD_RUSH("Blood Rush", 56, 33, SpriteID.SPELL_BLOOD_RUSH, true, 14286928, MenuAction.WIDGET_TARGET),
	SKELETAL_GRASP("Skeletal Grasp", 56, 33, SpriteID.SPELL_SKELETAL_GRASP, true, -1, null),
	ENCHANT_DIAMOND_JEWELLERY("Enchant Diamond Jewellery", 57, 67, SpriteID.SPELL_LVL_4_ENCHANT, false, 14286890, null),
	ENCHANT_DIAMOND_BOLT("Enchant Diamond Bolt", 57, 67, SpriteID.SPELL_ENCHANT_CROSSBOW_BOLT, true, -1, null),
	RESURRECT_SUPERIOR_THRALL("Resurrect Superior Thrall", 57, 70, SpriteID.SPELL_RESURRECT_SUPERIOR_SKELETON, true, -1, null),
	WATCHTOWER_TELEPORT("Watchtower Teleport", 58, 68, SpriteID.SPELL_WATCHTOWER_TELEPORT, true, 14286891, null),
	ICE_RUSH("Ice Rush", 58, 34, SpriteID.SPELL_ICE_RUSH, true, 14286924, MenuAction.WIDGET_TARGET),
	FIRE_BLAST("Fire Blast", 59, 34.5f, SpriteID.SPELL_FIRE_BLAST, false, 14286892, MenuAction.WIDGET_TARGET),
	MARK_OF_DARKNESS("Mark of Darkness", 59, 70, SpriteID.SPELL_MARK_OF_DARKNESS, true, -1, null),
	SENNTISTEN_TELEPORT("Senntisten Teleport", 60, 70, SpriteID.SPELL_SENNTISTEN_TELEPORT, true, 14286941, null),
	CLAWS_OF_GUTHIX("Claws Of Guthix", 60, 35, SpriteID.SPELL_CLAWS_OF_GUTHIX, true, 14286893, MenuAction.WIDGET_TARGET),
	FLAMES_OF_ZAMORAK("Flames Of Zamorak", 60, 35, SpriteID.SPELL_FLAMES_OF_ZAMORAK, true, 14286894, MenuAction.WIDGET_TARGET),
	SARADOMIN_STRIKE("Saradomin Strike", 60, 35, SpriteID.SPELL_SARADOMIN_STRIKE, true, 14286895, MenuAction.WIDGET_TARGET),
	CHARGE_EARTH_ORB("Charge Earth Orb", 60, 70, SpriteID.SPELL_CHARGE_EARTH_ORB, true, 14286896, MenuAction.WIDGET_TARGET),
	BONES_TO_PEACHES("Bones To Peaches", 60, 35.5f, SpriteID.SPELL_BONES_TO_PEACHES, true, 14286897, MenuAction.CC_OP),
	WEST_ARDOUGNE_TELEPORT("West Ardougne Teleport", 61, 68, SpriteID.SPELL_WEST_ARDOUGNE_TELEPORT, true, -1, null),
	TROLLHEIM_TELEPORT("Trollheim Teleport", 61, 68, SpriteID.SPELL_TROLLHEIM_TELEPORT, true, 14286898, null),
	SMOKE_BURST("Smoke Burst", 62, 36, SpriteID.SPELL_SMOKE_BURST, true, 14286934, MenuAction.WIDGET_TARGET),
	WIND_WAVE("Wind Wave", 62, 36, SpriteID.SPELL_WIND_WAVE, true, 14286899, MenuAction.WIDGET_TARGET),
	SUPERIOR_DEMONBANE("Superior Demonbane", 62, 36, SpriteID.SPELL_SUPERIOR_DEMONBANE, true, -1, null),
	CHARGE_FIRE_ORB("Charge Fire Orb", 63, 73, SpriteID.SPELL_CHARGE_FIRE_ORB, true, 14286900, MenuAction.WIDGET_TARGET),
	SHADOW_BURST("Shadow Burst", 64, 37, SpriteID.SPELL_SHADOW_BURST, true, 14286938, MenuAction.WIDGET_TARGET),
	TELEPORT_APE_ATOLL("Teleport Ape Atoll", 64, 74, SpriteID.SPELL_TELEPORT_TO_APE_ATOLL, true, 14286901, null),
	LESSER_CORRUPTION("Lesser Corruption", 64, 75, SpriteID.SPELL_LESSER_CORRUPTION, true, -1, null),
	BAKE_PIE("Bake Pie", 65, 60, SpriteID.SPELL_BAKE_PIE, true, 14286950, MenuAction.CC_OP),
	HARMONY_ISLAND_TELEPORT("Harmony Island Teleport", 65, 74, SpriteID.SPELL_HARMONY_ISLAND_TELEPORT, true, -1, null),
	GEOMANCY("Geomancy", 65, 60, SpriteID.SPELL_GEOMANCY, true, 14286990, MenuAction.CC_OP),
	WATER_WAVE("Water Wave", 65, 37.5f, SpriteID.SPELL_WATER_WAVE, true, 14286902, MenuAction.WIDGET_TARGET),
	CHARGE_AIR_ORB("Charge Air Orb", 66, 76, SpriteID.SPELL_CHARGE_AIR_ORB, true, 14286903, MenuAction.WIDGET_TARGET),
	CURE_PLANT("Cure Plant", 66, 60, SpriteID.SPELL_CURE_PLANT, true, 14286951, MenuAction.WIDGET_TARGET),
	KHARYRLL_TELEPORT("Kharyrll Teleport", 66, 76, SpriteID.SPELL_KHARYRLL_TELEPORT, true, 14286942, null),
	VULNERABILITY("Vulnerability", 66, 76, SpriteID.SPELL_VULNERABILITY, true, 14286904, MenuAction.WIDGET_TARGET),
	MONSTER_EXAMINE("Monster Examine", 66, 61, SpriteID.SPELL_MONSTER_EXAMINE, true, 14286952, MenuAction.WIDGET_TARGET),
	VILE_VIGOUR("Vile Vigour", 66, 76, SpriteID.SPELL_VILE_VIGOUR, true, -1, null),
	NPC_CONTACT("Npc Contact", 67, 63, SpriteID.SPELL_NPC_CONTACT, true, 14286953, MenuAction.CC_OP),
	BLOOD_BURST("Blood Burst", 68, 39, SpriteID.SPELL_BLOOD_BURST, true, 14286930, MenuAction.WIDGET_TARGET),
	CURE_OTHER("Cure Other", 68, 65, SpriteID.SPELL_CURE_OTHER, true, 14286954, MenuAction.WIDGET_TARGET),
	ENCHANT_DRAGONSTONE_JEWELLERY("Enchant Dragonstone Jewellery", 68, 78, SpriteID.SPELL_LVL_5_ENCHANT, true, 14286905, null),
	ENCHANT_DRAGONSTONE_BOLT("Enchant Dragonstone Bolt", 68, 78, SpriteID.SPELL_ENCHANT_CROSSBOW_BOLT, true, -1, null),
	HUMIDIFY("Humidify", 68, 65, SpriteID.SPELL_HUMIDIFY, true, 14286955, MenuAction.CC_OP),
	KOUREND_CASTLE_TELEPORT("Kourend Castle Teleport", 69, 81, SpriteID.SPELL_TELEPORT_TO_KOUREND, true, 14286906, null),
	MOONCLAN_TELEPORT("Moonclan Teleport", 69, 66, SpriteID.SPELL_MOONCLAN_TELEPORT, true, 14286956, null),
	EARTH_WAVE("Earth Wave", 70, 40, SpriteID.SPELL_EARTH_WAVE, true, 14286907, MenuAction.WIDGET_TARGET),
	ICE_BURST("Ice Burst", 70, 40, SpriteID.SPELL_ICE_BURST, true, 14286926, MenuAction.WIDGET_TARGET),
	TELE_GROUP_MOONCLAN("Tele Group Moonclan", 70, 67, SpriteID.SPELL_TELE_GROUP_MOONCLAN, true, 14286957, null),
	DEGRIME("Degrime", 70, 83, SpriteID.SPELL_DEGRIME, true, -1, null),
	OURANIA_TELEPORT("Ourania Teleport", 71, 69, SpriteID.SPELL_OURANIA_TELEPORT, true, 14286992, null),
	CEMETERY_TELEPORT("Cemetery Teleport", 71, 82, SpriteID.SPELL_CEMETERY_TELEPORT, true, -1, null),
	CURE_ME("Cure Me", 71, 69, SpriteID.SPELL_CURE_ME, true, 14286958, MenuAction.CC_OP),
	HUNTER_KIT("Hunter Kit", 71, 70, SpriteID.SPELL_HUNTER_KIT, true , 14286957, MenuAction.CC_OP),
	EXPERT_REANIMATION("Expert Reanimation", 72, 138, SpriteID.SPELL_EXPERT_REANIMATION, true, -1, null),
	LASSAR_TELEPORT("Lassar Teleport", 72, 82, SpriteID.SPELL_LASSAR_TELEPORT, true, 14286943, null),
	WATERBIRTH_TELEPORT("Waterbirth Teleport", 72, 71, SpriteID.SPELL_WATERBIRTH_TELEPORT, true, 14286960, null),
	TELE_GROUP_WATERBIRTH("Tele Group Waterbirth", 73, 72, SpriteID.SPELL_TELE_GROUP_WATERBIRTH, true, 14286961, null),
	ENFEEBLE("Enfeeble", 73, 83, SpriteID.SPELL_ENFEEBLE, true, 14286908, MenuAction.WIDGET_TARGET),
	WARD_OF_ARCEUUS("Ward of Arceuus", 73, 83, SpriteID.SPELL_WARD_OF_ARCEUUS, true, -1, null),
	TELEOTHER_LUMBRIDGE("Teleother Lumbridge", 74, 84, SpriteID.SPELL_TELEOTHER_LUMBRIDGE, true, 14286909, MenuAction.WIDGET_TARGET),
	SMOKE_BLITZ("Smoke Blitz", 74, 42, SpriteID.SPELL_SMOKE_BLITZ, true, 14286933, MenuAction.WIDGET_TARGET),
	CURE_GROUP("Cure Group", 74, 74, SpriteID.SPELL_CURE_GROUP, true, 14286962, MenuAction.CC_OP),
	STAT_SPY("Stat Spy", 75, 76, SpriteID.SPELL_STAT_SPY, true, 14286963, MenuAction.WIDGET_TARGET),
	BARBARIAN_TELEPORT("Barbarian Teleport", 75, 76, SpriteID.SPELL_BARBARIAN_TELEPORT, true, 14286964, null),
	FIRE_WAVE("Fire Wave", 75, 42.5f, SpriteID.SPELL_FIRE_WAVE, true, 14286910, MenuAction.WIDGET_TARGET),
	TELE_GROUP_BARBARIAN("Tele Group Barbarian", 76, 77, SpriteID.SPELL_TELE_GROUP_ICE_PLATEAU, true, 14286965, null),
	SHADOW_BLITZ("Shadow Blitz", 76, 43, SpriteID.SPELL_SHADOW_BLITZ, true, 14286937, MenuAction.WIDGET_TARGET),
	SPIN_FLAX("Spin Flax", 76, 75, SpriteID.SPELL_SPIN_FLAX, true, 14286991, MenuAction.CC_OP),
	RESURRECT_GREATER_THRALL("Resurrect Greater Thrall", 76, 88, SpriteID.SPELL_RESURRECT_GREATER_ZOMBIE, true, -1, null),
	SUPERGLASS_MAKE("Superglass Make", 77, 78, SpriteID.SPELL_SUPERGLASS_MAKE, true, 14286966, MenuAction.CC_OP),
	TAN_LEATHER("Tan Leather", 78, 81, SpriteID.SPELL_TAN_LEATHER, true, 14286967, MenuAction.CC_OP),
	KHAZARD_TELEPORT("Khazard Teleport", 78, 80, SpriteID.SPELL_KHAZARD_TELEPORT, true, 14286968, null),
	DAREEYAK_TELEPORT("Dareeyak Teleport", 78, 88, SpriteID.SPELL_DAREEYAK_TELEPORT, true, 14286944, null),
	RESURRECT_CROPS("Resurrect Crops", 78, 90, SpriteID.SPELL_RESURRECT_CROPS, true, -1, null),
	ENTANGLE("Entangle", 79, 89, SpriteID.SPELL_ENTANGLE, true, 14286911, null),
	TELE_GROUP_KHAZARD("Tele Group Khazard", 79, 81, SpriteID.SPELL_TELE_GROUP_KHAZARD, true, 14286969, null),
	DREAM("Dream", 79, 82, SpriteID.SPELL_DREAM, true, 14286970, MenuAction.CC_OP),
	UNDEAD_GRASP("Undead Grasp", 79, 46.5f, SpriteID.SPELL_UNDEAD_GRASP, true, -1, null),
	CHARGE("Charge", 80, 180, SpriteID.SPELL_CHARGE, true, 14286913, MenuAction.CC_OP),
	BLOOD_BLITZ("Blood Blitz", 80, 45, SpriteID.SPELL_BLOOD_BLITZ, true, 14286929, MenuAction.WIDGET_TARGET),
	STUN("Stun", 80, 90, SpriteID.SPELL_STUN, true, 14286912, null),
	STRING_JEWELLERY("String Jewellery", 80, 83, SpriteID.SPELL_STRING_JEWELLERY, true, 14286971, MenuAction.CC_OP),
	DEATH_CHARGE("Death Charge", 80, 90, SpriteID.SPELL_DEATH_CHARGE, true, -1, null),
	STAT_RESTORE_POT_SHARE("Stat Restore Pot Share", 81, 84, SpriteID.SPELL_STAT_RESTORE_POT_SHARE, true, 14286972, MenuAction.WIDGET_TARGET),
	WIND_SURGE("Wind Surge", 81, 44, SpriteID.SPELL_WIND_SURGE, true, 14286914, MenuAction.WIDGET_TARGET),
	TELEOTHER_FALADOR("Teleother Falador", 82, 92, SpriteID.SPELL_TELEOTHER_FALADOR, true, 14286915, MenuAction.WIDGET_TARGET),
	MAGIC_IMBUE("Magic Imbue", 82, 86, SpriteID.SPELL_MAGIC_IMBUE, true, 14286973, MenuAction.CC_OP),
	ICE_BLITZ("Ice Blitz", 82, 46, SpriteID.SPELL_ICE_BLITZ, true, 14286925, MenuAction.WIDGET_TARGET),
	DARK_DEMONBANE("Dark Demonbane", 82, 43.5f, SpriteID.SPELL_DARK_DEMONBANE, true, -1, null),
	FERTILE_SOIL("Fertile Soil", 83, 87, SpriteID.SPELL_FERTILE_SOIL, true, 14286974, MenuAction.WIDGET_TARGET),
	BARROWS_TELEPORT("Barrows Teleport", 83, 90, SpriteID.SPELL_BARROWS_TELEPORT, true, -1, null),
	CARRALLANGER_TELEPORT("Carrallanger Teleport", 84, 82, SpriteID.SPELL_CARRALLANGAR_TELEPORT, true, 14286945, null),
	BOOST_POTION_SHARE("Boost Potion Share", 84, 88, SpriteID.SPELL_BOOST_POTION_SHARE, true, 14286975, MenuAction.WIDGET_TARGET),
	DEMONIC_OFFERING("Demonic Offering", 84, 175, SpriteID.SPELL_DEMONIC_OFFERING, true, -1, null),
	WATER_SURGE("Water Surge", 85, 46, SpriteID.SPELL_WATER_SURGE, true, 14286916, MenuAction.WIDGET_TARGET),
	FISHING_GUILD_TELEPORT("Fishing Guild Teleport", 85, 89, SpriteID.SPELL_FISHING_GUILD_TELEPORT, true, 14286976, null),
	TELE_BLOCK("Tele Block", 85, 80, SpriteID.SPELL_TELE_BLOCK, false, 14286917, MenuAction.WIDGET_TARGET),
	TELEPORT_TO_TARGET("Teleport To Target", 85, 45, SpriteID.SPELL_TELEPORT_TO_BOUNTY_TARGET, true, 14286918, MenuAction.CC_OP),
	GREATER_CORRUPTION("Greater Corruption", 85, 95, SpriteID.SPELL_GREATER_CORRUPTION, true, -1, null),
	SMOKE_BARRAGE("Smoke Barrage", 86, 48, SpriteID.SPELL_SMOKE_BARRAGE, true, 14286935, MenuAction.WIDGET_TARGET),
	TELE_GROUP_FISHING_GUILD("Tele Group Fishing Guild", 86, 90, SpriteID.SPELL_TELE_GROUP_FISHING_GUILD, true, 14286977, null),
	PLANK_MAKE("Plank Make", 86, 90, SpriteID.SPELL_PLANK_MAKE, true, 14286978, MenuAction.WIDGET_TARGET),
	CATHERBY_TELEPORT("Catherby Teleport", 87, 92, SpriteID.SPELL_CATHERBY_TELEPORT, true, 14286979, null),
	ENCHANT_ONYX_JEWELLERY("Enchant Onyx Jewellery", 87, 97, SpriteID.SPELL_LVL_6_ENCHANT, true, 14286919, null),
	ENCHANT_ONYX_BOLT("Enchant Onyx Bolt", 87, 97, SpriteID.SPELL_ENCHANT_CROSSBOW_BOLT, true, -1, null),
	SHADOW_BARRAGE("Shadow Barrage", 88, 48, SpriteID.SPELL_SHADOW_BARRAGE, true, 14286939, MenuAction.WIDGET_TARGET),
	TELE_GROUP_CATHERBY("Tele Group Catherby", 88, 93, SpriteID.SPELL_TELE_GROUP_CATHERBY, true, 14286980, null),
	ICE_PLATEAU_TELEPORT("Ice Plateau Teleport", 89, 96, SpriteID.SPELL_ICE_PLATEAU_TELEPORT, true, 14286982, null),
	RECHARGE_DRAGONSTONE("Recharge Dragonstone", 89, 97.5f, SpriteID.SPELL_RECHARGE_DRAGONSTONE, true, 14286981, MenuAction.WIDGET_TARGET),
	ANNAKARL_TELEPORT("Annakarl Teleport", 90, 100, SpriteID.SPELL_ANNAKARL_TELEPORT, true, 14286946, null),
	EARTH_SURGE("Earth Surge", 90, 48, SpriteID.SPELL_EARTH_SURGE, true, 14286921, MenuAction.WIDGET_TARGET),
	MASTER_REANIMATION("Master Reanimation", 90, 170, SpriteID.SPELL_MASTER_REANIMATION, true, -1, null),
	TELE_GROUP_ICE_PLATEAU("Tele Group Ice Plateau", 90, 99, SpriteID.SPELL_TELE_GROUP_ICE_PLATEAU, true, 14286983, null),
	TELEOTHER_CAMELOT("Teleother Camelot", 90, 100, SpriteID.SPELL_TELEOTHER_CAMELOT, true, 14286920, MenuAction.WIDGET_TARGET),
	APE_ATOLL_TELEPORT("Ape Atoll Teleport", 90, 100, SpriteID.SPELL_APE_ATOLL_TELEPORT, true, -1, null),
	ENERGY_TRANSFER("Energy Transfer", 91, 100, SpriteID.SPELL_ENERGY_TRANSFER, true, 14286984, MenuAction.WIDGET_TARGET),
	BLOOD_BARRAGE("Blood Barrage", 92, 51, SpriteID.SPELL_BLOOD_BARRAGE, true, 14286931, MenuAction.WIDGET_TARGET),
	HEAL_OTHER("Heal Other", 92, 101, SpriteID.SPELL_HEAL_OTHER, true, 14286985, MenuAction.WIDGET_TARGET),
	SINISTER_OFFERING("Sinister Offering", 92, 180, SpriteID.SPELL_SINISTER_OFFERING, true, -1, null),
	VENGEANCE_OTHER("Vengeance Other", 93, 108, SpriteID.SPELL_VENGEANCE_OTHER, true, 14286986, MenuAction.WIDGET_TARGET),
	ENCHANT_ZENYTE_JEWELLERY("Enchant Zenyte Jewellery", 93, 110, SpriteID.SPELL_LVL_7_ENCHANT, true, 14286922, null),
	ICE_BARRAGE("Ice Barrage", 94, 52, SpriteID.SPELL_ICE_BARRAGE, true, 14286927, MenuAction.WIDGET_TARGET),
	VENGEANCE("Vengeance", 94, 112, SpriteID.SPELL_VENGEANCE, true, 14286987, MenuAction.CC_OP),
	HEAL_GROUP("Heal Group", 95, 124, SpriteID.SPELL_HEAL_GROUP, true, 14286988, null),
	FIRE_SURGE("Fire Surge", 95, 51, SpriteID.SPELL_FIRE_SURGE, true, 14286923, MenuAction.WIDGET_TARGET),
	GHORROCK_TELEPORT("Ghorrock Teleport", 96, 106, SpriteID.SPELL_GHORROCK_TELEPORT, true, 14286947, null),
	SPELLBOOK_SWAP("Spellbook Swap", 96, 130, SpriteID.SPELL_SPELLBOOK_SWAP, true, 14286989, null),
	;

	private final String name;
	private final int level;
	private final float xp;
	private final int sprite;
	private final boolean isMembers;
	private final int widgetId;
	private final MenuAction widgetAction;

	@Override
	public String getName(final ItemManager itemManager)
	{
		return getName();
	}

	@Override
	public boolean isMembers(final ItemManager itemManager)
	{
		return isMembers();
	}
}
