package net.runelite.client.plugins.hoseaplugins.RooftopAgility;

import lombok.Getter;

import static net.runelite.api.NullObjectID.NULL_36241;
import static net.runelite.api.NullObjectID.NULL_36242;
import static net.runelite.api.NullObjectID.NULL_36243;
import static net.runelite.api.NullObjectID.NULL_36244;
import static net.runelite.api.NullObjectID.NULL_36245;
import static net.runelite.api.NullObjectID.NULL_36246;

public enum Portals {
    PORTAL_ONE(NULL_36241, 1),
    PORTAL_TWO(NULL_36242, 2),
    PORTAL_THREE(NULL_36243, 3),
    PORTAL_FOUR(NULL_36244, 4),
    PORTAL_FIVE(NULL_36245, 5),
    PORTAL_SIX(NULL_36246, 6);

    //getters
    @Getter
    private final int portalID;

    @Getter
    private final int varbitValue;

    //constructor
    Portals(final int portalID, final int varbitValue) {
        this.portalID = portalID;
        this.varbitValue = varbitValue;
    }

    //function that we will use in our plugin. We provide the current Portal varbit value and it returns the correlating Portal. i.e. a varbit value of 2 would return portal 2
    public static Portals getPortal(int varbitValue) {
        for (Portals portal : values()) {
            if (portal.getVarbitValue() == varbitValue) {
                return portal;
            }
        }
        return null;
    }
}
