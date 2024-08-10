package net.runelite.client.plugins.microbot.util.antiban;

import net.runelite.client.plugins.microbot.util.antiban.enums.Activity;

/**
 * The {@code AntibanSetupTemplates} class provides a set of methods to apply specific antiban
 * configurations tailored to different activities within the game. These configurations adjust various
 * settings such as mouse movement, random intervals, and fatigue simulation to mimic human behavior and
 * reduce the likelihood of detection by anti-cheat systems.
 */
public class AntibanSetupTemplates {
    /**
     * Applies the antiban setup tailored for general combat activities.
     * This setup enables various human-like behaviors such as fatigue simulation, attention span,
     * and mouse movement variability to reduce detection risk.
     */
    public void applyCombatSetup() {
        Rs2AntibanSettings.antibanEnabled = true;
        Rs2AntibanSettings.usePlayStyle = true;
        Rs2AntibanSettings.randomIntervals = false;
        Rs2AntibanSettings.simulateFatigue = true;
        Rs2AntibanSettings.simulateAttentionSpan = true;
        Rs2AntibanSettings.behavioralVariability = true;
        Rs2AntibanSettings.nonLinearIntervals = true;
        Rs2AntibanSettings.profileSwitching = true;
        Rs2AntibanSettings.timeOfDayAdjust = false;
        Rs2AntibanSettings.simulateMistakes = false;
        Rs2AntibanSettings.naturalMouse = true;
        Rs2AntibanSettings.contextualVariability = true;
        Rs2AntibanSettings.dynamicIntensity = false;
        Rs2AntibanSettings.dynamicActivity = false;
        Rs2AntibanSettings.devDebug = false;
        Rs2AntibanSettings.takeMicroBreaks = true;
        Rs2AntibanSettings.playSchedule = true;
        Rs2AntibanSettings.microBreakDurationLow = 3;
        Rs2AntibanSettings.microBreakDurationHigh = 8;
        Rs2AntibanSettings.actionCooldownChance = 0.05;
        Rs2AntibanSettings.microBreakChance = 0.05;
        Rs2Antiban.setActivity(Activity.GENERAL_COMBAT);
    }

    /**
     * Applies the antiban setup tailored for runecrafting activities.
     * This setup adjusts settings to simulate human-like behaviors during runecrafting tasks.
     */
    public void applyRunecraftingSetup() {
        // Implementation for Runecrafting setup
        Rs2AntibanSettings.antibanEnabled = true;
        Rs2AntibanSettings.usePlayStyle = true;
        Rs2AntibanSettings.randomIntervals = false;
        Rs2AntibanSettings.simulateFatigue = true;
        Rs2AntibanSettings.simulateAttentionSpan = true;
        Rs2AntibanSettings.behavioralVariability = true;
        Rs2AntibanSettings.nonLinearIntervals = true;
        Rs2AntibanSettings.profileSwitching = true;
        Rs2AntibanSettings.timeOfDayAdjust = false;
        Rs2AntibanSettings.simulateMistakes = false;
        Rs2AntibanSettings.naturalMouse = true;
        Rs2AntibanSettings.contextualVariability = true;
        Rs2AntibanSettings.dynamicIntensity = false;
        Rs2AntibanSettings.dynamicActivity = false;
        Rs2AntibanSettings.devDebug = false;
        Rs2AntibanSettings.takeMicroBreaks = true;
        Rs2AntibanSettings.playSchedule = true;
        Rs2AntibanSettings.microBreakDurationLow = 3;
        Rs2AntibanSettings.microBreakDurationHigh = 8;
        Rs2AntibanSettings.actionCooldownChance = 0.05;
        Rs2AntibanSettings.microBreakChance = 0.05;
        Rs2Antiban.setActivity(Activity.GENERAL_RUNECRAFT);
    }

