package net.runelite.client.plugins.microbot.util.antiban;

public class Rs2AntibanSettings {
    public static boolean actionCooldownActive = false;
    public static boolean microBreakActive = false;
    public static boolean antibanEnabled = true;
    public static boolean usePlayStyle = true;
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
    public static boolean contextualVariability = true; //TODO: Implement this
    public static boolean dynamicIntensity = true;
    public static boolean dynamicActivity = true;
    public static boolean devDebug = true;
    public static boolean takeMicroBreaks = false; // will take micro breaks lasting 3-15 minutes at random intervals by default.
    public static boolean playSchedule = false; //TODO: Implement this
    public static int microBreakDurationLow = 3; // 3 minutes
    public static int microBreakDurationHigh = 15; // 15 minutes
    public static double actionCooldownChance = 0.1; // 10% chance of activating the action cooldown by default
    public static double microBreakChance = 0.1; // 10% chance of taking a micro break by default
}