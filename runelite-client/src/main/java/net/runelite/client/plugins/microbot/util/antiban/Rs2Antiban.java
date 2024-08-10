package net.runelite.client.plugins.microbot.util.antiban;

import com.google.common.collect.ImmutableSet;
import lombok.Getter;
import lombok.Setter;
import net.runelite.api.ChatMessageType;
import net.runelite.api.events.ChatMessage;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.breakhandler.BreakHandlerScript;
import net.runelite.client.plugins.microbot.util.antiban.enums.Activity;
import net.runelite.client.plugins.microbot.util.antiban.enums.ActivityIntensity;
import net.runelite.client.plugins.microbot.util.antiban.enums.Category;
import net.runelite.client.plugins.microbot.util.antiban.enums.PlayStyle;
import net.runelite.client.plugins.microbot.util.math.Random;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;
import net.runelite.client.ui.overlay.components.*;
import net.runelite.client.util.ColorUtil;

import java.awt.*;
import java.util.Set;

import static net.runelite.api.AnimationID.*;

@Getter
@Setter
public class Rs2Antiban {
    public static final ImmutableSet<Integer> MINING_ANIMATION_IDS = ImmutableSet.of(
            MINING_BRONZE_PICKAXE, MINING_MOTHERLODE_BRONZE, MINING_CRASHEDSTAR_BRONZE,
            MINING_IRON_PICKAXE, MINING_MOTHERLODE_IRON, MINING_CRASHEDSTAR_IRON,
            MINING_STEEL_PICKAXE, MINING_MOTHERLODE_STEEL, MINING_CRASHEDSTAR_STEEL,
            MINING_BLACK_PICKAXE, MINING_MOTHERLODE_BLACK, MINING_CRASHEDSTAR_BLACK,
            MINING_MITHRIL_PICKAXE, MINING_MOTHERLODE_MITHRIL, MINING_CRASHEDSTAR_MITHRIL,
            MINING_ADAMANT_PICKAXE, MINING_MOTHERLODE_ADAMANT, MINING_CRASHEDSTAR_ADAMANT,
            MINING_RUNE_PICKAXE, MINING_MOTHERLODE_RUNE, MINING_CRASHEDSTAR_RUNE,
            MINING_GILDED_PICKAXE, MINING_MOTHERLODE_GILDED, MINING_CRASHEDSTAR_GILDED,
            MINING_DRAGON_PICKAXE, MINING_MOTHERLODE_DRAGON, MINING_CRASHEDSTAR_DRAGON,
            MINING_DRAGON_PICKAXE_OR, MINING_MOTHERLODE_DRAGON_OR, MINING_CRASHEDSTAR_DRAGON_OR,
            MINING_DRAGON_PICKAXE_OR_TRAILBLAZER, MINING_MOTHERLODE_DRAGON_OR_TRAILBLAZER, MINING_CRASHEDSTAR_DRAGON_OR_TRAILBLAZER,
            MINING_DRAGON_PICKAXE_UPGRADED, MINING_MOTHERLODE_DRAGON_UPGRADED, MINING_CRASHEDSTAR_DRAGON_UPGRADED,
            MINING_INFERNAL_PICKAXE, MINING_MOTHERLODE_INFERNAL, MINING_CRASHEDSTAR_INFERNAL,
            MINING_3A_PICKAXE, MINING_MOTHERLODE_3A, MINING_CRASHEDSTAR_3A,
            MINING_CRYSTAL_PICKAXE, MINING_MOTHERLODE_CRYSTAL, MINING_CRASHEDSTAR_CRYSTAL,
            MINING_TRAILBLAZER_PICKAXE, MINING_TRAILBLAZER_PICKAXE_2, MINING_TRAILBLAZER_PICKAXE_3, MINING_MOTHERLODE_TRAILBLAZER
    );
    private static final Set<Integer> WOODCUTTING_ANIMS = ImmutableSet.of(
            WOODCUTTING_BRONZE, WOODCUTTING_IRON, WOODCUTTING_STEEL, WOODCUTTING_BLACK, WOODCUTTING_MITHRIL,
            WOODCUTTING_ADAMANT, WOODCUTTING_RUNE, WOODCUTTING_GILDED, WOODCUTTING_DRAGON, WOODCUTTING_DRAGON_OR,
            WOODCUTTING_INFERNAL, WOODCUTTING_3A_AXE, WOODCUTTING_CRYSTAL, WOODCUTTING_TRAILBLAZER,
            WOODCUTTING_2H_BRONZE, WOODCUTTING_2H_IRON, WOODCUTTING_2H_STEEL, WOODCUTTING_2H_BLACK,
            WOODCUTTING_2H_MITHRIL, WOODCUTTING_2H_ADAMANT, WOODCUTTING_2H_RUNE, WOODCUTTING_2H_DRAGON,
            WOODCUTTING_2H_CRYSTAL, WOODCUTTING_2H_CRYSTAL_INACTIVE, WOODCUTTING_2H_3A
    );

