package net.runelite.client.plugins.microbot.storm.plugins.actionHotkey.enums;

public enum Rs2Walker {
    WALK_FAST_CANVAS("walkFastCanvas");

    private final String actions;
    Rs2Walker(String actions) {
        this.actions = actions;
    }

    public String getAction() {
        return actions;
    }
}
