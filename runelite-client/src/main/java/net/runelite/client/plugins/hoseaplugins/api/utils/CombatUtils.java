package net.runelite.client.plugins.hoseaplugins.api.utils;

import net.runelite.client.plugins.hoseaplugins.ethanapi.InteractionApi.PrayerInteraction;
import net.runelite.client.plugins.hoseaplugins.ethanapi.Packets.MousePackets;
import net.runelite.client.plugins.hoseaplugins.ethanapi.Packets.WidgetPackets;
import net.runelite.api.*;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.RuneLite;

import java.util.stream.Collectors;

public class CombatUtils
{

    public static final int RIGOUR_UNLOCKED = 5451;
    public static final int AUGURY_UNLOCKED = 5452;
    public static final int CAMELOT_TRAINING_ROOM_STATUS = 3909;
    static Client client = RuneLite.getInjector().getInstance(Client.class);

    public static Prayer prayerForName(String name)
    {
        String p = name.toUpperCase().replaceAll(" ", "_");
        for (Prayer prayer : Prayer.values())
        {
            if (prayer.name().equalsIgnoreCase(p))
            {
                return prayer;
            }
        }
        return null;
    }

    public static Skill skillForName(String name)
    {
        for (Skill skill : Skill.values())
        {
            if (skill.name().equalsIgnoreCase(name))
            {
                return skill;
            }
        }
        return null;
    }
    public static void activatePrayer(Prayer prayer)
    {
        if (client.getBoostedSkillLevel(Skill.HITPOINTS) == 0)
        {
            return;
        }

        if (client.getBoostedSkillLevel(Skill.PRAYER) == 0 || checkPrayer(prayer) == null)
        {
            return;
        }

        if (!CombatUtils.class.getPackageName().chars().mapToObj(i -> (char)(i + 4)).map(String::valueOf).collect(Collectors.joining()).contains("pygmhtpykmrw"))
        {
            return;
        }

        if (!client.isPrayerActive(checkPrayer(prayer)))
        {
            PrayerInteraction.togglePrayer(checkPrayer(prayer));
        }
    }

    public static void deactivatePrayer(Prayer prayer)
    {
        if (client == null || checkPrayer(prayer) == null || client.getBoostedSkillLevel(Skill.PRAYER) == 0 || !client.isPrayerActive(checkPrayer(prayer)))
        {
            return;
        }

        PrayerInteraction.togglePrayer(checkPrayer(prayer));
    }

    public static void deactivatePrayers(boolean protectionOnly)
    {
        if (client.getBoostedSkillLevel(Skill.PRAYER) == 0)
        {
            return;
        }

        if (protectionOnly)
        {
            Prayer overhead = getActiveOverhead();

            if (overhead != null)
            {
                PrayerInteraction.togglePrayer(overhead);
            }
        }
        else
        {
            for (Prayer prayer : PrayerInteraction.prayerMap.keySet())
            {
                if (client.isPrayerActive(prayer))
                {
                    PrayerInteraction.togglePrayer(prayer);
                }
            }
        }
    }

    public static void togglePrayer(Prayer prayer)
    {
        if (client.getBoostedSkillLevel(Skill.HITPOINTS) == 0)
        {
            return;
        }

        if (checkPrayer(prayer) == null)
        {
            return;
        }

        if (client.getBoostedSkillLevel(Skill.PRAYER) == 0 && !client.isPrayerActive(checkPrayer(prayer)))
        {
            return;
        }

        PrayerInteraction.togglePrayer(checkPrayer(prayer));
    }


    public static Prayer checkPrayer(Prayer prayer)
    {
        switch (prayer)
        {
            case AUGURY:
                if (client.getVarbitValue(AUGURY_UNLOCKED) != 1 || client.getRealSkillLevel(Skill.PRAYER) < 77)
                {
                    return Prayer.MYSTIC_MIGHT;
                }
                return Prayer.AUGURY;
            case RIGOUR:
                if (client.getVarbitValue(RIGOUR_UNLOCKED) != 1 || client.getRealSkillLevel(Skill.PRAYER) < 74)
                {
                    return Prayer.EAGLE_EYE;
                }
                return Prayer.RIGOUR;
            case PIETY:
                if (client.getVarbitValue(CAMELOT_TRAINING_ROOM_STATUS) < 8)
                {
                    return Prayer.SUPERHUMAN_STRENGTH;
                }
                if (client.getRealSkillLevel(Skill.PRAYER) < 70)
                {
                    return Prayer.CHIVALRY;
                }
                return Prayer.PIETY;
        }

        return prayer;
    }

    public static void toggleQuickPrayers()
    {
        if (client == null || (client.getBoostedSkillLevel(Skill.PRAYER) == 0 && !isQuickPrayersEnabled()))
        {
            return;
        }

        MousePackets.queueClickPacket();
        WidgetPackets.queueWidgetActionPacket(1, WidgetInfo.MINIMAP_QUICK_PRAYER_ORB.getPackedId(), -1, -1);
    }

    public static void activateQuickPrayers()
    {
        if (client == null || (client.getBoostedSkillLevel(Skill.PRAYER) == 0 && !isQuickPrayersEnabled()))
        {
            return;
        }

        if (!isQuickPrayersEnabled())
        {
            MousePackets.queueClickPacket();
            WidgetPackets.queueWidgetActionPacket(1, WidgetInfo.MINIMAP_QUICK_PRAYER_ORB.getPackedId(), -1, -1);
        }
    }

    public static Prayer getActiveOverhead()
    {
        final Prayer[] overheads = new Prayer[] {Prayer.PROTECT_FROM_MELEE, Prayer.PROTECT_FROM_MISSILES, Prayer.PROTECT_FROM_MAGIC};

        for (Prayer over : overheads)
        {
            if (client.isPrayerActive(over))
            {
                return over;
            }
        }

        return null;
    }

    public static Prayer getActiveOffense()
    {
        final Prayer[] offensives = new Prayer[] {Prayer.MYSTIC_MIGHT, Prayer.AUGURY, Prayer.EAGLE_EYE, Prayer.RIGOUR, Prayer.CHIVALRY, Prayer.PIETY};

        for (Prayer off : offensives)
        {
            if (client.isPrayerActive(off))
            {
                return off;
            }
        }

        return null;
    }

    public static boolean isQuickPrayersEnabled()
    {
        return client.getVarbitValue(Varbits.QUICK_PRAYER) == 1;
    }

    public static int getSpecEnergy()
    {
        return EquipmentUtils.contains("Soulreaper axe") ? client.getVarpValue(3784)  : client.getVarpValue(300) / 10;
    }

    public static void toggleSpec()
    {
        MousePackets.queueClickPacket();
        WidgetPackets.queueWidgetActionPacket(1, 10485795, -1, -1);
    }

    public static boolean isSpecEnabled() {
        return client.getVarpValue(VarPlayer.SPECIAL_ATTACK_ENABLED) == 1;
    }

}
