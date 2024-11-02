package net.runelite.client.plugins.microbot.shortestpath.pathfinder;

import lombok.Getter;

import java.util.BitSet;
import java.util.Locale;

import static net.runelite.api.Constants.REGION_SIZE;

public class FlagMap {
    private static final byte FLAG_COUNT = 2;
    private final BitSet flags;
    @Getter
    private final byte planeCount;
    private final int minX;
    private final int minY;

    public FlagMap(int minX, int minY, byte planeCount) {
        this.minX = minX;
        this.minY = minY;
        this.planeCount = planeCount;
        flags = new BitSet(REGION_SIZE * REGION_SIZE * planeCount * FLAG_COUNT);
    }

    public FlagMap(int minX, int minY, byte[] bytes) {
        this.minX = minX;
        this.minY = minY;
        flags = BitSet.valueOf(bytes);
        int scale = REGION_SIZE * REGION_SIZE * FLAG_COUNT;
        this.planeCount = (byte) ((flags.size() + scale - 1) / scale);
    }

    public byte[] toBytes() {
        return flags.toByteArray();
    }

    public boolean get(int x, int y, int z, int flag) {
        if (x < minX || x >= (minX + REGION_SIZE) || y < minY || y >= (minY + REGION_SIZE) || z < 0 || z >= planeCount) {
            return false;
        }

        return flags.get(index(x, y, z, flag));
    }

    public void set(int x, int y, int z, int flag, boolean value) {
        flags.set(index(x, y, z, flag), value);
    }

    private int index(int x, int y, int z, int flag) {
        if (x < minX || x >= (minX + REGION_SIZE) || y < minY || y >= (minY + REGION_SIZE) || z < 0 || z >= planeCount || flag < 0 || flag >= FLAG_COUNT) {
            throw new IndexOutOfBoundsException(
                    String.format(Locale.ENGLISH, "[%d,%d,%d,%d] when extents are [>=%d,>=%d,>=%d,>=%d] - [<=%d,<=%d,<%d,<%d]",
                            x, y, z, flag,
                            minX, minY, 0, 0,
                            minX + REGION_SIZE - 1, minY + REGION_SIZE - 1, planeCount, FLAG_COUNT
                    )
            );
        }

        return (z * REGION_SIZE * REGION_SIZE + (y - minY) * REGION_SIZE + (x - minX)) * FLAG_COUNT + flag;
    }
}
