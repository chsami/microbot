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

import com.google.common.collect.EvictingQueue;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Table;
import com.google.common.collect.Tables;
import com.google.common.primitives.Ints;
import java.awt.Toolkit;
import java.awt.Color;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.text.DecimalFormat;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.regex.Pattern;
import javax.swing.JEditorPane;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.event.HyperlinkEvent;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.MenuAction;
import net.runelite.api.MenuEntry;
import net.runelite.api.MessageNode;
import net.runelite.api.Player;
import net.runelite.api.WorldType;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.CommandExecuted;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.MenuEntryAdded;
import net.runelite.api.events.MenuOpened;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.api.events.PlayerSpawned;
import net.runelite.api.events.WorldChanged;
import net.runelite.api.kit.KitType;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.chat.ChatColorType;
import net.runelite.client.chat.ChatCommandManager;
import net.runelite.client.chat.ChatMessageBuilder;
import net.runelite.client.chat.ChatMessageManager;
import net.runelite.client.chat.QueuedMessage;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.config.RuneScapeProfileType;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ClientShutdown;
import net.runelite.client.game.ItemManager;
import net.runelite.client.menus.MenuManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.events.ConfigChanged;
import javax.inject.Inject;
import net.runelite.client.plugins.PluginManager;
import net.runelite.client.plugins.griffinplugins.botdetector.events.BotDetectorPanelActivated;
import net.runelite.client.plugins.griffinplugins.botdetector.http.BotDetectorClient;
import net.runelite.client.plugins.griffinplugins.botdetector.http.UnauthorizedTokenException;
import net.runelite.client.plugins.griffinplugins.botdetector.http.ValidationException;
import net.runelite.client.plugins.griffinplugins.botdetector.model.*;

import static net.runelite.client.plugins.griffinplugins.botdetector.model.CaseInsensitiveString.wrap;

import net.runelite.client.plugins.griffinplugins.botdetector.ui.BotDetectorPanel;
import net.runelite.client.task.Schedule;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.util.ColorUtil;
import net.runelite.client.util.ImageUtil;
import net.runelite.client.util.LinkBrowser;
import net.runelite.client.util.Text;
import com.google.inject.Provides;
import org.apache.commons.lang3.StringUtils;

@Slf4j
@PluginDescriptor(
	name = "Griffin Bot Detector",
	description = "This plugin sends encountered Player Names to a server in order to detect Botting Behavior.",
	tags = {"Bot", "Detector", "Player"}
)
public class BotDetectorPlugin extends Plugin
{
	/** {@link PlayerSighting}s should only be created if the player is logged into a world set up for one of these {@link RuneScapeProfileType}s. **/
	private static final ImmutableSet<RuneScapeProfileType> ALLOWED_PROFILE_TYPES =
		ImmutableSet.of(
			RuneScapeProfileType.STANDARD
		);

	/** {@link PlayerSighting}s should only be created if the returned region id is <= this amount. **/
	private static final int MAX_ALLOWED_REGION_ID = 16000;

	private static final Pattern UUID_PATTERN = Pattern.compile("^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$");

	private static final String PREDICT_OPTION = "Predict";
	private static final String REPORT_OPTION = "Report";
	private static final String KICK_OPTION = "Kick";
	private static final String DELETE_OPTION = "Delete";
	private static final ImmutableSet<String> AFTER_OPTIONS =
		ImmutableSet.of("Message", "Add ignore", "Remove friend", DELETE_OPTION, KICK_OPTION);

	private static final ImmutableSet<MenuAction> PLAYER_MENU_ACTIONS = ImmutableSet.of(
		MenuAction.PLAYER_FIRST_OPTION, MenuAction.PLAYER_SECOND_OPTION, MenuAction.PLAYER_THIRD_OPTION, MenuAction.PLAYER_FOURTH_OPTION,
		MenuAction.PLAYER_FIFTH_OPTION, MenuAction.PLAYER_SIXTH_OPTION, MenuAction.PLAYER_SEVENTH_OPTION, MenuAction.PLAYER_EIGHTH_OPTION
	);


	private static final String VERIFY_DISCORD_COMMAND = "!code";
	private static final int VERIFY_DISCORD_CODE_SIZE = 4;
	private static final Pattern VERIFY_DISCORD_CODE_PATTERN = Pattern.compile("\\d{1," + VERIFY_DISCORD_CODE_SIZE + "}");

	private static final String STATS_CHAT_COMMAND = "!bdstats";

	private static final String COMMAND_PREFIX = "bd";
	private static final String MANUAL_FLUSH_COMMAND = COMMAND_PREFIX + "Flush";
	private static final String MANUAL_SIGHT_COMMAND = COMMAND_PREFIX + "Snap";
	private static final String MANUAL_REFRESH_COMMAND = COMMAND_PREFIX + "Refresh";
	private static final String SHOW_HIDE_ID_COMMAND = COMMAND_PREFIX + "ShowId";
	private static final String GET_AUTH_TOKEN_COMMAND = COMMAND_PREFIX + "GetToken";
	private static final String SET_AUTH_TOKEN_COMMAND = COMMAND_PREFIX + "SetToken";
	private static final String CLEAR_AUTH_TOKEN_COMMAND = COMMAND_PREFIX + "ClearToken";
	private static final String TOGGLE_SHOW_DISCORD_VERIFICATION_ERRORS_COMMAND = COMMAND_PREFIX + "ToggleShowDiscordVerificationErrors";
	private static final String TOGGLE_SHOW_DISCORD_VERIFICATION_ERRORS_COMMAND_ALIAS = COMMAND_PREFIX + "ToggleDVE";

