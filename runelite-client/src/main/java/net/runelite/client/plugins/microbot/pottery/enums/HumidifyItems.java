package net.runelite.client.plugins.microbot.pottery.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.runelite.api.ItemID;

@Getter
@RequiredArgsConstructor
public enum HumidifyItems {
    BOWL (
            "bowl", ItemID.BOWL,
            "bowl of water", ItemID.BOWL_OF_WATER
    ),
    BUCKET (
            "bucket", ItemID.BUCKET,
            "bucket of water", ItemID.BUCKET_OF_WATER
    ),
    JUG (
            "jug", ItemID.JUG,
            "jug of water", ItemID.JUG_OF_WATER
    ),
    VIAL (
            "vial", ItemID.VIAL,
            "vial of water", ItemID.VIAL_OF_WATER
    );

    private final String itemName;
    private final int itemID;
    private final String filledItemName;
    private final int filledItemID;
}