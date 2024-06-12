package net.runelite.client.plugins.hoseaplugins.AutoRifts.data;

import net.runelite.api.ItemID;
import net.runelite.api.ObjectID;

public class CellMapper {
    public static int GetCellTier(int cellID) {
        switch (cellID) {
            case ItemID.WEAK_CELL:
                return 1;
            case ItemID.MEDIUM_CELL:
                return 2;
            case ItemID.STRONG_CELL:
                return 3;
            case ItemID.OVERCHARGED_CELL:
                return 4;
            default:
                return -1;
        }
    }

    public static int GetShieldTier(int shieldID) {
        switch (shieldID) {
            case ObjectID.INACTIVE_CELL_TILE:
                return 0;
            case ObjectID.WEAK_CELL_TILE:
                return 1;
            case ObjectID.MEDIUM_CELL_TILE:
                return 2;
            case ObjectID.STRONG_CELL_TILE:
                return 3;
            case ObjectID.OVERPOWERED_CELL_TILE:
                return 4;
            default:
                return -1;
        }
    }
}
