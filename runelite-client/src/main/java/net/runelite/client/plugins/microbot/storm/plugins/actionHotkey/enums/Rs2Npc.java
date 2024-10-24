package net.runelite.client.plugins.microbot.storm.plugins.actionHotkey.enums;

public enum Rs2Npc {
    ATTACK("attack"),
    NPC_INTERACT("npcinteract");

    private final String actions;
    Rs2Npc(String actions) {
        this.actions = actions;
    }

    public String getAction() {
        return actions;
    }
}
