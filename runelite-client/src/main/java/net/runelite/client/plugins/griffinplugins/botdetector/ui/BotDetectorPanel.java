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
package net.runelite.client.plugins.griffinplugins.botdetector.ui;

import static net.runelite.client.plugins.griffinplugins.botdetector.BotDetectorPlugin.normalizeAndWrapPlayerName;
import static net.runelite.client.plugins.griffinplugins.botdetector.model.FeedbackPredictionLabel.normalizeLabel;
import com.google.common.primitives.Doubles;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ItemEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import javax.inject.Inject;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.plugins.griffinplugins.botdetector.BotDetectorPlugin;
import net.runelite.client.plugins.griffinplugins.botdetector.BotDetectorConfig;
import net.runelite.client.plugins.griffinplugins.botdetector.events.BotDetectorPanelActivated;
import net.runelite.client.plugins.griffinplugins.botdetector.http.BotDetectorClient;
import net.runelite.client.plugins.griffinplugins.botdetector.model.*;
import net.runelite.client.plugins.griffinplugins.botdetector.ui.components.ComboBoxSelfTextTooltipListRenderer;
import net.runelite.client.plugins.griffinplugins.botdetector.ui.components.JLimitedTextArea;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.PluginPanel;
import net.runelite.client.ui.components.IconTextField;
import net.runelite.client.ui.components.materialtabs.MaterialTab;
import net.runelite.client.ui.components.materialtabs.MaterialTabGroup;
import net.runelite.client.util.ColorUtil;
import net.runelite.client.util.LinkBrowser;
import net.runelite.client.util.QuantityFormatter;
import org.apache.commons.text.StringEscapeUtils;

public class BotDetectorPanel extends PluginPanel
{
	@Getter
	@AllArgsConstructor
	public enum WebLink
	{
		WEBSITE(Icons.WEB_ICON, "Our website", "https://www.osrsbotdetector.com/"),
		TWITTER(Icons.TWITTER_ICON, "Follow us on Twitter!", "https://www.twitter.com/OSRSBotDetector"),
		DISCORD(Icons.DISCORD_ICON, "Join our Discord!", "https://discord.com/invite/JCAGpcjbfP"),
		GITHUB(Icons.GITHUB_ICON, "Check out the project's source code", "https://github.com/Bot-detector"),
		PATREON(Icons.PATREON_ICON, "Help keep us going!", "https://www.patreon.com/Ferrariic")
		;

		private final ImageIcon image;
		private final String tooltip;
		private final String link;
	}

	@Getter
	@AllArgsConstructor
	public enum WarningLabel
	{
		ANONYMOUS(Icons.WARNING_ICON, " Anonymous Uploading Active",
			"<html>Your name will not be included with your uploads and your tallies will not increase."
				+ "<br>Manual bot flagging is also disabled.</html>"),
		BLOCKED_WORLD(Icons.WARNING_ICON, " No Uploading For Current World",
			"<html>You are currently logged into a world where player sightings are not being collected."
				+ "<br>Your tallies will not increase from seeing players in this world.</html>"),
		PLAYER_STATS_ERROR(Icons.ERROR_ICON, " Could Not Retrieve Statistics",
			"<html>Your player statistics could not be retrieved at this time."
				+ "<br>Either the server could not assign you an ID or the server is down at the moment.</html>"),
		NAME_ERROR(Icons.ERROR_ICON, " Invalid Player Name",
			"<html>Your player name could not be loaded correctly."
				+ "<br>Most likely you spawned on Tutorial Island or your name was forcibly reset by Jagex."
				+ "<br>Try relogging after setting a name.</html>")
		;

		private final Icon image;
		private final String message;
		private final String tooltip;
	}

	private static final int MAX_RSN_LENGTH = 12;
	private static final Pattern VALID_RSN_PATTERN = Pattern.compile("^[ _\\-]*[a-zA-Z0-9][\\w\\- ]*$");
	private static final Font BOLD_FONT = FontManager.getRunescapeBoldFont();
	private static final Font NORMAL_FONT = FontManager.getRunescapeFont();
	private static final Font SMALL_FONT = FontManager.getRunescapeSmallFont();

	private static final Color BACKGROUND_COLOR = ColorScheme.DARK_GRAY_COLOR;
	private static final Color SUB_BACKGROUND_COLOR = ColorScheme.DARKER_GRAY_COLOR;
	private static final Color LINK_HEADER_COLOR = ColorScheme.LIGHT_GRAY_COLOR;
	private static final Color HEADER_COLOR = Color.WHITE;
	private static final Color TEXT_COLOR = ColorScheme.LIGHT_GRAY_COLOR;
	private static final Color VALUE_COLOR = Color.WHITE;
	private static final Color ERROR_COLOR = ColorScheme.PROGRESS_ERROR_COLOR;
	private static final Color POSITIVE_BUTTON_COLOR = ColorScheme.PROGRESS_COMPLETE_COLOR;
	private static final Color NEUTRAL_BUTTON_COLOR = ColorScheme.PROGRESS_INPROGRESS_COLOR;
	private static final Color NEGATIVE_BUTTON_COLOR = ColorScheme.PROGRESS_ERROR_COLOR;

	private static final String EMPTY_LABEL = "---";

	private static final int HEADER_PAD = 3;
	private static final int WARNING_PAD = 5;
	private static final int VALUE_PAD = 2;
	private static final int SUB_PANEL_SEPARATION_HEIGHT = 10;
	private static final Border SUB_PANEL_BORDER = new EmptyBorder(5, 10, 10, 10);
	private static final Dimension HEADER_PREFERRED_SIZE = new Dimension(0, 25);

	private static final int MAX_FEEDBACK_TEXT_CHARS = 250;
	private static final Dimension FEEDBACK_TEXTBOX_PREFERRED_SIZE = new Dimension(0, 75);

	private static final FeedbackPredictionLabel UNSURE_PREDICTION_LABEL = new FeedbackPredictionLabel("Unsure", null, FeedbackValue.NEUTRAL);
	private static final FeedbackPredictionLabel SOMETHING_ELSE_PREDICTION_LABEL = new FeedbackPredictionLabel("Something_else", null, FeedbackValue.NEGATIVE);
	private static final FeedbackPredictionLabel CORRECT_FALLBACK_PREDICTION_LABEL = new FeedbackPredictionLabel("Correct", null, FeedbackValue.POSITIVE);

	private static final PlayerStatsType[] PLAYER_STAT_TYPES = {
		PlayerStatsType.TOTAL, PlayerStatsType.PASSIVE, PlayerStatsType.MANUAL
	};

	private final IconTextField searchBar;
	private final JPanel linksPanel;
	private final JPanel playerStatsPanel;
	private final JPanel primaryPredictionPanel;
	private final JPanel predictionFeedbackPanel;
	private final JPanel predictionFlaggingPanel;
	private final JPanel predictionBreakdownPanel;

	private final BotDetectorPlugin plugin;
	private final BotDetectorClient detectorClient;
	private final BotDetectorConfig config;
	private final NameAutocompleter nameAutocompleter;
	private final EventBus eventBus;

	/** Components that can have their font switched on the fly. See {@link #setFontType(PanelFontType)}. **/
	private final Set<JComponent> switchableFontComponents = new HashSet<>();

	private boolean statsLoading;
	private boolean searchBarLoading;

