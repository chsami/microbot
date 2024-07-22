package net.runelite.client.plugins.microbot.magetrainingarena.enums.staves;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.runelite.api.ItemID;

@Getter
@AllArgsConstructor
public enum FireStaves {
    STAFF_OF_FIRE(ItemID.STAFF_OF_FIRE),
    LAVA_BATTLESTAFF(ItemID.LAVA_BATTLESTAFF),
    MYSTIC_LAVA_STAFF(ItemID.MYSTIC_LAVA_STAFF);

    private final int itemId;
}