    /**
     * Applies the antiban setup tailored for construction activities.
     * This setup focuses on mimicking human-like behaviors during construction tasks.
     */
    public void applyConstructionSetup() {
        // Implementation for Construction setup
        Rs2AntibanSettings.antibanEnabled = true;
        Rs2AntibanSettings.usePlayStyle = true;
        Rs2AntibanSettings.randomIntervals = false;
        Rs2AntibanSettings.simulateFatigue = true;
        Rs2AntibanSettings.simulateAttentionSpan = true;
        Rs2AntibanSettings.behavioralVariability = true;
        Rs2AntibanSettings.nonLinearIntervals = true;
        Rs2AntibanSettings.profileSwitching = true;
        Rs2AntibanSettings.timeOfDayAdjust = false;
        Rs2AntibanSettings.simulateMistakes = false;
        Rs2AntibanSettings.naturalMouse = true;
        Rs2AntibanSettings.contextualVariability = true;
        Rs2AntibanSettings.dynamicIntensity = false;
        Rs2AntibanSettings.dynamicActivity = false;
        Rs2AntibanSettings.devDebug = false;
        Rs2AntibanSettings.takeMicroBreaks = true;
        Rs2AntibanSettings.playSchedule = true;
        Rs2AntibanSettings.microBreakDurationLow = 3;
        Rs2AntibanSettings.microBreakDurationHigh = 8;
        Rs2AntibanSettings.actionCooldownChance = 0.05;
        Rs2AntibanSettings.microBreakChance = 0.05;
        Rs2Antiban.setActivity(Activity.GENERAL_CONSTRUCTION);
    }

    /**
     * Applies the antiban setup tailored for agility activities.
     * This setup includes adjustments to simulate human-like behaviors during agility tasks.
     */

    public void applyAgilitySetup() {
        // Implementation for Agility setup
        Rs2AntibanSettings.antibanEnabled = true;
        Rs2AntibanSettings.usePlayStyle = true;
        Rs2AntibanSettings.randomIntervals = false;
        Rs2AntibanSettings.simulateFatigue = true;
        Rs2AntibanSettings.simulateAttentionSpan = true;
        Rs2AntibanSettings.behavioralVariability = true;
        Rs2AntibanSettings.nonLinearIntervals = true;
        Rs2AntibanSettings.profileSwitching = true;
        Rs2AntibanSettings.timeOfDayAdjust = false;
        Rs2AntibanSettings.simulateMistakes = false;
        Rs2AntibanSettings.naturalMouse = true;
        Rs2AntibanSettings.contextualVariability = true;
        Rs2AntibanSettings.dynamicIntensity = false;
        Rs2AntibanSettings.dynamicActivity = false;
        Rs2AntibanSettings.devDebug = false;
        Rs2AntibanSettings.takeMicroBreaks = true;
        Rs2AntibanSettings.playSchedule = true;
        Rs2AntibanSettings.microBreakDurationLow = 3;
        Rs2AntibanSettings.microBreakDurationHigh = 8;
        Rs2AntibanSettings.actionCooldownChance = 0.05;
        Rs2AntibanSettings.microBreakChance = 0.05;
        Rs2Antiban.setActivity(Activity.GENERAL_AGILITY);
    }

    /**
     * Applies the antiban setup tailored for herblore activities.
     * This setup configures settings to mimic human-like behaviors during herblore tasks.
     */
    public void applyHerbloreSetup() {
        // Implementation for Herblore setup
        Rs2AntibanSettings.antibanEnabled = true;
        Rs2AntibanSettings.usePlayStyle = true;
        Rs2AntibanSettings.randomIntervals = false;
        Rs2AntibanSettings.simulateFatigue = true;
        Rs2AntibanSettings.simulateAttentionSpan = true;
        Rs2AntibanSettings.behavioralVariability = true;
        Rs2AntibanSettings.nonLinearIntervals = true;
        Rs2AntibanSettings.profileSwitching = true;
        Rs2AntibanSettings.timeOfDayAdjust = false;
        Rs2AntibanSettings.simulateMistakes = false;
        Rs2AntibanSettings.naturalMouse = true;
        Rs2AntibanSettings.contextualVariability = true;
        Rs2AntibanSettings.dynamicIntensity = false;
        Rs2AntibanSettings.dynamicActivity = false;
        Rs2AntibanSettings.devDebug = false;
        Rs2AntibanSettings.takeMicroBreaks = true;
        Rs2AntibanSettings.playSchedule = true;
        Rs2AntibanSettings.microBreakDurationLow = 3;
        Rs2AntibanSettings.microBreakDurationHigh = 8;
        Rs2AntibanSettings.actionCooldownChance = 0.05;
        Rs2AntibanSettings.microBreakChance = 0.05;
        Rs2Antiban.setActivity(Activity.GENERAL_HERBLORE);
    }

