package net.runelite.client.plugins.microbot.globval;

public class VarpIndices {
    /**
     * Permitted values: 0 - 6, 8
     * Note: 0 = not started, 1 = started, 8 = finished
     */
    public static final int QUEST_CLOCK_TOWER = 10;
    /**
     * Permitted values: 0 - ??
     * Note: 0 = not-envenomed,
     */
    public static final int PLAYER_VENOM_STATE = 12;
    /**
     * Permitted values: 0 - 1
     * Note: 0 = manual, 1 = automatic
     */
    public static final int TOGGLE_MUSIC_PLAY_OPTION = 18;
    /**
     * Permitted values: 0 - 1
     * Note: 0 = disabled, 1 = enabled
     */
    public static final int TOGGLE_LOOP_MUSIC = 19;
    /**
     * Permitted values: 0 - 2
     * Note: 0 = not started, 1 = started, 2 = finished
     */
    public static final int QUEST_COOK_ASSISTANT = 29;
    /**
     * Permitted values: sequence 0:10:80
     * Note: 0 = not started, 10 = started, 80 = finished
     */
    public static final int QUEST_MONKS_FRIEND = 30;
    /**
     * Permitted values: 0,10,100
     * Note: 0 = not started, 10 = started, 100 = finished
     */
    public static final int QUEST_DORICS_QUEST = 31;
    /**
     * Permitted values: 0 - 3
     * Note: 0 = not started, 1 = started, 3 = finished
     */
    public static final int QUEST_EARNEST_THE_CHICKEN = 32;
    /**
     * Permitted values: 0 - 4
     * Note: 0 - 3 attack styles, 4 both defensive autocast/autocast
     */
    public static final int COMBAT_STYLE = 43;
    /**
     * Permitted values: 0 - 3
     * Note: 0 = not started, 1 = started, 3 = finished
     */
    public static final int QUEST_SHEEP_HERDER = 60;
    /**
     * Permitted values: 0 - 6
     * Note: 0 = not started, 1 = started, 6 = finished
     */
    public static final int QUEST_RUNE_MYSTERIES = 63;
    /**
     * Permitted values: 0 - 8, 10
     * Note: 0 = not started, 1 = started, 10 = finished
     */
    public static final int QUEST_WATERFALL_QUEST = 65;
    // TODO: recheck this
    public static final int PLAYER_POISONED_STATE = 83;
    /**
     * Permitted values: 0 - 279
     * Note: total quest points count
     */
    public static final int QUEST_POINTS_COUNT = 101;
    /**
     * 	Poison immune = -1
     * 	Poisoned = 0 - 1000000
     * 	Envenomed = this >= 1000000
     * 	Poison damage = ceil(this / 5.0f)
     * 	Venom damage = (this - 999997) * 2
     */
    public static final int POISON = 102;
    /**
     * Permitted values: 0 - 5
     * Note: 0 = not started, 1 = started, 5 = finished
     */
    public static final int QUEST_RESTLESS_GHOST = 107;
    public static final int SELECTED_AUTOCAST_SPELL = 108;
    /**
     * Permitted values: 0 - 1
     * Note: 0 = item, 1 = note
     */
    public static final int TOGGLE_BANK_WITHDRAW_MODE = 115;
    public static final int TYPE_SHOP = 118;
    /**
     * Permitted values: 0 - 7
     * Note: 0 = not started, 1 = started, 7 = finished
     */
    public static final int QUEST_THE_KNIGHTS_SWORD = 122;
    /**
     * Permitted values: 0 - 4
     * Note: 0 = not started, 1 = started, 4 = finished
     */
    public static final int QUEST_BLACK_KNIGHTS_FORTRESS = 130;
    /**
     * Permitted values: 0,10,20,30,40,50,60,100
     * Note: 0 = not started, 10 = started, 100 = finished
     */
    public static final int QUEST_ROMEO_AND_JULIET = 144;
    /**
     * Permitted values: 0 - 2
     * Note: 0 = not started, 1 = started, 2 = finished
     */
    public static final int QUEST_IMP_CATCHER = 160;
    /**
     * Permitted values: 0 - 100
     * Note: 0 = minBrightness, 100 = maxBrightness
     */
    public static final int ADJUST_SCREEN_BRIGHTNESS = 166;
    /**
     * Permitted values: 0 - 100
     * Note: 0 = noVolume, 100 = maxVolume
     */
    public static final int ADJUST_MUSIC_VOLUME = 168;
    /**
     * Permitted values: 0 - 100
     * Note: 0 = noVolume, 100 = maxVolume
     */
    public static final int ADJUST_SOUND_EFFECT_VOLUME = 169;
    public static final int MOUSE_BUTTONS = 170;
    public static final int TOGGLE_CHAT_EFFECTS = 171;
    /**
     * Permitted values: 0 - 1
     * Note: 0 = enabled, 1 = disabled
     * Note: Its always by default enabled.
     */
    public static final int TOGGLE_AUTO_RETALIATE = 172;
    /**
     * Permitted values: 0 - 1
     * Note: 0 = disabled, 1 = enabled
     */
    public static final int TOGGLE_RUN = 173;
    /**
     * Permitted values: 0 - 3
     * Note: 0 = not started, 1 = started, 3 = finished
     */
    public static final int QUEST_VAMPYRE_SLAYER = 178;
    /**
     * Permitted values: 0 - 21
     * Note: 0 = not started, 1 = started, 21 = finished
     */
    public static final int QUEST_SHEEP_SHEARER = 179;
    /**
     * Permitted values: 0,10,20,30,31,40,50,100,110
     * Note: 0 = not started, 1 = started, 110 = finished
     */
    public static final int QUEST_PRINCE_ALI_RESCUE = 273;
    public static final int SPLIT_PRIVATE_CHAT = 287;
    /**
     * Permitted values: 0 - 100
     * Note: Holds the current special attack energy in percents.
     */
    public static final int SPECIAL_ATTACK_ENERGY = 300;
    /**
     * Permitted values: 0 - 1
     * Note: 0 = disabled, 1 = enabled
     */
    public static final int TOGGLE_SPECIAL_ATTACK = 301;
    /**
     * Permitted values: 0 - 1
     * Note: 0 = swap, 1 = insert
     */
    public static final int TOGGLE_BANK_REARRANGE_MODE = 304;
    /**
     * Permitted values: 0 - 1
     * Note: 0 = disabled, 1 = enabled
     */
    public static final int TOGGLE_ACCEPT_AID = 427;
    /**
     * Permitted values: 0 - ??
     * Note: 0 = standard
     */
    public static final int ACTIVE_MAGIC_BOOK = 439;
    public static final int BLAST_FURNACE_COOLING_STATE = 543;
    public static final int BLAST_FURNACE_STORED_ORE = 547;
    public static final int BLAST_FURNACE_COFFER_COINS = 795;
    /**
     * Permitted values: 0 - 100
     * Note: 0 = noVolume, 100 = maxVolume
     */
    public static final int ADJUST_AREA_SOUND_EFFECT_VOLUME = 872;
    /**
     * Permitted values: 0 - 3
     * Note: 0 = No bar processing.
     * Note: 1 = Ores are being processed on conveyor belt.
     * Note: 2 = Bars are cooling down.
     * Note: 3 = Bars can be collected.
     */
    public static final int BLAST_FURNACE_BAR_DISPENSER = 936;
    public static final int SWAP_QUEST_DIARY = 1002;
    public static final int SPRITE_UNSELECTED_VALUE = 1077;
    public static final int SPRITE_SELECTED_VALUE = 1079;
}
