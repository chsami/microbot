package net.runelite.client.plugins.hoseaplugins.lucidcustomprayers;

import lombok.Getter;
import net.runelite.api.Prayer;

public class ExportableConfig
{
    @Getter
    private boolean[] prayerEnabled;

    @Getter
    private String[] prayerIds;

    @Getter
    private String[] prayerDelays;

    @Getter
    private Prayer[] prayChoice;

    @Getter
    private EventType[] eventType;

    @Getter
    private boolean[] toggle;

    @Getter
    private boolean[] ignoreNonTargetEvents;


    public ExportableConfig()
    {
        this.prayerEnabled = new boolean[10];
        this.prayerIds = new String[10];
        this.prayerDelays = new String[10];
        this.prayChoice = new Prayer[10];
        this.eventType = new EventType[10];
        this.toggle = new boolean[10];
        this.ignoreNonTargetEvents = new boolean[10];
    }

    public void setPrayer(int index, final boolean prayerEnabled, final String prayerIds, final String prayerDelays, final Prayer prayChoice,
                           final EventType eventType1, final boolean toggle1, final boolean ignoreNonTargetEvents)
    {
        this.prayerEnabled[index] = prayerEnabled;
        this.prayerIds[index] = prayerIds;
        this.prayerDelays[index] = prayerDelays;
        this.prayChoice[index] = prayChoice;
        this.eventType[index] = eventType1;
        this.toggle[index] = toggle1;
        this.ignoreNonTargetEvents[index] = ignoreNonTargetEvents;
    }
}