	/** Command to method map to be used in {@link #onCommandExecuted(CommandExecuted)}. **/
	private final ImmutableMap<CaseInsensitiveString, Consumer<String[]>> commandConsumerMap =
		ImmutableMap.<CaseInsensitiveString, Consumer<String[]>>builder()
			.put(wrap(MANUAL_FLUSH_COMMAND), s -> manualFlushCommand())
			.put(wrap(MANUAL_SIGHT_COMMAND), s -> manualSightCommand())
			.put(wrap(MANUAL_REFRESH_COMMAND), s -> manualRefreshStatsCommand())
			.put(wrap(SHOW_HIDE_ID_COMMAND), this::showHideIdCommand)
			.put(wrap(GET_AUTH_TOKEN_COMMAND), s -> putAuthTokenIntoClipboardCommand())
			.put(wrap(SET_AUTH_TOKEN_COMMAND), s -> setAuthTokenFromClipboardCommand())
			.put(wrap(CLEAR_AUTH_TOKEN_COMMAND), s -> clearAuthTokenCommand())
			.put(wrap(TOGGLE_SHOW_DISCORD_VERIFICATION_ERRORS_COMMAND), s -> toggleShowDiscordVerificationErrors())
			.put(wrap(TOGGLE_SHOW_DISCORD_VERIFICATION_ERRORS_COMMAND_ALIAS), s -> toggleShowDiscordVerificationErrors())
			.build();

	private static final int MANUAL_FLUSH_COOLDOWN_SECONDS = 60;
	private static final int AUTO_REFRESH_STATS_COOLDOWN_SECONDS = 150;
	private static final int AUTO_REFRESH_LAST_FLUSH_GRACE_PERIOD_SECONDS = 30;
	private static final int API_HIT_SCHEDULE_SECONDS = 5;

	private static final String CHAT_MESSAGE_HEADER = "[Bot Detector] ";
	public static final String ANONYMOUS_USER_NAME = "AnonymousUser";
	public static final String ANONYMOUS_USER_NAME_UUID_FORMAT = ANONYMOUS_USER_NAME + "_%s";

	@Inject
	private Client client;

	@Inject
	private ClientThread clientThread;

	@Inject
	private ConfigManager configManager;

	@Inject
	private MenuManager menuManager;

	@Inject
	private ItemManager itemManager;

	@Inject
	private BotDetectorConfig config;

	@Inject
	private PluginManager pluginManager;

	@Inject
	private ClientToolbar clientToolbar;

	@Inject
	private ChatCommandManager chatCommandManager;

	@Inject
	private ChatMessageManager chatMessageManager;

	@Inject
	private BotDetectorClient detectorClient;

	private BotDetectorPanel panel;
	private NavigationButton navButton;

