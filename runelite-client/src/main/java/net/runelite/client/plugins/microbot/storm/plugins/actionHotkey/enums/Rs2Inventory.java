package net.runelite.client.plugins.microbot.storm.plugins.actionHotkey.enums;
//TODO add sections to the config to select categories, e.g. Rs2Npc, Rs2Player, Rs2Inventory, etc. then also have selections for those specific categories
public enum Rs2Inventory {
    INV_INTERACT("invInteract");

    private final String actions;
    Rs2Inventory(String actions) {
        this.actions = actions;
    }

    public String getAction() {
        return actions;
    }
}
