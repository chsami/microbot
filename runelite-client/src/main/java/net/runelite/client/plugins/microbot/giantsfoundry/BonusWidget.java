package net.runelite.client.plugins.microbot.giantsfoundry;

import net.runelite.api.widgets.Widget;
import net.runelite.client.plugins.microbot.util.widget.Rs2Widget;

public class BonusWidget {
    private static final int BONUS_WIDGET = 49414148;
    private static final int BONUS_COLOR = 0xfcd703;

    static boolean isActive() {
        Widget bonusWidget = Rs2Widget.getWidget(BONUS_WIDGET);
        return bonusWidget != null
                && bonusWidget.getChildren() != null
                && bonusWidget.getChildren().length != 0
                && bonusWidget.getChild(0).getTextColor() == BONUS_COLOR;
    }
}
