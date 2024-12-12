package net.runelite.client.plugins.microbot.util.grounditem;

/**
 *      * @param minValue           The minimum value of the items to be looted.
 *      * @param maxValue           The maximum value of the items to be looted.
 *      * @param range              The range within which the items to be looted are located.
 *      * @param minItems           The minimum number of items to be looted.
 *      * @param minInvSlots        The minimum number of inventory slots to have open.
 *      * @param delayedLooting     A boolean indicating whether looting should be delayed.
 *      * @param antiLureProtection A boolean indicating whether anti-lure protection should be enabled.
 */
public class LootingParameters {

    private int minValue, maxValue, range, minItems, minQuantity, minInvSlots;
    private boolean delayedLooting, lootOwnItems;
    private String[] names;
    private int[] ids;

    /**
     * This constructor is used to create a new LootingParameters object.
     * It sets the minimum value, maximum value, range, minimum items, delayed looting, and anti-lure protection.
     *
     * @param minValue           The minimum value of the items to be looted.
     * @param maxValue           The maximum value of the items to be looted.
     * @param range              The range within which the items to be looted are located.
     * @param minItems           The minimum number of items to be looted.
     * @param minInvSlots        The minimum number of inventory slots to have open.
     * @param delayedLooting     A boolean indicating whether looting should be delayed.
     * @param lootOwnItems A boolean indicating whether anti-lure protection should be enabled.
     */
    @Deprecated(since = "1.6.4.3 - please use new LootingParameters.Builder()", forRemoval = true)
    public LootingParameters(int minValue, int maxValue, int range, int minItems, int minInvSlots, boolean delayedLooting, boolean lootOwnItems) {
        setValues(minValue, maxValue, range, minItems, 1, minInvSlots, delayedLooting, lootOwnItems, null);
    }

    /**
     * This constructor is used to create a new LootingParameters object.
     * It sets the range, minimum items, minimum quantity, delayed looting, anti-lure protection, and names of the items to be looted.
     *
     * @param range              The range within which the items to be looted are located.
     * @param minItems           The minimum number of items to be looted.
     * @param minQuantity        The minimum quantity of items to be looted.
     * @param minInvSlots        The minimum number of inventory slots to have open.
     * @param delayedLooting     A boolean indicating whether looting should be delayed.
     * @param lootOwnItems A boolean indicating whether anti-lure protection should be enabled.
     * @param names              The names of the items to be looted.
     */
    @Deprecated(since = "1.6.4.3 - please use new LootingParameters.Builder()", forRemoval = true)
    public LootingParameters(int range, int minItems, int minQuantity, int minInvSlots, boolean delayedLooting, boolean lootOwnItems, String... names) {
        setValues(0, 0, range, minItems, minQuantity, minInvSlots, delayedLooting, lootOwnItems, names);
    }

    private void setValues(int minValue, int maxValue, int range, int minItems, int minQuantity, int minInvSlots, boolean delayedLooting, boolean lootOwnItems, String[] names) {
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.range = range;
        this.minItems = minItems;
        this.minQuantity = minQuantity;
        this.minInvSlots = minInvSlots;
        this.delayedLooting = delayedLooting;
        this.lootOwnItems = lootOwnItems;
        this.names = names;
    }

    private LootingParameters(Builder builder) {
        this.minValue = builder.minValue;
        this.maxValue = builder.maxValue;
        this.range = builder.range;
        this.minItems = builder.minItems;
        this.minQuantity = builder.minQuantity;
        this.minInvSlots = builder.minInvSlots;
        this.delayedLooting = builder.delayedLooting;
        this.lootOwnItems = builder.lootOwnItems;
        this.names = builder.names;
        this.ids = builder.ids;
    }

    public static class Builder {
        private int minValue = 0, maxValue = Integer.MAX_VALUE, range = 20, minItems = 0, minQuantity = 0, minInvSlots = 0;
        private boolean delayedLooting = false, lootOwnItems = false;
        private String[] names = null;
        private int[] ids = null;

        public Builder setMinValue(int minValue) {
            this.minValue = minValue;
            return this;
        }

        public Builder setMaxValue(int maxValue) {
            this.maxValue = maxValue;
            return this;
        }

        public Builder setRange(int range) {
            this.range = range;
            return this;
        }

        public Builder setMinItems(int minItems) {
            this.minItems = minItems;
            return this;
        }

        public Builder setMinQuantity(int minQuantity) {
            this.minQuantity = minQuantity;
            return this;
        }

        public Builder setMinInvSlots(int minInvSlots) {
            this.minInvSlots = minInvSlots;
            return this;
        }

        public Builder setDelayedLooting(boolean delayedLooting) {
            this.delayedLooting = delayedLooting;
            return this;
        }

        public Builder setLootOwnItems(boolean lootOwnItems) {
            this.lootOwnItems = lootOwnItems;
            return this;
        }

        public Builder setNames(String... names) {
            this.names = names;
            return this;
        }

        public Builder setIds(int... ids) {
            this.ids = ids;
            return this;
        }

        public LootingParameters build() {
            return new LootingParameters(this);
        }
    }

    // Getters
    public int getMinValue() {
        return minValue;
    }

    public int getMaxValue() {
        return maxValue;
    }

    public int getRange() {
        return range;
    }

    public int getMinItems() {
        return minItems;
    }

    public int getMinQuantity() {
        return minQuantity;
    }

    public int getMinInvSlots() {
        return minInvSlots;
    }

    public boolean isDelayedLooting() {
        return delayedLooting;
    }

    @Deprecated(since = "1.6.4.3", forRemoval = true)
    public boolean isAntiLureProtection() {
        return lootOwnItems;
    }

    public String[] getNames() {
        return names;
    }

    public int[] getIds() {
        return ids;
    }
}