	@Provides
	BotDetectorConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(BotDetectorConfig.class);
	}

	/** The currently logged in player name, or {@code null} if the user is logged out. **/
	@Getter
	private String loggedPlayerName;
	/** The next time an automatic call to {@link #flushPlayersToClient(boolean)} should be allowed to run. **/
	private Instant timeToAutoSend;
	/** The total number of names uploaded in the current login session. **/
	private int namesUploaded;
	/** The last time a {@link #flushPlayersToClient(boolean)} was successfully attempted. **/
	private Instant lastFlush = Instant.MIN;
	/** The last time a {@link #refreshPlayerStats(boolean)}} was successfully attempted. **/
	private Instant lastStatsRefresh = Instant.MIN;
	/** See {@link #processCurrentWorld()}. **/
	private int currentWorldNumber;
	/** See {@link #processCurrentWorld()}. **/
	private boolean isCurrentWorldMembers;
	/** See {@link #processCurrentWorld()}. **/
	private boolean isCurrentWorldPVP;
	/** A blocked world should not log {@link PlayerSighting}s (see {@link #processCurrentWorld()} and {@link #ALLOWED_PROFILE_TYPES}). **/
	private boolean isCurrentWorldBlocked;
	/** A queue containing the last two {@link GameState}s from {@link #onGameStateChanged(GameStateChanged)}. **/
	private EvictingQueue<GameState> previousTwoGameStates = EvictingQueue.create(2);

	/** The currently loaded token or {@link AuthToken#EMPTY_TOKEN} if no valid token is loaded. **/
	@Getter
	private AuthToken authToken = AuthToken.EMPTY_TOKEN;

	/** The currently loaded anonymous UUID. **/
	private String anonymousUUID;

	/**
	 * Contains the last {@link PlayerSighting} for the given {@code player} and {@code regionId}
	 * since the last successful call to {@link #flushPlayersToClient(boolean, boolean)}.
	 * Always use {@link #normalizeAndWrapPlayerName(String)} when keying into this table.
	 */
	@Getter
	private final Table<CaseInsensitiveString, Integer, PlayerSighting> sightingTable = Tables.synchronizedTable(HashBasedTable.create());

	/**
	 * Contains the last {@link PlayerSighting} for the given {@code player} for the current login session.
	 * Always use {@link #normalizeAndWrapPlayerName(String)} when keying into this map.
	 */
	@Getter
	private final Map<CaseInsensitiveString, PlayerSighting> persistentSightings = new ConcurrentHashMap<>();

	/**
	 * Contains the feedbacks (See {@link FeedbackPredictionLabel}) sent per {@code player} for the current login session.
	 * Always use {@link #normalizeAndWrapPlayerName(String)} when keying into this map.
	 */
	@Getter
	private final Map<CaseInsensitiveString, FeedbackPredictionLabel> feedbackedPlayers = new ConcurrentHashMap<>();

	/**
	 * Contains the feedback texts sent per {@code player} for the current login session.
	 * Always use {@link #normalizeAndWrapPlayerName(String)} when keying into this map.
	 */
	@Getter
	private final Map<CaseInsensitiveString, String> feedbackedPlayersText = new ConcurrentHashMap<>();

	/**
	 * Contains the flagging (yes/no) sent per {@code player} for the current login session.
	 * Always use {@link #normalizeAndWrapPlayerName(String)} when keying into this map.
	 */
	@Getter
	private final Map<CaseInsensitiveString, Boolean> flaggedPlayers = new ConcurrentHashMap<>();

	@Override
	protected void startUp()
	{
		// Get current version of the plugin using properties file generated by build.gradle
		// Thanks to https://github.com/dillydill123/inventory-setups/
		try
		{
			final Properties props = new Properties();
			props.load(getClass().getResourceAsStream("version.txt"));
			detectorClient.setPluginVersion(props.getProperty("version"));
		}
		catch (Exception e)
		{
			log.error("Could not parse plugin version from properties file!", e);

			// Turn plugin back off and display an error message
			pluginManager.setPluginEnabled(this, false);
			displayPluginVersionError();

			return;
		}

		// Load up the anonymous UUID
		anonymousUUID = configManager.getConfiguration(BotDetectorConfig.CONFIG_GROUP, BotDetectorConfig.ANONYMOUS_UUID_KEY);
		if (StringUtils.isBlank(anonymousUUID) || !UUID_PATTERN.matcher(anonymousUUID).matches())
		{
			anonymousUUID = UUID.randomUUID().toString();
			configManager.setConfiguration(BotDetectorConfig.CONFIG_GROUP, BotDetectorConfig.ANONYMOUS_UUID_KEY, anonymousUUID);
		}

		panel = injector.getInstance(BotDetectorPanel.class);
		SwingUtilities.invokeLater(() ->
		{
			panel.setWarningVisible(BotDetectorPanel.WarningLabel.ANONYMOUS, config.enableAnonymousUploading());
			panel.setPluginVersion(detectorClient.getPluginVersion());
			panel.setNamesUploaded(0, false);
			panel.setNamesUploaded(0, true);
			panel.setFeedbackTextboxVisible(config.showFeedbackTextbox());
		});

		processCurrentWorld();

		final BufferedImage icon = ImageUtil.loadImageResource(getClass(), "bot-icon.png");

		navButton = NavigationButton.builder()
			.panel(panel)
			.tooltip("Bot Detector")
			.icon(icon)
			.priority(90)
			.build();

		clientToolbar.addNavigation(navButton);

		if (config.addPredictOption() && client != null)
		{
			menuManager.addPlayerMenuItem(PREDICT_OPTION);
		}

		updateTimeToAutoSend();

		authToken = AuthToken.fromFullToken(config.authFullToken());

		previousTwoGameStates.offer(client.getGameState());

		chatCommandManager.registerCommand(VERIFY_DISCORD_COMMAND, this::verifyDiscord);
		chatCommandManager.registerCommand(STATS_CHAT_COMMAND, this::statsChatCommand);
	}

	@Override
	protected void shutDown()
	{
		panel.shutdown();

		flushPlayersToClient(false);
		persistentSightings.clear();
		feedbackedPlayers.clear();
		feedbackedPlayersText.clear();
		flaggedPlayers.clear();

		if (client != null)
		{
			menuManager.removePlayerMenuItem(PREDICT_OPTION);
		}

		clientToolbar.removeNavigation(navButton);

		namesUploaded = 0;
		loggedPlayerName = null;
		lastFlush = Instant.MIN;
		lastStatsRefresh = Instant.MIN;
		authToken = AuthToken.EMPTY_TOKEN;

		previousTwoGameStates.clear();

		chatCommandManager.unregisterCommand(VERIFY_DISCORD_COMMAND);
		chatCommandManager.unregisterCommand(STATS_CHAT_COMMAND);
	}

	/**
	 * Updates {@link #timeToAutoSend} according to {@link BotDetectorConfig#autoSendMinutes()}.
	 */
	private void updateTimeToAutoSend()
	{
		timeToAutoSend = Instant.now().plusSeconds(60L *
			Ints.constrainToRange(config.autoSendMinutes(),
				BotDetectorConfig.AUTO_SEND_MINIMUM_MINUTES,
				BotDetectorConfig.AUTO_SEND_MAXIMUM_MINUTES));
	}

	/**
	 * Do not call this method in code. Continuously calls the automatic variants of API calling methods.
	 */
	@Schedule(period = API_HIT_SCHEDULE_SECONDS, unit = ChronoUnit.SECONDS, asynchronous = true)
	public void hitApi()
	{
		if (loggedPlayerName == null)
		{
			return;
		}

		if (!config.onlySendAtLogout() && Instant.now().isAfter(timeToAutoSend))
		{
			flushPlayersToClient(true);
		}

		refreshPlayerStats(false);
	}

	/**
	 * Attempts to send the contents of {@link #sightingTable} to {@link BotDetectorClient#sendSightings(Collection, String, boolean)}.
	 * @param restoreOnFailure The table is cleared before sending. If {@code true}, re-insert the cleared sightings into the table on failure.
	 * @return A completable future if there were any names to attempt to send, {@code null} otherwise.
	 */
	public synchronized CompletableFuture<Boolean> flushPlayersToClient(boolean restoreOnFailure)
	{
		return flushPlayersToClient(restoreOnFailure, false);
	}

	/**
	 * Attempts to send the contents of {@link #sightingTable} to {@link BotDetectorClient#sendSightings(Collection, String, boolean)}.
	 * @param restoreOnFailure The table is cleared before sending. If {@code true}, re-insert the cleared sightings into the table on failure.
	 * @param forceChatNotification Force displays the chat notifications.
	 * @return A completable future if there were any names to attempt to send, {@code null} otherwise.
	 */
	public synchronized CompletableFuture<Boolean> flushPlayersToClient(boolean restoreOnFailure, boolean forceChatNotification)
	{
		String uploader = getUploaderName();
		if (uploader == null)
		{
			return null;
		}

		updateTimeToAutoSend();

		int uniqueNames;
		Collection<PlayerSighting> sightings;
		int numUploads;
		synchronized (sightingTable)
		{
			uniqueNames = sightingTable.rowKeySet().size();
			if (uniqueNames <= 0)
			{
				return null;
			}

			sightings = new ArrayList<>(sightingTable.values());
			sightingTable.clear();
			numUploads = sightings.size();
		}

		lastFlush = Instant.now();

		return detectorClient.sendSightings(sightings, getUploaderName(), false)
			.whenComplete((b, ex) ->
			{
				if (ex == null && b)
				{
					namesUploaded += uniqueNames;
					SwingUtilities.invokeLater(() -> panel.setNamesUploaded(namesUploaded, false));
					sendChatStatusMessage("Successfully uploaded " + numUploads +
						" locations for " + uniqueNames + " unique players.",
						forceChatNotification);
				}
				else
				{
					sendChatStatusMessage("Error sending player sightings!", forceChatNotification);
					// Put the sightings back, but not if it's because of a validation error
					if (restoreOnFailure && !(ex instanceof ValidationException))
					{
						synchronized (sightingTable)
						{
							sightings.forEach(s ->
							{
								CaseInsensitiveString name = wrap(s.getPlayerName());
								int region = s.getRegionID();
								// Don't replace if new sightings were added to the table during the request
								if (!sightingTable.contains(name, region))
								{
									sightingTable.put(name, region, s);
								}
							});
						}
					}
				}
			});
	}

	/**
	 * Attempts to refresh the current player uploading statistics on the plugin panel according to various checks.
	 * @param forceRefresh If {@code true}, ignore checks in place meant for the automatic calling of this method.
	 */
	public synchronized void refreshPlayerStats(boolean forceRefresh)
	{
		if (!forceRefresh)
		{
			Instant now = Instant.now();
			// Only perform non-manual refreshes when a player is not anon, logged in and the panel is open
			if (config.enableAnonymousUploading() || loggedPlayerName == null || !navButton.isSelected()
				|| now.isBefore(lastStatsRefresh.plusSeconds(AUTO_REFRESH_STATS_COOLDOWN_SECONDS))
				|| now.isBefore(lastFlush.plusSeconds(AUTO_REFRESH_LAST_FLUSH_GRACE_PERIOD_SECONDS)))
			{
				return;
			}
		}

		lastStatsRefresh = Instant.now();

		if (config.enableAnonymousUploading() || loggedPlayerName == null)
		{
			SwingUtilities.invokeLater(() ->
			{
				panel.setPlayerStatsMap(null);
				panel.setPlayerStatsLoading(false);
				panel.setWarningVisible(BotDetectorPanel.WarningLabel.ANONYMOUS, config.enableAnonymousUploading());
				panel.setWarningVisible(BotDetectorPanel.WarningLabel.PLAYER_STATS_ERROR, false);
				if (loggedPlayerName == null)
				{
					panel.forceHideFeedbackPanel();
				}
				panel.forceHideFlaggingPanel();
			});
			return;
		}

		SwingUtilities.invokeLater(() ->
		{
			panel.setPlayerStatsLoading(true);
			panel.setWarningVisible(BotDetectorPanel.WarningLabel.ANONYMOUS, false);
		});

		String nameAtRequest = loggedPlayerName;
		detectorClient.requestPlayerStats(nameAtRequest)
			.whenComplete((psm, ex) ->
			{
				// Player could have logged out in the mean time, don't update panel
				// Player could also have switched to anon mode, don't update either.
				if (config.enableAnonymousUploading() || !nameAtRequest.equals(loggedPlayerName))
				{
					return;
				}

				SwingUtilities.invokeLater(() -> panel.setPlayerStatsLoading(false));

				if (ex == null && psm != null)
				{
					SwingUtilities.invokeLater(() ->
					{
						panel.setPlayerStatsMap(psm);
						panel.setWarningVisible(BotDetectorPanel.WarningLabel.PLAYER_STATS_ERROR, false);
					});
				}
				else
				{
					SwingUtilities.invokeLater(() ->
						panel.setWarningVisible(BotDetectorPanel.WarningLabel.PLAYER_STATS_ERROR, true));
				}
			});
	}

	@Subscribe
	private void onBotDetectorPanelActivated(BotDetectorPanelActivated event)
	{
		if (!config.enableAnonymousUploading())
		{
			refreshPlayerStats(false);
		}
	}

	@Subscribe
	private void onConfigChanged(ConfigChanged event)
	{
		if (!event.getGroup().equals(BotDetectorConfig.CONFIG_GROUP) || event.getKey() == null)
		{
			return;
		}

		switch (event.getKey())
		{
			case BotDetectorConfig.ADD_PREDICT_OPTION_KEY:
				if (client != null)
				{
					menuManager.removePlayerMenuItem(PREDICT_OPTION);

					if (config.addPredictOption())
					{
						menuManager.addPlayerMenuItem(PREDICT_OPTION);
					}
				}
				break;
			case BotDetectorConfig.ANONYMOUS_UPLOADING_KEY:
				refreshPlayerStats(true);
				SwingUtilities.invokeLater(() ->
				{
					panel.forceHideFeedbackPanel();
					panel.forceHideFlaggingPanel();
				});
				break;
			case BotDetectorConfig.PANEL_FONT_TYPE_KEY:
				SwingUtilities.invokeLater(() -> panel.setFontType(config.panelFontType()));
				break;
			case BotDetectorConfig.SHOW_FEEDBACK_TEXTBOX:
				SwingUtilities.invokeLater(() -> panel.setFeedbackTextboxVisible(config.showFeedbackTextbox()));
				break;
			case BotDetectorConfig.AUTO_SEND_MINUTES_KEY:
			case BotDetectorConfig.ONLY_SEND_AT_LOGOUT_KEY:
				updateTimeToAutoSend();
				break;
		}
	}

	@Subscribe
	private void onGameStateChanged(GameStateChanged event)
	{
		switch (event.getGameState())
		{
			case LOGIN_SCREEN:
				if (loggedPlayerName != null)
				{
					flushPlayersToClient(false);
					persistentSightings.clear();
					feedbackedPlayers.clear();
					feedbackedPlayersText.clear();
					flaggedPlayers.clear();
					loggedPlayerName = null;

					refreshPlayerStats(true);
					SwingUtilities.invokeLater(() -> panel.setWarningVisible(BotDetectorPanel.WarningLabel.NAME_ERROR, false));
					lastStatsRefresh = Instant.MIN;
				}
				break;
			case LOGGED_IN:
				// Reload Sighting cache when passing from LOGGED_IN -> LOADING -> LOGGED_IN
				if (!isCurrentWorldBlocked && loggedPlayerName != null
					&& previousTwoGameStates.contains(GameState.LOGGED_IN)
					&& previousTwoGameStates.contains(GameState.LOADING))
				{
					client.getPlayers().forEach(this::processPlayer);
				}
				break;
		}
		previousTwoGameStates.offer(event.getGameState());
	}

	@Subscribe
	private void onPlayerSpawned(PlayerSpawned event)
	{
		processPlayer(event.getPlayer());
	}

	/**
	 * Processes the given {@code player}, creating and saving a {@link PlayerSighting}.
	 * @param player The player to process.
	 */
	private void processPlayer(Player player)
	{
		if (player == null)
		{
			return;
		}

		String rawName = player.getName();

		boolean invalidName = rawName == null || rawName.length() == 0 || rawName.charAt(0) == '#' || rawName.charAt(0) == '[';

		if (player == client.getLocalPlayer())
		{
			if (loggedPlayerName == null || !loggedPlayerName.equals(rawName))
			{
				if (invalidName)
				{
					loggedPlayerName = null;
					SwingUtilities.invokeLater(() -> panel.setWarningVisible(BotDetectorPanel.WarningLabel.NAME_ERROR, true));
				}
				else
				{
					loggedPlayerName = rawName;
					updateTimeToAutoSend();
					refreshPlayerStats(true);
					SwingUtilities.invokeLater(() -> panel.setWarningVisible(BotDetectorPanel.WarningLabel.NAME_ERROR, false));
				}
			}
			return;
		}

		// Block processing AFTER local player check
		if (isCurrentWorldBlocked || invalidName)
		{
			return;
		}

		String playerName = normalizePlayerName(rawName);
		CaseInsensitiveString wrappedName = wrap(playerName);

		// Maybe using clientThread will help with whatever is going on with instance regions sneaking through?
		// Theory is on some machines, maybe isInInstance() returns false, but player gets changed before getWorldLocation() runs?
		// IDK man I can't ever seem to be able to repro this...
		clientThread.invoke(() ->
			{
				boolean instanced = client.isInInstancedRegion();

				WorldPoint wp = !instanced ? player.getWorldLocation()
					: WorldPoint.fromLocalInstance(client, player.getLocalLocation());

				if (wp.getRegionID() > MAX_ALLOWED_REGION_ID)
				{
					log.warn(String.format("Player sighting with invalid region ID. (name:'%s' x:%d y:%d z:%d r:%d s:%d)",
						playerName, wp.getX(), wp.getY(), wp.getPlane(), wp.getRegionID(),
						(instanced ? 1 : 0) + (client.isInInstancedRegion() ? 2 : 0))); // Sanity check
					return;
				}

				// Get player's equipment item ids (botanicvelious/Equipment-Inspector)
				Map<KitType, Integer> equipment = new HashMap<>();
				long geValue = 0;
				for (KitType kitType : KitType.values())
				{
					int itemId = player.getPlayerComposition().getEquipmentId(kitType);
					if (itemId >= 0)
					{
						equipment.put(kitType, itemId);
						// Use GE price, not Wiki price
						geValue += itemManager.getItemPriceWithSource(itemId, false);
					}
				}

				PlayerSighting p = PlayerSighting.builder()
					.playerName(playerName)
					.regionID(wp.getRegionID())
					.worldX(wp.getX())
					.worldY(wp.getY())
					.plane(wp.getPlane())
					.equipment(equipment)
					.equipmentGEValue(geValue)
					.timestamp(Instant.now())
					.worldNumber(currentWorldNumber)
					.inMembersWorld(isCurrentWorldMembers)
					.inPVPWorld(isCurrentWorldPVP)
					.build();

				synchronized (sightingTable)
				{
					sightingTable.put(wrappedName, p.getRegionID(), p);
				}
				persistentSightings.put(wrappedName, p);
			}
		);
	}

	@Subscribe
	private void onCommandExecuted(CommandExecuted event)
	{
		Consumer<String[]> consumer = commandConsumerMap.get(wrap(event.getCommand()));
		if (consumer != null)
		{
			consumer.accept(event.getArguments());
		}
	}

	@Subscribe
	private void onClientShutdown(ClientShutdown event)
	{
		if (config.uploadOnShutdown())
		{
			CompletableFuture<Boolean> future = flushPlayersToClient(false);
			if (future != null)
			{
				event.waitFor(future);
			}
		}
	}

	/**
	 * Parses the Author and Code from the given message arguments and sends them over to
	 * {@link BotDetectorClient#verifyDiscord(String, String, String)} for verification.
	 * Requires that {@link #authToken} has the {@link AuthTokenPermission#VERIFY_DISCORD} permission.
	 * @param chatMessage The ChatMessage event object.
	 * @param message The actual chat message.
	 */
	private void verifyDiscord(ChatMessage chatMessage, String message)
	{
		if (!authToken.getTokenType().getPermissions().contains(AuthTokenPermission.VERIFY_DISCORD))
		{
			return;
		}

		if (message.length() <= VERIFY_DISCORD_COMMAND.length())
		{
			return;
		}

		String author;
		if (chatMessage.getType().equals(ChatMessageType.PRIVATECHATOUT))
		{
			author = loggedPlayerName;
		}
		else
		{
			author = Text.sanitize(chatMessage.getName());
		}

		String code = message.substring(VERIFY_DISCORD_COMMAND.length() + 1).trim();

		if (!VERIFY_DISCORD_CODE_PATTERN.matcher(code).matches())
		{
			return;
		}

		detectorClient.verifyDiscord(authToken.getToken(), author,
			StringUtils.leftPad(code, VERIFY_DISCORD_CODE_SIZE, '0'))
			.whenComplete((b, ex) ->
			{
				if (ex == null && b)
				{
					sendChatStatusMessage("Discord verified for '" + author + "'!", true);
				}
				else if (ex instanceof UnauthorizedTokenException)
				{
					sendChatStatusMessage("Invalid token for Discord verification, cannot verify '" + author + "'.", true);
				}
				else if (config.showDiscordVerificationErrors())
				{
					sendChatStatusMessage("Could not verify Discord for '" + author + "'" + (ex != null ? ": " + ex.getMessage() : "."), true);
				}
			});
	}

	/**
	 * Displays the Bot Detector statistics for the message's author
	 * @param chatMessage The ChatMessage event object.
	 * @param message The actual chat message.
	 */
	private void statsChatCommand(ChatMessage chatMessage, String message)
	{
		if (message.length() != STATS_CHAT_COMMAND.length())
		{
			return;
		}

		final StatsCommandDetailLevel detailLevel = config.statsChatCommandDetailLevel();
		if (detailLevel == StatsCommandDetailLevel.OFF)
		{
			return;
		}

		final String author;
		if (chatMessage.getType().equals(ChatMessageType.PRIVATECHATOUT))
		{
			author = loggedPlayerName;
		}
		else
		{
			author = Text.sanitize(chatMessage.getName());
		}

		detectorClient.requestPlayerStats(author)
			.whenComplete((map, ex) ->
			{
				if (ex == null && map != null)
				{
					PlayerStats totalStats = map.get(PlayerStatsType.TOTAL);

					ChatMessageBuilder response = new ChatMessageBuilder()
						.append(ChatColorType.HIGHLIGHT)
						.append("Bot Detector stats -");

					if (totalStats == null || totalStats.getNamesUploaded() <= 0)
					{
						response.append(ChatColorType.NORMAL)
							.append(" No plugin stats for this player");
					}
					else
					{
						if (detailLevel == StatsCommandDetailLevel.DETAILED)
						{
							response.append(ChatColorType.NORMAL)
								.append(" Total Uploads:")
								.append(ChatColorType.HIGHLIGHT)
								.append(String.format(" %,d", totalStats.getNamesUploaded()))
								.append(ChatColorType.NORMAL)
								.append(" Feedback Sent:")
								.append(ChatColorType.HIGHLIGHT)
								.append(String.format(" %,d", totalStats.getFeedbackSent()))
								.append(ChatColorType.NORMAL)
								.append(" Possible Bans:")
								.append(ChatColorType.HIGHLIGHT)
								.append(String.format(" %,d", totalStats.getPossibleBans()));
						}

						response.append(ChatColorType.NORMAL)
							.append(" Confirmed Bans:")
							.append(ChatColorType.HIGHLIGHT)
							.append(String.format(" %,d", totalStats.getConfirmedBans()));

						PlayerStats manualStats = map.get(PlayerStatsType.MANUAL);
						if (manualStats != null && manualStats.getNamesUploaded() > 0)
						{
							if (detailLevel == StatsCommandDetailLevel.DETAILED)
							{
								response.append(ChatColorType.NORMAL)
									.append(" Manual Flags:")
									.append(ChatColorType.HIGHLIGHT)
									.append(String.format(" %,d", manualStats.getNamesUploaded()))
									.append(ChatColorType.NORMAL)
									.append(" Manual Possible Bans:")
									.append(ChatColorType.HIGHLIGHT)
									.append(String.format(" %,d", manualStats.getPossibleBans()));
							}

							response.append(ChatColorType.NORMAL)
								.append(" Manual Confirmed Bans:")
								.append(ChatColorType.HIGHLIGHT)
								.append(String.format(" %,d", manualStats.getConfirmedBans()));

							response.append(ChatColorType.NORMAL)
								.append(" Manual Flag Accuracy:")
								.append(ChatColorType.HIGHLIGHT)
								.append(new DecimalFormat(" 0.00%").format(manualStats.getAccuracy()));
						}
					}

					final String builtResponse = response.build();
					final MessageNode messageNode = chatMessage.getMessageNode();

					clientThread.invokeLater(() ->
					{
						messageNode.setRuneLiteFormatMessage(builtResponse);
						client.refreshChat();
					});
				}
			});
	}

	@Subscribe
	private void onMenuEntryAdded(MenuEntryAdded event)
	{
		if (!config.addPredictOption())
		{
			return;
		}

		final int componentId = event.getActionParam1();
		int groupId = WidgetInfo.TO_GROUP(componentId);
		String option = event.getOption();

		if (groupId == WidgetInfo.FRIENDS_LIST.getGroupId() || groupId == WidgetInfo.FRIENDS_CHAT.getGroupId() ||
			groupId == WidgetInfo.CHATBOX.getGroupId() && !KICK_OPTION.equals(option) ||
			groupId == WidgetInfo.RAIDING_PARTY.getGroupId() || groupId == WidgetInfo.PRIVATE_CHAT_MESSAGE.getGroupId() ||
			groupId == WidgetInfo.IGNORE_LIST.getGroupId() ||
			componentId == WidgetInfo.CLAN_MEMBER_LIST.getId() || componentId == WidgetInfo.CLAN_GUEST_MEMBER_LIST.getId())
		{
			if (!AFTER_OPTIONS.contains(option) || (option.equals(DELETE_OPTION) && groupId != WidgetInfo.IGNORE_LIST.getGroupId()))
			{
				return;
			}

			// TODO: Properly use the new menu entry callbacks
			client.createMenuEntry(-1)
				.setOption(getPredictOption(event.getTarget()))
				.setTarget(event.getTarget())
				.setType(MenuAction.RUNELITE)
				.setParam0(event.getActionParam0())
				.setParam1(event.getActionParam1())
				.setIdentifier(event.getIdentifier());
		}
	}

	@Subscribe
	private void onMenuOpened(MenuOpened event)
	{
		// If neither color changing options are set, this is unnecessary
		if (config.predictOptionDefaultColor() == null && config.predictOptionFlaggedColor() == null)
		{
			return;
		}

		boolean changeReportOption = config.applyPredictColorsOnReportOption();
		// Do this once when the menu opens
		// Avoids having to loop the menu entries on every 'added' event
		MenuEntry[] menuEntries = event.getMenuEntries();
		for (MenuEntry entry : menuEntries)
		{
			int type = entry.getType().getId();
			if (type >= MenuAction.MENU_ACTION_DEPRIORITIZE_OFFSET)
			{
				type -= MenuAction.MENU_ACTION_DEPRIORITIZE_OFFSET;
			}

			if (type == MenuAction.RUNELITE_PLAYER.getId()
				&& entry.getOption().equals(PREDICT_OPTION))
			{
				Player player = client.getCachedPlayers()[entry.getIdentifier()];
				if (player != null)
				{
					entry.setOption(getPredictOption(player.getName()));
				}
			}

			// Check for Report option
			if (changeReportOption && entry.getOption().equals(REPORT_OPTION)
				&& (PLAYER_MENU_ACTIONS.contains(entry.getType()) || entry.getType() == MenuAction.CC_OP_LOW_PRIORITY))
			{
				Player player = client.getCachedPlayers()[entry.getIdentifier()];
				if (player != null)
				{
					entry.setOption(getReportOption(player.getName()));
				}
			}
		}
	}

	@Subscribe
	private void onMenuOptionClicked(MenuOptionClicked event)
	{
		String optionText = Text.removeTags(event.getMenuOption());
		if (((event.getMenuAction() == MenuAction.RUNELITE || event.getMenuAction() == MenuAction.RUNELITE_PLAYER)
				&& optionText.equals(PREDICT_OPTION))
			|| (config.predictOnReport() && (PLAYER_MENU_ACTIONS.contains(event.getMenuAction()) || event.getMenuAction() == MenuAction.CC_OP_LOW_PRIORITY)
				&& optionText.equals(REPORT_OPTION)))
		{
			String name;
			if (event.getMenuAction() == MenuAction.RUNELITE_PLAYER
				|| PLAYER_MENU_ACTIONS.contains(event.getMenuAction()))
			{
				Player player = client.getCachedPlayers()[event.getId()];

				if (player == null)
				{
					return;
				}

				name = player.getName();
			}
			else
			{
				name = event.getMenuTarget();
			}

			if (name != null)
			{
				String toPredict = Text.removeTags(name);
				if (config.predictOptionCopyName())
				{
					Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(toPredict), null);
				}
				predictPlayer(toPredict);
			}
		}
	}

	@Subscribe
	private void onWorldChanged(WorldChanged event)
	{
		processCurrentWorld();
	}

	/**
	 * Opens the plugin panel and sends over {@code playerName} to {@link BotDetectorPanel#predictPlayer(String)} for prediction.
	 * @param playerName The player name to predict.
	 */
	public void predictPlayer(String playerName)
	{
		SwingUtilities.invokeLater(() ->
		{
			if (!navButton.isSelected())
			{
				navButton.getOnSelect().run();
			}

			panel.predictPlayer(playerName);
		});
	}

	/**
	 * Sends a message to the in-game chatbox if {@link BotDetectorConfig#enableChatStatusMessages()} is {@code true}.
	 * @param msg The message to send.
	 */
	public void sendChatStatusMessage(String msg)
	{
		sendChatStatusMessage(msg, false);
	}

	/**
	 * Sends a message to the in-game chatbox.
	 * @param msg The message to send.
	 * @param forceShow If {@code true}, bypasses {@link BotDetectorConfig#enableChatStatusMessages()}.
	 */
	public void sendChatStatusMessage(String msg, boolean forceShow)
	{
		if ((forceShow || config.enableChatStatusMessages()) && loggedPlayerName != null)
		{
			final String message = new ChatMessageBuilder()
				.append(ChatColorType.HIGHLIGHT)
				.append(CHAT_MESSAGE_HEADER + msg)
				.build();

			chatMessageManager.queue(
				QueuedMessage.builder()
					.type(ChatMessageType.CONSOLE)
					.runeLiteFormattedMessage(message)
					.build());
		}
	}

	/**
	 * Sets various class variables and panel warnings according to what {@link Client#getWorld()} returns.
	 */
	private void processCurrentWorld()
	{
		currentWorldNumber = client.getWorld();
		EnumSet<WorldType> types = client.getWorldType();
		isCurrentWorldMembers = types.contains(WorldType.MEMBERS);
		isCurrentWorldPVP = types.contains(WorldType.PVP);
		isCurrentWorldBlocked = !ALLOWED_PROFILE_TYPES.contains(RuneScapeProfileType.getCurrent(client));
		SwingUtilities.invokeLater(() ->
			panel.setWarningVisible(BotDetectorPanel.WarningLabel.BLOCKED_WORLD, isCurrentWorldBlocked));
	}

	/**
	 * Gets the name that should be used when an uploader name is required,
	 * according to {@link BotDetectorConfig#enableAnonymousUploading()}.
	 * @return {@link #loggedPlayerName} or {@link #ANONYMOUS_USER_NAME}. Returns {@code null} if logged out.
	 */
	public String getUploaderName()
	{
		return getUploaderName(false);
	}

	/**
	 * Gets the name that should be used when an uploader name is required,
	 * according to {@link BotDetectorConfig#enableAnonymousUploading()}.
	 * @param useAnonymousUUIDFormat Whether or not to use the UUID anonymous username format.
	 * @return {@link #loggedPlayerName} if not anonymous. When anonymous, returns
	 * {@link #ANONYMOUS_USER_NAME_UUID_FORMAT} with {@link #anonymousUUID}
	 * or simply {@link #ANONYMOUS_USER_NAME} depending on {@code useAnonymousUUIDFormat}.
	 * Returns {@code null} if logged out.
	 */
	public String getUploaderName(boolean useAnonymousUUIDFormat)
	{
		if (loggedPlayerName == null)
		{
			return null;
		}

		if (config.enableAnonymousUploading())
		{
			return useAnonymousUUIDFormat ?
				String.format(ANONYMOUS_USER_NAME_UUID_FORMAT, anonymousUUID)
				: ANONYMOUS_USER_NAME;
		}

		return loggedPlayerName;
	}

	/**
	 * Gets the correct variant of {@link #PREDICT_OPTION} to show for the given {@code player}.
	 * @param playerName The player to get the menu option string for.
	 * @return A variant of {@link #PREDICT_OPTION} prepended or not with some color.
	 */
	private String getPredictOption(String playerName)
	{
		return getMenuOption(playerName, PREDICT_OPTION);
	}

	/**
	 * Gets the correct variant of {@link #REPORT_OPTION} to show for the given {@code player}.
	 * @param playerName The player to get the menu option string for.
	 * @return A variant of {@link #REPORT_OPTION} prepended or not with some color.
	 */
	private String getReportOption(String playerName)
	{
		return getMenuOption(playerName, REPORT_OPTION);
	}

	/**
	 * Gets the correct variant of the given option string to show for the given {@code player}.
	 * @param playerName The player to get the menu option string for.
	 * @return A variant of the option string prepended or not with some color.
	 */
	private String getMenuOption(String playerName, String option)
	{
		CaseInsensitiveString name = normalizeAndWrapPlayerName(playerName);
		Color prepend = (feedbackedPlayers.containsKey(name) || flaggedPlayers.containsKey(name)) ?
			config.predictOptionFlaggedColor() : config.predictOptionDefaultColor();

		return prepend != null ? ColorUtil.prependColorTag(option, prepend) : option;
	}

	/**
	 * Normalizes the given {@code playerName} by sanitizing the player name string,
	 * removing any Jagex tags and replacing any {@code _} or {@code -} with spaces.
	 * @param playerName The player name to normalize.
	 * @return The normalized {@code playerName}.
	 */
	public static String normalizePlayerName(String playerName)
	{
		if (playerName == null)
		{
			return null;
		}

		return Text.removeTags(Text.toJagexName(playerName));
	}

	/**
	 * Normalizes the given {@code playerName} using {@link #normalizePlayerName(String)},
	 * then wraps the resulting {@link String} with {@link CaseInsensitiveString#wrap(String)}.
	 * @param playerName The player name to normalize and wrap.
	 * @return A {@link CaseInsensitiveString} containing the normalized {@code playerName}.
	 */
	public static CaseInsensitiveString normalizeAndWrapPlayerName(String playerName)
	{
		return wrap(normalizePlayerName(playerName));
	}

	//region Commands

	/**
	 * Manually executes {@link #flushPlayersToClient(boolean, boolean)},
	 * first checking that {@link #lastFlush} did not occur within {@link #MANUAL_FLUSH_COOLDOWN_SECONDS}.
	 */
	private void manualFlushCommand()
	{
		Instant canFlush = lastFlush.plusSeconds(MANUAL_FLUSH_COOLDOWN_SECONDS);
		Instant now = Instant.now();
		if (now.isAfter(canFlush))
		{
			if (flushPlayersToClient(true, true) == null)
			{
				sendChatStatusMessage("No player sightings to flush!", true);
			}
		}
		else
		{
			long secs = (Duration.between(now, canFlush).toMillis() / 1000) + 1;
			sendChatStatusMessage("Please wait " + secs + " seconds before manually flushing players.", true);
		}
	}

	/**
	 * Manually force a full rescan of all players in {@link Client#getPlayers()} using {@link #processPlayer(Player)}.
	 */
	private void manualSightCommand()
	{
		if (isCurrentWorldBlocked)
		{
			sendChatStatusMessage("Cannot refresh player sightings on a blocked world.", true);
		}
		else if (client.getGameState() != GameState.LOGGED_IN)
		{
			// Just in case!
			sendChatStatusMessage("Current game state must be 'LOGGED_IN'!", true);
		}
		else
		{
			client.getPlayers().forEach(this::processPlayer);
			sendChatStatusMessage("Player sightings refreshed.", true);
		}
	}

	/**
	 * Manually force executes {@link #refreshPlayerStats(boolean)}.
	 */
	private void manualRefreshStatsCommand()
	{
		refreshPlayerStats(true);
		sendChatStatusMessage("Refreshing player stats...", true);
	}

	/**
	 * Shows or hides the player ID field in the plugin panel using {@link BotDetectorPanel#setPlayerIdVisible(boolean)}.
	 * @param args String arguments from {@link CommandExecuted#getArguments()}, requires 1 argument being either "0" or "1".
	 */
	private void showHideIdCommand(String[] args)
	{
		String arg = args.length > 0 ? args[0] : "";
		switch (arg)
		{
			case "1":
				SwingUtilities.invokeLater(() -> panel.setPlayerIdVisible(true));
				sendChatStatusMessage("Player ID field added to panel.", true);
				break;
			case "0":
				SwingUtilities.invokeLater(() -> panel.setPlayerIdVisible(false));
				sendChatStatusMessage("Player ID field hidden.", true);
				break;
			default:
				sendChatStatusMessage("Argument must be 0 or 1.", true);
				break;
		}
	}

	/**
	 * Gets the currently loaded {@link AuthToken} and copies it into the user's system clipboard.
	 */
	private void putAuthTokenIntoClipboardCommand()
	{
		if (authToken.getTokenType() == AuthTokenType.NONE)
		{
			sendChatStatusMessage("No auth token currently set.", true);
		}
		else
		{
			Toolkit.getDefaultToolkit().getSystemClipboard().setContents(
				new StringSelection(authToken.toFullToken()), null);
			sendChatStatusMessage(authToken.getTokenType() + " auth token copied to clipboard.", true);
		}
	}

	/**
	 * Sets the {@link AuthToken} saved in {@link BotDetectorConfig#authFullToken()} to the contents of the clipboard,
	 * assuming the contents respect the defined token format in {@link AuthToken#AUTH_TOKEN_PATTERN}.
	 */
	private void setAuthTokenFromClipboardCommand()
	{
		final String clipboardText;
		try
		{
			clipboardText = Toolkit.getDefaultToolkit()
				.getSystemClipboard()
				.getData(DataFlavor.stringFlavor)
				.toString().trim();
		}
		catch (IOException | UnsupportedFlavorException ex)
		{
			sendChatStatusMessage("Unable to read system clipboard for dev token.", true);
			log.warn("Error reading clipboard", ex);
			return;
		}

		AuthToken token = AuthToken.fromFullToken(clipboardText);

		if (token.getTokenType() == AuthTokenType.NONE)
		{
			sendChatStatusMessage(AuthToken.AUTH_TOKEN_DESCRIPTION_MESSAGE, true);
		}
		else
		{
			authToken = token;
			config.setAuthFullToken(token.toFullToken());
			sendChatStatusMessage(token.getTokenType() + " auth token successfully set from clipboard.", true);
		}
	}

	/**
	 * Clears the current {@link AuthToken} saved in {@link BotDetectorConfig#authFullToken()}.
	 */
	private void clearAuthTokenCommand()
	{
		authToken = AuthToken.EMPTY_TOKEN;
		config.setAuthFullToken(null);
		sendChatStatusMessage("Auth token cleared.", true);
	}

	/**
	 * Toggles the config value in {@link BotDetectorConfig#showDiscordVerificationErrors()} and notifies the user of the change.
	 */
	private void toggleShowDiscordVerificationErrors()
	{
		boolean newVal = !config.showDiscordVerificationErrors();
		config.setShowDiscordVerificationErrors(newVal);
		if (newVal)
		{
			sendChatStatusMessage("Discord verification errors will now be shown in the chat", true);
		}
		else
		{
			sendChatStatusMessage("Discord verification errors will no longer be shown in the chat", true);
		}
	}

	//endregion


	/**
	 * Displays an error message about being unable to parse a plugin version and links to the Bot Detector Discord.
	 */
	private void displayPluginVersionError()
	{
		JEditorPane ep = new JEditorPane("text/html",
			"<html><body>Could not parse the plugin version from the properties file!"
			+ "<br>This should never happen! Please contact us on our <a href="
			+ BotDetectorPanel.WebLink.DISCORD.getLink() + ">Discord</a>.</body></html>");
		ep.addHyperlinkListener(e ->
		{
			if (e.getEventType().equals(HyperlinkEvent.EventType.ACTIVATED))
			{
				LinkBrowser.browse(e.getURL().toString());
			}
		});
		ep.setEditable(false);
		JOptionPane.showOptionDialog(null, ep,
			"Error starting Bot Detector!", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE,
			null, new String[]{"Ok"}, "Ok");
	}
}
