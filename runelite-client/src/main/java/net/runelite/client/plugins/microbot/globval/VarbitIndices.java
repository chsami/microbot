package net.runelite.client.plugins.microbot.globval;

public class VarbitIndices {
    public static final int RUNE_POUCH_FIRST_SLOT_RUNE = 29; // Permitted values: enum of allowed runes
    public static final int SPELL_TO_AUTOCAST = 276; // Permitted values: WidgetIndices.DynamicComponents.AutocastSpells
    public static final int EQUIPPED_WEAPON_TYPE = 357;
    public static final int PRISON_PETE_CORRECT_KEYS_GIVEN = 1547;
    public static final int RUNE_POUCH_SECOND_SLOT_RUNE = 1622; // Permitted values: enum of allowed runes
    public static final int RUNE_POUCH_THIRD_SLOT_RUNE = 1623; // Permitted values: enum of allowed runes
    public static final int RUNE_POUCH_FIRST_SLOT_AMOUNT = 1624;
    public static final int RUNE_POUCH_SECOND_SLOT_AMOUNT = 1625;
    public static final int RUNE_POUCH_THIRD_SLOT_AMOUNT = 1626;
    public static final int MOTHERLODE_MINE_Z_PLANE = 2086;
    /**
     * Permitted values: 0,3,4,5,6
     * Note: 0 = not started, 3 = started, 6 = finished
     */
    public static final int QUEST_GOBLIN_DIPLOMACY = 2378;
    /**
     * Permitted values: 0 - 3
     * Note: 0 = not started, 1 = started, 3 = finished
     */
    public static final int QUEST_DEMON_SLAYER = 2561;
    public static final int DEFENSIVE_AUTOCAST = 2668;
    /**
     * Permitted values: 0,5,10,15,20,25,30,35,40,45,50,55,60,65,70,75,80,85,90,95,100,105,110,111,115,120,125,130,135
     * Note: 0 = not started, 5 = started, 135 = finished
     */
    public static final int QUEST_MISTHALIN_MYSTERY = 3468;
    public static final int TOGGLE_BANK_ALWAYS_SET_PLACEHOLDERS = 3755;
    public static final int TOGGLE_BANK_WITHDRAW_MODE = 3958;
    public static final int TOGGLE_BANK_REARRANGE_MODE = 3959;
    /**
     * Permitted values: 0 - (2^31)-1
     * Note: last integer value set when withdrawing with withdraw X option
     */
    public static final int BANK_DEFAULT_WITHDRAW_X_QUANTITY = 3960;
    public static final int FAIRY_RING_LEFT_DIAL_ADCB = 3985;
    public static final int FAIRY_RING_MIDDLE_DIAL_ILJK = 3986;
    public static final int FAIRY_RING_RIGHT_DIAL_PSRQ = 3987;
    public static final int ACTIVE_PRAYER = 4101; // Permitted values: Bitmap of enum definition 860
    public static final int SELECTED_QUICK_PRAYERS = 4102; // Permitted values: Bitmap of selected prayers?
    public static final int QUICK_PRAYER = 4103;
    public static final int PRAYER_THICK_SKIN = 4104;
    public static final int PRAYER_BURST_OF_STRENGTH = 4105;
    public static final int PRAYER_CLARITY_OF_THOUGHT = 4106;
    public static final int PRAYER_ROCK_SKIN = 4107;
    public static final int PRAYER_SUPERHUMAN_STRENGTH = 4108;
    public static final int PRAYER_IMPROVED_REFLEXES = 4109;
    public static final int PRAYER_RAPID_RESTORE = 4110;
    public static final int PRAYER_RAPID_HEAL = 4111;
    public static final int PRAYER_PROTECT_ITEM = 4112;
    public static final int PRAYER_STEEL_SKIN = 4113;
    public static final int PRAYER_ULTIMATE_STRENGTH = 4114;
    public static final int PRAYER_INCREDIBLE_REFLEXES = 4115;
    public static final int PRAYER_PROTECT_FROM_MAGIC = 4116;
    public static final int PRAYER_PROTECT_FROM_MISSILES = 4117;
    public static final int PRAYER_PROTECT_FROM_MELEE = 4118;
    public static final int PRAYER_RETRIBUTION = 4119;
    public static final int PRAYER_REDEMPTION = 4120;
    public static final int PRAYER_SMITE = 4121;
    public static final int PRAYER_SHARP_EYE = 4122;
    public static final int PRAYER_MYSTIC_WILL = 4123;
    public static final int PRAYER_HAWK_EYE = 4124;
    public static final int PRAYER_MYSTIC_LORE = 4125;
    public static final int PRAYER_EAGLE_EYE = 4126;
    public static final int PRAYER_MYSTIC_MIGHT = 4127;
    public static final int PRAYER_CHIVALRY = 4128;
    public static final int PRAYER_PIETY = 4129;
    public static final int PRAYER_PROTECT_FROM_MAGIC_OVERHEAD = 4130;
    public static final int PRAYER_PROTECT_FROM_MISSILES_OVERHEAD = 4131;
    public static final int PRAYER_PROTECT_FROM_MELEE_OVERHEAD = 4132;
    public static final int WILDERNESS_ZONE_ENTRY_COUNT = 4149;  // Note: number of times char has entered wildy in total
    public static final int BANK_ACTIVE_TAB = 4150;
    public static final int BANK_TAB_DISPLAY = 4170;
    public static final int BANK_TAB_ONE_COUNT = 4171;
    public static final int BANK_TAB_TWO_COUNT = 4172;
    public static final int BANK_TAB_THREE_COUNT = 4173;
    public static final int BANK_TAB_FOUR_COUNT = 4174;
    public static final int BANK_TAB_FIVE_COUNT = 4175;
    public static final int BANK_TAB_SIX_COUNT = 4176;
    public static final int BANK_TAB_SEVEN_COUNT = 4177;
    public static final int BANK_TAB_EIGHT_COUNT = 4178;
    public static final int BANK_TAB_NINE_COUNT = 4179;
    public static final int GE_OFFER_QUANTITY = 4396; // Note: count of items offered
    public static final int GE_OFFER_TYPE = 4397;
    public static final int GE_OFFER_PRICE_PER_ITEM = 4398; // Note: price per each item
    public static final int DEPOSIT_BOX_DEPOSIT_AMOUNT = 4430;
    public static final int GE_OFFER_SLOT_OPEN = 4439;
    public static final int MULTICOMBAT_ZONE = 4605;
    public static final int TOGGLE_XP_DROPS = 4702;
    public static final int TOGGLE_BANK_INCINERATOR = 5102;
    public static final int TOGGLE_BANK_DEPOSIT_WORN_ITEMS = 5364;
    public static final int FAIRY_RING_LAST_DESTINATION = 5374;
    public static final int MOTHERLODE_MINE_SACK = 5558;
    public static final int NEW_PLAYER_NAME_STATUS = 5605; // 1 is 'unavailable' or 'not checked', 2 is 'checking', 4 is 'available', 5 is 'accept'
    public static final int NAME_SELECTED_STATUS = 5607; // 0 is 'name not selected' 1 is 'name set'. changing to 1 usually clears 437 and 436 too
    public static final int WILDNERESS_ZONE = 5963;
    public static final int CHATBOX_INTERFACE_SIZE = 5983; // Note: manipulates the size of Widget 162.559
    /**
     * Permitted values: 0,5,10,15,20,25,30,35,40,45,49,50,52,55,60
     * Note: 0 = not started, 1 = started, 60 = finished
     */
    public static final int QUEST_CORSAIR_CURSE = 6071;
    public static final int CABIN_BOY_COLIN = 6072;
    public static final int GNOCCI_COOK = 6073;
    public static final int ARSEN_THIEF = 6074;
    public static final int ITHOI_NAVIGATOR = 6075;
    public static final int QUESTS_COMPLETED_COUNT = 6347;
    public static final int TOGGLE_MOUSE_WHEEL_ZOOM = 6357;
    public static final int BANK_WITHDRAW_QUANTITY = 6590;
    public static final int TOGGLE_MAGIC_FILTER_COMBAT_SPELLS = 6605;
    public static final int TOGGLE_MAGIC_FILTER_UTILITY_SPELLS = 6606;
    public static final int TOGGLE_MAGIC_FILTER_NO_LEVEL_SPELLS = 6607;
    public static final int TOGGLE_MAGIC_FILTER_NO_RUNES_SPELLS = 6608;
    public static final int TOGGLE_MAGIC_FILTER_TELEPORT_SPELLS = 6609;
    /**
     * Permitted values: 0 - 8
     * Note: 0 = not started, 1 = started, 8 = finished
     */
    public static final int QUEST_X_MARKS_THE_SPOT = 8063;
    public static final int REQUEST_PLAYER_MODEL_SELECTION = 8119; // 0 is 'do nothing' 1 is 'show selection'
    public static final int PVP_SPEC_ORB = 8121;
    public static final int TAB_QUEST_LIST_SUBTAB_INDEX = 8168;
    public static final int TOGGLE_BANK_DEPOSIT_INVENTORY = 8352;
    public static final int BACKGROUND_TICK_INDEX = 8354; // auto-incrementing digit that increases every 100 ticks
    public static final int TAB_SETTINGS_SUBTAB_INDEX = 9683;
    public static final int TAB_ACC_MANAGEMENT_SUBTAB_INDEX = 10060;
    public static final int TOGGLE_BANK_INVENTORY_OPTIONS = 10079;
    public static final int BANK_TUTORIAL_PROGRESS = 10308;
    public static final int TOGGLE_BANK_TUTORIAL = 10336;
    /**
     * Permitted values: 0,5,7,10,15,20,25,30,35,40,120
     * Note: 0 = not started, 5 = started, 120 = finished
     */
    public static final int QUEST_BELOW_ICE_MOUNTAIN = 12063;
    public static final int MARLEY = 12064;
    public static final int CHECKAL = 12065;
    public static final int BURNTOF = 12066;
    public static final int RUINS_OF_CAMDOZAAL = 12067;
    public static final int RAMARNO = 12068;
    public static final int TOGGLE_MAGIC_FILTER_LACKING_REQUIREMENTS_SPELLS = 12137;
    public static final int TOGGLE_ROOFS = 12378;
    public static final int TAB_CHAT_CHANNEL_SUBTAB_INDEX = 13071;
}