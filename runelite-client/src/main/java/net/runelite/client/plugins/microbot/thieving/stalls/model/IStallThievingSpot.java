package net.runelite.client.plugins.microbot.thieving.stalls.model;

public interface IStallThievingSpot {
    void thieve();
    void bank();

    Integer[] getItemIdsToDrop();
}
