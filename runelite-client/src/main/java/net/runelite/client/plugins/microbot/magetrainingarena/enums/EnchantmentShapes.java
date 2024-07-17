package net.runelite.client.plugins.microbot.magetrainingarena.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.runelite.api.ItemID;
import net.runelite.api.ObjectID;

@Getter
@AllArgsConstructor
public enum EnchantmentShapes {
    PENTAMID(ObjectID.PENTAMID_PILE, ItemID.PENTAMID, 195, 14),
    ICOSAHEDRON(ObjectID.ICOSAHEDRON_PILE, ItemID.ICOSAHEDRON, 195, 16),
    CUBE(ObjectID.CUBE_PILE, ItemID.CUBE, 195, 10),
    CYLINDER(ObjectID.CYLINDER_PILE, ItemID.CYLINDER, 195, 12);

    private final int objectId;
    private final int itemId;
    private final int widgetId;
    private final int widgetChildId;
}
