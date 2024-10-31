package net.runelite.client.plugins.microbot.storm.plugins.actionHotkey.enums;

public enum aOther implements Actionable {
    PRINTLN("println");

    private final String actions;
    aOther(String actions) {
        this.actions = actions;
    }

    @Override
    public String getAction() {
        return actions;
    }
}
