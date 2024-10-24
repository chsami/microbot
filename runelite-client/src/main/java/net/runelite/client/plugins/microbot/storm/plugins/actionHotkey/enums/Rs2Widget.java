package net.runelite.client.plugins.microbot.storm.plugins.actionHotkey.enums;

public enum Rs2Widget {
    GET_WIDGET("getWidget");

    private final String actions;
    Rs2Widget(String actions) {
        this.actions = actions;
    }

    public String getAction() {
        return actions;
    }
}
