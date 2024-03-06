package net.runelite.client.plugins.microbot.vorkath;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.val;

@Getter
@RequiredArgsConstructor
public enum RANGE_POTION {
    DIVINE_RANGING_POTION("Ranging Potion"),
    DIVINE_BASTION_POTION("Bastion Potion");

    private final String potionName;

    @Override
    public String toString() { return potionName; }
}