    /**
     * Applies the antiban setup tailored for thieving activities.
     * This setup simulates human-like behaviors during thieving tasks to reduce detection risk.
     */
    public void applyThievingSetup() {
        // Implementation for Thieving setup
        Rs2AntibanSettings.antibanEnabled = true;
        Rs2AntibanSettings.usePlayStyle = true;
        Rs2AntibanSettings.randomIntervals = false;
        Rs2AntibanSettings.simulateFatigue = true;
        Rs2AntibanSettings.simulateAttentionSpan = true;
        Rs2AntibanSettings.behavioralVariability = true;
        Rs2AntibanSettings.nonLinearIntervals = true;
        Rs2AntibanSettings.profileSwitching = true;
        Rs2AntibanSettings.timeOfDayAdjust = false;
        Rs2AntibanSettings.simulateMistakes = false;
        Rs2AntibanSettings.naturalMouse = true;
        Rs2AntibanSettings.contextualVariability = true;
        Rs2AntibanSettings.dynamicIntensity = false;
        Rs2AntibanSettings.dynamicActivity = false;
        Rs2AntibanSettings.devDebug = false;
        Rs2AntibanSettings.takeMicroBreaks = true;
        Rs2AntibanSettings.playSchedule = true;
        Rs2AntibanSettings.microBreakDurationLow = 3;
        Rs2AntibanSettings.microBreakDurationHigh = 8;
        Rs2AntibanSettings.actionCooldownChance = 0.05;
        Rs2AntibanSettings.microBreakChance = 0.05;
        Rs2Antiban.setActivity(Activity.GENERAL_THIEVING);
    }

    /**
     * Applies the antiban setup tailored for crafting activities.
     * This setup focuses on human-like behavior simulation during crafting tasks.
     */
    public void applyCraftingSetup() {
        // Implementation for Crafting setup
        Rs2AntibanSettings.antibanEnabled = true;
        Rs2AntibanSettings.usePlayStyle = true;
        Rs2AntibanSettings.randomIntervals = false;
        Rs2AntibanSettings.simulateFatigue = true;
        Rs2AntibanSettings.simulateAttentionSpan = true;
        Rs2AntibanSettings.behavioralVariability = true;
        Rs2AntibanSettings.nonLinearIntervals = true;
        Rs2AntibanSettings.profileSwitching = true;
        Rs2AntibanSettings.timeOfDayAdjust = false;
        Rs2AntibanSettings.simulateMistakes = false;
        Rs2AntibanSettings.naturalMouse = true;
        Rs2AntibanSettings.contextualVariability = true;
        Rs2AntibanSettings.dynamicIntensity = false;
        Rs2AntibanSettings.dynamicActivity = false;
        Rs2AntibanSettings.devDebug = false;
        Rs2AntibanSettings.takeMicroBreaks = true;
        Rs2AntibanSettings.playSchedule = true;
        Rs2AntibanSettings.microBreakDurationLow = 3;
        Rs2AntibanSettings.microBreakDurationHigh = 8;
        Rs2AntibanSettings.actionCooldownChance = 0.05;
        Rs2AntibanSettings.microBreakChance = 0.05;
        Rs2Antiban.setActivity(Activity.GENERAL_CRAFTING);
    }

