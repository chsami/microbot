package net.runelite.client.plugins.hoseaplugins.strategyexample;

import net.runelite.client.plugins.hoseaplugins.ethanapi.PacketUtils.WidgetInfoExtended;
import lombok.Getter;

public enum SmithingItem {
    DART_TIP(1, 10, WidgetInfoExtended.SMITHING_ANVIL_DART_TIPS),
    NAILS(1, 15, WidgetInfoExtended.SMITHING_ANVIL_NAILS),
    UNFINISHED_BOLTS(1, 10, WidgetInfoExtended.SMITHING_ANVIL_BOLTS),
    ARROW_TIPS(1, 15, WidgetInfoExtended.SMITHING_ANVIL_ARROW_HEADS),
    DAGGGER(1, 1, WidgetInfoExtended.SMITHING_ANVIL_DAGGER),
    SCIMITAR(2, 1, WidgetInfoExtended.SMITHING_ANVIL_SCIMITAR),
    CHAIN_BODY(3, 1, WidgetInfoExtended.SMITHING_ANVIL_CHAIN_BODY),
    KITE_SHIELD(3, 1, WidgetInfoExtended.SMITHING_ANVIL_KITE_SHIELD),
    TWO_H_SWORD(3, 1, WidgetInfoExtended.SMITHING_ANVIL_TWO_H_SWORD),
    PLATE_LEGS(3, 1, WidgetInfoExtended.SMITHING_ANVIL_PLATE_LEGS),
    PLATE_SKIRT(3, 1, WidgetInfoExtended.SMITHING_ANVIL_PLATE_SKIRT),
    PLATE_BODY(5, 1, WidgetInfoExtended.SMITHING_ANVIL_PLATE_BODY);

    @Getter
    private final int barsRequired;
    @Getter
    private final int madePerBar;
    @Getter
    private final WidgetInfoExtended widgetInfo;

    SmithingItem(int barsRequired, int madePerBar, WidgetInfoExtended widgetInfo) {
        this.barsRequired = barsRequired;
        this.madePerBar = madePerBar;
        this.widgetInfo = widgetInfo;
    }
}