	// Player Stats
	private JLabel playerStatsHeaderLabel;
	private JLabel playerStatsPluginVersionLabel;
	private JLabel playerStatsUploadedNamesLabel;
	private JLabel playerStatsTotalUploadsLabel;
	private JLabel playerStatsFeedbackSentLabel;
	private JLabel playerStatsPossibleBansLabel;
	private JLabel playerStatsConfirmedBansLabel;
	private JLabel playerStatsIncorrectFlagsLabel;
	private JLabel playerStatsFlagAccuracyLabel;
	private final Map<WarningLabel, JLabel> warningLabels = new HashMap<>();
	private Map<PlayerStatsType, PlayerStats> playerStatsMap;
	private int playerCurrentManualUploads;
	private int playerCurrentPassiveUploads;
	private final MaterialTabGroup playerStatsTabGroup;
	private PlayerStatsType currentPlayerStatsType;

	// Primary Prediction
	private JLabel predictionPlayerIdTextLabel;
	private JLabel predictionPlayerIdLabel;
	private JLabel predictionPlayerNameLabel;
	private JLabel predictionTypeLabel;
	private JLabel predictionConfidenceLabel;

	// Prediction Breakdown
	private JLabel predictionBreakdownLabel;

	// For feedback/flag
	private JLabel feedbackHeaderLabel;
	private JComboBox<FeedbackPredictionLabel> feedbackLabelComboBox;
	private JButton feedbackSendButton;
	private JScrollPane feedbackTextScrollPane;
	private JLimitedTextArea feedbackTextbox;
	private JLabel flaggingHeaderLabel;
	private JButton flaggingYesButton;
	private JButton flaggingNoButton;
	private Prediction lastPrediction;
	private PlayerSighting lastPredictionPlayerSighting;
	private String lastPredictionUploaderName;

	@Inject
	public BotDetectorPanel(
		BotDetectorPlugin plugin,
		BotDetectorClient detectorClient,
		BotDetectorConfig config,
		NameAutocompleter nameAutocompleter,
		EventBus eventBus)
	{
		this.plugin = plugin;
		this.detectorClient = detectorClient;
		this.config = config;
		this.nameAutocompleter = nameAutocompleter;
		this.eventBus = eventBus;

		setBorder(new EmptyBorder(10, 10, 10, 10));
		setBackground(BACKGROUND_COLOR);
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		currentPlayerStatsType = config.panelDefaultStatsType();

		searchBar = playerSearchBar();
		linksPanel = linksPanel();
		// Used in the next panel, define now!
		playerStatsTabGroup = playerStatsTabGroup();
		playerStatsPanel = playerStatsPanel();
		primaryPredictionPanel = primaryPredictionPanel();
		predictionFeedbackPanel = putInBoxPanelWithVerticalSeparator(predictionFeedbackPanel());
		predictionFeedbackPanel.setVisible(false);
		predictionFlaggingPanel = putInBoxPanelWithVerticalSeparator(predictionFlaggingPanel());
		predictionFlaggingPanel.setVisible(false);
		predictionBreakdownPanel = putInBoxPanelWithVerticalSeparator(predictionBreakdownPanel());
		predictionBreakdownPanel.setVisible(false);

		add(linksPanel);

		add(Box.createVerticalStrut(SUB_PANEL_SEPARATION_HEIGHT));
		add(playerStatsPanel);

		add(Box.createVerticalStrut(SUB_PANEL_SEPARATION_HEIGHT));
		add(searchBar);

		add(Box.createVerticalStrut(SUB_PANEL_SEPARATION_HEIGHT));
		add(primaryPredictionPanel);

		add(predictionBreakdownPanel);

		add(predictionFeedbackPanel);

		add(predictionFlaggingPanel);

		setPlayerIdVisible(false);
		setPrediction(null);
		setPlayerStatsMap(null);
		setFontType(config.panelFontType());

		addInputKeyListener(nameAutocompleter);
	}

	public void shutdown()
	{
		removeInputKeyListener(nameAutocompleter);
	}

	@Override
	public void onActivate()
	{
		eventBus.post(new BotDetectorPanelActivated());
	}

	/**
	 * Puts the panel in a box layout panel with a vertical pad above ({@link #SUB_PANEL_SEPARATION_HEIGHT}).
	 * @param panel The panel.
	 * @return A panel containing the previous panel with a vertical padding element above.
	 */
	private static JPanel putInBoxPanelWithVerticalSeparator(JPanel panel)
	{
		JPanel newPanel = new JPanel();
		newPanel.setBorder(new EmptyBorder(0, 0, 0, 0));
		newPanel.setLayout(new BoxLayout(newPanel, BoxLayout.Y_AXIS));
		newPanel.add(Box.createVerticalStrut(SUB_PANEL_SEPARATION_HEIGHT));
		newPanel.add(panel);
		return newPanel;
	}

	/**
	 * Generates and sets variables related to the links panel.
	 * @return The panel containing all the related elements.
	 */
	private JPanel linksPanel()
	{
		JPanel linksPanel = new JPanel();
		linksPanel.setBorder(new EmptyBorder(0, 0, 0, 0));
		linksPanel.setBackground(SUB_BACKGROUND_COLOR);

		JLabel title = new JLabel("Connect With Us: ");
		title.setForeground(LINK_HEADER_COLOR);
		title.setFont(NORMAL_FONT);

		linksPanel.add(title);

		for (WebLink w : WebLink.values())
		{
			JLabel link = new JLabel(w.getImage());
			link.setToolTipText(w.getTooltip());
			link.addMouseListener(new MouseAdapter()
			{
				@Override
				public void mousePressed(MouseEvent e)
				{
					LinkBrowser.browse(w.getLink());
				}
			});

			linksPanel.add(link);
		}

		return linksPanel;
	}

	/**
	 * Generates a tab group object to be used in {@link #playerStatsPanel()},
	 * allowing splitting stats by {@link #currentPlayerStatsType}.
	 * @return The tab group object.
	 */
	private MaterialTabGroup playerStatsTabGroup()
	{
		MaterialTabGroup tabGroup = new MaterialTabGroup();
		tabGroup.setLayout(new GridLayout(1, PLAYER_STAT_TYPES.length, 7, 7));
		tabGroup.setBorder(new EmptyBorder(VALUE_PAD, 0, VALUE_PAD, 0));

		for (PlayerStatsType pst : PLAYER_STAT_TYPES)
		{
			MaterialTab tab = new MaterialTab(pst.getShorthand(), tabGroup, null);
			tab.setToolTipText(pst.getDescription());
			tab.setFont(SMALL_FONT);
			tab.setHorizontalAlignment(JLabel.CENTER);

			tab.setOnSelectEvent(() ->
			{
				currentPlayerStatsType = pst;
				return true;
			});

			tab.addMouseListener(new MouseAdapter()
			{
				@Override
				public void mousePressed(MouseEvent mouseEvent)
				{
					updatePlayerStatsLabels();
				}
			});

			tabGroup.addTab(tab);
			if (currentPlayerStatsType == pst)
			{
				tabGroup.select(tab);
			}
		}

		return tabGroup;
	}

