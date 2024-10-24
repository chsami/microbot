package net.runelite.client.plugins.microbot.storm.plugins.actionHotkey.enums;
//TODO add sections to the config to select categories, e.g. Rs2Npc, Rs2Player, Rs2Inventory, etc. then also have selections for those specific categories
public enum Rs2Bank {
    OPEN_BANK("openBank"),
    WITHDRAW_ALL("withdrawAll"),
    DEPOSIT_ALL("depositAll"),
    WITHDRAW_ONE("withdrawOne");

    private final String actions;
    Rs2Bank(String actions) {
        this.actions = actions;
    }

    public String getAction() {
        return actions;
    }
}
