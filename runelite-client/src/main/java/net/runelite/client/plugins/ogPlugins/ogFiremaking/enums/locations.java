package net.runelite.client.plugins.ogPlugins.ogFiremaking.enums;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.runelite.api.NpcID;
import net.runelite.api.coords.WorldPoint;

@Getter
@RequiredArgsConstructor
public enum locations {

    VARROCK_WEST(
            new WorldPoint[]{new WorldPoint(3200,3432,0), new WorldPoint(3209,3430,0),new WorldPoint(3209,3429,0),new WorldPoint(3209,3428,0),new WorldPoint(3200,3431,0)},
                new int[]{NpcID.BANKER_2897, NpcID.BANKER_2898},
                    null),
    GRAND_EXCHANGE(
            new WorldPoint[]{new WorldPoint(3196,3491,0), new WorldPoint(3196,3490,0),new WorldPoint(3196,3489,0),new WorldPoint(3196,3488,0)},
                new int[]{NpcID.BANKER_1634, NpcID.BANKER_3089},
                    null);



    private final WorldPoint[] firemakingStartingSpots;
    private final int[] bankers;
    private final WorldPoint[] returnPoints;

    public WorldPoint[] getFiremakingStartingSpots() {
        return firemakingStartingSpots;
    }

    public int[] getBankers() {
        return bankers;
    }

    public WorldPoint[] getReturnPoints() {return returnPoints;}
}