    /**
     * Applies the antiban setup tailored for fletching activities.
     * This setup adjusts settings to mimic human behavior during fletching tasks.
     */
    public void applyFletchingSetup() {
        // Implementation for Fletching setup
        Rs2AntibanSettings.antibanEnabled = true;
        Rs2AntibanSettings.usePlayStyle = true;
        Rs2AntibanSettings.randomIntervals = false;
        Rs2AntibanSettings.simulateFatigue = true;
        Rs2AntibanSettings.simulateAttentionSpan = true;
        Rs2AntibanSettings.behavioralVariability = true;
        Rs2AntibanSettings.nonLinearIntervals = true;
        Rs2AntibanSettings.profileSwitching = true;
        Rs2AntibanSettings.timeOfDayAdjust = false;
        Rs2AntibanSettings.simulateMistakes = false;
        Rs2AntibanSettings.naturalMouse = true;
        Rs2AntibanSettings.contextualVariability = true;
        Rs2AntibanSettings.dynamicIntensity = false;
        Rs2AntibanSettings.dynamicActivity = false;
        Rs2AntibanSettings.devDebug = false;
        Rs2AntibanSettings.takeMicroBreaks = true;
        Rs2AntibanSettings.playSchedule = true;
        Rs2AntibanSettings.microBreakDurationLow = 3;
        Rs2AntibanSettings.microBreakDurationHigh = 8;
        Rs2AntibanSettings.actionCooldownChance = 0.05;
        Rs2AntibanSettings.microBreakChance = 0.05;
        Rs2Antiban.setActivity(Activity.GENERAL_FLETCHING);
    }

    public void applySlayerSetup() {
        // Implementation for Slayer setup
    }

    /**
     * Applies the antiban setup tailored for hunter activities.
     * This setup simulates human-like behaviors during hunting tasks.
     */
    public void applyHunterSetup() {
        // Implementation for Hunter setup
        Rs2AntibanSettings.antibanEnabled = true;
        Rs2AntibanSettings.usePlayStyle = true;
        Rs2AntibanSettings.randomIntervals = false;
        Rs2AntibanSettings.simulateFatigue = true;
        Rs2AntibanSettings.simulateAttentionSpan = true;
        Rs2AntibanSettings.behavioralVariability = true;
        Rs2AntibanSettings.nonLinearIntervals = true;
        Rs2AntibanSettings.profileSwitching = true;
        Rs2AntibanSettings.timeOfDayAdjust = false;
        Rs2AntibanSettings.simulateMistakes = false;
        Rs2AntibanSettings.naturalMouse = true;
        Rs2AntibanSettings.contextualVariability = true;
        Rs2AntibanSettings.dynamicIntensity = false;
        Rs2AntibanSettings.dynamicActivity = false;
        Rs2AntibanSettings.devDebug = false;
        Rs2AntibanSettings.takeMicroBreaks = true;
        Rs2AntibanSettings.playSchedule = true;
        Rs2AntibanSettings.microBreakDurationLow = 3;
        Rs2AntibanSettings.microBreakDurationHigh = 8;
        Rs2AntibanSettings.actionCooldownChance = 0.05;
        Rs2AntibanSettings.microBreakChance = 0.05;
        Rs2Antiban.setActivity(Activity.GENERAL_HUNTER);
    }

    /**
     * Applies the antiban setup tailored for mining activities.
     * This setup includes adjustments to mimic human behaviors during mining tasks.
     */
    public void applyMiningSetup() {
        // Implementation for Mining setup
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
        Rs2AntibanSettings.contextualVariability = false;
        Rs2AntibanSettings.dynamicIntensity = false;
        Rs2AntibanSettings.dynamicActivity = false;
        Rs2AntibanSettings.devDebug = false;
        Rs2AntibanSettings.takeMicroBreaks = true;
        Rs2AntibanSettings.playSchedule = true;
        Rs2AntibanSettings.microBreakDurationLow = 1;
        Rs2AntibanSettings.microBreakDurationHigh = 4;
        Rs2AntibanSettings.actionCooldownChance = 0.05;
        Rs2AntibanSettings.microBreakChance = 0.05;
        Rs2Antiban.setActivity(Activity.GENERAL_MINING);
    }

