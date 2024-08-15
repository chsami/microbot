package net.runelite.client.plugins.microbot.util.antiban;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.AnimationID;
import net.runelite.api.GameState;
import net.runelite.api.Skill;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.StatChanged;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ProfileChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.breakhandler.BreakHandlerPlugin;
import net.runelite.client.plugins.microbot.util.antiban.enums.Activity;
import net.runelite.client.plugins.microbot.util.antiban.enums.ActivityIntensity;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.ImageUtil;

import javax.inject.Inject;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.time.Duration;
import java.time.Instant;
import java.util.EnumMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

@PluginDescriptor(
        name = PluginDescriptor.See1Duck + "Antiban",
        description = "Antiban for microbot",
        tags = {"main", "microbot", "antiban parent"},
        alwaysOn = true,
        hidden = true
)
@Slf4j
public class AntibanPlugin extends Plugin {

    private static final int COOK_TIMEOUT = 3;
    private static final int MINING_TIMEOUT = 3;
    private static final int IDLE_TIMEOUT = 1;
    public static int ticksSinceLogin;
    private static Instant lastCookingAction = Instant.MIN;
    private static Instant lastMiningAction = Instant.MIN;
    private static int idleTicks = 0;
    private final Map<Skill, Integer> skillExp = new EnumMap<>(Skill.class);
    private boolean ready;
    private Skill lastSkillChanged;
    private NavigationButton navButton;

    @Inject
    private OverlayManager overlayManager;

    @Inject
    private ClientToolbar clientToolbar;

    public static boolean isCooking() {
        return Rs2Player.getAnimation() == AnimationID.COOKING_FIRE
                || Rs2Player.getAnimation() == AnimationID.COOKING_RANGE
                || Duration.between(lastCookingAction, Instant.now()).getSeconds() < COOK_TIMEOUT;
    }

    public static boolean isMining() {
        return Rs2Antiban.isMining()
                || Duration.between(lastMiningAction, Instant.now()).getSeconds() < MINING_TIMEOUT;
    }

    public static boolean isIdle() {
        return idleTicks > IDLE_TIMEOUT;
    }

    public static void updateIdleTicks() {
        idleTicks++;
    }

    private static void updateLastCookingAction() {
        lastCookingAction = Instant.now();
    }

    private static void updateLastMiningAction() {
        lastMiningAction = Instant.now();
    }

    public static void performActionBreak() {
        if (Rs2AntibanSettings.actionCooldownActive) {
            if (Rs2Antiban.getTIMEOUT() > 0) {
                if (!Rs2Antiban.getCategory().isBusy()) {
                    Rs2Antiban.TIMEOUT--;
                }
            } else {
                Rs2AntibanSettings.actionCooldownActive = false;
                Microbot.pauseAllScripts = false;
            }
        }
    }

