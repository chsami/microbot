package net.runelite.client.plugins.hoseaplugins.lucidgauntlet.overlay;

public enum WidgetInfoPlus
{
    PRAYER_THICK_SKIN(WidgetIDPlus.PRAYER_GROUP_ID, WidgetIDPlus.Prayer.THICK_SKIN),
    PRAYER_BURST_OF_STRENGTH(WidgetIDPlus.PRAYER_GROUP_ID, WidgetIDPlus.Prayer.BURST_OF_STRENGTH),
    PRAYER_CLARITY_OF_THOUGHT(WidgetIDPlus.PRAYER_GROUP_ID, WidgetIDPlus.Prayer.CLARITY_OF_THOUGHT),
    PRAYER_SHARP_EYE(WidgetIDPlus.PRAYER_GROUP_ID, WidgetIDPlus.Prayer.SHARP_EYE),
    PRAYER_MYSTIC_WILL(WidgetIDPlus.PRAYER_GROUP_ID, WidgetIDPlus.Prayer.MYSTIC_WILL),
    PRAYER_ROCK_SKIN(WidgetIDPlus.PRAYER_GROUP_ID, WidgetIDPlus.Prayer.ROCK_SKIN),
    PRAYER_SUPERHUMAN_STRENGTH(WidgetIDPlus.PRAYER_GROUP_ID, WidgetIDPlus.Prayer.SUPERHUMAN_STRENGTH),
    PRAYER_IMPROVED_REFLEXES(WidgetIDPlus.PRAYER_GROUP_ID, WidgetIDPlus.Prayer.IMPROVED_REFLEXES),
    PRAYER_RAPID_RESTORE(WidgetIDPlus.PRAYER_GROUP_ID, WidgetIDPlus.Prayer.RAPID_RESTORE),
    PRAYER_RAPID_HEAL(WidgetIDPlus.PRAYER_GROUP_ID, WidgetIDPlus.Prayer.RAPID_HEAL),
    PRAYER_PROTECT_ITEM(WidgetIDPlus.PRAYER_GROUP_ID, WidgetIDPlus.Prayer.PROTECT_ITEM),
    PRAYER_HAWK_EYE(WidgetIDPlus.PRAYER_GROUP_ID, WidgetIDPlus.Prayer.HAWK_EYE),
    PRAYER_MYSTIC_LORE(WidgetIDPlus.PRAYER_GROUP_ID, WidgetIDPlus.Prayer.MYSTIC_LORE),
    PRAYER_STEEL_SKIN(WidgetIDPlus.PRAYER_GROUP_ID, WidgetIDPlus.Prayer.STEEL_SKIN),
    PRAYER_ULTIMATE_STRENGTH(WidgetIDPlus.PRAYER_GROUP_ID, WidgetIDPlus.Prayer.ULTIMATE_STRENGTH),
    PRAYER_INCREDIBLE_REFLEXES(WidgetIDPlus.PRAYER_GROUP_ID, WidgetIDPlus.Prayer.INCREDIBLE_REFLEXES),
    PRAYER_PROTECT_FROM_MAGIC(WidgetIDPlus.PRAYER_GROUP_ID, WidgetIDPlus.Prayer.PROTECT_FROM_MAGIC),
    PRAYER_PROTECT_FROM_MISSILES(WidgetIDPlus.PRAYER_GROUP_ID, WidgetIDPlus.Prayer.PROTECT_FROM_MISSILES),
    PRAYER_PROTECT_FROM_MELEE(WidgetIDPlus.PRAYER_GROUP_ID, WidgetIDPlus.Prayer.PROTECT_FROM_MELEE),
    PRAYER_EAGLE_EYE(WidgetIDPlus.PRAYER_GROUP_ID, WidgetIDPlus.Prayer.EAGLE_EYE),
    PRAYER_MYSTIC_MIGHT(WidgetIDPlus.PRAYER_GROUP_ID, WidgetIDPlus.Prayer.MYSTIC_MIGHT),
    PRAYER_RETRIBUTION(WidgetIDPlus.PRAYER_GROUP_ID, WidgetIDPlus.Prayer.RETRIBUTION),
    PRAYER_REDEMPTION(WidgetIDPlus.PRAYER_GROUP_ID, WidgetIDPlus.Prayer.REDEMPTION),
    PRAYER_SMITE(WidgetIDPlus.PRAYER_GROUP_ID, WidgetIDPlus.Prayer.SMITE),
    PRAYER_PRESERVE(WidgetIDPlus.PRAYER_GROUP_ID, WidgetIDPlus.Prayer.PRESERVE),
    PRAYER_CHIVALRY(WidgetIDPlus.PRAYER_GROUP_ID, WidgetIDPlus.Prayer.CHIVALRY),
    PRAYER_PIETY(WidgetIDPlus.PRAYER_GROUP_ID, WidgetIDPlus.Prayer.PIETY),
    PRAYER_RIGOUR(WidgetIDPlus.PRAYER_GROUP_ID, WidgetIDPlus.Prayer.RIGOUR),
    PRAYER_AUGURY(WidgetIDPlus.PRAYER_GROUP_ID, WidgetIDPlus.Prayer.AUGURY),
    GAUNTLET_MAP(WidgetIDPlus.GAUNTLET_MAP_GROUP_ID, WidgetIDPlus.GauntletMap.CONTAINER),
    ;

    private final int groupId;
    private final int childId;

    WidgetInfoPlus(int groupId, int childId)
    {
        this.groupId = groupId;
        this.childId = childId;
    }

    /**
     * Gets the ID of the group-child pairing.
     *
     * @return the ID
     */
    public int getId()
    {
        return groupId << 16 | childId;
    }

    /**
     * Gets the group ID of the pair.
     *
     * @return the group ID
     */
    public int getGroupId()
    {
        return groupId;
    }

    /**
     * Gets the ID of the child in the group.
     *
     * @return the child ID
     */
    public int getChildId()
    {
        return childId;
    }

    /**
     * Gets the packed widget ID.
     *
     * @return the packed ID
     */
    public int getPackedId()
    {
        return groupId << 16 | childId;
    }

    /**
     * Utility method that converts an ID returned by {@link #getId()} back
     * to its group ID.
     *
     * @param id passed group-child ID
     * @return the group ID
     */
    public static int TO_GROUP(int id)
    {
        return id >>> 16;
    }

    /**
     * Utility method that converts an ID returned by {@link #getId()} back
     * to its child ID.
     *
     * @param id passed group-child ID
     * @return the child ID
     */
    public static int TO_CHILD(int id)
    {
        return id & 0xFFFF;
    }

    /**
     * Packs the group and child IDs into a single integer.
     *
     * @param groupId the group ID
     * @param childId the child ID
     * @return the packed ID
     */
    public static int PACK(int groupId, int childId)
    {
        return groupId << 16 | childId;
    }

}