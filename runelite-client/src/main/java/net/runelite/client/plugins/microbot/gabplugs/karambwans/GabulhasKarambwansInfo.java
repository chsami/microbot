package net.runelite.client.plugins.microbot.gabplugs.karambwans;

import lombok.Getter;
import net.runelite.api.ItemID;

public class GabulhasKarambwansInfo {
    public static states botStatus;


    public enum states {
        FISHING,
        WALKING_TO_RING_TO_BANK,
        WALKING_TO_BANK,
        BANKING,
        WALKING_TO_RING_TO_FISH,


        }

}
