package net.runelite.client.plugins.microbot.mahoganyhomez;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.runelite.api.ItemID;

@AllArgsConstructor
@Getter
public enum PlankEnum {
    NORMAL(ItemID.PLANK, 1,1),
    OAK(ItemID.OAK_PLANK, 2,20),
    TEAK(ItemID.TEAK_PLANK, 3,50),
    MAHOGANY(ItemID.MAHOGANY_PLANK, 4,70);

    private final int plankId;
    private final int chatOption;
    private final int levelRequirement;
}
