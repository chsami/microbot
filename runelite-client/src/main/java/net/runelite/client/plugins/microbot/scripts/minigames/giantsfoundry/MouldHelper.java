package net.runelite.client.plugins.microbot.scripts.minigames.giantsfoundry;

import net.runelite.api.widgets.Widget;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.scripts.minigames.giantsfoundry.enums.CommissionType;
import net.runelite.client.plugins.microbot.scripts.minigames.giantsfoundry.enums.Mould;
import net.runelite.client.plugins.microbot.util.widget.Rs2Widget;

import java.util.LinkedHashMap;
import java.util.Map;

import static net.runelite.client.plugins.microbot.util.Global.sleep;

public class MouldHelper {
    static final int MOULD_LIST_PARENT = 47054857;
    static final int DRAW_MOULD_LIST_SCRIPT = 6093;
    static final int REDRAW_MOULD_LIST_SCRIPT = 6095;
    static final int RESET_MOULD_SCRIPT = 6108;
    public static final int SELECT_MOULD_SCRIPT = 6098;
    public static final int SWORD_TYPE_1_VARBIT = 13907; // 4=Broad
    public static final int SWORD_TYPE_2_VARBIT = 13908; // 3=Flat
    private static final int DISABLED_TEXT_COLOR = 0x9f9f9f;
    private static final int GREEN = 0xdc10d;


    public static void selectBest() {
        if (Microbot.getClient().isClientThread()) return;
        Widget parent = Rs2Widget.getWidget(MOULD_LIST_PARENT);
        if (parent == null || parent.getChildren() == null) {
            return;
        }

        Map<Mould, Widget> mouldToChild = getOptions(parent.getChildren());

        int bestScore = -1;
        Widget bestWidget = null;
        CommissionType type1 = CommissionType.forVarbit(Microbot.getVarbitValue(SWORD_TYPE_1_VARBIT));
        CommissionType type2 = CommissionType.forVarbit(Microbot.getVarbitValue(SWORD_TYPE_2_VARBIT));
        for (Map.Entry<Mould, Widget> entry : mouldToChild.entrySet()) {
            Mould mould = entry.getKey();
            int score = mould.getScore(type1, type2);
            if (score > bestScore) {
                bestScore = score;
                bestWidget = entry.getValue();
            }
        }
        if (bestWidget != null) {
            bestWidget.setTextColor(GREEN);
        }
        Microbot.getMouse().click(bestWidget.getBounds());
        sleep(1000, 2000);
    }

    private static Map<Mould, Widget> getOptions(Widget[] children) {
        Map<Mould, Widget> mouldToChild = new LinkedHashMap<>();
        for (int i = 2; i < children.length; i += 17) {
            Widget child = children[i];
            Mould mould = Mould.forName(child.getText());
            if (mould != null && child.getTextColor() != DISABLED_TEXT_COLOR) {
                mouldToChild.put(mould, child);
            }
        }
        return mouldToChild;
    }
}
