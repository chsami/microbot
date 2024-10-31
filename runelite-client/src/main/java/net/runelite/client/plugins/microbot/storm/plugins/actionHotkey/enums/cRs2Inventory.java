package net.runelite.client.plugins.microbot.storm.plugins.actionHotkey.enums;

public enum cRs2Inventory implements Actionable {
    HAS_ITEM("hasItem");

    private final String actions;
    cRs2Inventory(String actions) {
        this.actions = actions;
    }

    @Override
    public String getAction() {
        return actions;
    }
}