    /**
     * Applies the antiban setup tailored for smithing activities.
     * This setup configures settings to simulate human-like behaviors during smithing tasks.
     */
    public void applySmithingSetup() {
        // Implementation for Smithing setup
        Rs2AntibanSettings.antibanEnabled = true;
        Rs2AntibanSettings.usePlayStyle = true;
        Rs2AntibanSettings.randomIntervals = false;
        Rs2AntibanSettings.simulateFatigue = true;
        Rs2AntibanSettings.simulateAttentionSpan = true;
        Rs2AntibanSettings.behavioralVariability = true;
        Rs2AntibanSettings.nonLinearIntervals = true;
        Rs2AntibanSettings.profileSwitching = true;
        Rs2AntibanSettings.timeOfDayAdjust = false;
        Rs2AntibanSettings.simulateMistakes = false;
        Rs2AntibanSettings.naturalMouse = true;
        Rs2AntibanSettings.contextualVariability = true;
        Rs2AntibanSettings.dynamicIntensity = false;
        Rs2AntibanSettings.dynamicActivity = false;
        Rs2AntibanSettings.devDebug = false;
        Rs2AntibanSettings.takeMicroBreaks = true;
        Rs2AntibanSettings.playSchedule = true;
        Rs2AntibanSettings.microBreakDurationLow = 3;
        Rs2AntibanSettings.microBreakDurationHigh = 8;
        Rs2AntibanSettings.actionCooldownChance = 0.05;
        Rs2AntibanSettings.microBreakChance = 0.05;
        Rs2Antiban.setActivity(Activity.GENERAL_SMITHING);
    }

    /**
     * Applies the antiban setup tailored for fishing activities.
     * This setup focuses on mimicking human-like behaviors during fishing tasks.
     */
    public void applyFishingSetup() {
        // Implementation for Fishing setup
        Rs2AntibanSettings.antibanEnabled = true;
        Rs2AntibanSettings.usePlayStyle = true;
        Rs2AntibanSettings.randomIntervals = false;
        Rs2AntibanSettings.simulateFatigue = true;
        Rs2AntibanSettings.simulateAttentionSpan = true;
        Rs2AntibanSettings.behavioralVariability = true;
        Rs2AntibanSettings.nonLinearIntervals = true;
        Rs2AntibanSettings.profileSwitching = true;
        Rs2AntibanSettings.timeOfDayAdjust = false;
        Rs2AntibanSettings.simulateMistakes = false;
        Rs2AntibanSettings.naturalMouse = true;
        Rs2AntibanSettings.contextualVariability = true;
        Rs2AntibanSettings.dynamicIntensity = false;
        Rs2AntibanSettings.dynamicActivity = false;
        Rs2AntibanSettings.devDebug = false;
        Rs2AntibanSettings.takeMicroBreaks = true;
        Rs2AntibanSettings.playSchedule = true;
        Rs2AntibanSettings.microBreakDurationLow = 3;
        Rs2AntibanSettings.microBreakDurationHigh = 8;
        Rs2AntibanSettings.actionCooldownChance = 0.05;
        Rs2AntibanSettings.microBreakChance = 0.05;
        Rs2Antiban.setActivity(Activity.GENERAL_FISHING);
    }

