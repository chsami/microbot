/*
 * Copyright (c) 2019, Jacky <liangj97@gmail.com>
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
package net.runelite.client.plugins.hoseaplugins.inferno;

import java.awt.Color;
import java.awt.Font;

import net.runelite.client.plugins.hoseaplugins.inferno.displaymodes.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.runelite.api.Prayer;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;
import net.runelite.client.config.Range;

@ConfigGroup("inferno")
public interface InfernoConfig extends Config
{
	@ConfigSection(
		name = "Prayer",
		description = "Configuration options for prayer",
		position = 0
	)
	String PrayerSection = "Prayer";

	@ConfigSection(
		name = "Safespots",
		description = "Configuration options for Safespots",
		position = 1
	)
	String SafespotsSection = "Safespots";

	@ConfigSection(
		name = "Waves",
		description = "Configuration options for Waves",
		position = 2
	)
	String WavesSection = "Waves";

	@ConfigSection(
		name = "Extra",
		description = "Configuration options for Extras",
		position = 3
	)
	String ExtraSection = "Extra";

	@ConfigSection(
		name = "Nibblers",
		description = "Configuration options for Nibblers",
		position = 4
	)
	String NibblersSection = "Nibblers";

	@ConfigSection(
		name = "Bats",
		description = "Configuration options for Bats",
		position = 5
	)
	String BatsSection = "Bats";

	@ConfigSection(
		name = "Blobs",
		description = "Configuration options for Blobs",
		position = 6
	)
	String BlobsSection = "Blobs";

	@ConfigSection(
		name = "Meleers",
		description = "Configuration options for Meleers",
		position = 7
	)
	String MeleersSection = "Meleers";

	@ConfigSection(
		name = "Rangers",
		description = "Configuration options for Rangers",
		position = 8
	)
	String RangersSection = "Rangers";

	@ConfigSection(
		name = "Magers",
		description = "Configuration options for Magers",
		position = 9
	)
	String MagersSection = "Magers";

	@ConfigSection(
		name = "Jad",
		description = "Configuration options for Jad",
		position = 10
	)
	String JadSection = "Jad";

	@ConfigSection(
		name = "Jad Healers",
		description = "Configuration options for Jad Healers",
		position = 11
	)
	String JadHealersSection = "Jad Healers";

	@ConfigSection(
		name = "Zuk",
		description = "Configuration options for Zuk",
		position = 12
	)
	String ZukSection = "Zuk";

	@ConfigSection(
		name = "Zuk Healers",
		description = "Configuration options for Zuk Healers",
		position = 13
	)
	String ZukHealersSection = "Zuk Healers";


	@ConfigItem(
		position = 0,
		keyName = "prayerDisplayMode",
		name = "Prayer Display Mode",
		description = "Display prayer indicator in the prayer tab or in the bottom right corner of the screen",
		section = PrayerSection
	)
	default InfernoPrayerDisplayMode prayerDisplayMode()
	{
		return InfernoPrayerDisplayMode.BOTH;
	}

	@ConfigItem(
		position = 1,
		keyName = "indicateWhenPrayingCorrectly",
		name = "Indicate When Praying Correctly",
		description = "Indicate the correct prayer, even if you are already praying that prayer",
		section = PrayerSection
	)
	default boolean indicateWhenPrayingCorrectly()
	{
		return true;
	}

	@ConfigItem(
		position = 2,
		keyName = "descendingBoxes",
		name = "Descending Boxes",
		description = "Draws timing boxes above the prayer icons, as if you were playing Piano Tiles",
		section = PrayerSection
	)
	default boolean descendingBoxes()
	{
		return true;
	}

	@ConfigItem(
		position = 3,
		keyName = "indicateNonPriorityDescendingBoxes",
		name = "Indicate Non-Priority Boxes",
		description = "Render descending boxes for prayers that are not the priority prayer for that tick",
		section = PrayerSection
	)
	default boolean indicateNonPriorityDescendingBoxes()
	{
		return true;
	}

	@ConfigItem(
		position = 4,
		keyName = "alwaysShowPrayerHelper",
		name = "Always Show Prayer Helper",
		description = "Render prayer helper at all time, even when other inventory tabs are open.",
		section = PrayerSection
	)
	default boolean alwaysShowPrayerHelper()
	{
		return false;
	}

	@ConfigItem(
			position = 5,
			keyName = "autoPray",
			name = "Auto pray",
			description = "Does auto pray for closest attack. Doesn't attempt to conserve prayer.",
			section = PrayerSection
	)
	default boolean autoPray()
	{
		return false;
	}

	@ConfigItem(
			position = 6,
			keyName = "oneTickPray",
			name = "One Tick Flicking",
			description = "Does one tick flicking.",
			section = PrayerSection
	)
	default boolean oneTickPray()
	{
		return false;
	}

	@ConfigItem(
			position = 7,
			keyName = "oneTickPrayOffense",
			name = "One Tick Offense",
			description = "Does one tick flicking on offensive prayer while",
			section = PrayerSection
	)
	default boolean oneTickPrayOffense()
	{
		return false;
	}

	@ConfigItem(
			position = 8,
			keyName = "offTickMeleeJad",
			name = "Jadd Off-tick Melee",
			description = "Will pray melee during jads if there are no incoming attacks",
			section = PrayerSection
	)
	default boolean offTickMeleeJad()
	{
		return false;
	}

	@ConfigItem(
		position = 4,
		keyName = "safespotDisplayMode",
		name = "Tile Safespots",
		description = "Indicate safespots on the ground: safespot (white), pray melee (red), pray range (green), pray magic (blue) and combinations of those",
		section = SafespotsSection
	)
	default InfernoSafespotDisplayMode safespotDisplayMode()
	{
		return InfernoSafespotDisplayMode.AREA;
	}

	@ConfigItem(
		position = 5,
		keyName = "safespotsCheckSize",
		name = "Tile Safespots Check Size",
		description = "The size of the area around the player that should be checked for safespots (SIZE x SIZE area)",
		section = SafespotsSection
	)
	default int safespotsCheckSize()
	{
		return 6;
	}

	@ConfigItem(
		position = 6,
		keyName = "indicateNonSafespotted",
		name = "Non-safespotted NPC's Overlay",
		description = "Red overlay for NPC's that can attack you",
		section = SafespotsSection
	)
	default boolean indicateNonSafespotted()
	{
		return false;
	}

	@ConfigItem(
		position = 7,
		keyName = "indicateTemporarySafespotted",
		name = "Temporary safespotted NPC's Overlay",
		description = "Orange overlay for NPC's that have to move to attack you",
		section = SafespotsSection
	)
	default boolean indicateTemporarySafespotted()
	{
		return false;
	}

	@ConfigItem(
		position = 8,
		keyName = "indicateSafespotted",
		name = "Safespotted NPC's Overlay",
		description = "Green overlay for NPC's that are safespotted (can't attack you)",
		section = SafespotsSection
	)
	default boolean indicateSafespotted()
	{
		return false;
	}

	@ConfigItem(
		position = 0,
		keyName = "waveDisplay",
		name = "Wave Display",
		description = "Shows monsters that will spawn on the selected wave(s).",
		section = WavesSection
	)
	default InfernoWaveDisplayMode waveDisplay()
	{
		return InfernoWaveDisplayMode.BOTH;
	}

	@ConfigItem(
		position = 1,
		keyName = "npcNaming",
		name = "NPC Naming",
		description = "Simple (ex: Bat) or Complex (ex: Jal-MejRah) NPC naming",
		section = WavesSection
	)
	default InfernoNamingDisplayMode npcNaming()
	{
		return InfernoNamingDisplayMode.SIMPLE;
	}

	@ConfigItem(
		position = 2,
		keyName = "npcLevels",
		name = "NPC Levels",
		description = "Show the combat level of the NPC next to their name",
		section = WavesSection
	)
	default boolean npcLevels()
	{
		return false;
	}

	@ConfigItem(
		position = 3,
		keyName = "getWaveOverlayHeaderColor",
		name = "Wave Header",
		description = "Color for Wave Header",
		section = WavesSection
	)
	default Color getWaveOverlayHeaderColor()
	{
		return Color.ORANGE;
	}

	@ConfigItem(
		position = 4,
		keyName = "getWaveTextColor",
		name = "Wave Text Color",
		description = "Color for Wave Texts",
		section = WavesSection
	)
	default Color getWaveTextColor()
	{
		return Color.WHITE;
	}

	@ConfigItem(
		position = 0,
		keyName = "indicateObstacles",
		name = "Obstacles",
		description = "Indicate obstacles that NPC's cannot pass through",
		section = ExtraSection
	)
	default boolean indicateObstacles()
	{
		return false;
	}

	@ConfigItem(
		position = 1,
		keyName = "spawnTimerInfobox",
		name = "Spawn Timer Infobox",
		description = "Display an Infobox that times spawn sets during Zuk fight.",
		section = ExtraSection
	)
	default boolean spawnTimerInfobox()
	{
		return false;
	}

	@ConfigItem(
			position = 2,
			keyName = "spawnTimerDebug",
			name = "Spawn Timer Debug",
			description = "Log debug information for spawn sets",
			section = ExtraSection
	)
	default boolean spawnTimerDebug()
	{
		return false;
	}

	@ConfigItem(
		position = 0,
		keyName = "indicateNibblers",
		name = "Indicate Nibblers",
		description = "Indicates nibblers that are alive",
		section = NibblersSection
	)
	default boolean indicateNibblers()
	{
		return true;
	}

	@ConfigItem(
		position = 1,
		keyName = "indicateCentralNibbler",
		name = "Indicate Central Nibbler",
		description = "Indicate the most central nibbler. If multiple nibblers will freeze the same amount of other nibblers, " +
			"the nibbler closest to the player's location is chosen.",
		section = NibblersSection
	)
	default boolean indicateCentralNibbler()
	{
		return true;
	}

	@ConfigItem(
		position = 0,
		keyName = "prayerBat",
		name = "Prayer Helper",
		description = "Indicate the correct prayer when this NPC attacks",
		section = BatsSection
	)
	default boolean prayerBat()
	{
		return true;
	}

	@ConfigItem(
		position = 1,
		keyName = "ticksOnNpcBat",
		name = "Ticks on NPC",
		description = "Draws the amount of ticks before an NPC is going to attack on the NPC",
		section = BatsSection
	)
	default boolean ticksOnNpcBat()
	{
		return true;
	}

	@ConfigItem(
		position = 2,
		keyName = "safespotsBat",
		name = "Safespots",
		description = "Enable or disable safespot calculation for this specific NPC. 'Tile Safespots' in the 'Safespots' category needs to be turned on for this to take effect.",
		section = BatsSection
	)
	default boolean safespotsBat()
	{
		return true;
	}

	@ConfigItem(
		position = 3,
		keyName = "indicateNpcPositionBat",
		name = "Indicate Main Tile",
		description = "Indicate the main tile for multi-tile NPC's. This tile is used for and pathfinding.",
		section = BatsSection
	)
	default boolean indicateNpcPositionBat()
	{
		return false;
	}

	@ConfigItem(
		position = 0,
		keyName = "prayerBlob",
		name = "Prayer Helper",
		description = "Indicate the correct prayer when this NPC attacks",
		section = BlobsSection
	)
	default boolean prayerBlob()
	{
		return true;
	}

	@ConfigItem(
		position = 1,
		keyName = "indicateBlobDetectionTick",
		name = "Indicate Blob Detection Tick",
		description = "Show a prayer indicator (default: magic) for the tick on which the blob will detect prayer",
		section = BlobsSection
	)
	default boolean indicateBlobDetectionTick()
	{
		return true;
	}

	@ConfigItem(
		position = 2,
		keyName = "indicateBlobDeathLocation",
		name = "Indicate Blob Death Location",
		description = "Highlight the death tiles with a tick countdown until mini-blobs spawn",
		section = BlobsSection
	)
	default boolean indicateBlobDeathLocation()
	{
		return false;
	}

	@ConfigItem(
		position = 3,
		keyName = "getBlobDeathLocationColor",
		name = "Blob Death Color",
		description = "Color for blob death location outline",
		section = BlobsSection
	)
	default Color getBlobDeathLocationColor()
	{
		return Color.ORANGE;
	}

	@ConfigItem(
		position = 4,
		keyName = "blobDeathLocationFade",
		name = "Fade Tile",
		description = "Fades the death tile for a smoother transition.",
		section = BlobsSection
	)
	default boolean blobDeathLocationFade()
	{
		return true;
	}


	@ConfigItem(
		position = 5,
		keyName = "ticksOnNpcBlob",
		name = "Ticks on NPC",
		description = "Draws the amount of ticks before an NPC is going to attack on the NPC",
		section = BlobsSection
	)
	default boolean ticksOnNpcBlob()
	{
		return true;
	}

	@ConfigItem(
		position = 6,
		keyName = "safespotsBlob",
		name = "Safespots",
		description = "Enable or disable safespot calculation for this specific NPC. 'Tile Safespots' in the 'Safespots' category needs to be turned on for this to take effect.",
		section = BlobsSection
	)
	default boolean safespotsBlob()
	{
		return true;
	}

	@ConfigItem(
		position = 7,
		keyName = "indicateNpcPositionBlob",
		name = "Indicate Main Tile",
		description = "Indicate the main tile for multi-tile NPC's. This tile is used for pathfinding.",
		section = BlobsSection
	)
	default boolean indicateNpcPositionBlob()
	{
		return false;
	}


	@ConfigItem(
		position = 0,
		keyName = "prayerMeleer",
		name = "Prayer Helper",
		description = "Indicate the correct prayer when this NPC attacks",
		section = MeleersSection
	)
	default boolean prayerMeleer()
	{
		return true;
	}

	@ConfigItem(
		position = 1,
		keyName = "ticksOnNpcMeleer",
		name = "Ticks on NPC",
		description = "Draws the amount of ticks before an NPC is going to attack on the NPC",
		section = MeleersSection
	)
	default boolean ticksOnNpcMeleer()
	{
		return true;
	}

	@ConfigItem(
		position = 2,
		keyName = "ticksOnNpcMeleerDig",
		name = "Dig Timer",
		description = "Draws the amount of ticks before the melee will begin the dig animation.\n" +
			"The amount of time is currently unknown. Plugin will count up to 50 \n" +
			"then count up with the danger overlay. This should give a general idea of the time. \n" +
			"Once more data can be collected this can be improved",
		section = MeleersSection
	)
	default boolean ticksOnNpcMeleerDig()
	{
		return false;
	}

	@Range(
		min = 1,
		max = 50
	)
	@ConfigItem(
		position = 3,
		keyName = "digTimerThreshold",
		name = "Tick Draw Threshold",
		description = "Number at which the dig timer should be drawn",
		section = MeleersSection
	)
	default int digTimerThreshold()
	{
		return 20;
	}

	@Range(
		min = 30,
		max = 70
	)
	@ConfigItem(
		position = 4,
		keyName = "digTimerDangerThreshold",
		name = "Tick Danger Threshold",
		description = "Number at which the dig timer should be dangerous",
		section = MeleersSection
	)
	default int digTimerDangerThreshold()
	{
		return 50;
	}


	@ConfigItem(
		position = 5,
		keyName = "getMeleeDigSafeColor",
		name = "Dig Safe Color",
		description = "Color for melee when can not dig",
		section = MeleersSection
	)
	default Color getMeleeDigSafeColor()
	{
		return Color.LIGHT_GRAY;
	}

	@ConfigItem(
		position = 6,
		keyName = "getMeleeDigDangerColor",
		name = "Dig Danger Color",
		description = "Color for melee when it can dig",
		section = MeleersSection
	)
	default Color getMeleeDigDangerColor()
	{
		return Color.ORANGE;
	}

	@Range(min = 10,
		max = 48)
	@ConfigItem(
		position = 7,
		keyName = "getMeleeDigFontSize",
		name = "Font size",
		description = "Font size to use under the melee",
		section = MeleersSection
	)
	default int getMeleeDigFontSize()
	{
		return 11;
	}

	@ConfigItem(
		position = 8,
		keyName = "safespotsMeleer",
		name = "Safespots",
		description = "Enable or disable safespot calculation for this specific NPC. 'Tile Safespots' in the 'Safespots' category needs to be turned on for this to take effect.",
		section = MeleersSection
	)
	default boolean safespotsMeleer()
	{
		return true;
	}

	@ConfigItem(
		position = 9,
		keyName = "indicateNpcPositionMeleer",
		name = "Indicate Main Tile",
		description = "Indicate the main tile for multi-tile NPC's. This tile is used for pathfinding.",
		section = MeleersSection
	)
	default boolean indicateNpcPositionMeleer()
	{
		return false;
	}

	@ConfigItem(
		position = 0,
		keyName = "prayerRanger",
		name = "Prayer Helper",
		description = "Indicate the correct prayer when this NPC attacks",
		section = RangersSection
	)
	default boolean prayerRanger()
	{
		return true;
	}

	@ConfigItem(
		position = 1,
		keyName = "ticksOnNpcRanger",
		name = "Ticks on NPC",
		description = "Draws the amount of ticks before an NPC is going to attack on the NPC",
		section = RangersSection
	)
	default boolean ticksOnNpcRanger()
	{
		return true;
	}

	@ConfigItem(
		position = 2,
		keyName = "safespotsRanger",
		name = "Safespots",
		description = "Enable or disable safespot calculation for this specific NPC. 'Tile Safespots' in the 'Safespots' category needs to be turned on for this to take effect.",
		section = RangersSection
	)
	default boolean safespotsRanger()
	{
		return true;
	}

	@ConfigItem(
		position = 3,
		keyName = "indicateNpcPositionRanger",
		name = "Indicate Main Tile",
		description = "Indicate the main tile for multi-tile NPC's. This tile is used for pathfinding.",
		section = RangersSection
	)
	default boolean indicateNpcPositionRanger()
	{
		return false;
	}

	@ConfigItem(
		position = 0,
		keyName = "prayerMage",
		name = "Prayer Helper",
		description = "Indicate the correct prayer when this NPC attacks",
		section = MagersSection
	)
	default boolean prayerMage()
	{
		return true;
	}

	@ConfigItem(
		position = 1,
		keyName = "ticksOnNpcMage",
		name = "Ticks on NPC",
		description = "Draws the amount of ticks before an NPC is going to attack on the NPC",
		section = MagersSection
	)
	default boolean ticksOnNpcMage()
	{
		return true;
	}

	@ConfigItem(
		position = 2,
		keyName = "safespotsMage",
		name = "Safespots",
		description = "Enable or disable safespot calculation for this specific NPC. 'Tile Safespots' in the 'Safespots' category needs to be turned on for this to take effect.",
		section = MagersSection
	)
	default boolean safespotsMage()
	{
		return true;
	}

	@ConfigItem(
		position = 3,
		keyName = "indicateNpcPositionMage",
		name = "Indicate Main Tile",
		description = "Indicate the main tile for multi-tile NPC's. This tile is used for pathfinding.",
		section = MagersSection
	)
	default boolean indicateNpcPositionMage()
	{
		return false;
	}

	@ConfigItem(
		position = 0,
		keyName = "prayerHealersJad",
		name = "Prayer Helper",
		description = "Indicate the correct prayer when this NPC attacks",
		section = JadHealersSection
	)
	default boolean prayerHealerJad()
	{
		return false;
	}

	@ConfigItem(
		position = 1,
		keyName = "ticksOnNpcHealersJad",
		name = "Ticks on NPC",
		description = "Draws the amount of ticks before an NPC is going to attack on the NPC",
		section = JadHealersSection
	)
	default boolean ticksOnNpcHealerJad()
	{
		return false;
	}

	@ConfigItem(
		position = 2,
		keyName = "safespotsHealersJad",
		name = "Safespots",
		description = "Enable or disable safespot calculation for this specific NPC. 'Tile Safespots' in the 'Safespots' category needs to be turned on for this to take effect.",
		section = JadHealersSection
	)
	default boolean safespotsHealerJad()
	{
		return true;
	}

	@ConfigItem(
		position = 3,
		keyName = "indicateActiveHealersJad",
		name = "Indicate Active Healers",
		description = "Indicate healers that are still healing Jad",
		section = JadHealersSection
	)
	default boolean indicateActiveHealerJad()
	{
		return true;
	}

	@ConfigItem(
		position = 0,
		keyName = "prayerJad",
		name = "Prayer Helper",
		description = "Indicate the correct prayer when this NPC attacks",
		section = JadSection
	)
	default boolean prayerJad()
	{
		return true;
	}

	@ConfigItem(
		position = 1,
		keyName = "ticksOnNpcJad",
		name = "Ticks on NPC",
		description = "Draws the amount of ticks before an NPC is going to attack on the NPC",
		section = JadSection
	)
	default boolean ticksOnNpcJad()
	{
		return true;
	}

	@ConfigItem(
		position = 2,
		keyName = "safespotsJad",
		name = "Safespots (Melee Range Only)",
		description = "Enable or disable safespot calculation for this specific NPC. 'Tile Safespots' in the 'Safespots' category needs to be turned on for this to take effect.",
		section = JadSection
	)
	default boolean safespotsJad()
	{
		return true;
	}

	@ConfigItem(
		position = 0,
		keyName = "indicateActiveHealersZuk",
		name = "Indicate Active Healers (UNTESTED)",
		description = "Indicate healers that are still healing Zuk",
		section = ZukHealersSection
	)
	default boolean indicateActiveHealerZuk()
	{
		return true;
	}

	@ConfigItem(
		position = 0,
		keyName = "ticksOnNpcZuk",
		name = "Ticks on NPC",
		description = "Draws the amount of ticks before an NPC is going to attack on the NPC",
		section = ZukSection
	)
	default boolean ticksOnNpcZuk()
	{
		return true;
	}

	@ConfigItem(
		position = 1,
		keyName = "safespotsZukShieldBeforeHealers",
		name = "Safespots (Before Healers)",
		description = "Indicate the zuk shield safespots. 'Tile Safespots' in the 'Safespots' category needs to be turned on for this to take effect. Shield must go back and forth at least 1 time before the predict option will work.",
		section = ZukSection
	)
	default InfernoZukShieldDisplayMode safespotsZukShieldBeforeHealers()
	{
		return InfernoZukShieldDisplayMode.PREDICT;
	}

	@ConfigItem(
		position = 2,
		keyName = "safespotsZukShieldAfterHealers",
		name = "Safespots (After Healers)",
		description = "Indicate the zuk shield safespots. 'Tile Safespots' in the 'Safespots' category needs to be turned on for this to take effect.",
		section = ZukSection
	)
	default InfernoZukShieldDisplayMode safespotsZukShieldAfterHealers()
	{
		return InfernoZukShieldDisplayMode.LIVE;
	}

	@ConfigItem(
		position = 3,
		keyName = "hideTzKalZukDeath",
		name = "Hide On Death",
		description = "Hide TzKal-Zuk on death animation",
		section = ZukSection
	)
	default boolean hideZukDeath()
	{
		return false;
	}

	@ConfigItem(
		position = 4,
		keyName = "ticksOnNpcZukShield",
		name = "Ticks on Zuk Shield",
		description = "Draws the amount of ticks before Zuk attacks on the floating shield",
		section = ZukSection
	)
	default boolean ticksOnNpcZukShield()
	{
		return false;
	}

	@Getter
	@AllArgsConstructor
	enum FontStyle
	{
		BOLD("Bold", Font.BOLD),
		ITALIC("Italic", Font.ITALIC),
		PLAIN("Plain", Font.PLAIN);

		private String name;
		private int font;

		@Override
		public String toString()
		{
			return getName();
		}
	}

	enum OffensivePrayer
	{
		MYSTIC_MIGHT(Prayer.MYSTIC_MIGHT), AUGURY(Prayer.AUGURY), EAGLE_EYE(Prayer.EAGLE_EYE), RIGOUR(Prayer.RIGOUR), NONE(Prayer.CHIVALRY);

		@Getter
		final Prayer prayer;

		OffensivePrayer(Prayer prayer)
		{
			this.prayer = prayer;
		}
	}
}
