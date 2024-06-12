package net.runelite.client.plugins.hoseaplugins.AutoRuneDragon.data;

public enum SubState {
    // Idle,
    IDLE,
    // Banking
    FIND_BANK,
    WITHDRAW,
    DEPOSIT,
    // Traveling
    TELE_POH,
    TELE_EDGE,
    TELE_LITH,
    MOVE_DOWNSTAIRS,
    OPEN_DOOR,
    WALK_DOOR,
    ENTER_LAIR,
    DRINK_POOL,
    // Consume,
    ACTIVATE_PRAYER,
    DEACTIVATE_PRAYER,
    DRINK_POTIONS,
    EAT_FOOD,
    // Combat,
    ATTACK_DRAGON,
    LOOT,
    EQUP_GEAR,
    USE_SPECIAL,
}
