package net.runelite.client.plugins.microbot.storm.plugins.actionHotkey.enums;
//TODO add sections to the config to select categories, e.g. Rs2Npc, Rs2Player, Rs2Inventory, etc. then also have selections for those specific categories
public enum Actions {
    OPEN_BANK("openBank"),//Rs2Bank
    WITHDRAW_ALL("withdrawAll"),//Rs2Bank
    DEPOSIT_ALL("depositAll"),//Rs2Bank
    WITHDRAW_ONE("withdrawOne"),//Rs2Bank
    INV_INTERACT("invInteract"),//Rs2Inventory
    OBJ_INTERACT("objinteract"),//Rs2GameObject
    NPC_INTERACT("npcinteract"),//Rs2Npc
    ATTACK("attack"),//Rs2Npc
    GET_WIDGET("getWidget"),//Rs2Widget
    PRINTLN("println"),//Other
    WALK_FAST_CANVAS("walkFastCanvas");//Rs2Walker

    private final String actions;
    Actions(String actions) {
        this.actions = actions;
    }

    public String getAction() {
        return actions;
    }
}
/*Rs2Bank,
Rs2Inventory
Rs2GameObject
Rs2Npc
Rs2Widget
Rs2Walker
Other
 */