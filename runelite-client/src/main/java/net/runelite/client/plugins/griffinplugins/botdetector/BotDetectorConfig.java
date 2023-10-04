/*
 * Copyright (c) 2021, Ferrariic, Seltzer Bro, Cyborger1
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package net.runelite.client.plugins.griffinplugins.botdetector;

import java.awt.Color;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;
import net.runelite.client.config.Range;
import net.runelite.client.config.Units;
import net.runelite.client.plugins.griffinplugins.botdetector.model.PlayerStatsType;
import net.runelite.client.plugins.griffinplugins.botdetector.model.StatsCommandDetailLevel;
import net.runelite.client.plugins.griffinplugins.botdetector.ui.PanelFontType;

@ConfigGroup(BotDetectorConfig.CONFIG_GROUP)
public interface BotDetectorConfig extends Config
{
	String CONFIG_GROUP = "botdetector";
	String ONLY_SEND_AT_LOGOUT_KEY = "sendAtLogout";
	String AUTO_SEND_MINUTES_KEY = "autoSendMinutes";
	String ADD_PREDICT_OPTION_KEY = "addDetectOption"; // I know it says detect, don't change it.
	String ANONYMOUS_UPLOADING_KEY = "enableAnonymousReporting";
	String PANEL_FONT_TYPE_KEY = "panelFontType";
	String AUTH_FULL_TOKEN_KEY = "authToken";
	String SHOW_FEEDBACK_TEXTBOX = "showFeedbackTextbox";
	String SHOW_DISCORD_VERIFICATION_ERRORS = "showDiscordVerificationErrors";
	String ANONYMOUS_UUID_KEY = "anonymousUUID";

	int AUTO_SEND_MINIMUM_MINUTES = 5;
	int AUTO_SEND_MAXIMUM_MINUTES = 360;

	@ConfigSection(
		position = 1,
		name = "Upload Settings",
		description = "Settings for how the plugin uploads player data."
	)
	String uploadSection = "uploadSection";

	@ConfigSection(
		position = 2,
		name = "Panel Settings",
		description = "Settings for the plugin's panel."
	)
	String panelSection = "panelSection";

	@ConfigSection(
		position = 3,
		name = "'Predict' Settings",
		description = "Settings for the 'Predict' right-click option."
	)
	String predictSection = "predictSection";

	@ConfigSection(
		position = 4,
		name = "Other Settings",
		description = "Other miscellaneous settings."
	)
	String miscSection = "miscSection";

	@ConfigItem(
		position = 1,
		keyName = ANONYMOUS_UPLOADING_KEY,
		name = "Anonymous Uploading",
		description = "Your name will not be included with your name uploads.<br>Disable if you'd like to track your contributions.",
		section = uploadSection
	)
	default boolean enableAnonymousUploading()
	{
		return true;
	}

	@ConfigItem(
		position = 2,
		keyName = ONLY_SEND_AT_LOGOUT_KEY,
		name = "Send Names Only After Logout",
		description = "Waits to upload names until you've logged out. Use this if you have a poor connection."
			+ "<br><span style='color:red'>WARNING:</span> Names <b>will not</b> be sent if RuneLite is closed completely"
			+ "<br>before logging out, unless 'Attempt Send on Close' is turned on.",
		section = uploadSection
	)
	default boolean onlySendAtLogout()
	{
		return false;
	}

	@ConfigItem(
		position = 3,
		keyName = "uploadOnShutdown",
		name = "Attempt Send on Close",
		description = "Attempts to upload names when closing RuneLite while being logged in."
			+ "<br><span style='color:red'>WARNING:</span> This may cause the client to take significantly longer to close"
			+ "<br>in the event that the Bot Detector server is being slow or unresponsive.",
		section = uploadSection
	)
	default boolean uploadOnShutdown()
	{
		return false;
	}

	@ConfigItem(
		position = 4,
		keyName = AUTO_SEND_MINUTES_KEY,
		name = "Send Names Every",
		description = "Sets the amount of time between automatic name uploads.",
		section = uploadSection
	)
	@Range(min = AUTO_SEND_MINIMUM_MINUTES, max = AUTO_SEND_MAXIMUM_MINUTES)
	@Units(Units.MINUTES)
	default int autoSendMinutes()
	{
		return 5;
	}

	@ConfigItem(
		position = 1,
		keyName = "autocomplete",
		name = "Prediction Autocomplete",
		description = "Autocomplete names when typing a name to predict in the prediction panel.",
		section = panelSection
	)
	default boolean panelAutocomplete()
	{
		return true;
	}

	@ConfigItem(
		position = 2,
		keyName = "showBreakdownOnNullConfidence",
		name = "Show Breakdown in Special Cases",
		description = "Show the Prediction Breakdown when predicting certain types of accounts, such as 'Stats Too Low'.",
		section = panelSection
	)
	default boolean showBreakdownOnNullConfidence()
	{
		return false;
	}

	@ConfigItem(
		position = 3,
		keyName = SHOW_FEEDBACK_TEXTBOX,
		name = "Show Feedback Textbox",
		description = "Show a textbox on the prediction feedback panel where you can explain your feedback to us.",
		section = panelSection
	)
	default boolean showFeedbackTextbox()
	{
		return true;
	}

	@ConfigItem(
		position = 4,
		keyName = "panelDefaultStatsType",
		name = "Panel Default Stats Tab",
		description = "Sets the initial player statistics tab in the prediction panel for when the plugin is launched.",
		section = panelSection
	)
	default PlayerStatsType panelDefaultStatsType()
	{
		return PlayerStatsType.TOTAL;
	}

	@ConfigItem(
		position = 5,
		keyName = PANEL_FONT_TYPE_KEY,
		name = "Panel Font Size",
		description = "Sets the size of the label fields in the prediction panel.",
		section = panelSection
	)
	default PanelFontType panelFontType()
	{
		return PanelFontType.NORMAL;
	}

	@ConfigItem(
		position = 1,
		keyName = ADD_PREDICT_OPTION_KEY,
		name = "Right-click 'Predict' Players",
		description = "Adds an entry to player menus to quickly check them in the prediction panel.",
		section = predictSection
	)
	default boolean addPredictOption()
	{
		return false;
	}

	@ConfigItem(
		position = 2,
		keyName = "predictOnReport",
		name = "'Predict' on Right-click 'Report'",
		description = "Makes the in-game right-click 'Report' option also open the prediction panel.",
		section = predictSection
	)
	default boolean predictOnReport()
	{
		return false;
	}

	@ConfigItem(
		position = 3,
		keyName = "predictOptionCopyName",
		name = "'Predict' Copy Name to Clipboard",
		description = "Copies the player's name to the clipboard when right-click predicting a player.",
		section = predictSection
	)
	default boolean predictOptionCopyName()
	{
		return false;
	}

	@ConfigItem(
		position = 4,
		keyName = "predictOptionDefaultColor",
		name = "'Predict' Default Color",
		description = "When right-clicking on a player, the predict option will be this color by default.",
		section = predictSection
	)
	Color predictOptionDefaultColor();

	@ConfigItem(
		position = 5,
		keyName = "predictOptionFlaggedColor",
		name = "'Predict' Voted/Flagged Color",
		description = "When right-clicking on a player that has been flagged or given feedback, the predict option will be this color instead.",
		section = predictSection
	)
	Color predictOptionFlaggedColor();

	@ConfigItem(
		position = 6,
		keyName = "applyPredictColorsOnReportOption",
		name = "Apply Colors to 'Report'",
		description = "Applies the above 'Predict' color options to the in-game 'Report' option as well.",
		section = predictSection,
		warning = "Enabling this setting may cause issues with other plugins that rely on the 'Report' option being unchanged."
	)
	default boolean applyPredictColorsOnReportOption()
	{
		return false;
	}

	@ConfigItem(
		position = 1,
		keyName = "enableChatNotifications",
		name = "Enable Chat Status Messages",
		description = "Show various plugin status messages in the game chat.",
		section = miscSection
	)
	default boolean enableChatStatusMessages()
	{
		return false;
	}

	@ConfigItem(
		position = 2,
		keyName = "statsChatCommandDetailLevel",
		name = "'!bdstats' Chat Command Detail Level",
		description = "Enable processing the '!bdstats' command when it appears in the chatbox,"
			+ "<br>which will fetch the message author's plugin stats and display them.",
		section = miscSection
	)
	default StatsCommandDetailLevel statsChatCommandDetailLevel()
	{
		return StatsCommandDetailLevel.CONFIRMED_ONLY;
	}

	@ConfigItem(
		keyName = AUTH_FULL_TOKEN_KEY,
		name = "",
		description = "",
		hidden = true
	)
	default String authFullToken()
	{
		return null;
	}

	@ConfigItem(
		keyName = AUTH_FULL_TOKEN_KEY,
		name = "",
		description = "",
		hidden = true
	)
	void setAuthFullToken(String fullToken);

	@ConfigItem(
		keyName = SHOW_DISCORD_VERIFICATION_ERRORS,
		name = "",
		description = "",
		hidden = true
	)
	default boolean showDiscordVerificationErrors()
	{
		return true;
	}

	@ConfigItem(
		keyName = SHOW_DISCORD_VERIFICATION_ERRORS,
		name = "",
		description = "",
		hidden = true
	)
	void setShowDiscordVerificationErrors(boolean show);
}
