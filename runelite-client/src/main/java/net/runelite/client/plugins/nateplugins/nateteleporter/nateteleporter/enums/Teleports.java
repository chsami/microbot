package net.runelite.client.plugins.nateplugins.nateteleporter.nateteleporter.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.runelite.client.plugins.microbot.util.magic.Teleport;

@Getter
@RequiredArgsConstructor
public enum Teleports {
    FALADOR("Falador teleport", Teleport.FALADOR),
    LUMBRIDGE("Lumbridge teleport", Teleport.LUMBRIDGE),
    VARROK("Varrock teleport", Teleport.VARROCK),
    CAMELOT("Camelot teleport", Teleport.CAMELOT),
    ARDOUGNE("Ardougne teleport", Teleport.ARDOUGNE);

    private final String name;
    private final Teleport teleport;

    @Override
    public String toString() {
        return name;
    }
}
