package net.runelite.client.plugins.microbot.storm.plugins.actionHotkey.enums;

public enum sRs2Storm implements Actionable {
    INV_GET_RANDOM("invGetRandom");

    private final String actions;
    sRs2Storm(String actions) {
        this.actions = actions;
    }

    @Override
    public String getAction() {
        return actions;
    }
}