    // Mouse fatigue class
    public static MouseFatigue mouseFatigue = new MouseFatigue();

    // Antiban setup class
    public static AntibanSetupTemplates antibanSetupTemplates = new AntibanSetupTemplates();

    @Getter
    @Setter
    public static int TIMEOUT = 0;
    @Getter
    private static Activity activity;
    @Getter
    private static ActivityIntensity activityIntensity;
    @Getter
    @Setter
    private static Category category;
    @Getter
    @Setter
    private static PlayStyle playStyle;

    /**
     * <h1>Basic Setup</h1>
     * This method sets up the basic configuration for the antiban system.
     * It is used to enable the minimal functionality that works out of the box without any additional configuration.
     * The basic setup utilizes natural mouse movements to create natural delays between actions
     */
    public static void basicSetup() {
        Rs2AntibanSettings.actionCooldownActive = false;
        Rs2AntibanSettings.microBreakActive = false;
        Rs2AntibanSettings.antibanEnabled = true;
        Rs2AntibanSettings.usePlayStyle = false;
        Rs2AntibanSettings.randomIntervals = false;
        Rs2AntibanSettings.simulateFatigue = false;
        Rs2AntibanSettings.simulateAttentionSpan = false;
        Rs2AntibanSettings.behavioralVariability = false;
        Rs2AntibanSettings.nonLinearIntervals = false;
        Rs2AntibanSettings.profileSwitching = false;
        Rs2AntibanSettings.timeOfDayAdjust = false;
        Rs2AntibanSettings.simulateMistakes = false;
        Rs2AntibanSettings.naturalMouse = true;
        Rs2AntibanSettings.contextualVariability = false;
        Rs2AntibanSettings.dynamicIntensity = true;
        Rs2AntibanSettings.dynamicActivity = true;
        Rs2AntibanSettings.devDebug = false;
        Rs2AntibanSettings.actionCooldownChance = 0.1;
        Rs2AntibanSettings.microBreakChance = 0.1;
        Rs2AntibanSettings.microBreakDurationLow = 3;
        Rs2AntibanSettings.microBreakDurationHigh = 15;
    }

    /**
     * <h1>Basic Play Style Setup</h1>
     * This method sets up the basic configuration for the antiban system with play style enabled.
     * Provides action cooldowns in the most basic and aggressive play style.
     * To activate the action cooldown, call the actionCooldown() method after the desired action.
     *
     * @param activity The activity to be performed
     * @see Rs2Antiban#actionCooldown()
     */
    public static void basicPlayStyleSetup(Activity activity) {
        Rs2AntibanSettings.actionCooldownActive = false;
        Rs2AntibanSettings.microBreakActive = false;
        Rs2AntibanSettings.antibanEnabled = true;
        Rs2AntibanSettings.usePlayStyle = true;
        Rs2AntibanSettings.randomIntervals = false;
        Rs2AntibanSettings.simulateFatigue = false;
        Rs2AntibanSettings.simulateAttentionSpan = false;
        Rs2AntibanSettings.behavioralVariability = false;
        Rs2AntibanSettings.nonLinearIntervals = false;
        Rs2AntibanSettings.profileSwitching = false;
        Rs2AntibanSettings.timeOfDayAdjust = false;
        Rs2AntibanSettings.simulateMistakes = false;
        Rs2AntibanSettings.naturalMouse = false;
        Rs2AntibanSettings.contextualVariability = false;
        Rs2AntibanSettings.dynamicIntensity = false;
        Rs2AntibanSettings.dynamicActivity = false;
        Rs2AntibanSettings.devDebug = false;
        Rs2AntibanSettings.actionCooldownChance = 0.1;
        Rs2AntibanSettings.microBreakChance = 0.1;
        Rs2AntibanSettings.microBreakDurationLow = 3;
        Rs2AntibanSettings.microBreakDurationHigh = 15;
        setActivity(activity);
        Rs2Antiban.playStyle = PlayStyle.EXTREME_AGGRESSIVE;

    }

