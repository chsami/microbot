package net.runelite.client.plugins.microbot.util.reflection;

import lombok.SneakyThrows;
import net.runelite.api.*;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.util.keyboard.Rs2Keyboard;
import net.runelite.client.plugins.microbot.util.math.Rs2Random;

import java.awt.event.KeyEvent;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 *  @ObfuscatedName("cr")
 *  @ObfuscatedGetter(
 *  intValue = 1400873349 --> animationMultiplier
 *  )
 *  @Export("sequence")
 *  int sequence;
 *  @ObfuscatedName("cz")
 *  @ObfuscatedGetter(
 *  intValue = -1043355907
 )
 *
 *     @ObfuscatedName("hw")
 * @Implements("NPCComposition")
 * public class NPCComposition extends DualNode
 *
 *     @ObfuscatedName("bd")
 *     @Export("headIconSpriteIndex")
 *     short[] headIconSpriteIndex = null;
 *
 *     URL to check new gamepack: https://oldschool42.runescape.com/jav_config.ws
 */

public class Rs2Reflection {
    static String animationField = null;
    static Method doAction = null;

    /**
     * sequence maps to an actor animation
     * actor can be an npc/player
     */
    static int animationMultiplier = 1400873349;

    /**
     * Credits to EthanApi
     * @param npc
     * @return
     */
    @SneakyThrows
    public static int getAnimation(NPC npc) {
        if (npc == null) {
            return -1;
        }
        try {
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
                    if (npc.getAnimation() == animationMultiplier * 4795789) {
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
            int anim = animation.getInt(npc) * animationMultiplier;
            animation.setAccessible(false);
            return anim;
        } catch(Exception ex) {
            Microbot.log("Failed to get animation : " + ex.getMessage());
        }
        return -1;
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
        if (Microbot.getClient().getKeyboardIdleTicks() > Rs2Random.between(5000, 10000)) {
            Rs2Keyboard.keyPress(KeyEvent.VK_BACK_SPACE);
        }
        System.out.println("[INVOKE] => param0: " + param0 + " param1: " + param1 + " opcode: " + opcode + " id: " + identifier + " itemid: " + itemId);
        doAction.setAccessible(false);
    }

    /**
     * Credits to EthanApi
     * @param npc
     * @return
     */
    @SneakyThrows
    public static HeadIcon getHeadIcon(NPC npc) {
        Field ab = npc.getClass().getDeclaredField("ab");
        ab.setAccessible(true);
        Object aqObj = ab.get(npc);
        if (aqObj == null) {
            ab.setAccessible(false);
            return getOldHeadIcon(npc);
        }
        Field bdField = aqObj.getClass().getDeclaredField("bd");
        bdField.setAccessible(true);
        short[] bd = (short[]) bdField.get(aqObj);
        bdField.setAccessible(false);
        ab.setAccessible(false);
        if (bd == null) {
            return getOldHeadIcon(npc);
        }
        if (bd.length == 0) {
            return getOldHeadIcon(npc);
        }
        short headIcon = bd[0];
        if (headIcon == -1) {
            return getOldHeadIcon(npc);
        }
        return HeadIcon.values()[headIcon];
    }

    /**
     * Credits to EthanApi
     * @param npc
     * @return
     */
    @SneakyThrows
    public static HeadIcon getOldHeadIcon(NPC npc) {
        Method getHeadIconMethod;
        for (Method declaredMethod : npc.getClass().getDeclaredMethods()) {
            if (declaredMethod.getName().length() == 2 && declaredMethod.getReturnType() == short[].class && declaredMethod.getParameterCount() == 0) {
                getHeadIconMethod = declaredMethod;
                getHeadIconMethod.setAccessible(true);
                short[] headIcon = null;
                try {
                    headIcon = (short[]) getHeadIconMethod.invoke(npc);
                } catch (Exception e) {
                    //nothing
                }
                getHeadIconMethod.setAccessible(false);

                if (headIcon == null) {
                    continue;
                }
                System.out.println("old := " + getHeadIconMethod.getName());

                return HeadIcon.values()[headIcon[0]];
            }
        }
        return null;
    }
}
