package net.runelite.client.plugins.hoseaplugins.lucidhotkeys2;

public enum Action
{
    NONE(0, 1, "nothing"),
    PRINT_VARIABLE(1, 2, "print"),
    SET_DELAY(2, 2, "setDelay"),
    RESET_DELAY(3, 1, "resetDelay"),
    CLOSE_BANK(4, 1, "bankClose"),
    WALK_RELATIVE_TO_SELF(5, 3, "walkRel"),
    WALK_ABSOLUTE_LOCATION(6, 3, "walkAbs"),
    INTERACT_NEAREST_NPC(7, 3, "iNpc"),
    INTERACT_NEAREST_OBJECT(8, 3, "iObj"),
    INTERACT_INVENTORY_ITEM(9, 3, "iItem"),
    RELOAD_VARS(13, 1, "relVars"),
    INTERACT_NEAREST_TILE_ITEM(15, 3, "iTItem"),
    SET_TICK_METRONOME_MAX_VALUE(17, 2, "setMetMax"),
    SET_TICK_METRONOME_VALUE(18, 2, "setMet"),
    SET_TICK_METRONOME_TO_MAX(19, 1, "metMax"),

    INTERACT_PLAYER(21, 3, "iPlay"),

    INTERACT_LAST_PLAYER_YOU_TARGETED(22, 2, "iLPYT"),
    INTERACT_LAST_PLAYER_TARGETED_YOU(23, 2, "iLPTY"),
    INTERACT_LAST_NPC_YOU_TARGETED(24, 2, "iLNYT"),
    INTERACT_LAST_NPC_TARGETED_YOU(25, 2, "iLNTY"),

    ACTIVATE_PRAYER(26, 2, "prayAct"),
    TOGGLE_PRAYER(27, 2, "prayTog"),
    TOGGLE_SPEC(28, 1, "specTog"),
    WALK_SCENE_LOCATION(29, 3, "walkS"),

    INTERACT_INVENTORY_SLOT(30, 3, "iIS"),

    ADD_TILE_MARKER_WORLD_POINT(31, 3, "addTMWP"),
    ADD_TILE_MARKER_WORLD_POINT_WITH_TEXT(32, 4, "addTMWPT"),
    REMOVE_TILE_MARKER_WORLD_POINT(33, 3, "remTMWP"),
    ADD_TILE_MARKER_REGION_POINT(34, 4, "addTMRP"),
    ADD_TILE_MARKER_REGION_POINT_WITH_TEXT(35, 5, "addTMRPT"),
    REMOVE_TILE_MARKER_REGION_POINT(36, 4, "remTMRP"),
    REMOVE_ALL_REGION_POINT_TILE_MARKERS(37, 1, "remAllTMRP"),
    REMOVE_ALL_WORLD_POINT_TILE_MARKERS(38, 1, "remAllTMWP"),
    ADD_TILE_MARKER_FOR_NPC_NAME(39, 2, "addTMN"),
    REMOVE_TILE_MARKER_FOR_NPC_NAME(40, 2, "remTMN"),
    ADD_TILE_MARKER_FOR_PLAYER_NAME(41, 2, "addTMP"),
    REMOVE_TILE_MARKER_FOR_PLAYER_NAME(42, 2, "remTMP"),

    REMOVE_ALL_NPC_MARKERS(43, 1, "remAllTMN"),
    REMOVE_ALL_PLAYER_MARKERS(44, 1, "remAllTMP"),
    WALK_REGION_LOCATION(45, 4, "walkR"),

    ITEM_ON_ITEM(46, 3, "itemOnItem"),
    ITEM_ON_NPC(48, 3, "itemOnNpc"),

    ITEM_ON_OBJECT(50, 3, "itemOnObj"),

    WIDGET_ACTION(52, 4, "widgetAct"),
    WIDGET_SUBCHILD_ACTION(53, 5, "widgetSAct"),
    WIDGET_RESUME_PAUSE(54, 4, "widgetRP"),
    EVALUATE_HOTKEY_IN_X_TICKS(56, 3, "evalHotkeyIn"),
    DEACTIVATE_PRAYER(57, 2, "prayDeact"),

    EVAL_HOTKEY_IMMEDIATELY(58, 2, "evalHotkeyNow"),
    SET_NPC_FILTER(59, 9, "setNpcFilter"),
    RESET_NPC_FILTER(60, 1, "resetNpcFilter"),
    SET_PLAYER_FILTER(61, 10, "setPlayerFilter"),
    RESET_PLAYER_FILTER(62, 1, "resetPlayerFilter"),
    SET_OBJECT_FILTER(63, 5, "setObjectFilter"),
    RESET_OBJECT_FILTER(64, 1, "resetObjectFilter"),
    SET_ITEM_FILTER(65, 7, "setItemFilter"),
    RESET_ITEM_FILTER(66, 1, "resetItemFilter"),
    SET_TILE_ITEM_FILTER(67, 6, "setTItemFilter"),
    RESET_TILE_ITEM_FILTER(68, 1, "resetTItemFilter"),

    CAST_SPELL_ON_NPC(69, 3, "spellOnNpc"),
    CAST_SPELL(70, 2, "spell"),
    CAST_SPELL_ALT(71, 2, "spellAlt"),
    CAST_SPELL_ON_PLAYER(72, 3, "spellOnPlayer"),
    CAST_SPELL_ON_OBJECT(73, 3, "spellOnObject"),
    CAST_SPELL_ON_TILE_ITEM(74, 3, "spellOnTItem"),
    CAST_SPELL_ON_ITEM(75, 3, "spellOnItem"),
    WIDGET_CC_OP_1(76, 3, "ccOp1"),
    WIDGET_CC_OP_2(77, 3, "ccOp2"),
    INVOKE_MENU_ACTION(78, 5, "menuAction"),
    SEND_CLIENTSCRIPT(79, 2, "cs2"),
    SET_TILE_FILTER(80, 9, "setTileFilter"),
    RESET_TILE_FILTER(81, 1, "resetTileFilter"),

    SET_AUTO_COMBAT(82, 8, "setAutoCombat"),
    SET_PLUGIN_CONFIG(83, 4, "setPluginConfig"),
    CLAIM_CLOSEST_CANNON(84, 1, "claimClosestCannon"),
    UNCLAIM_CANNON(85, 1, "unclaimCannon")
    ;

    int id;
    int paramsNeeded;

    String nickname;

    Action(int id, int paramsNeeded, String nickname)
    {
        this.id = id;
        this.paramsNeeded = paramsNeeded;
        this.nickname = nickname;
    }

    public static Action forId(int id)
    {
        for (Action action : values())
        {
            if (action.getId() == id)
            {
                return action;
            }
        }
        return NONE;
    }

    public static Action forNickname(String nickname)
    {
        for (Action action : values())
        {
            if (action.getNickname().equals(nickname))
            {
                return action;
            }
        }
        return NONE;
    }


    public int getId()
    {
        return id;
    }

    public int getParamsNeeded()
    {
        return paramsNeeded;
    }

    public String getNickname()
    {
        return nickname;
    }

}