    @Override
    protected void startUp() throws AWTException {
        final AntibanPluginPanel panel = injector.getInstance(AntibanPluginPanel.class);
        final BufferedImage icon = ImageUtil.loadImageResource(getClass(), "antiban.png");
        navButton = NavigationButton.builder()
                .tooltip("Antiban")
                .icon(icon)
                .priority(1)
                .panel(panel)
                .build();

        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                SwingUtilities.invokeLater(panel::loadSettings);
            }
        }, 0, 600);

        clientToolbar.addNavigation(navButton);
        overlayManager.add(new AntibanOverlay());
    }

    @Override
    protected void shutDown() {
        overlayManager.removeIf(overlay -> overlay instanceof AntibanOverlay);
        clientToolbar.removeNavigation(navButton);
    }

    @Subscribe
    public void onChatMessage(ChatMessage event) {
        if (Rs2Antiban.checkForCookingEvent(event)) {
            updateLastCookingAction();
        }
    }

    @Subscribe
    public void onProfileChanged(ProfileChanged event) {
        Rs2Antiban.resetAntiban();
    }

    @Subscribe
    public void onGameStateChanged(GameStateChanged event) {
        GameState state = event.getGameState();

        switch (state) {
            case LOGGING_IN:
            case HOPPING:
                ready = true;
                break;
            case LOGGED_IN:
                if (ready) {
                    ticksSinceLogin = 0;
                    ready = false;
                }
                break;
        }
    }

    @Subscribe
    public void onGameTick(GameTick event) {
        ticksSinceLogin++;

        if (!Rs2AntibanSettings.antibanEnabled) {
            return;
        }

        if (!Rs2Player.isAnimating()) {
            updateIdleTicks();
        } else {
            if (Rs2AntibanSettings.simulateFatigue) {
                ticksSinceLogin -= idleTicks;
            }
            idleTicks = 0;
        }

        if (Rs2AntibanSettings.takeMicroBreaks && !Microbot.isPluginEnabled(BreakHandlerPlugin.class)) {
            Microbot.log("BreakHandlerPlugin is not enabled, attempting to enable it....");
            Plugin breakHandlerPlugin = Microbot.getPluginManager().getPlugins()
                    .stream()
                    .filter(p -> p.getName().contains("BreakHandler"))
                    .findFirst()
                    .orElse(null);
            Microbot.startPlugin(breakHandlerPlugin);
        }

        if (Rs2Antiban.isMining()) {
            updateLastMiningAction();
        }

        if (Rs2AntibanSettings.actionCooldownActive) {
            performActionBreak();
        }

        if (Rs2AntibanSettings.simulateAttentionSpan && Rs2AntibanSettings.profileSwitching &&
                Rs2Antiban.getPlayStyle().shouldSwitchProfileBasedOnAttention()) {
            Rs2Antiban.setPlayStyle(Rs2Antiban.getPlayStyle().switchProfile());
            Rs2Antiban.getPlayStyle().resetPlayStyle();
        }
    }

    @Subscribe
    public void onStatChanged(StatChanged statChanged) {
        if (!Rs2AntibanSettings.antibanEnabled) {
            return;
        }

        final Skill skill = statChanged.getSkill();
        final int exp = statChanged.getXp();
        final Integer previous = skillExp.put(skill, exp);

        if (lastSkillChanged != null && lastSkillChanged.equals(skill)) {
            if (Rs2AntibanSettings.universalAntiban && !Rs2AntibanSettings.actionCooldownActive) {
                Rs2Antiban.actionCooldown();
            }
            return;
        }

        lastSkillChanged = skill;

        if (previous == null || previous >= exp) {
            return;
        }

        updateAntibanSettings(skill);
    }

    private void updateAntibanSettings(Skill skill) {
        final ActivityIntensity activityIntensity = ActivityIntensity.fromSkill(skill);
        final Activity activity = Activity.fromSkill(skill);

        if (activity != null && Rs2AntibanSettings.dynamicActivity) {
            Rs2Antiban.setActivity(activity);
            Microbot.log("Activity changed, new activity: " + activity);
            if (Rs2AntibanSettings.universalAntiban) {
                Rs2Antiban.actionCooldown();
            }
        }

        if (activityIntensity != null && Rs2AntibanSettings.dynamicIntensity) {
            Rs2Antiban.setActivityIntensity(activityIntensity);
            Microbot.log("Activity changed, new activity intensity: " + activityIntensity);
        }
    }

    private void applyContextualVariabilitySetup() {
        Rs2AntibanSettings.antibanEnabled = true;
        Rs2AntibanSettings.usePlayStyle = true;
        Rs2AntibanSettings.randomIntervals = false;
        Rs2AntibanSettings.simulateFatigue = true;
        Rs2AntibanSettings.simulateAttentionSpan = true;
        Rs2AntibanSettings.behavioralVariability = true;
        Rs2AntibanSettings.nonLinearIntervals = true;
        Rs2AntibanSettings.profileSwitching = true;
        Rs2AntibanSettings.timeOfDayAdjust = false;
        Rs2AntibanSettings.simulateMistakes = true;
        Rs2AntibanSettings.naturalMouse = true;
        Rs2AntibanSettings.contextualVariability = true;
        Rs2AntibanSettings.dynamicIntensity = true;
        Rs2AntibanSettings.dynamicActivity = true;
        Rs2AntibanSettings.devDebug = false;
        Rs2AntibanSettings.takeMicroBreaks = true;
        Rs2AntibanSettings.playSchedule = true;
        Rs2AntibanSettings.microBreakDurationLow = 3;
        Rs2AntibanSettings.microBreakDurationHigh = 8;
        Rs2AntibanSettings.actionCooldownChance = 0.05;
        Rs2AntibanSettings.microBreakChance = 0.05;
    }
}
