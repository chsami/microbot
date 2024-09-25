package net.runelite.client.plugins.microbot.util.reflection;

import lombok.SneakyThrows;
import net.runelite.api.ItemComposition;
import net.runelite.api.MenuEntry;
import net.runelite.api.NPC;
import net.runelite.api.ObjectID;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.util.keyboard.Rs2Keyboard;
import net.runelite.client.plugins.microbot.util.math.Random;

import java.awt.event.KeyEvent;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Rs2Reflection {
    static String animationField = null;
    static Method doAction = null;

    /**
     * sequence maps to an actor animation
     * actor can be an npc/player
     */
    static int sequence = 1036457443;

    @SneakyThrows
    public static int getAnimation(NPC npc) {
        if (npc == null) {
            return -1;
        }
        if (animationField == null) {
            for (Field declaredField : npc.getClass().getSuperclass().getDeclaredFields()) {
                if (declaredField == null) {
                    continue;
                }
                declaredField.setAccessible(true);
                if (declaredField.getType() != int.class) {
                    continue;
                }
                if (Modifier.isFinal(declaredField.getModifiers())) {
                    continue;
                }
                if (Modifier.isStatic(declaredField.getModifiers())) {
                    continue;
                }
                int value = declaredField.getInt(npc);
                declaredField.setInt(npc, 4795789);
                if (npc.getAnimation() == sequence * 4795789) {
                    animationField = declaredField.getName();
                    declaredField.setInt(npc, value);
                    declaredField.setAccessible(false);
                    break;
                }
                declaredField.setInt(npc, value);
                declaredField.setAccessible(false);
            }
        }
        if (animationField == null) {
            return -1;
        }
        Field animation = npc.getClass().getSuperclass().getDeclaredField(animationField);
        animation.setAccessible(true);
        int anim = animation.getInt(npc) * sequence;
        animation.setAccessible(false);
        return anim;
    }

    @SneakyThrows
    public static String[] getGroundItemActions(ItemComposition item) {
        List<Field> fields = Arrays.stream(item.getClass().getFields()).filter(x -> x.getType().isArray()).collect(Collectors.toList());
        for (Field field : fields) {
            if (field.getType().getComponentType().getName().equals("java.lang.String")) {
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
        var list =  Arrays.stream(menuEntry.getClass().getMethods())
                .filter(x -> x.getName().equals("setItemId"))
                .collect(Collectors.toList());

         list.get(0)
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

    @SneakyThrows
    public static void invokeMenu(int param0, int param1, int opcode, int identifier, int itemId, String option, String target, int x,
                              int y) {
        if (doAction == null) {
            doAction = Arrays.stream(Microbot.getClient().getClass().getDeclaredMethods())
                    .filter(m -> m.getReturnType().getName().equals("void") && m.getParameters().length == 9 && Arrays.stream(m.getParameters())
                            .anyMatch(p -> p.getType() == String.class))
                    .findFirst()
                    .orElse(null);

            if (doAction == null) {
                Microbot.showMessage("InvokeMenuAction method is broken!");
                return;
            }
        }

        doAction.setAccessible(true);
        Microbot.getClientThread().runOnClientThread(() -> doAction.invoke(null, param0, param1, opcode, identifier, itemId, option, target, x, y));
        if (Microbot.getClient().getKeyboardIdleTicks() > Random.random(5000, 10000)) {
            Rs2Keyboard.keyPress(KeyEvent.VK_BACK_SPACE);
        }
        System.out.println("[INVOKE] => param0: " + param0 + " param1: " + param1 + " opcode: " + opcode + " id: " + identifier + " itemid: " + itemId);
        doAction.setAccessible(false);
    }
}
