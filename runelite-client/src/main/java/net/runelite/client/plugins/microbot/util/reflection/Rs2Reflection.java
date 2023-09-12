package net.runelite.client.plugins.microbot.util.reflection;

import lombok.SneakyThrows;
import net.runelite.api.ItemComposition;
import net.runelite.api.MenuEntry;
import net.runelite.api.NPC;
import net.runelite.api.ObjectID;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Rs2Reflection {
    @SneakyThrows
    public static int getAnimation(NPC npc) {
        Field animation = npc.getClass().getSuperclass().getDeclaredField("ck");
        animation.setAccessible(true);
        int anim = animation.getInt(npc) * -1553687919;
        animation.setAccessible(false);
        return anim;
    }

    @SneakyThrows
    public static String[] getGroundItemActions(ItemComposition item) {
        List<Field> fields = Arrays.stream(item.getClass().getFields()).filter(x -> x.getType().isArray()).collect(Collectors.toList());
        for (Field field : fields) {
            if (field.getType().getComponentType().getName() == "java.lang.String") {
                String[] actions = (String[]) field.get(item);
                if (Arrays.stream(actions).anyMatch(x -> x != null && x.equalsIgnoreCase("take"))) {
                    field.setAccessible(true);
                    return actions;
                }
            }
        }
        return new String[]{};
    }

    @SneakyThrows
    public static void setItemId(MenuEntry menuEntry, int itemId) throws IllegalAccessException, InvocationTargetException {
        Arrays.stream(menuEntry.getClass().getMethods())
                .filter(x -> x.getReturnType().getName() == "void" && x.getParameters().length > 0 && x.getParameters()[0].getType().getName() == "int")
                .collect(Collectors.toList())
                .get(0)
                .invoke(menuEntry, itemId); //use the setItemId method through reflection
    }


    @SneakyThrows
    public static ArrayList<Integer> getObjectByName(String[] names, boolean exact) {
        ArrayList<Integer> objectIds = new ArrayList<Integer>();
        Field[] fields = ObjectID.class.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);

            if (field.getType() == int.class) {
                int fieldValue = field.getInt(null);

                if (exact)
                    if (Arrays.stream(names).noneMatch(name -> field.getName().equalsIgnoreCase(name)))
                        continue;

                if (!exact)
                    if (Arrays.stream(names).noneMatch(name -> field.getName().toLowerCase().contains(name.toLowerCase())))
                        continue;

                objectIds.add(fieldValue);
            }
        }
        return objectIds;
    }

    @SneakyThrows
    public static ArrayList<Integer> getObjectByName(String name, boolean exact) {
        return getObjectByName(new String[]{name}, exact);
    }
}
