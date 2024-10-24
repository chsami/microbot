package net.runelite.client.plugins.microbot.storm.plugins.actionHotkey.enums;

public enum Other {
    PRINTLN("println");

    private final String actions;
    Other(String actions) {
        this.actions = actions;
    }

    public String getAction() {
        return actions;
    }
}
