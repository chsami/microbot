package net.runelite.client.plugins.microbot.storm.plugins.actionHotkey.enums;

public enum aRs2Widget implements Actionable {
    GET_WIDGET("getWidget");

    private final String actions;
    aRs2Widget(String actions) {
        this.actions = actions;
    }

    @Override
    public String getAction() {
        return actions;
    }
}
