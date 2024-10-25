package net.runelite.client.plugins.microbot.storm.plugins.actionHotkey.enums;

public enum sRs2Inventory implements Actionable {
    INV_INTERACT("invInteract"),
    WIELD("wield"),
    WEAR("wield"),
    EQUIP("equip"),
    USE_RANDOM("useRandom"),
    USE_LAST("useLast"),
    DROP_ALL("dropAll"),
    DROP_ITEM("dropItem");

    private final String actions;
    sRs2Inventory(String actions) {
        this.actions = actions;
    }

    @Override
    public String getAction() {
        return actions;
    }
}
