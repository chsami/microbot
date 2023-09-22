package net.runelite.client.plugins.ogPlugins.ogPrayer.enums;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.runelite.api.ObjectID;

@Getter
@RequiredArgsConstructor
public enum Locations {
    GILDED_ALTAR("Altar", new int[]{ObjectID.ALTAR_40878,ObjectID.ALTAR_13197,ObjectID.ALTAR_13198}),
    CHAOS_ALTAR("Chaos Altar",new int[]{ObjectID.CHAOS_ALTAR_411});

    private final String name;
    private final int[] alterID;
}