    /**
     * Applies the antiban setup tailored for cooking activities.
     * This setup simulates human-like behaviors during cooking tasks to reduce detection risk.
     */
    public void applyCookingSetup() {
        // Implementation for Cooking setup
        Rs2AntibanSettings.antibanEnabled = true;
        Rs2AntibanSettings.usePlayStyle = true;
        Rs2AntibanSettings.randomIntervals = false;
        Rs2AntibanSettings.simulateFatigue = true;
        Rs2AntibanSettings.simulateAttentionSpan = true;
        Rs2AntibanSettings.behavioralVariability = true;
        Rs2AntibanSettings.nonLinearIntervals = true;
        Rs2AntibanSettings.profileSwitching = true;
        Rs2AntibanSettings.timeOfDayAdjust = false;
        Rs2AntibanSettings.simulateMistakes = false;
        Rs2AntibanSettings.naturalMouse = true;
        Rs2AntibanSettings.contextualVariability = true;
        Rs2AntibanSettings.dynamicIntensity = false;
        Rs2AntibanSettings.dynamicActivity = false;
        Rs2AntibanSettings.devDebug = false;
        Rs2AntibanSettings.takeMicroBreaks = true;
        Rs2AntibanSettings.playSchedule = true;
        Rs2AntibanSettings.microBreakDurationLow = 3;
        Rs2AntibanSettings.microBreakDurationHigh = 8;
        Rs2AntibanSettings.actionCooldownChance = 0.05;
        Rs2AntibanSettings.microBreakChance = 0.05;
        Rs2Antiban.setActivity(Activity.GENERAL_COOKING);
    }

    /**
     * Applies the antiban setup tailored for firemaking activities.
     * This setup is designed to simulate human behavior during firemaking tasks.
     */
    public void applyFiremakingSetup() {
        // Implementation for Firemaking setup
        Rs2AntibanSettings.antibanEnabled = true;
        Rs2AntibanSettings.usePlayStyle = true;
        Rs2AntibanSettings.randomIntervals = false;
        Rs2AntibanSettings.simulateFatigue = true;
        Rs2AntibanSettings.simulateAttentionSpan = true;
        Rs2AntibanSettings.behavioralVariability = true;
        Rs2AntibanSettings.nonLinearIntervals = true;
        Rs2AntibanSettings.profileSwitching = true;
        Rs2AntibanSettings.timeOfDayAdjust = false;
        Rs2AntibanSettings.simulateMistakes = false;
        Rs2AntibanSettings.naturalMouse = true;
        Rs2AntibanSettings.contextualVariability = true;
        Rs2AntibanSettings.dynamicIntensity = false;
        Rs2AntibanSettings.dynamicActivity = false;
        Rs2AntibanSettings.devDebug = false;
        Rs2AntibanSettings.takeMicroBreaks = true;
        Rs2AntibanSettings.playSchedule = true;
        Rs2AntibanSettings.microBreakDurationLow = 3;
        Rs2AntibanSettings.microBreakDurationHigh = 8;
        Rs2AntibanSettings.actionCooldownChance = 0.05;
        Rs2AntibanSettings.microBreakChance = 0.05;
        Rs2Antiban.setActivity(Activity.GENERAL_FIREMAKING);
    }

    /**
     * Applies the antiban setup tailored for woodcutting activities.
     * This setup mimics human-like behaviors during woodcutting tasks to reduce detection risk.
     */
    public void applyWoodcuttingSetup() {
        // Implementation for Woodcutting setup
        Rs2AntibanSettings.antibanEnabled = true;
        Rs2AntibanSettings.usePlayStyle = true;
        Rs2AntibanSettings.randomIntervals = false;
        Rs2AntibanSettings.simulateFatigue = true;
        Rs2AntibanSettings.simulateAttentionSpan = true;
        Rs2AntibanSettings.behavioralVariability = true;
        Rs2AntibanSettings.nonLinearIntervals = true;
        Rs2AntibanSettings.profileSwitching = true;
        Rs2AntibanSettings.timeOfDayAdjust = false;
        Rs2AntibanSettings.simulateMistakes = false;
        Rs2AntibanSettings.naturalMouse = true;
        Rs2AntibanSettings.contextualVariability = true;
        Rs2AntibanSettings.dynamicIntensity = false;
        Rs2AntibanSettings.dynamicActivity = false;
        Rs2AntibanSettings.devDebug = false;
        Rs2AntibanSettings.takeMicroBreaks = true;
        Rs2AntibanSettings.playSchedule = true;
        Rs2AntibanSettings.microBreakDurationLow = 3;
        Rs2AntibanSettings.microBreakDurationHigh = 8;
        Rs2AntibanSettings.actionCooldownChance = 0.05;
        Rs2AntibanSettings.microBreakChance = 0.05;
        Rs2Antiban.setActivity(Activity.GENERAL_WOODCUTTING);
    }

