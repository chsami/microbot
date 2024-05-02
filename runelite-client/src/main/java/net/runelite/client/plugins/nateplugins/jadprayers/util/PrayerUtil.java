package net.runelite.client.plugins.nateplugins.jadprayers.util;

import net.runelite.api.MenuAction;
import net.runelite.api.Prayer;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.util.menu.NewMenuEntry;

import java.awt.*;
import java.util.HashMap;

public class PrayerUtil {
    public static final HashMap<Prayer, WidgetInfoExtended> prayerMap = new HashMap<>();
    static{
        prayerMap.put(Prayer.AUGURY,WidgetInfoExtended.PRAYER_AUGURY); //
        prayerMap.put(Prayer.BURST_OF_STRENGTH,WidgetInfoExtended.PRAYER_BURST_OF_STRENGTH); //
        prayerMap.put(Prayer.CHIVALRY,WidgetInfoExtended.PRAYER_CHIVALRY); //
        prayerMap.put(Prayer.CLARITY_OF_THOUGHT,WidgetInfoExtended.PRAYER_CLARITY_OF_THOUGHT); //
        prayerMap.put(Prayer.EAGLE_EYE,WidgetInfoExtended.PRAYER_EAGLE_EYE); //
        prayerMap.put(Prayer.HAWK_EYE,WidgetInfoExtended.PRAYER_HAWK_EYE); //
        prayerMap.put(Prayer.IMPROVED_REFLEXES,WidgetInfoExtended.PRAYER_IMPROVED_REFLEXES); //
        prayerMap.put(Prayer.INCREDIBLE_REFLEXES,WidgetInfoExtended.PRAYER_INCREDIBLE_REFLEXES); //
        prayerMap.put(Prayer.MYSTIC_MIGHT,WidgetInfoExtended.PRAYER_MYSTIC_MIGHT); //
        prayerMap.put(Prayer.PIETY,WidgetInfoExtended.PRAYER_PIETY); //
        prayerMap.put(Prayer.PRESERVE,WidgetInfoExtended.PRAYER_PRESERVE); //
        prayerMap.put(Prayer.PROTECT_FROM_MAGIC,WidgetInfoExtended.PRAYER_PROTECT_FROM_MAGIC); //
        prayerMap.put(Prayer.PROTECT_FROM_MELEE,WidgetInfoExtended.PRAYER_PROTECT_FROM_MELEE); //
        prayerMap.put(Prayer.PROTECT_FROM_MISSILES,WidgetInfoExtended.PRAYER_PROTECT_FROM_MISSILES); //
        prayerMap.put(Prayer.RETRIBUTION,WidgetInfoExtended.PRAYER_RETRIBUTION); //
        prayerMap.put(Prayer.RIGOUR,WidgetInfoExtended.PRAYER_RIGOUR); //
        prayerMap.put(Prayer.ROCK_SKIN,WidgetInfoExtended.PRAYER_ROCK_SKIN); //
        prayerMap.put(Prayer.SHARP_EYE,WidgetInfoExtended.PRAYER_SHARP_EYE); //
        prayerMap.put(Prayer.SMITE,WidgetInfoExtended.PRAYER_SMITE); //
        prayerMap.put(Prayer.STEEL_SKIN,WidgetInfoExtended.PRAYER_STEEL_SKIN); //
        prayerMap.put(Prayer.THICK_SKIN,WidgetInfoExtended.PRAYER_THICK_SKIN); //
        prayerMap.put(Prayer.ULTIMATE_STRENGTH,WidgetInfoExtended.PRAYER_ULTIMATE_STRENGTH); //
        prayerMap.put(Prayer.REDEMPTION,WidgetInfoExtended.PRAYER_REDEMPTION); //
        prayerMap.put(Prayer.RAPID_RESTORE,WidgetInfoExtended.PRAYER_RAPID_RESTORE); //
        prayerMap.put(Prayer.RAPID_HEAL,WidgetInfoExtended.PRAYER_RAPID_HEAL); //
        prayerMap.put(Prayer.PROTECT_ITEM,WidgetInfoExtended.PRAYER_PROTECT_ITEM); //
        prayerMap.put(Prayer.MYSTIC_LORE,WidgetInfoExtended.PRAYER_MYSTIC_LORE); //
        prayerMap.put(Prayer.SUPERHUMAN_STRENGTH,WidgetInfoExtended.PRAYER_SUPERHUMAN_STRENGTH); //
        prayerMap.put(Prayer.MYSTIC_WILL,WidgetInfoExtended.PRAYER_MYSTIC_WILL); //
    }

    public static int getPrayerWidgetId(Prayer prayer) {
        return prayerMap.getOrDefault(prayer, WidgetInfoExtended.PRAYER_THICK_SKIN).getPackedId();
    }

    public static boolean isPrayerActive(Prayer prayer) {
        return Microbot.getClient().isPrayerActive(prayer);
    }

    public static void toggle(Prayer prayer) {
        if (prayer == null) {
            return;
        }
        Microbot.doInvoke(new NewMenuEntry(-1, getPrayerWidgetId(prayer), MenuAction.CC_OP.getId(), 1,-1, "Activate"), new Rectangle(1, 1, Microbot.getClient().getCanvasWidth(), Microbot.getClient().getCanvasHeight()));

    }
}
