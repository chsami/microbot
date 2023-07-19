package net.runelite.client.plugins.nateteleporter.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Teleports {
    FALADOR("falador teleport", 37),
    LUMBRIDGE("lumbridge teleport", 31),
    VARROK("varrok teleport", 25);

    private final String name;
    private final int magicLevel;

    @Override
    public String toString() {
        return name;
    }
}