    /**
     * Applies the antiban setup tailored for farming activities.
     * This setup configures settings to simulate human-like behaviors during farming tasks.
     */
    public void applyFarmingSetup() {
        // Implementation for Farming setup
        Rs2AntibanSettings.antibanEnabled = true;
        Rs2AntibanSettings.usePlayStyle = true;
        Rs2AntibanSettings.randomIntervals = false;
        Rs2AntibanSettings.simulateFatigue = true;
        Rs2AntibanSettings.simulateAttentionSpan = true;
        Rs2AntibanSettings.behavioralVariability = true;
        Rs2AntibanSettings.nonLinearIntervals = true;
        Rs2AntibanSettings.profileSwitching = true;
        Rs2AntibanSettings.timeOfDayAdjust = false;
        Rs2AntibanSettings.simulateMistakes = false;
        Rs2AntibanSettings.naturalMouse = true;
        Rs2AntibanSettings.contextualVariability = false;
        Rs2AntibanSettings.dynamicIntensity = false;
        Rs2AntibanSettings.dynamicActivity = false;
        Rs2AntibanSettings.devDebug = false;
        Rs2AntibanSettings.takeMicroBreaks = true;
        Rs2AntibanSettings.playSchedule = true;
        Rs2AntibanSettings.microBreakDurationLow = 3;
        Rs2AntibanSettings.microBreakDurationHigh = 8;
        Rs2AntibanSettings.actionCooldownChance = 0.05;
        Rs2AntibanSettings.microBreakChance = 0.05;
        Rs2Antiban.setActivity(Activity.GENERAL_FARMING);
    }

    /**
     * Applies the basic antiban setup.
     * This setup configures settings to simulate human-like mouse movement and reduce detection risk.
     * This setup does not include advanced features such as action cooldown, attention span or micro breaks.
     */
    public void applyGeneralBasicSetup() {
        // Implementation for General Basic setup
        Rs2AntibanSettings.antibanEnabled = true;
        Rs2AntibanSettings.usePlayStyle = false;
        Rs2AntibanSettings.randomIntervals = false;
        Rs2AntibanSettings.simulateFatigue = true;
        Rs2AntibanSettings.simulateAttentionSpan = false;
        Rs2AntibanSettings.behavioralVariability = false;
        Rs2AntibanSettings.nonLinearIntervals = false;
        Rs2AntibanSettings.profileSwitching = false;
        Rs2AntibanSettings.timeOfDayAdjust = false;
        Rs2AntibanSettings.simulateMistakes = true;
        Rs2AntibanSettings.naturalMouse = true;
        Rs2AntibanSettings.contextualVariability = false;
        Rs2AntibanSettings.dynamicIntensity = true;
        Rs2AntibanSettings.dynamicActivity = true;
        Rs2AntibanSettings.devDebug = false;
        Rs2AntibanSettings.takeMicroBreaks = false;
        Rs2AntibanSettings.playSchedule = false;
        Rs2AntibanSettings.microBreakDurationLow = 3;
        Rs2AntibanSettings.microBreakDurationHigh = 8;
        Rs2AntibanSettings.actionCooldownChance = 0.05;
        Rs2AntibanSettings.microBreakChance = 0.05;
    }


}