    /**
     * <h1>Intermediate Play Style Setup</h1>
     * This method sets up the intermediate configuration for the antiban system with play style enabled.
     * Provides action cooldowns based on the activity intensity and play style.
     * The action cooldown is randomized within the primary and secondary tick intervals in the current play style.
     * To activate the action cooldown, call the actionCooldown() method after the desired action.
     *
     * @param activity The activity to be performed
     * @see Rs2Antiban#actionCooldown()
     */

    // Advanced play style setup: maximal functionality with play style enabled
    public static void intermediatePlayStyleSetup(Activity activity) {
        Rs2AntibanSettings.actionCooldownActive = false;
        Rs2AntibanSettings.microBreakActive = false;
        Rs2AntibanSettings.antibanEnabled = true;
        Rs2AntibanSettings.usePlayStyle = true;
        Rs2AntibanSettings.randomIntervals = false;
        Rs2AntibanSettings.simulateFatigue = false;
        Rs2AntibanSettings.simulateAttentionSpan = false;
        Rs2AntibanSettings.behavioralVariability = true;
        Rs2AntibanSettings.nonLinearIntervals = false;
        Rs2AntibanSettings.profileSwitching = false; //TODO: Implement this
        Rs2AntibanSettings.timeOfDayAdjust = false; //TODO: Implement this
        Rs2AntibanSettings.simulateMistakes = false; //Handled by the natural mouse
        Rs2AntibanSettings.naturalMouse = true;
        Rs2AntibanSettings.contextualVariability = false; //TODO: Implement this
        Rs2AntibanSettings.dynamicIntensity = false;
        Rs2AntibanSettings.dynamicActivity = false;
        Rs2AntibanSettings.devDebug = true;
        Rs2AntibanSettings.actionCooldownChance = 0.1;
        Rs2AntibanSettings.microBreakChance = 0.1;
        Rs2AntibanSettings.microBreakDurationLow = 1;
        Rs2AntibanSettings.microBreakDurationHigh = 5;
        setActivity(activity);
    }

    /**
     * <h1>Advanced Play Style Setup</h1>
     * This method sets up the advanced configuration for the antiban system with play style enabled.
     * Provides action cooldowns based on the activity intensity and play style.
     * The action cooldown is randomized within the primary and secondary tick intervals in the current play style.
     * Attention span is simulated to switch play styles to create a more human-like drift in attention.
     * Non-linear intervals are used to create Anti-patterns in the action cooldowns to avoid fingerprinting.
     * To activate the action cooldown, call the actionCooldown() method after the desired action.
     *
     * @param activity The activity to be performed
     * @see Rs2Antiban#actionCooldown()
     */
    public static void advancedPlayStyleSetup(Activity activity) {
        Rs2AntibanSettings.actionCooldownActive = false;
        Rs2AntibanSettings.microBreakActive = false;
        Rs2AntibanSettings.antibanEnabled = true;
        Rs2AntibanSettings.usePlayStyle = true;
        Rs2AntibanSettings.randomIntervals = false;
        Rs2AntibanSettings.simulateFatigue = false;
        Rs2AntibanSettings.simulateAttentionSpan = true;
        Rs2AntibanSettings.behavioralVariability = true;
        Rs2AntibanSettings.nonLinearIntervals = true;
        Rs2AntibanSettings.profileSwitching = false; //TODO: Implement this
        Rs2AntibanSettings.timeOfDayAdjust = false; //TODO: Implement this
        Rs2AntibanSettings.simulateMistakes = false; //Handled by the natural mouse
        Rs2AntibanSettings.naturalMouse = true;
        Rs2AntibanSettings.contextualVariability = false; //TODO: Implement this
        Rs2AntibanSettings.dynamicIntensity = false;
        Rs2AntibanSettings.dynamicActivity = false;
        Rs2AntibanSettings.devDebug = true;
        Rs2AntibanSettings.takeMicroBreaks = true;
        Rs2AntibanSettings.actionCooldownChance = 0.1;
        Rs2AntibanSettings.microBreakChance = 0.1;
        Rs2AntibanSettings.microBreakDurationLow = 1;
        Rs2AntibanSettings.microBreakDurationHigh = 5;
        setActivity(activity);
    }


