package net.runelite.client.plugins.microbot.util.bank.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.runelite.api.coords.WorldPoint;

@Getter
@RequiredArgsConstructor
public enum BankLocation {
    AL_KHARID("AL-Kharid", new WorldPoint(3270, 3166, 0)),
    EAST_ARDOUGNE("East ardougne", new WorldPoint(2652, 3283, 0)),
    GRAND_EXCHANGE("grand exchange", new WorldPoint(3166, 3485, 0)),
    DRAYNOR_VILLAGE("Draynor village", new WorldPoint(3093, 3245, 0)),
    FALADOR_EAST("Falador east", new WorldPoint(3014, 3355, 0)),
    FALADOR_WEST("Falador west", new WorldPoint(2945, 3369, 0)),

    VARROCK_WEST("Varrock west", new WorldPoint(3183, 3441, 0)),

    VARROCK_EAST("Varrock east", new WorldPoint(3253, 3422, 0));

    private final String name;
    private final WorldPoint worldPoint;

    @Override
    public String toString() {
        return name;
    }
}
