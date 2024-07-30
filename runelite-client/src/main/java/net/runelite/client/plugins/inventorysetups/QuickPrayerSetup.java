package net.runelite.client.plugins.inventorysetups;

import net.runelite.client.plugins.microbot.util.prayer.Rs2PrayerEnum;

public class QuickPrayerSetup
{
    private Rs2PrayerEnum prayer;

    public QuickPrayerSetup(Rs2PrayerEnum prayer)
    {
        this.prayer = prayer;
    }

    public Rs2PrayerEnum getPrayer()
    {
        return prayer;
    }

    public void setPrayer(Rs2PrayerEnum prayer)
    {
        this.prayer = prayer;
    }
}