    public static void setActivity(Activity activity) {
        Rs2AntibanSettings.dynamicIntensity = false;
        Rs2AntibanSettings.dynamicActivity = false;
        Rs2Antiban.activity = activity;
        Rs2Antiban.category = activity.getCategory();
        Rs2Antiban.activityIntensity = activity.getActivityIntensity();

        if (Rs2AntibanSettings.simulateAttentionSpan) {
            Rs2Antiban.playStyle = PlayStyle.EXTREME_AGGRESSIVE;
            //Rs2Antiban.playStyle = activityIntensity.getPlayStyle();
        } else
            Rs2Antiban.playStyle = activityIntensity.getPlayStyle();
        if (Rs2AntibanSettings.randomIntervals) {
            Rs2Antiban.playStyle = PlayStyle.RANDOM;
        }
        playStyle.frequency = activityIntensity.getFrequency();
        playStyle.amplitude = activityIntensity.getAmplitude();
        Rs2Antiban.playStyle.resetPlayStyle();


    }

    public static void setActivityIntensity(ActivityIntensity activityIntensity) {
        Rs2AntibanSettings.dynamicIntensity = false;
        Rs2Antiban.activityIntensity = activityIntensity;
    }


    public static boolean checkForCookingEvent(ChatMessage event) {
        if (event.getType() != ChatMessageType.SPAM) {
            return false;
        }
        final String message = event.getMessage();
        return message.startsWith("You successfully cook")
                || message.startsWith("You successfully bake")
                || message.startsWith("You successfully fry")
                || message.startsWith("You manage to cook")
                || message.startsWith("You roast a")
                || message.startsWith("You spit-roast")
                || message.startsWith("You cook")
                || message.startsWith("Eventually the Jubbly")
                || message.startsWith("You half-cook")
                || message.startsWith("The undead meat is now cooked")
                || message.startsWith("The undead chicken is now cooked")
                || message.startsWith("You successfully scramble")
                || message.startsWith("You dry a piece of meat")
                || message.startsWith("You accidentally burn")
                || message.equals("You burn the mushroom in the fire.")
                || message.startsWith("Unfortunately the Jubbly")
                || message.startsWith("You accidentally spoil");
    }

    public static boolean isWoodcutting() {
        return WOODCUTTING_ANIMS.contains(Rs2Player.getAnimation());
    }

    public static boolean isMining() {
        return MINING_ANIMATION_IDS.contains(Rs2Player.getAnimation());
    }

    public static boolean isIdle() {
        return AntibanPlugin.isIdle();
    }

    public static void actionCooldown() {
        if (!Rs2AntibanSettings.usePlayStyle) {
            Microbot.log("PlayStyle not enabled, cannot perform action cooldown");
            return;
        }

        if (Rs2AntibanSettings.contextualVariability)
            Microbot.pauseAllScripts = true;
        if (Rs2AntibanSettings.nonLinearIntervals)
            playStyle.evolvePlayStyle();
        if (Rs2AntibanSettings.behavioralVariability)
            TIMEOUT = playStyle.getRandomTickInterval();
        else
            TIMEOUT = playStyle.getPrimaryTickInterval();
        Rs2AntibanSettings.actionCooldownActive = true;
        if (Rs2AntibanSettings.moveMouseOffScreen)
            moveMouseOffScreen();
    }

