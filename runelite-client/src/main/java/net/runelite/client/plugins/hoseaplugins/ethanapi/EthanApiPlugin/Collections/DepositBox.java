package net.runelite.client.plugins.hoseaplugins.ethanapi.EthanApiPlugin.Collections;

import net.runelite.client.plugins.hoseaplugins.ethanapi.EthanApiPlugin.Collections.query.ItemQuery;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.RuneLite;
import net.runelite.client.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class DepositBox {
    static Client client = RuneLite.getInjector().getInstance(Client.class);
    static List<Widget> depositBoxItems = new ArrayList<>();
    static int lastUpdateTick = 0;

    public static ItemQuery search() {
        if (lastUpdateTick < client.getTickCount()) {
            DepositBox.depositBoxItems =
                    Arrays.stream(client.getWidget(WidgetInfo.DEPOSIT_BOX_INVENTORY_ITEMS_CONTAINER).getDynamicChildren()).filter(Objects::nonNull).filter(x -> x.getItemId() != 6512 && x.getItemId() != -1).collect(Collectors.toList());
            lastUpdateTick = client.getTickCount();
        }
        return new ItemQuery(depositBoxItems);
    }

//    @Subscribe
//    public void onWidgetLoaded(WidgetLoaded e) {
//        if (e.getGroupId() == WidgetID.DEPOSIT_BOX_GROUP_ID) {
//            try {
//                DepositBox.depositBoxItems =
//                        Arrays.stream(client.getWidget(WidgetInfo.DEPOSIT_BOX_INVENTORY_ITEMS_CONTAINER).getDynamicChildren()).filter(Objects::nonNull).filter(x -> x.getItemId() != 6512 && x.getItemId() != -1).collect(Collectors.toList());
//            } catch (NullPointerException err) {
//                DepositBox.depositBoxItems.clear();
//            }
//        }
//    }

//    @Subscribe
//    public void onItemContainerChanged(ItemContainerChanged e) {
//        if (e.getContainerId() == 93) {
//            if (client.getWidget(WidgetInfo.DEPOSIT_BOX_INVENTORY_ITEMS_CONTAINER) == null) {
//                DepositBox.depositBoxItems.clear();
//                return;
//            }
//            try {
//                DepositBox.depositBoxItems =
//                        Arrays.stream(client.getWidget(WidgetInfo.DEPOSIT_BOX_INVENTORY_ITEMS_CONTAINER).getDynamicChildren()).filter(Objects::nonNull).filter(x -> x.getItemId() != 6512 && x.getItemId() != -1).collect(Collectors.toList());
//                return;
//            } catch (NullPointerException err) {
//                DepositBox.depositBoxItems.clear();
//                return;
//            }
//        }
//    }

    @Subscribe
    public void onGameStateChanged(GameStateChanged gameStateChanged) {
        if (gameStateChanged.getGameState() == GameState.HOPPING || gameStateChanged.getGameState() == GameState.LOGIN_SCREEN || gameStateChanged.getGameState() == GameState.CONNECTION_LOST) {
            DepositBox.depositBoxItems.clear();
        }
    }
}
