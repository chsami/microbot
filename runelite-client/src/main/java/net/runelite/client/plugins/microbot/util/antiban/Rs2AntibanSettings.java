package net.runelite.client.plugins.microbot.util.antiban;

public class Rs2AntibanSettings {
    public static boolean actionCooldownActive = false;
    public static boolean microBreakActive = false;
    public static boolean antibanEnabled = true;
    public static boolean usePlayStyle = false;
    public static boolean randomIntervals = false;
    public static boolean simulateFatigue = false;
    public static boolean simulateAttentionSpan = false;
    public static boolean behavioralVariability = false;
    public static boolean nonLinearIntervals = false;
    public static boolean profileSwitching = false;
    public static boolean timeOfDayAdjust = false; //TODO: Implement this
    public static boolean simulateMistakes = false; //Handled by the natural mouse
    public static boolean naturalMouse = false;
    public static boolean moveMouseOffScreen = false;
    public static boolean moveMouseRandomly = false;
    public static boolean contextualVariability = false;
    public static boolean dynamicIntensity = false;
    public static boolean dynamicActivity = false;
    public static boolean devDebug = false;
    public static boolean takeMicroBreaks = false; // will take micro breaks lasting 3-15 minutes at random intervals by default.
    public static boolean playSchedule = false; //TODO: Implement this
    public static boolean universalAntiban = false; // Will attempt to use the same antiban settings for all plugins that has not yet implemented their own antiban settings.
    public static int microBreakDurationLow = 3; // 3 minutes
    public static int microBreakDurationHigh = 15; // 15 minutes
    public static double actionCooldownChance = 0.1; // 10% chance of activating the action cooldown by default
    public static double microBreakChance = 0.1; // 10% chance of taking a micro break by default
    public static double moveMouseRandomlyChance = 0.1; // 10% chance of moving the mouse randomly by default
}