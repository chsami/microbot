package net.runelite.client.plugins.microbot.vorkath;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.val;

@Getter
@RequiredArgsConstructor
public enum PRAYER_POTION {
    PRAYER("prayer potion"),
    SUPER_RESTORE("Super restore potion");

    private final String potionName;

    @Override
    public String toString() { return potionName; }
}
