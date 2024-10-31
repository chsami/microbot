package net.runelite.client.plugins.microbot.storm.plugins.actionHotkey.enums;

public enum aRs2Storm implements Actionable {
    INV_GET_RANDOM("invGetRandom");

    private final String actions;
    aRs2Storm(String actions) {
        this.actions = actions;
    }

    @Override
    public String getAction() {
        return actions;
    }
}
