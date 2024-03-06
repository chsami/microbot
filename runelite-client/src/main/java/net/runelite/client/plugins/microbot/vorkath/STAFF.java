package net.runelite.client.plugins.microbot.vorkath;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum STAFF {
    CAST("Cast"),
    SLAYER_STAFF("Slayer's Staff"),
    SLAYER_STAFF_E("Slayer's Staff (e)");

    private final String staffName;

    @Override
    public String toString() {
        return staffName;
    }

}
