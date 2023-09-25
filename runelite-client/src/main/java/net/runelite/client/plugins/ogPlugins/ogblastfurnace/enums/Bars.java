package net.runelite.client.plugins.ogPlugins.ogblastfurnace.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.runelite.api.ItemID;
import net.runelite.api.Varbits;

@Getter
@RequiredArgsConstructor
public enum Bars {
    BRONZE_BAR(
            ItemID.BRONZE_BAR,
            ItemID.TIN_ORE,
            1,
            ItemID.COPPER_ORE,
            1,
            Varbits.BLAST_FURNACE_BRONZE_BAR,
            Varbits.BLAST_FURNACE_TIN_ORE,
            Varbits.BLAST_FURNACE_COPPER_ORE
            ),
    IRON_BAR(
            ItemID.IRON_BAR,
            ItemID.IRON_ORE,
            1,
            null,
            null,
            Varbits.BLAST_FURNACE_IRON_BAR,
            Varbits.BLAST_FURNACE_IRON_ORE,
            null
            ),
    SILVER_BAR(
            ItemID.SILVER_BAR,
            ItemID.SILVER_ORE,
            1,
            null,
            null,
            Varbits.BLAST_FURNACE_SILVER_BAR,
            Varbits.BLAST_FURNACE_SILVER_ORE,
            null
    ),
    STEEL_BAR(
            ItemID.STEEL_BAR,
            ItemID.IRON_ORE,
            1,
            ItemID.COAL,
            1,
            Varbits.BLAST_FURNACE_STEEL_BAR,
            Varbits.BLAST_FURNACE_IRON_ORE,
            Varbits.BLAST_FURNACE_COAL
    ),
    GOLD_BAR(
            ItemID.GOLD_BAR,
            ItemID.GOLD_ORE,
            1,
            null,
            null,
            Varbits.BLAST_FURNACE_GOLD_BAR,
            Varbits.BLAST_FURNACE_GOLD_ORE,
            null
    ),
    MITHRIL_BAR(
            ItemID.MITHRIL_BAR,
            ItemID.MITHRIL_ORE,
            1,
            ItemID.COAL,
            2,
            Varbits.BLAST_FURNACE_MITHRIL_BAR,
            Varbits.BLAST_FURNACE_MITHRIL_ORE,
            Varbits.BLAST_FURNACE_COAL
    ),
    ADAMANTITE_BAR(
            ItemID.ADAMANTITE_BAR,
            ItemID.ADAMANTITE_ORE,
            1,
            ItemID.COAL,
            6,
            Varbits.BLAST_FURNACE_ADAMANTITE_BAR,
            Varbits.BLAST_FURNACE_ADAMANTITE_ORE,
            Varbits.BLAST_FURNACE_COAL
    ),
    RUNITE_BAR(
            ItemID.RUNITE_BAR,
            ItemID.RUNITE_ORE,
            1,
            ItemID.COAL,
            8,
            Varbits.BLAST_FURNACE_RUNITE_BAR,
            Varbits.BLAST_FURNACE_RUNITE_ORE,
            Varbits.BLAST_FURNACE_COAL
    );

    private final int barID;
    private final int PrimaryOre;
    private final int PrimaryOreNeeded;
    private final Integer SecondaryOre;
    private final Integer SecondaryOreNeeded;
    private final int BFBarID;
    private final int BFPrimaryOreID;
    private final Integer BFSecondaryOreID;

    public int getBarID() {return barID;}
    public int getPrimaryOre() {return PrimaryOre;}
    public int getPrimaryOreNeeded() {return PrimaryOreNeeded;}
    public Integer getSecondaryOre() {return SecondaryOre;}
    public Integer getSecondaryOreNeeded() {return SecondaryOreNeeded;}
    public int getBFBarID() {return BFBarID;}
    public int getBFPrimaryOreID() {return BFPrimaryOreID;}
    public Integer getBFSecondaryOreID() {return BFSecondaryOreID;}


}
