package net.runelite.client.plugins.hoseaplugins.AutoCombat;

import lombok.Getter;

public enum SlayerNpc {
    ROCKSLUG("Rockslug", "Bag of salt", 1, "",  ""),
    DESERT_LIZARD("Desert lizard", "Ice cooler", 1, "", ""),
    GARGOYLE("Gargoyle", "Rock hammer", 1, "", ""),
    MUTATED_ZYGOMITE("Mutated zygomite", "Fungicide spray", 1, "Pick", "Mutated Fungi"),
    ANCIENT_ZYGOMITE("Ancient Zygomite", "Fungicide spray", 1, "Pick", "Ancient Fungi");

    @Getter
    private final String npcName;
    @Getter
    private final String itemName;
    @Getter
    private final int useHp;
    @Getter
    private final String disturbAction;
    @Getter
    private final String undisturbedName;

    SlayerNpc(String npcName, String itemName, int useHp, String disturbAction, String undisturbedName) {
        this.npcName = npcName;
        this.itemName = itemName;
        this.useHp = useHp;
        this.disturbAction = disturbAction;
        this.undisturbedName = undisturbedName;
    }
}