	/**
	 * Generates and sets variables related to the player contributions panel.
	 * @return The panel containing all the related elements.
	 */
	private JPanel playerStatsPanel()
	{
		JLabel label;

		JPanel uploadingStatsPanel = new JPanel();
		uploadingStatsPanel.setBackground(SUB_BACKGROUND_COLOR);
		uploadingStatsPanel.setBorder(SUB_PANEL_BORDER);

		uploadingStatsPanel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;

		playerStatsHeaderLabel = new JLabel("Player Statistics");
		playerStatsHeaderLabel.setHorizontalTextPosition(JLabel.LEFT);
		playerStatsHeaderLabel.setFont(BOLD_FONT);
		playerStatsHeaderLabel.setForeground(HEADER_COLOR);
		playerStatsHeaderLabel.setPreferredSize(HEADER_PREFERRED_SIZE);
		playerStatsHeaderLabel.setMinimumSize(HEADER_PREFERRED_SIZE);

		c.gridx = 0;
		c.gridy = 0;
		c.ipady = HEADER_PAD;
		c.gridwidth = 2;
		c.weightx = 1;
		c.anchor = GridBagConstraints.NORTH;
		uploadingStatsPanel.add(playerStatsHeaderLabel, c);

		label = new JLabel("Plugin Version: ");
		label.setToolTipText("The Bot Detector plugin version you're running.");
		label.setForeground(TEXT_COLOR);

		c.gridy = 1;
		c.gridy++;
		c.ipady = VALUE_PAD;
		c.gridwidth = 1;
		c.weightx = 0;
		uploadingStatsPanel.add(label, c);
		switchableFontComponents.add(label);

		playerStatsPluginVersionLabel = new JLabel();
		playerStatsPluginVersionLabel.setForeground(VALUE_COLOR);
		c.gridx = 1;
		c.weightx = 1;
		uploadingStatsPanel.add(playerStatsPluginVersionLabel, c);
		switchableFontComponents.add(playerStatsPluginVersionLabel);

		label = new JLabel("Current Uploads: ");
		label.setToolTipText("How many names uploaded during the current Runelite session.");
		label.setForeground(TEXT_COLOR);

		c.gridy++;
		c.gridx = 0;
		c.weightx = 0;
		uploadingStatsPanel.add(label, c);
		switchableFontComponents.add(label);

		playerStatsUploadedNamesLabel = new JLabel();
		playerStatsUploadedNamesLabel.setForeground(VALUE_COLOR);
		c.gridx = 1;
		c.weightx = 1;
		uploadingStatsPanel.add(playerStatsUploadedNamesLabel, c);
		switchableFontComponents.add(playerStatsUploadedNamesLabel);

		label = new JLabel("Total Uploads: ");
		label.setToolTipText("How many unique names sent to us that were attributed to you.");
		label.setForeground(TEXT_COLOR);
		c.gridy++;
		c.gridx = 0;
		c.weightx = 0;
		uploadingStatsPanel.add(label, c);
		switchableFontComponents.add(label);

		playerStatsTotalUploadsLabel = new JLabel();
		playerStatsTotalUploadsLabel.setForeground(VALUE_COLOR);
		c.gridx = 1;
		c.weightx = 1;
		uploadingStatsPanel.add(playerStatsTotalUploadsLabel, c);
		switchableFontComponents.add(playerStatsTotalUploadsLabel);

		label = new JLabel("Feedback Sent: ");
		label.setToolTipText("How many prediction feedbacks you've sent us.");
		label.setForeground(TEXT_COLOR);
		c.gridy++;
		c.gridx = 0;
		c.weightx = 0;
		uploadingStatsPanel.add(label, c);
		switchableFontComponents.add(label);

		playerStatsFeedbackSentLabel = new JLabel();
		playerStatsFeedbackSentLabel.setForeground(VALUE_COLOR);
		c.gridx = 1;
		c.weightx = 1;
		uploadingStatsPanel.add(playerStatsFeedbackSentLabel, c);
		switchableFontComponents.add(playerStatsFeedbackSentLabel);

		label = new JLabel("Possible Bans: ");
		label.setToolTipText(
			"<html>How many of your uploaded names may have been banned." +
			"<br>For example: Names that no longer appear on the Hiscores.</html>");
		label.setForeground(TEXT_COLOR);
		c.gridy++;
		c.gridx = 0;
		c.weightx = 0;
		uploadingStatsPanel.add(label, c);
		switchableFontComponents.add(label);

		playerStatsPossibleBansLabel = new JLabel();
		playerStatsPossibleBansLabel.setForeground(VALUE_COLOR);
		c.gridx = 1;
		c.weightx = 1;
		uploadingStatsPanel.add(playerStatsPossibleBansLabel, c);
		switchableFontComponents.add(playerStatsPossibleBansLabel);

		label = new JLabel("Confirmed Bans: ");
		label.setToolTipText("How many of your uploaded names were confirmed to have been banned by Jagex.");
		label.setForeground(TEXT_COLOR);
		c.gridy++;
		c.gridx = 0;
		c.weightx = 0;
		uploadingStatsPanel.add(label, c);
		switchableFontComponents.add(label);

		playerStatsConfirmedBansLabel = new JLabel();
		playerStatsConfirmedBansLabel.setForeground(VALUE_COLOR);
		c.gridx = 1;
		c.weightx = 1;
		uploadingStatsPanel.add(playerStatsConfirmedBansLabel, c);
		switchableFontComponents.add(playerStatsConfirmedBansLabel);

		label = new JLabel("Incorrect Flags: ");
		label.setToolTipText("How many of your flagged names were confirmed to have been real players by Jagex.");
		label.setForeground(TEXT_COLOR);
		c.gridy++;
		c.gridx = 0;
		c.weightx = 0;
		uploadingStatsPanel.add(label, c);
		switchableFontComponents.add(label);

		playerStatsIncorrectFlagsLabel = new JLabel();
		playerStatsIncorrectFlagsLabel.setForeground(VALUE_COLOR);
		c.gridx = 1;
		c.weightx = 1;
		uploadingStatsPanel.add(playerStatsIncorrectFlagsLabel, c);
		switchableFontComponents.add(playerStatsIncorrectFlagsLabel);

		label = new JLabel("Flag Accuracy: ");
		label.setToolTipText("How accurate your flagging has been.");
		label.setForeground(TEXT_COLOR);
		c.gridy++;
		c.gridx = 0;
		c.weightx = 0;
		uploadingStatsPanel.add(label, c);
		switchableFontComponents.add(label);

		playerStatsFlagAccuracyLabel = new JLabel();
		playerStatsFlagAccuracyLabel.setForeground(VALUE_COLOR);
		c.gridx = 1;
		c.weightx = 1;
		uploadingStatsPanel.add(playerStatsFlagAccuracyLabel, c);
		switchableFontComponents.add(playerStatsFlagAccuracyLabel);

		c.gridy++;
		c.gridx = 0;
		c.weightx = 1;
		c.gridwidth = 2;
		c.ipady = 0;
		uploadingStatsPanel.add(playerStatsTabGroup, c);

		c.ipady = WARNING_PAD;
		for (WarningLabel wl : WarningLabel.values())
		{
			c.gridy++;
			label = new JLabel(wl.getMessage());
			label.setToolTipText(wl.getTooltip());
			label.setIcon(wl.getImage());
			label.setFont(NORMAL_FONT);
			label.setForeground(HEADER_COLOR);
			label.setVisible(false);
			uploadingStatsPanel.add(label, c);
			warningLabels.put(wl, label);
		}

		return uploadingStatsPanel;
	}