    // method to activate the action cooldown by chance
    public static void actionCooldownByChance() {
        if (!Rs2AntibanSettings.usePlayStyle) {
            Microbot.log("PlayStyle not enabled, cannot perform action cooldown");
            return;
        }


        if (Math.random() < Rs2AntibanSettings.actionCooldownChance) {

            if (Rs2AntibanSettings.nonLinearIntervals)
                playStyle.evolvePlayStyle();
            if (Rs2AntibanSettings.behavioralVariability)
                TIMEOUT = playStyle.getRandomTickInterval();
            else
                TIMEOUT = playStyle.getPrimaryTickInterval();
            Rs2AntibanSettings.actionCooldownActive = true;
            if (Rs2AntibanSettings.moveMouseOffScreen)
                moveMouseOffScreen();
        }

    }

    // method to take a micro break by chance
    public static void takeMicroBreakByChance() {
        if (Math.random() < Rs2AntibanSettings.microBreakChance) {
            Rs2AntibanSettings.microBreakActive = true;
            BreakHandlerScript.breakDuration = Random.random(Rs2AntibanSettings.microBreakDurationLow * 60, Rs2AntibanSettings.microBreakDurationHigh * 60);
            if (Rs2AntibanSettings.moveMouseOffScreen)
                moveMouseOffScreen();

        }
    }


    public static void renderAntibanOverlayComponents(PanelComponent panelComponent) {
        final ProgressBarComponent progressBarComponent = new ProgressBarComponent();
        progressBarComponent.setBackgroundColor(Color.DARK_GRAY);
        progressBarComponent.setForegroundColor(ColorUtil.fromHex("#cc8400"));
        progressBarComponent.setMaximum(0);
        progressBarComponent.setMaximum(playStyle.getSecondaryTickInterval());
        progressBarComponent.setValue(TIMEOUT);
        progressBarComponent.setLabelDisplayMode(ProgressBarComponent.LabelDisplayMode.TEXT_ONLY);
        progressBarComponent.setLeftLabel("0");
        progressBarComponent.setRightLabel(String.valueOf(playStyle.getSecondaryTickInterval()));
        progressBarComponent.setCenterLabel(String.valueOf(TIMEOUT));

        panelComponent.getChildren().add(TitleComponent.builder().text("\uD83E\uDD86 Humanizer \uD83E\uDD86")
                .color(Color.ORANGE).build());
        panelComponent.getChildren().add(LineComponent.builder().build());
        panelComponent.getChildren().add(SplitComponent.builder()
                .first(LineComponent.builder().left("Activity: " + activity.getMethod()).right(Rs2AntibanSettings.devDebug ? "Dynamic: " + (Rs2AntibanSettings.dynamicActivity ? "✔" : "❌") : "").build())
                .second(LineComponent.builder().left("Category: " + category.getName()).build()).build());
        panelComponent.getChildren().add(LineComponent.builder().build());
        panelComponent.getChildren().add(LineComponent.builder().left("Activity Intensity: " + activityIntensity.getName()).right(Rs2AntibanSettings.devDebug ? "Dynamic: " + (Rs2AntibanSettings.dynamicIntensity ? "✔" : "❌") : "").build());
        if (Rs2AntibanSettings.devDebug) {
            panelComponent.getChildren().add(LineComponent.builder().left("isActionCooldownActive: " + (Rs2AntibanSettings.actionCooldownActive ? "✔" : "❌")).build());
            panelComponent.getChildren().add(LineComponent.builder().left("isEnabled: " + (Rs2AntibanSettings.antibanEnabled ? "✔" : "❌")).build());
            panelComponent.getChildren().add(LineComponent.builder().left("useRandomIntervals: " + (Rs2AntibanSettings.randomIntervals ? "✔" : "❌")).build());
            panelComponent.getChildren().add(LineComponent.builder().left("simulateFatigue: " + (Rs2AntibanSettings.simulateFatigue ? "✔" : "❌")).build());
            panelComponent.getChildren().add(LineComponent.builder().left("simulateAttentionSpan: " + (Rs2AntibanSettings.simulateAttentionSpan ? "✔" : "❌")).build());
            panelComponent.getChildren().add(LineComponent.builder().left("useBehavioralVariability: " + (Rs2AntibanSettings.behavioralVariability ? "✔" : "❌")).build());
            panelComponent.getChildren().add(LineComponent.builder().left("useNonLinearIntervals: " + (Rs2AntibanSettings.nonLinearIntervals ? "✔" : "❌")).build());
            panelComponent.getChildren().add(LineComponent.builder().left("enableProfileSwitching: " + (Rs2AntibanSettings.profileSwitching ? "✔" : "❌")).build());
            panelComponent.getChildren().add(LineComponent.builder().left("adjustForTimeOfDay: " + (Rs2AntibanSettings.timeOfDayAdjust ? "✔" : "❌")).build());
            panelComponent.getChildren().add(LineComponent.builder().left("simulateMistakes: " + (Rs2AntibanSettings.simulateMistakes ? "✔" : "❌")).build());
            panelComponent.getChildren().add(LineComponent.builder().left("useNaturalMouse: " + (Rs2AntibanSettings.naturalMouse ? "✔" : "❌")).build());
            panelComponent.getChildren().add(LineComponent.builder().left("useContextualVariability: " + (Rs2AntibanSettings.contextualVariability ? "✔" : "❌")).build());
        }
        if (playStyle != null) {
            panelComponent.getChildren().add(LineComponent.builder().left("Play Style: " + playStyle.getName()).build());
            if (Rs2AntibanSettings.simulateAttentionSpan) {
                panelComponent.getChildren().add(LineComponent.builder().left("Play style change in: " + playStyle.getTimeLeftUntilNextSwitch()).build());
            }
            panelComponent.getChildren().add(LineComponent.builder().build());
            if (Rs2Antiban.getCategory().isBusy()) {
                panelComponent.getChildren().add(LineComponent.builder().left("We are busy").build());
            } else {
                panelComponent.getChildren().add(LineComponent.builder().left("Not busy anymore, breaking").build());
            }
            panelComponent.getChildren().add(LineComponent.builder().build());
            panelComponent.getChildren().add(TitleComponent.builder().text("Action cooldown(Tick)").color(Color.WHITE).build());
            panelComponent.getChildren().add(progressBarComponent);
        }

    }

