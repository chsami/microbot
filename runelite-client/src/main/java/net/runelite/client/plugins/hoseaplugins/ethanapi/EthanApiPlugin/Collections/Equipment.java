package net.runelite.client.plugins.hoseaplugins.ethanapi.EthanApiPlugin.Collections;

import net.runelite.client.plugins.hoseaplugins.ethanapi.EthanApiPlugin.Collections.query.EquipmentItemQuery;
import net.runelite.api.Client;
import net.runelite.api.InventoryID;
import net.runelite.api.Item;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.RuneLite;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Equipment {
    static Client client = RuneLite.getInjector().getInstance(Client.class);
    static List<EquipmentItemWidget> equipment = new ArrayList<>();
    static HashMap<Integer, Integer> equipmentSlotWidgetMapping = new HashMap<>();
    static HashMap<Integer, Integer> mappingToIterableIntegers = new HashMap<>();
    static int lastUpdateTick = 0;

    static {
        equipmentSlotWidgetMapping.put(0, 15);
        equipmentSlotWidgetMapping.put(1, 16);
        equipmentSlotWidgetMapping.put(2, 17);
        equipmentSlotWidgetMapping.put(3, 18);
        equipmentSlotWidgetMapping.put(4, 19);
        equipmentSlotWidgetMapping.put(5, 20);
        equipmentSlotWidgetMapping.put(7, 21);
        equipmentSlotWidgetMapping.put(9, 22);
        equipmentSlotWidgetMapping.put(10, 23);
        equipmentSlotWidgetMapping.put(12, 24);
        equipmentSlotWidgetMapping.put(13, 25);


        mappingToIterableIntegers.put(0, 0);
        mappingToIterableIntegers.put(1, 1);
        mappingToIterableIntegers.put(2, 2);
        mappingToIterableIntegers.put(3, 3);
        mappingToIterableIntegers.put(4, 4);
        mappingToIterableIntegers.put(5, 5);
        mappingToIterableIntegers.put(6, 7);
        mappingToIterableIntegers.put(7, 9);
        mappingToIterableIntegers.put(8, 10);
        mappingToIterableIntegers.put(9, 12);
        mappingToIterableIntegers.put(10, 13);
    }

    public static EquipmentItemQuery search() {
        if (lastUpdateTick < client.getTickCount()) {
            int x = 25362447;
            for (int i = 0; i < 11; i++) {
                client.runScript(545, (x + i), mappingToIterableIntegers.get(i), 1, 1, 2);
            }
            equipment.clear();
            int i = -1;
            if(client.getItemContainer(InventoryID.EQUIPMENT.getId()) == null){
                return new EquipmentItemQuery(equipment);
            }
            for (Item item : client.getItemContainer(InventoryID.EQUIPMENT.getId()).getItems()) {
                i++;
                if (item == null) {
                    continue;
                }
                if (item.getId() == 6512 || item.getId() == -1) {
                    continue;
                }
                Widget w = client.getWidget(WidgetInfo.EQUIPMENT.getGroupId(), equipmentSlotWidgetMapping.get(i));
                if (w == null || w.getActions() == null) {
                    continue;
                }
                equipment.add(new EquipmentItemWidget(w.getName(), item.getId(), w.getId(), -1, w.getActions()));
            }
            lastUpdateTick = client.getTickCount();
        }
        return new EquipmentItemQuery(equipment);
    }

//    @SneakyThrows
//    @Subscribe
//    public void onItemContainerChanged(ItemContainerChanged e) {
//        if (e.getContainerId() == InventoryID.EQUIPMENT.getId()) {
//            int x = 25362447;
//            for (int i = 0; i < 11; i++) {
//                client.runScript(545, (x + i), mappingToIterableIntegers.get(i), 1, 1, 2);
//            }
//            equipment.clear();
//            int i = -1;
//            for (Item item : e.getItemContainer().getItems()) {
//                i++;
//                if (item == null) {
//                    continue;
//                }
//                if (item.getId() == 6512 || item.getId() == -1) {
//                    continue;
//                }
//                Widget w = client.getWidget(WidgetInfo.EQUIPMENT.getGroupId(), equipmentSlotWidgetMapping.get(i));
//                if (w == null || w.getActions() == null) {
//                    continue;
//                }
//                equipment.add(new EquipmentItemWidget(w.getName(), item.getId(), w.getId(), i, w.getActions()));
//            }
//        }
//    }
}

