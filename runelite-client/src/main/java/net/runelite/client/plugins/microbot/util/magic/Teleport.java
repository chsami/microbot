package net.runelite.client.plugins.microbot.util.magic;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.runelite.api.coords.WorldPoint;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;

@Getter
@RequiredArgsConstructor
public enum Teleport {
    VARROCK(25,
            new Pair[] {Pair.of("law rune", 1), Pair.of("air rune", 3), Pair.of("fire rune", 1)},
            new WorldPoint(3213, 3425, 0), "<col=00ff00>Varrock Teleport</col>", "Varrock teleport"),
    LUMBRIDGE(31,
                    new Pair[] {Pair.of("law rune", 1), Pair.of("air rune", 3), Pair.of("earth rune", 1)},
            new WorldPoint(3222, 3218, 0), "<col=00ff00>Lumbridge Teleport</col>", "Lumbridge teleport");

    private final int level;
    private final Pair[] itemsRequired;
    private final WorldPoint destination;
    private final String widgetText;
    private final String tabletName;
}
