package net.runelite.client.plugins.microbot.util.map;

import net.runelite.client.plugins.microbot.Microbot;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Rs2WorldMap {
    public static boolean inRegion(int regionID) {
        List<Integer> mapRegions = Arrays.stream(Microbot.getClient().getMapRegions()).boxed().collect(Collectors.toList());
        return mapRegions.contains(regionID);
    }
}
