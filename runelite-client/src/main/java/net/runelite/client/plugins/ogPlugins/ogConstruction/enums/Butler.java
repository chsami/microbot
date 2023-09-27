package net.runelite.client.plugins.ogPlugins.ogConstruction.enums;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.runelite.api.NpcID;

@Getter
@RequiredArgsConstructor
public enum Butler {
    DEMON_BUTLER("Demon Butler", NpcID.DEMON_BUTLER, 24, 10),
    BUTLER("Butler",NpcID.BUTLER,20,20),
    COOK("Cook", NpcID.COOK,16,30),
    MAID("Maid", NpcID.MAID,10,50),
    RICK("Rick",NpcID.RICK,6,100);

    @Getter
    private final String name;
    @Getter
    private final int butlerID;
    @Getter
    private final int fetchCapacity;
    @Getter
    private final int ticksNeededToBank;


}
