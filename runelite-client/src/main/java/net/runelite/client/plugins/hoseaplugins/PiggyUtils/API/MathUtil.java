package net.runelite.client.plugins.hoseaplugins.PiggyUtils.API;

import net.runelite.api.coords.WorldPoint;

import java.util.concurrent.ThreadLocalRandom;

public class MathUtil {

    public static int random(int min, int max){
        return ThreadLocalRandom.current().nextInt(min, max + 1);
    }

    /**
     * Returns a random tile +/- rX and +/- rY from the given tile
     * @param wp
     * @param rX
     * @param rY
     * @return
     */
    public static WorldPoint randomizeTile(WorldPoint wp, int rX, int rY) {
        return wp.dx(random(-rX, rX + 1)).dy(random(-rY, rY + 1));
    }

    public static WorldPoint randomizeTile2(WorldPoint wp, int rX, int rY) {
        return wp.dx(random(rX, rX + 1)).dy(random(rY, rY + 1));
    }

}