    /**
     * <h1>Move Mouse Off Screen</h1>
     * This method moves the mouse off the screen with a 1/4 chance to trigger.
     * This is used to simulate a user moving the mouse off the screen to take a break.
     */
    public static void moveMouseOffScreen() {
        Microbot.naturalMouse.moveOffScreen();
    }

    public static void activateAntiban() {
        Rs2AntibanSettings.antibanEnabled = true;
    }

    public static void deactivateAntiban() {
        Rs2AntibanSettings.antibanEnabled = false;
    }

    // reset all the variables
    public static void resetAntiban() {
        Rs2AntibanSettings.antibanEnabled = false;
        Rs2AntibanSettings.microBreakActive = false;
        Rs2AntibanSettings.actionCooldownActive = false;
        Rs2AntibanSettings.usePlayStyle = false;
        Rs2AntibanSettings.randomIntervals = false;
        Rs2AntibanSettings.simulateFatigue = false;
        Rs2AntibanSettings.simulateAttentionSpan = false;
        Rs2AntibanSettings.behavioralVariability = false;
        Rs2AntibanSettings.nonLinearIntervals = false;
        Rs2AntibanSettings.profileSwitching = false;
        Rs2AntibanSettings.timeOfDayAdjust = false;
        Rs2AntibanSettings.simulateMistakes = false;
        Rs2AntibanSettings.naturalMouse = false;
        Rs2AntibanSettings.contextualVariability = false;
        Rs2AntibanSettings.dynamicIntensity = true;
        Rs2AntibanSettings.dynamicActivity = true;
        Rs2AntibanSettings.devDebug = true;
        TIMEOUT = 0;
        activity = null;
        activityIntensity = null;
        category = null;
        playStyle = null;
        Rs2AntibanSettings.takeMicroBreaks = false;
        Rs2AntibanSettings.actionCooldownChance = 0.1;
        Rs2AntibanSettings.microBreakChance = 0.1;
        Rs2AntibanSettings.microBreakDurationLow = 3;
        Rs2AntibanSettings.microBreakDurationHigh = 15;
    }

}
