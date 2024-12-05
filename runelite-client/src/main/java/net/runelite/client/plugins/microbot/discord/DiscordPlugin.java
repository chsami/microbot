package net.runelite.client.plugins.microbot.discord;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.Player;
import net.runelite.api.Skill;
import net.runelite.api.events.ActorDeath;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.StatChanged;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.microbot.util.discord.Rs2Discord;
import net.runelite.client.plugins.microbot.util.discord.models.DiscordEmbed;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.util.ImageUtil;

import javax.inject.Inject;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;

@PluginDescriptor(
        name = "Discord Notifier",
        description = "Sends notifications to Discord",
        tags = {"discord", "notification", "messages"},
        enabledByDefault = true
)
@Slf4j
public class DiscordPlugin extends Plugin {
    @Inject
    private DiscordConfig config;

    @Inject
    private Client client;

    @Inject
    private ConfigManager configManager;

    @Inject
    private ClientToolbar clientToolbar;

    private NavigationButton navButton;
    private DiscordPanel panel;
    private GameState lastGameState = null;
    private String lastUsername = null;
    private final Map<Skill, Integer> skillLevels = new EnumMap<>(Skill.class);
    private boolean skillsInitialized = false;
    private boolean seenLoginScreen = false;

    @Provides
    DiscordConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(DiscordConfig.class);
    }

    @Override
    protected void startUp() {
        panel = injector.getInstance(DiscordPanel.class);
        BufferedImage icon = ImageUtil.loadImageResource(getClass(), "discord.png");

        navButton = NavigationButton.builder()
                .tooltip("Discord Notifications")
                .icon(icon)
                .priority(5)
                .panel(panel)
                .build();

        clientToolbar.addNavigation(navButton);
    }

    @Override
    protected void shutDown() {
        clientToolbar.removeNavigation(navButton);
        skillLevels.clear();
        skillsInitialized = false;
        seenLoginScreen = false;
    }

    private void initializeSkillLevels() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        if (client.getGameState() != GameState.LOGGED_IN || client.getLocalPlayer() == null) {
            return;
        }

        skillLevels.clear();
        for (Skill skill : Skill.values()) {
            if (skill != Skill.OVERALL) {
                int level = client.getRealSkillLevel(skill);
                skillLevels.put(skill, level);
            }
        }
        skillsInitialized = true;
    }

    public void updateConfig(String key, Object value) {
        configManager.setConfiguration("discordnotifier", key, value);
    }

    public void testWebhook() {
        try {
            DiscordEmbed embed = new DiscordEmbed();
            embed.setTitle("Test Message");
            embed.setDescription("Your Discord webhook is working correctly!");
            embed.setColor(Rs2Discord.convertColorToInt(Color.BLUE));

            Rs2Discord.sendWebhookMessage("Webhook Test", Collections.singletonList(embed));
        } catch (Exception e) {
        }
    }

    @Subscribe
    public void onStatChanged(StatChanged statChanged) {
        if (!skillsInitialized || !config.enableNotifications() || !config.notifyLevelUp()) {
            return;
        }

        Skill skill = statChanged.getSkill();
        if (skill == Skill.OVERALL) {
            return;
        }

        int newLevel = client.getRealSkillLevel(skill);
        int oldLevel = skillLevels.get(skill);

        if (newLevel > oldLevel) {
            skillLevels.put(skill, newLevel);
            
            try {
                String username = client.getLocalPlayer().getName();
                DiscordEmbed embed = new DiscordEmbed();
                embed.setTitle("Level Up Notification");
                embed.setDescription(String.format("%s has reached level %d in %s!", 
                    username, newLevel, skill.getName()));
                embed.setColor(Rs2Discord.convertColorToInt(Color.CYAN));

                Rs2Discord.sendWebhookMessage("Level Up Notification", Collections.singletonList(embed));
            } catch (Exception e) {
            }
        }
    }

    private void sendLoginNotification() {
        if (!config.enableNotifications() || !config.notifyLoginLogout()) {
            return;
        }

        if (client.getLocalPlayer() == null) {
            return;
        }

        String username = client.getLocalPlayer().getName();
        if (username == null || username.isEmpty()) {
            return;
        }

        lastUsername = username;

        try {
            DiscordEmbed embed = new DiscordEmbed();
            embed.setTitle("Login Notification");
            embed.setDescription(username + " has logged in");
            embed.setColor(Rs2Discord.convertColorToInt(Color.GREEN));

            Rs2Discord.sendWebhookMessage("Login Notification", Collections.singletonList(embed));
            
            new Thread(this::initializeSkillLevels).start();
        } catch (Exception e) {
        }
    }

    @Subscribe
    public void onGameStateChanged(GameStateChanged gameStateChanged) {
        GameState newState = gameStateChanged.getGameState();
        
        if (newState == GameState.LOGIN_SCREEN) {
            seenLoginScreen = true;
            skillsInitialized = false;
            skillLevels.clear();
        }
        
        if (newState == GameState.LOGGED_IN && seenLoginScreen) {
            seenLoginScreen = false;
            new Thread(() -> {
                try {
                    for (int i = 0; i < 50; i++) {
                        if (client.getLocalPlayer() != null && client.getLocalPlayer().getName() != null) {
                            sendLoginNotification();
                            break;
                        }
                        Thread.sleep(100);
                    }
                } catch (InterruptedException e) {
                }
            }).start();
        }
        else if (lastGameState == GameState.LOGGED_IN && newState == GameState.LOGIN_SCREEN) {
            if (!config.enableNotifications() || !config.notifyLoginLogout()) {
                return;
            }

            try {
                DiscordEmbed embed = new DiscordEmbed();
                embed.setTitle("Logout Notification");
                String logoutMessage = (lastUsername != null) ? 
                    lastUsername + " has logged out" : 
                    "Your player has logged out";
                embed.setDescription(logoutMessage);
                embed.setColor(Rs2Discord.convertColorToInt(Color.YELLOW));

                Rs2Discord.sendWebhookMessage("Logout Notification", Collections.singletonList(embed));
            } catch (Exception e) {
            }
        }

        lastGameState = newState;
    }

    @Subscribe
    public void onActorDeath(ActorDeath event) {
        if (!config.enableNotifications() || !config.notifyDeath()) {
            return;
        }

        if (!(event.getActor() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getActor();
        if (player != client.getLocalPlayer()) {
            return;
        }

        try {
            String username = player.getName();
            String location = player.getWorldLocation().toString();

            DiscordEmbed embed = new DiscordEmbed();
            embed.setTitle("Death Notification");
            embed.setDescription(username + " has died at " + location);
            embed.setColor(Rs2Discord.convertColorToInt(Color.RED));

            Rs2Discord.sendWebhookMessage("Death Notification", Collections.singletonList(embed));
        } catch (Exception e) {
        }
    }
} 