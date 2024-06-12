package net.runelite.client.plugins.hoseaplugins.PiggyUtils.API;

import net.runelite.client.plugins.hoseaplugins.ethanapi.EthanApiPlugin.Collections.Equipment;
import net.runelite.client.plugins.hoseaplugins.ethanapi.EthanApiPlugin.Collections.EquipmentItemWidget;
import lombok.Getter;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class EquipmentUtil {

    public enum EquipmentSlot {
        HEAD(0), CAPE(1), NECKLACE(2), MAIN_HAND(3),
        TORSO(4), OFF_HAND(5), AMMO(13), LEGS(7),
        HANDS(9), FEET(10), RING(12);
        @Getter
        private final int index;

        EquipmentSlot(int index) {
            this.index = index;
        }
    }

    public static Optional<EquipmentItemWidget> getItemInSlot(EquipmentSlot slot) {
        return Equipment.search().filter(item -> {
            EquipmentItemWidget iw = (EquipmentItemWidget) item;
            return iw.getEquipmentIndex() == slot.getIndex();
        }).first();
    }


    public static boolean hasItem(String name) {
        return Equipment.search().nameContainsNoCase(name).first().isPresent();
    }


    public static boolean hasAnyItems(String... names) {
        for (String name : names) {
            if (hasItem(name)) {
                return true;
            }
        }

        return false;
    }

    @Deprecated
    public static boolean hasItems(String... names) {
        for (String name : names) {
            if (!hasItem(name)) {
                return false;
            }
        }

        return true;
    }

    public static boolean hasItems(int... ids) {
        for (int id : ids) {
            if (!hasItem(id)) {
                return false;
            }
        }

        return true;
    }

    public static boolean hasItems(List<Integer> ids) {
        for (int id : ids) {
            if (!hasItem(id)) {
                return false;
            }
        }

        return true;
    }

    public static boolean hasItem(int id) {
        return Equipment.search().withId(id).first().isPresent();
    }
}
