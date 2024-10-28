package net.runelite.client.plugins.microbot.storm.plugins.actionHotkey.enums;

public enum Other implements Actionable {
    PRINTLN("println");

    private final String actions;
    Other(String actions) {
        this.actions = actions;
    }

    @Override
    public String getAction() {
        return actions;
    }
}