	/**
	 * Generates a search bar object to be placed in the plugin panel.
	 * @return The seach bar object.
	 */
	private IconTextField playerSearchBar()
	{
		IconTextField searchBar = new IconTextField();
		searchBar.setIcon(IconTextField.Icon.SEARCH);
		searchBar.setPreferredSize(new Dimension(PluginPanel.PANEL_WIDTH - 20, 30));
		searchBar.setBackground(SUB_BACKGROUND_COLOR);
		searchBar.setHoverBackgroundColor(ColorScheme.DARK_GRAY_HOVER_COLOR);
		searchBar.setMinimumSize(new Dimension(0, 30));
		searchBar.addActionListener(e -> predictPlayer());
		searchBar.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent e)
			{
				if (e.getClickCount() != 2)
				{
					return;
				}

				String name = plugin.getLoggedPlayerName();
				if (name != null)
				{
					predictPlayer(name);
				}
			}
		});
		searchBar.addClearListener(() ->
		{
			searchBar.setIcon(IconTextField.Icon.SEARCH);
			searchBar.setEditable(true);
			searchBarLoading = false;
		});

		return searchBar;
	}

	/**
	 * Generates and sets variables related to the main prediction panel.
	 * @return The panel containing all the related elements.
	 */
	private JPanel primaryPredictionPanel()
	{
		JLabel label;

		JPanel primaryPredictionPanel = new JPanel();
		primaryPredictionPanel.setBackground(SUB_BACKGROUND_COLOR);
		primaryPredictionPanel.setLayout(new GridBagLayout());
		primaryPredictionPanel.setBorder(SUB_PANEL_BORDER);

		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;

		label = new JLabel("Primary Prediction");
		label.setFont(BOLD_FONT);
		label.setForeground(HEADER_COLOR);
		label.setPreferredSize(HEADER_PREFERRED_SIZE);
		label.setMinimumSize(HEADER_PREFERRED_SIZE);
		c.gridx = 0;
		c.gridy = 0;
		c.ipady = HEADER_PAD;
		c.gridwidth = 2;
		c.weightx = 1;
		primaryPredictionPanel.add(label, c);

		predictionPlayerIdTextLabel = new JLabel("Player ID: ");
		predictionPlayerIdTextLabel.setForeground(TEXT_COLOR);
		c.gridy = 1;
		c.gridy++;
		c.ipady = VALUE_PAD;
		c.gridwidth = 1;
		c.weightx = 0;
		c.anchor = GridBagConstraints.NORTH;
		primaryPredictionPanel.add(predictionPlayerIdTextLabel, c);
		switchableFontComponents.add(predictionPlayerIdTextLabel);

		predictionPlayerIdLabel = new JLabel();
		c.gridx = 1;
		c.weightx = 1;
		primaryPredictionPanel.add(predictionPlayerIdLabel, c);
		switchableFontComponents.add(predictionPlayerIdLabel);

		label = new JLabel("Player Name: ");
		label.setForeground(TEXT_COLOR);
		c.gridx = 0;
		c.weightx = 0;
		c.gridy++;
		primaryPredictionPanel.add(label, c);
		switchableFontComponents.add(label);

		predictionPlayerNameLabel = new JLabel();
		c.gridx = 1;
		c.weightx = 1;
		primaryPredictionPanel.add(predictionPlayerNameLabel, c);
		switchableFontComponents.add(predictionPlayerNameLabel);

		label = new JLabel("Prediction: ");
		label.setForeground(TEXT_COLOR);
		c.gridx = 0;
		c.weightx = 0;
		c.gridy++;
		primaryPredictionPanel.add(label, c);
		switchableFontComponents.add(label);

		predictionTypeLabel = new JLabel();
		c.gridx = 1;
		c.weightx = 1;
		primaryPredictionPanel.add(predictionTypeLabel, c);
		switchableFontComponents.add(predictionTypeLabel);

		label = new JLabel("Confidence: ");
		label.setForeground(TEXT_COLOR);
		c.gridx = 0;
		c.weightx = 0;
		c.gridy++;
		primaryPredictionPanel.add(label, c);
		switchableFontComponents.add(label);

		predictionConfidenceLabel = new JLabel();
		c.gridx = 1;
		c.weightx = 1;
		primaryPredictionPanel.add(predictionConfidenceLabel, c);
		switchableFontComponents.add(predictionConfidenceLabel);

		return primaryPredictionPanel;
	}

	/**
	 * Generates and sets variables related to the prediction feedback panel.
	 * @return The panel containing all the related elements.
	 */
	private JPanel predictionFeedbackPanel()
	{
		JPanel panel = new JPanel();
		panel.setBackground(SUB_BACKGROUND_COLOR);
		panel.setLayout(new GridBagLayout());
		panel.setBorder(SUB_PANEL_BORDER);

		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;

		feedbackHeaderLabel = new JLabel("Send a prediction feedback?");
		feedbackHeaderLabel.setHorizontalTextPosition(JLabel.LEFT);
		feedbackHeaderLabel.setFont(NORMAL_FONT);
		feedbackHeaderLabel.setForeground(HEADER_COLOR);
		feedbackHeaderLabel.setPreferredSize(HEADER_PREFERRED_SIZE);
		feedbackHeaderLabel.setMinimumSize(HEADER_PREFERRED_SIZE);
		c.gridx = 0;
		c.gridy = 0;
		c.ipady = HEADER_PAD;
		c.gridwidth = 3;
		c.weightx = 1;
		panel.add(feedbackHeaderLabel, c);

		feedbackTextbox = new JLimitedTextArea(MAX_FEEDBACK_TEXT_CHARS);
		feedbackTextbox.setToolTipText("Please explain your feedback (max " + MAX_FEEDBACK_TEXT_CHARS + " characters).");
		feedbackTextbox.setForeground(HEADER_COLOR);
		feedbackTextbox.setBackground(BACKGROUND_COLOR);
		feedbackTextbox.setFont(SMALL_FONT);
		feedbackTextbox.setWrapStyleWord(true);
		feedbackTextbox.setLineWrap(true);
		feedbackTextbox.setTabSize(2);
		feedbackTextbox.setBorder(new EmptyBorder(2, 2, 2, 2));
		feedbackTextScrollPane = new JScrollPane(feedbackTextbox);
		feedbackTextScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		feedbackTextScrollPane.setPreferredSize(FEEDBACK_TEXTBOX_PREFERRED_SIZE);
		feedbackTextScrollPane.setMinimumSize(FEEDBACK_TEXTBOX_PREFERRED_SIZE);
		feedbackTextScrollPane.setBorder(new EmptyBorder(0, 0, 10, 0));
		feedbackTextScrollPane.setOpaque(false);
		c.gridy++;
		panel.add(feedbackTextScrollPane, c);

		JLabel label = new JLabel("Please select the correct label:");
		label.setHorizontalTextPosition(JLabel.LEFT);
		label.setForeground(HEADER_COLOR);
		label.setFont(NORMAL_FONT);
		c.gridy++;
		panel.add(label, c);

		feedbackLabelComboBox = new JComboBox<>();
		feedbackLabelComboBox.addItemListener(e ->
		{
			if (e.getStateChange() == ItemEvent.SELECTED)
			{
				Object o = feedbackLabelComboBox.getSelectedItem();
				feedbackLabelComboBox.setToolTipText(o != null ? o.toString() : null);
			}
		});
		feedbackLabelComboBox.setRenderer(new ComboBoxSelfTextTooltipListRenderer<>());
		c.gridy++;
		c.gridx = 0;
		c.weightx = 2.0 / 3;
		c.gridwidth = 2;
		panel.add(feedbackLabelComboBox, c);

		feedbackSendButton = new JButton("Send");
		feedbackSendButton.setToolTipText("<html>Tell us the correct label for <b>primary prediction</b>! Doing so will help us improve our model." +
			"<br><span style='color:red'>Please</span>, do not vote against a prediction simply because the percentage is not high enough.</html>");
		feedbackSendButton.setForeground(HEADER_COLOR);
		feedbackSendButton.setFont(SMALL_FONT);
		feedbackSendButton.addActionListener(l -> sendFeedbackToClient((FeedbackPredictionLabel)feedbackLabelComboBox.getSelectedItem()));
		feedbackSendButton.setFocusable(false);
		c.gridx = 2;
		c.weightx = 1.0 / 3;
		c.gridwidth = 1;
		panel.add(feedbackSendButton, c);

		return panel;
	}

	/**
	 * Generates and sets variables related to the prediction flagging panel.
	 * @return The panel containing all the related elements.
	 */
	private JPanel predictionFlaggingPanel()
	{
		JPanel panel = new JPanel();
		panel.setBackground(SUB_BACKGROUND_COLOR);
		panel.setLayout(new GridBagLayout());
		panel.setBorder(SUB_PANEL_BORDER);

		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;

		flaggingHeaderLabel = new JLabel("Flag this player as a bot?");
		flaggingHeaderLabel.setHorizontalTextPosition(JLabel.LEFT);
		flaggingHeaderLabel.setFont(NORMAL_FONT);
		flaggingHeaderLabel.setForeground(HEADER_COLOR);
		flaggingHeaderLabel.setPreferredSize(HEADER_PREFERRED_SIZE);
		flaggingHeaderLabel.setMinimumSize(HEADER_PREFERRED_SIZE);
		c.gridx = 0;
		c.gridy = 0;
		c.ipady = HEADER_PAD;
		c.gridwidth = 2;
		c.weightx = 1;
		panel.add(flaggingHeaderLabel, c);

		flaggingYesButton = new JButton("Yes");
		flaggingYesButton.setToolTipText(
			"<html>This is <span style='color:red'>NOT</span> the same as reporting the player in-game!" +
			"<br>Flagging a player as a bot tells us to pay more attention to them when training our model.</html>");
		flaggingYesButton.setForeground(HEADER_COLOR);
		flaggingYesButton.setFont(SMALL_FONT);
		flaggingYesButton.addActionListener(l -> sendFlagToClient(true));
		flaggingYesButton.setFocusable(false);
		c.gridy++;
		c.weightx = 0.5;
		c.gridwidth = 1;
		panel.add(flaggingYesButton, c);

		flaggingNoButton = new JButton("No");
		flaggingNoButton.setForeground(HEADER_COLOR);
		flaggingNoButton.setFont(SMALL_FONT);
		flaggingNoButton.addActionListener(l -> sendFlagToClient(false));
		flaggingNoButton.setFocusable(false);
		c.gridx++;
		panel.add(flaggingNoButton, c);

		return panel;
	}

	/**
	 * Generates and sets variables related to the prediction breakdown panel.
	 * @return The panel containing all the related elements.
	 */
	private JPanel predictionBreakdownPanel()
	{
		JPanel predictionBreakdownPanel = new JPanel();
		predictionBreakdownPanel.setBackground(SUB_BACKGROUND_COLOR);
		predictionBreakdownPanel.setBorder(SUB_PANEL_BORDER);
		predictionBreakdownPanel.setLayout(new GridBagLayout());

		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;

		JLabel label = new JLabel("Prediction Breakdown");
		label.setFont(BOLD_FONT);
		label.setForeground(HEADER_COLOR);
		label.setPreferredSize(HEADER_PREFERRED_SIZE);
		label.setMinimumSize(HEADER_PREFERRED_SIZE);
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1.0;
		c.ipady = HEADER_PAD;
		predictionBreakdownPanel.add(label, c);

		predictionBreakdownLabel = new JLabel();
		predictionBreakdownLabel.setForeground(TEXT_COLOR);
		c.anchor = GridBagConstraints.PAGE_END;
		c.gridy++;
		predictionBreakdownPanel.add(predictionBreakdownLabel, c);
		switchableFontComponents.add(predictionBreakdownLabel);

		return predictionBreakdownPanel;
	}

	/**
	 * Sets the displayed plugin version string in the panel.
	 * @param pluginVersion The plugin version to display.
	 */
	public void setPluginVersion(String pluginVersion)
	{
		playerStatsPluginVersionLabel.setText(pluginVersion);
	}

	/**
	 * Sets the number of passive/manual uploads, then updates the label (see {@link #updateCurrentUploadsLabel()}).
	 * @param num The number of uploads to display.
	 * @param manual Whether {@code num} is for passive or manual uploads.
	 */
	public void setNamesUploaded(int num, boolean manual)
	{
		if (manual)
		{
			playerCurrentManualUploads = num;
		}
		else
		{
			playerCurrentPassiveUploads = num;
		}
		updateCurrentUploadsLabel();
	}

	/**
	 * Sets the current {@link #playerStatsMap}, then calls {@link #updatePlayerStatsLabels()}.
	 * @param psm The player stats map to set.
	 */
	public void setPlayerStatsMap(Map<PlayerStatsType, PlayerStats> psm)
	{
		playerStatsMap = psm;
		updatePlayerStatsLabels();
	}

	/**
	 * Updates all the fields in the player contributions panel according to
	 * {@link #playerStatsMap} and {@link #currentPlayerStatsType}.
	 * If the map is null or there is no {@link PlayerStats} for the currently displayed type, the fields are cleared.
	 */
	private void updatePlayerStatsLabels()
	{
		PlayerStats ps = playerStatsMap != null ? playerStatsMap.get(currentPlayerStatsType) : null;
		if (ps != null)
		{
			playerStatsTotalUploadsLabel.setText(QuantityFormatter.formatNumber(ps.getNamesUploaded()));
			playerStatsConfirmedBansLabel.setText(QuantityFormatter.formatNumber(ps.getConfirmedBans()));
			playerStatsPossibleBansLabel.setText(QuantityFormatter.formatNumber(ps.getPossibleBans()));
			if (currentPlayerStatsType.canDisplayAccuracy())
			{
				playerStatsIncorrectFlagsLabel.setText(QuantityFormatter.formatNumber(ps.getIncorrectFlags()));
				playerStatsFlagAccuracyLabel.setText(wrapHTML(toColoredPercentSpan(ps.getAccuracy()), false));
			}
			else
			{
				playerStatsIncorrectFlagsLabel.setText(EMPTY_LABEL);
				playerStatsFlagAccuracyLabel.setText(EMPTY_LABEL);
			}
		}
		else
		{
			playerStatsTotalUploadsLabel.setText(EMPTY_LABEL);
			playerStatsConfirmedBansLabel.setText(EMPTY_LABEL);
			playerStatsPossibleBansLabel.setText(EMPTY_LABEL);
			playerStatsIncorrectFlagsLabel.setText(EMPTY_LABEL);
			playerStatsFlagAccuracyLabel.setText(EMPTY_LABEL);
		}

		// Process Feedback sent field separately, it's only available in "Total".
		PlayerStats totalStats = playerStatsMap != null ? playerStatsMap.get(PlayerStatsType.TOTAL) : null;
		if (totalStats != null)
		{
			playerStatsFeedbackSentLabel.setText(QuantityFormatter.formatNumber(totalStats.getFeedbackSent()));
		}
		else
		{
			playerStatsFeedbackSentLabel.setText(EMPTY_LABEL);
		}

		updateCurrentUploadsLabel();
	}

	/**
	 * Updates the value in {@link #playerStatsUploadedNamesLabel} according to
	 * {@link #currentPlayerStatsType}, {@link #playerCurrentManualUploads} and {@link #playerCurrentPassiveUploads}.
	 */
	private void updateCurrentUploadsLabel()
	{
		int val;
		switch (currentPlayerStatsType)
		{
			case MANUAL:
				val = playerCurrentManualUploads;
				break;
			case PASSIVE:
				val = playerCurrentPassiveUploads;
				break;
			default:
				val = playerCurrentManualUploads + playerCurrentPassiveUploads;
				break;
		}
		playerStatsUploadedNamesLabel.setText(QuantityFormatter.formatNumber(val));
	}

	/**
	 * Gets the current visibility of the given warning label.
	 * @param wl The warning label to get the visibility for.
	 * @return True if the warning label is currently visible, false otherwise.
	 */
	public boolean getWarningVisible(WarningLabel wl)
	{
		JLabel label = warningLabels.get(wl);
		return label != null && label.isVisible();
	}

	/**
	 * Sets the visibility of the given warning in the player contributions panel.
	 * @param wl The warning label to set visibility.
	 * @param visible The visibility to set on the warning label.
	 */
	public void setWarningVisible(WarningLabel wl, boolean visible)
	{
		JLabel label = warningLabels.get(wl);
		if (label != null)
		{
			label.setVisible(visible);
		}
	}

	/**
	 * Sets the player contributions panel to be in 'loading' mode.
	 * @param loading Whether or not the player contributions panel is loading.
	 */
	public void setPlayerStatsLoading(boolean loading)
	{
		statsLoading = loading;
		playerStatsHeaderLabel.setIcon(loading ? Icons.LOADING_SPINNER : null);
	}

	/**
	 * Sets the visibility of the player ID field in the main prediction panel.
	 * @param visible The visibility to apply on the player ID field.
	 */
	public void setPlayerIdVisible(boolean visible)
	{
		predictionPlayerIdTextLabel.setVisible(visible);
		predictionPlayerIdLabel.setVisible(visible);
	}

	/**
	 * Sets the visibility of the text box in the feedback panel.
	 * @param visible The visibility to apply on the feedback text box.
	 */
	public void setFeedbackTextboxVisible(boolean visible)
	{
		feedbackTextScrollPane.setVisible(visible);
	}

	/**
	 * Forcibly hides the feedback panel.
	 */
	public void forceHideFeedbackPanel()
	{
		predictionFeedbackPanel.setVisible(false);
	}

	/**
	 * Forcibly hides the flagging panel.
	 */
	public void forceHideFlaggingPanel()
	{
		predictionFlaggingPanel.setVisible(false);
	}

	/**
	 * Sets the given color on all value labels in the main prediction panel.
	 * @param color The color to set on all value labels in the main prediction panel.
	 */
	private void setPredictionLabelsColor(Color color)
	{
		predictionPlayerIdLabel.setForeground(color);
		predictionPlayerNameLabel.setForeground(color);
		predictionTypeLabel.setForeground(color);
		predictionConfidenceLabel.setForeground(color);
	}

	/**
	 * Sets up the prediction and feedback panels according to the given prediction.
	 * @param pred The prediction to display. If {@code null}, clears and resets all panels.
	 */
	public void setPrediction(Prediction pred)
	{
		setPrediction(pred, null);
	}

	/**
	 * Sets up the prediction, feedback and flagging panels according to the given parameters.
	 * @param pred The prediction to display. If {@code null}, clears and resets all panels.
	 * @param sighting The player sighting to associate with the prediction for the flagging panel.
	 */
	public void setPrediction(Prediction pred, PlayerSighting sighting)
	{
		setPredictionLabelsColor(VALUE_COLOR);

		feedbackLabelComboBox.removeAllItems();

		if (pred != null)
		{
			final boolean isNullConfidence = pred.getConfidence() == null;

			nameAutocompleter.addToSearchHistory(pred.getPlayerName().toLowerCase());
			lastPrediction = pred;
			lastPredictionPlayerSighting = sighting;
			lastPredictionUploaderName = plugin.getUploaderName(true);
			predictionPlayerIdLabel.setText(String.valueOf(pred.getPlayerId()));
			predictionPlayerNameLabel.setText(wrapHTML(pred.getPlayerName()));
			predictionTypeLabel.setText(wrapHTML(normalizeLabel(pred.getPredictionLabel())));
			predictionConfidenceLabel.setText(isNullConfidence ? EMPTY_LABEL : wrapHTML(toColoredPercentSpan(pred.getConfidence()), false));

			feedbackLabelComboBox.addItem(UNSURE_PREDICTION_LABEL);
			feedbackLabelComboBox.setSelectedItem(UNSURE_PREDICTION_LABEL);
			feedbackLabelComboBox.addItem(SOMETHING_ELSE_PREDICTION_LABEL);

			if (pred.getPredictionBreakdown() == null || pred.getPredictionBreakdown().size() == 0)
			{
				predictionBreakdownLabel.setText(EMPTY_LABEL);
				predictionBreakdownPanel.setVisible(false);

				feedbackLabelComboBox.addItem(CORRECT_FALLBACK_PREDICTION_LABEL);
			}
			else
			{
				if (isNullConfidence && !config.showBreakdownOnNullConfidence())
				{
					predictionBreakdownLabel.setText(EMPTY_LABEL);
					predictionBreakdownPanel.setVisible(false);
				}
				else
				{
					predictionBreakdownLabel.setText(toPredictionBreakdownString(pred.getPredictionBreakdown()));
					predictionBreakdownPanel.setVisible(true);
				}

				final String primaryLabel = pred.getPredictionLabel();

				pred.getPredictionBreakdown().entrySet().stream().sorted(Map.Entry.comparingByKey()).forEach(
					entry ->
					{
						FeedbackPredictionLabel pLabel = new FeedbackPredictionLabel(entry.getKey(), entry.getValue(),
							entry.getKey().equals(primaryLabel) ? FeedbackValue.POSITIVE : FeedbackValue.NEGATIVE);
						feedbackLabelComboBox.addItem(pLabel);
						if (pLabel.getFeedbackValue() == FeedbackValue.POSITIVE)
						{
							feedbackLabelComboBox.setSelectedItem(pLabel);
						}
					});
			}

			// Must be logged in
			if (lastPredictionUploaderName != null)
			{
				resetFeedbackPanel(true);
				CaseInsensitiveString name = normalizeAndWrapPlayerName(pred.getPlayerName());
				if (pred.getPlayerId() <= 0)
				{
					predictionFeedbackPanel.setVisible(false);
				}
				else
				{
					// If the player has already been feedbacked, ensure the panels reflect this
					FeedbackPredictionLabel feedbacked = plugin.getFeedbackedPlayers().get(name);

					if (feedbacked != null)
					{
						disableAndSetComboBoxOnFeedbackPanel(feedbacked, true);
					}

					// If there was some feedback text from a previous send, either successful or failed
					String feedbackText = plugin.getFeedbackedPlayersText().get(name);
					if (feedbackText != null)
					{
						feedbackTextbox.setText(feedbackText);
					}

					predictionFeedbackPanel.setVisible(true);
				}

				resetFlaggingPanel();
				if (sighting == null || !shouldAllowFlagging())
				{
					predictionFlaggingPanel.setVisible(false);
				}
				else
				{
					// If the player has already been flagged, ensure the panels reflect this
					Boolean flagged = plugin.getFlaggedPlayers().get(name);
					if (flagged != null)
					{
						disableAndSetColorOnFlaggingPanel(flagged);
					}
					predictionFlaggingPanel.setVisible(true);
				}
			}
			else
			{
				predictionFeedbackPanel.setVisible(false);
				predictionFlaggingPanel.setVisible(false);
			}
		}
		else
		{
			lastPrediction = null;
			lastPredictionPlayerSighting = null;
			lastPredictionUploaderName = null;
			predictionPlayerIdLabel.setText(EMPTY_LABEL);
			predictionPlayerNameLabel.setText(EMPTY_LABEL);
			predictionTypeLabel.setText(EMPTY_LABEL);
			predictionConfidenceLabel.setText(EMPTY_LABEL);
			predictionBreakdownLabel.setText(EMPTY_LABEL);

			predictionBreakdownPanel.setVisible(false);
			predictionFeedbackPanel.setVisible(false);
			predictionFlaggingPanel.setVisible(false);
		}
	}

	/**
	 * Sets up the prediction panel to display the given error message without including a detailed error.
	 * @param playerName The player's name.
	 * @param error The short, main error descriptor.
	 */
	public void setPredictionError(String playerName, String error)
	{
		setPredictionError(playerName, error, EMPTY_LABEL);
	}

	/**
	 * Sets up the prediction panel to display the given error message.
	 * @param playerName The player's name.
	 * @param error The short, main error descriptor.
	 * @param details The detailed error explanation.
	 */
	public void setPredictionError(String playerName, String error, String details)
	{
		setPrediction(null);
		setPredictionLabelsColor(ERROR_COLOR);

		predictionPlayerNameLabel.setText(wrapHTML(playerName));
		predictionTypeLabel.setText(wrapHTML(error));
		predictionConfidenceLabel.setText(wrapHTML(details));
	}

	/**
	 * Sets the given {@code playerName} in the panel's {@link #searchBar}, then calls {@link #predictPlayer()}.
	 * @param playerName The player to predict.
	 */
	public void predictPlayer(String playerName)
	{
		searchBar.setText(playerName);
		predictPlayer();
	}

	/**
	 * Hits up the API and retrieves the bot prediction for the player name in the panel's {@link #searchBar}.
	 */
	private void predictPlayer()
	{
		String target = sanitize(searchBar.getText());

		if (target.length() <= 0)
		{
			return;
		}

		if (target.length() > MAX_RSN_LENGTH)
		{
			searchBar.setIcon(IconTextField.Icon.ERROR);
			searchBarLoading = false;
			setPredictionError(target.substring(0, MAX_RSN_LENGTH) + "...",
				"Name Input Error",
				"Name cannot be longer than " + MAX_RSN_LENGTH + " characters");
			return;
		}
		else if (!isValidPlayerName(target))
		{
			searchBar.setIcon(IconTextField.Icon.ERROR);
			searchBarLoading = false;
			setPredictionError(target,
				"Name Input Error",
				"Entered name is not a valid Runescape name");
			return;
		}

		searchBar.setIcon(IconTextField.Icon.LOADING_DARKER);
		searchBar.setEditable(false);
		searchBarLoading = true;

		setPrediction(null);

		detectorClient.requestPrediction(target).whenCompleteAsync((pred, ex) ->
			SwingUtilities.invokeLater(() ->
			{
				if (!sanitize(searchBar.getText()).equals(target))
				{
					// Target has changed in the meantime
					return;
				}

				searchBar.setEditable(true);
				searchBarLoading = false;

				if (ex != null)
				{
					searchBar.setIcon(IconTextField.Icon.ERROR);
					setPredictionError(target, "Server Error", ex.getMessage());
					return;
				}

				searchBar.setIcon(IconTextField.Icon.SEARCH);

				// Build a dummy prediction if player not found in API
				Prediction p = pred;
				if (p == null)
				{
					p = Prediction.builder()
						.playerName(target)
						.playerId(-1) // Prevents feedback panel from appearing
						.confidence(null)
						.predictionBreakdown(null)
						.predictionLabel("Player not found")
						.build();
				}

				setPrediction(p, plugin.getPersistentSightings().get(normalizeAndWrapPlayerName(target)));
			}));
	}

	/**
	 * Processes the user input from the prediction feedback panel.
	 * @param proposedLabel The intended label from the user for {@link #lastPrediction} to be sent to the API.
	 */
	private void sendFeedbackToClient(FeedbackPredictionLabel proposedLabel)
	{
		if (lastPrediction == null
			|| lastPredictionUploaderName == null
			|| proposedLabel == null
			|| proposedLabel.getLabel() == null)
		{
			return;
		}

		disableAndSetComboBoxOnFeedbackPanel(proposedLabel, false);

		CaseInsensitiveString wrappedName = normalizeAndWrapPlayerName(lastPrediction.getPlayerName());
		Map<CaseInsensitiveString, FeedbackPredictionLabel> feedbackMap = plugin.getFeedbackedPlayers();
		feedbackMap.put(wrappedName, proposedLabel);

		String feedbackText = feedbackTextbox.getText().trim();
		if (feedbackText.isEmpty())
		{
			feedbackText = null;
		}
		else
		{
			// Will not get reset upon send failure, so don't need to keep reference
			plugin.getFeedbackedPlayersText().put(wrappedName, feedbackText);
		}

		feedbackHeaderLabel.setIcon(Icons.LOADING_SPINNER);
		feedbackHeaderLabel.setToolTipText(null);
		detectorClient.sendFeedback(lastPrediction, lastPredictionUploaderName, proposedLabel, feedbackText)
			.whenComplete((b, ex) ->
			{
				boolean stillSame = lastPrediction != null &&
					wrappedName.equals(normalizeAndWrapPlayerName(lastPrediction.getPlayerName()));

				String message;
				if (ex == null && b)
				{
					message = "Thank you for your prediction feedback for '%s'!";
					if (stillSame)
					{
						feedbackHeaderLabel.setIcon(null);
						feedbackHeaderLabel.setToolTipText(null);
					}
				}
				else
				{
					message = "Error sending your prediction feedback for '%s'.";
					// Didn't work so remove from feedback map
					feedbackMap.remove(wrappedName);
					if (stillSame)
					{
						resetFeedbackPanel(false);
						feedbackHeaderLabel.setIcon(Icons.ERROR_ICON);
						feedbackHeaderLabel.setToolTipText(ex != null ? ex.getMessage() : "Unknown error");
					}
				}

				plugin.sendChatStatusMessage(String.format(message, wrappedName));
			});
	}

	/**
	 * Processes the user input from the flagging panel.
	 * @param doFlag Whether or not the user intends to flag {@link #lastPredictionPlayerSighting} to the API.
	 */
	private void sendFlagToClient(boolean doFlag)
	{
		if (lastPredictionPlayerSighting == null
			|| !shouldAllowFlagging())
		{
			return;
		}

		disableAndSetColorOnFlaggingPanel(doFlag);

		CaseInsensitiveString wrappedName = normalizeAndWrapPlayerName(lastPredictionPlayerSighting.getPlayerName());
		Map<CaseInsensitiveString, Boolean> flagMap = plugin.getFlaggedPlayers();
		flagMap.put(wrappedName, doFlag);

		// Didn't want to flag? Work is done!
		if (!doFlag)
		{
			return;
		}

		flaggingHeaderLabel.setIcon(Icons.LOADING_SPINNER);
		flaggingHeaderLabel.setToolTipText(null);
		detectorClient.sendSighting(lastPredictionPlayerSighting, lastPredictionUploaderName, true)
			.whenComplete((b, ex) ->
			{
				boolean stillSame = lastPredictionPlayerSighting != null &&
					wrappedName.equals(normalizeAndWrapPlayerName(lastPredictionPlayerSighting.getPlayerName()));

				String message;
				if (ex == null && b)
				{
					message = "Thank you for flagging '%s' as a bot to us!";
					setNamesUploaded(playerCurrentManualUploads + 1, true);
					if (stillSame)
					{
						flaggingHeaderLabel.setIcon(null);
						flaggingHeaderLabel.setToolTipText(null);
					}
				}
				else
				{
					message = "Error sending your bot flag for '%s'.";
					// Didn't work so remove from flagged map
					flagMap.remove(wrappedName);
					if (stillSame)
					{
						resetFlaggingPanel();
						flaggingHeaderLabel.setIcon(Icons.ERROR_ICON);
						flaggingHeaderLabel.setToolTipText(ex != null ? ex.getMessage() : "Unknown error");
					}
				}

				plugin.sendChatStatusMessage(String.format(message, wrappedName));
			});
	}

	/**
	 * Checks if flagging should be allowed.
	 * @return True if {@link BotDetectorPlugin#getUploaderName()} indicates a logged user that isn't in anonymous mode.
	 */
	private boolean shouldAllowFlagging()
	{
		return lastPredictionUploaderName != null
			&& !lastPredictionUploaderName.startsWith(BotDetectorPlugin.ANONYMOUS_USER_NAME);
	}

	/**
	 * Clears and re-enables the components in the feedback panel.
	 */
	private void resetFeedbackPanel(boolean clearText)
	{
		feedbackHeaderLabel.setIcon(null);
		feedbackHeaderLabel.setToolTipText(null);
		feedbackSendButton.setBackground(null);
		feedbackSendButton.setEnabled(true);
		feedbackTextbox.setEnabled(true);
		feedbackLabelComboBox.setEnabled(true);
		if (clearText)
		{
			feedbackTextbox.setText("");
		}
	}

	/**
	 * Disables the feedback panel and sets the label combobox according to the parameter.
	 * @param label The label to display in the combobox.
	 * @param clearAndForceSetComboBoxLabel Set to true if the {@code label} is not expected to exist exactly in the combo box.
	 */
	private void disableAndSetComboBoxOnFeedbackPanel(FeedbackPredictionLabel label, boolean clearAndForceSetComboBoxLabel)
	{
		feedbackSendButton.setEnabled(false);
		feedbackTextbox.setEnabled(false);
		feedbackLabelComboBox.setEnabled(false);

		if (clearAndForceSetComboBoxLabel)
		{
			feedbackLabelComboBox.removeAllItems();
			feedbackLabelComboBox.addItem(label);
		}

		feedbackLabelComboBox.setSelectedItem(label);

		switch (label.getFeedbackValue())
		{
			case POSITIVE:
				feedbackSendButton.setBackground(POSITIVE_BUTTON_COLOR);
				break;
			case NEUTRAL:
				feedbackSendButton.setBackground(NEUTRAL_BUTTON_COLOR);
				break;
			case NEGATIVE:
				feedbackSendButton.setBackground(NEGATIVE_BUTTON_COLOR);
				break;
		}
	}

	/**
	 * Clears and re-enables the components in the flagging panel.
	 */
	private void resetFlaggingPanel()
	{
		flaggingHeaderLabel.setIcon(null);
		flaggingHeaderLabel.setToolTipText(null);
		flaggingYesButton.setBackground(null);
		flaggingYesButton.setEnabled(true);
		flaggingNoButton.setBackground(null);
		flaggingNoButton.setEnabled(true);
	}

	/**
	 * Disables the flagging panel and sets a color on either the 'Yes' or 'No' button according to the parameter.
	 * @param flagged If true, highlight the 'Yes' button, otherwise highlight the 'No' button.
	 */
	private void disableAndSetColorOnFlaggingPanel(boolean flagged)
	{
		flaggingYesButton.setEnabled(false);
		flaggingNoButton.setEnabled(false);
		if (flagged)
		{
			flaggingYesButton.setBackground(POSITIVE_BUTTON_COLOR);
		}
		else
		{
			flaggingNoButton.setBackground(NEGATIVE_BUTTON_COLOR);
		}
	}

	/**
	 * Sets the appropriate font for every components in {@link #switchableFontComponents}.
	 * @param fontType The font type to apply.
	 */
	public void setFontType(PanelFontType fontType)
	{
		Font f;

		switch (fontType)
		{
			case SMALL:
				f = SMALL_FONT;
				break;
			case BOLD:
				f = BOLD_FONT;
				break;
			default:
				f = NORMAL_FONT;
				break;
		}

		switchableFontComponents.forEach(c -> c.setFont(f));
	}

	/**
	 * Wraps the given string in HTML tags. The input string is escaped for HTML.
	 * Use this in {@link JLabel#setText(String)} to make text wrapping work.
	 * @param str The string to wrap with HTML tags.
	 * @return The wrapped string.
	 */
	private static String wrapHTML(String str)
	{
		return wrapHTML(str, true);
	}

	/**
	 * Wraps the given string in HTML tags.
	 * @param str The string to wrap with HTML tags.
	 * @param escape Whether or not to escape the input string for HTML.
	 * @return The wrapped string.
	 */
	private static String wrapHTML(String str, boolean escape)
	{
		return "<html>"
			+ (escape ? StringEscapeUtils.escapeHtml4(str) : str)
			+ "</html>";
	}

	/**
	 * Replaces {@code NBSP}s in the given string with spaces.
	 * @param lookup The string to sanitize.
	 * @return The sanitized string.
	 */
	private static String sanitize(String lookup)
	{
		return lookup.replace('\u00A0', ' ');
	}

	/**
	 * Checks if the given player name is a valid Jagex name.
	 * @param playerName The player name to check.
	 * @return True if the name is valid, false otherwise.
	 */
	private static boolean isValidPlayerName(String playerName)
	{
		if (playerName == null || playerName.length() > MAX_RSN_LENGTH)
		{
			return false;
		}

		return VALID_RSN_PATTERN.matcher(playerName).matches();
	}

	/**
	 * Gets the {@link Color} to use for the given double value.
	 * @param percent A double representing a percent value from {@code 0.0} to {@code 1.0}.
	 * @return The recommended {@link Color}.
	 */
	private static Color getPercentColor(double percent)
	{
		percent = Doubles.constrainToRange(percent, 0, 1);
		return percent < 0.5 ?
			ColorUtil.colorLerp(Color.RED, Color.YELLOW, percent * 2)
			: ColorUtil.colorLerp(Color.YELLOW, Color.GREEN, (percent - 0.5) * 2);
	}

	/**
	 * Formats the given double into a percent string with two decimal points (ex.: {@code 0.57458 -> 57.46%}).
	 * @param percent The double percent value to format.
	 * @return The formatted percent value.
	 */
	private static String toPercentString(double percent)
	{
		return new DecimalFormat("0.00%").format(percent);
	}

	/**
	 * Wraps the given percent double in a HTML span tag, with color and text format
	 * from {@link #getPercentColor(double)} and {@link #toPercentString(double)} respectively.
	 * @param percent The double percent value to wrap and format.
	 * @return The formatted percent value wrapped in a HTML span tag.
	 */
	private static String toColoredPercentSpan(double percent)
	{
		return String.format("<span style='color:%s'>%s</span>",
			ColorUtil.toHexColor(getPercentColor(percent)),
			toPercentString(percent));
	}

	/**
	 * Creates a HTML table string for the given prediction breakdown map.
	 * @param predictionMap The prediction breakdown map to process.
	 * @return A HTML table string containing the data from the given prediction breakdown map.
	 */
	private static String toPredictionBreakdownString(Map<String, Double> predictionMap)
	{
		if (predictionMap == null || predictionMap.size() == 0)
		{
			return null;
		}

		String openingTags = "<html><body style='margin:0;padding:0;color:"
			+ ColorUtil.toHexColor(TEXT_COLOR) + "'>"
			+ "<table border='0' cellspacing='0' cellpadding='0'>";
		String closingTags = "</table></body></html>";

		StringBuilder sb = new StringBuilder();
		sb.append(openingTags);

		String rowString = "<tr><td>%s:</td><td style='padding-left:5;text-align:right;color:%s'>%s</td></tr>";
		predictionMap.entrySet().stream().filter(e -> e.getValue() > 0)
			.sorted(Map.Entry.<String, Double>comparingByValue().reversed().thenComparing(Map.Entry.comparingByKey()))
			.forEach(e -> sb.append(String.format(rowString,
				normalizeLabel(e.getKey()),
				ColorUtil.toHexColor(getPercentColor(e.getValue())),
				toPercentString(e.getValue()))));

		return sb.append(closingTags).toString();
	}

	void addInputKeyListener(KeyListener l)
	{
		this.searchBar.addKeyListener(l);
	}

	void removeInputKeyListener(KeyListener l)
	{
		this.searchBar.removeKeyListener(l);
	}
}
