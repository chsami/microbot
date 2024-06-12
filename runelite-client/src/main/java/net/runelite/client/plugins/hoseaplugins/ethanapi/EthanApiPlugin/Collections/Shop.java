package net.runelite.client.plugins.hoseaplugins.ethanapi.EthanApiPlugin.Collections;

import net.runelite.client.plugins.hoseaplugins.ethanapi.EthanApiPlugin.Collections.query.ItemQuery;
import net.runelite.api.Client;
import net.runelite.api.widgets.Widget;
import net.runelite.client.RuneLite;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Shop {
    private static final int SHOP_ITEM_CONTAINER_PACKED_ID = 19660816;
    static Client client = RuneLite.getInjector().getInstance(Client.class);
    static List<Widget> shopItems = new ArrayList<>();
    static int lastUpdateTick = 0;

    public static ItemQuery search() {
        if (lastUpdateTick < client.getTickCount()) {
            shopItems =
                    Arrays.stream(client.getWidget(SHOP_ITEM_CONTAINER_PACKED_ID).getDynamicChildren()).filter(Objects::nonNull).filter(x -> x.getItemId() != 6512 && x.getItemId() != -1).collect(Collectors.toList());
            lastUpdateTick = client.getTickCount();
        }
        return new ItemQuery(shopItems.stream().filter(Objects::nonNull).collect(Collectors.toList()));
    }

//    @Subscribe
//    public void onWidgetLoaded(WidgetLoaded e)
//    {
//        if (e.getGroupId() == WidgetID.SHOP_GROUP_ID)
//        {
//            try
//            {
//                shopItems =
//                        Arrays.stream(client.getWidget(SHOP_ITEM_CONTAINER_PACKED_ID).getDynamicChildren()).filter(Objects::nonNull).filter(x -> x.getItemId() != 6512 && x.getItemId() != -1).collect(Collectors.toList());
//            }
//            catch (NullPointerException err)
//            {
//                shopItems.clear();
//            }
//        }
//    }
//
//    @Subscribe
//    public void onItemContainerChanged(ItemContainerChanged e)
//    {
//        if (client.getWidget(SHOP_ITEM_CONTAINER_PACKED_ID) == null)
//        {
//            shopItems.clear();
//            return;
//        }
//        try
//        {
//            shopItems =
//                    Arrays.stream(client.getWidget(SHOP_ITEM_CONTAINER_PACKED_ID).getDynamicChildren()).filter(Objects::nonNull).filter(x -> x.getItemId() != 6512 && x.getItemId() != -1).collect(Collectors.toList());
//        }
//        catch (NullPointerException err)
//        {
//            shopItems.clear();
//        }
//    }
}