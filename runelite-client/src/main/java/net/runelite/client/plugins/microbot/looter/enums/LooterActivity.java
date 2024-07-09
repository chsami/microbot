package net.runelite.client.plugins.microbot.looter.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum LooterActivity {
    DEFAULT("Default"),
    FLAX("Flax Picking"),
    NATURE_RUNE_CHEST("Nature Rune Chest");

    private final String name;
}
