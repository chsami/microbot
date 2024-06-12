package net.runelite.client.plugins.hoseaplugins.ethanapi.PacketUtils;

import net.runelite.client.plugins.hoseaplugins.ethanapi.Packets.BufferMethods;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.client.RuneLite;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class PacketReflection {
    public static Client getClient(){
        return RuneLite.getInjector().getInstance(Client.class);
    }
    public static Class loadClassFromClientClassLoader(String name){
        try {
            ClassLoader clientLoader = getClient().getClass().getClassLoader();
            return clientLoader.loadClass(name);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
    public static Class getClassWithGetPacketBufferNode(){
        return loadClassFromClientClassLoader(ObfuscatedNames.classContainingGetPacketBufferNodeName);
    }
    public static Method getGetPacketBufferNode(){
        try {
            return Arrays.stream(getClassWithGetPacketBufferNode().getDeclaredMethods()).filter(m -> m.getReturnType().equals(getPacketBufferNodeClass())).collect(Collectors.toList()).get(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    public static Class getClientPacketClass(){
        return loadClassFromClientClassLoader(ObfuscatedNames.clientPacketClassName);
    }
    public static Field getPacketWriterField() {
        try {
            return getClient().getClass().getDeclaredField(ObfuscatedNames.packetWriterFieldName);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        return null;
    }
    public static Class getPacketWriterClass(){
        try {
            Field packetWriterField = getPacketWriterField();
            packetWriterField.setAccessible(true);
            Class packetWriterClass = packetWriterField.get(null).getClass();
            packetWriterField.setAccessible(false);
            return packetWriterClass;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }
    public static Object getIsaacObject(){
        try {
            Field isaacField = getPacketWriterClass().getDeclaredField(ObfuscatedNames.isaacCipherFieldName);
            isaacField.setAccessible(true);
            Object isaacObject = isaacField.get(getPacketWriteObject());
            isaacField.setAccessible(false);
            return isaacObject;
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }
    public static Class getIsaacClass(){
        return getIsaacObject().getClass();
    }
    public static Class getPacketBufferNodeClass(){
        return loadClassFromClientClassLoader(ObfuscatedNames.packetBufferNodeClassName);
    }
    public static Object getPacketWriteObject(){
        Field packetWriterField = getPacketWriterField();
        packetWriterField.setAccessible(true);
        try {
            Object packetWriter = packetWriterField.get(null);
            packetWriterField.setAccessible(false);
            return packetWriter;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static void sendPacket(PacketDef def, Object... objects) {
        Object packetBufferNode = null;
        Method getPacketBufferNode = getGetPacketBufferNode();
        Class ClientPacket = getClientPacketClass();
        Object isaac = getIsaacObject();
        getPacketBufferNode.setAccessible(true);
        long garbageValue = Math.abs(Long.parseLong(ObfuscatedNames.getPacketBufferNodeGarbageValue));
        if (garbageValue < 256) {
            try {
                packetBufferNode = getPacketBufferNode.invoke(null, fetchPacketField(def.name).get(ClientPacket),
                        isaac, Byte.parseByte(ObfuscatedNames.getPacketBufferNodeGarbageValue));
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        } else if (garbageValue < 32768) {
            try {
                //System.out.println("getPacketBufferNode: "+getPacketBufferNode);
                //System.out.println("isaac: "+isaac);
                packetBufferNode = getPacketBufferNode.invoke(null, fetchPacketField(def.name).get(ClientPacket),
                        isaac, Short.parseShort(ObfuscatedNames.getPacketBufferNodeGarbageValue));
                //System.out.println("packetBufferNode: "+packetBufferNode);
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        } else if (garbageValue < Integer.MAX_VALUE) {
            try {
                packetBufferNode = getPacketBufferNode.invoke(null, fetchPacketField(def.name).get(ClientPacket),
                        isaac, Integer.parseInt(ObfuscatedNames.getPacketBufferNodeGarbageValue));
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        Object buffer = null;
        try {
            buffer = packetBufferNode.getClass().getDeclaredField(ObfuscatedNames.packetBufferFieldName).get(packetBufferNode);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
        }
        getPacketBufferNode.setAccessible(false);
        List<String> params = null;
        if (def.type == PacketType.RESUME_PAUSEBUTTON) {
            params = List.of("var0", "var1");
        }
        if (def.type == PacketType.IF_BUTTON) {
            params = List.of("widgetId", "slot", "itemId");
        }
        if (def.type == PacketType.OPLOC) {
            params = List.of("objectId", "worldPointX", "worldPointY", "ctrlDown");
        }
        if (def.type == PacketType.OPNPC) {
            params = List.of("npcIndex", "ctrlDown");
        }
        if (def.type == PacketType.OPPLAYER) {
            params = List.of("playerIndex", "ctrlDown");
        }
        if (def.type == PacketType.OPOBJ) {
            params = List.of("objectId", "worldPointX", "worldPointY", "ctrlDown");
        }
        if (def.type == PacketType.OPOBJT) {
            params = List.of("objectId", "worldPointX", "worldPointY", "slot", "itemId", "widgetId",
                    "ctrlDown");
        }
        if (def.type == PacketType.EVENT_MOUSE_CLICK) {
            params = List.of("mouseInfo", "mouseX", "mouseY");
        }
        if (def.type == PacketType.MOVE_GAMECLICK) {
            params = List.of("worldPointX", "worldPointY", "ctrlDown", "5");
        }
        if (def.type == PacketType.IF_BUTTONT) {
            params = List.of("sourceWidgetId", "sourceSlot", "sourceItemId", "destinationWidgetId",
                    "destinationSlot", "destinationItemId");
        }
        if (def.type == PacketType.OPLOCT) {
            params = List.of("objectId", "worldPointX", "worldPointY", "slot", "itemId", "widgetId",
                    "ctrlDown");
        }
        if (def.type == PacketType.OPPLAYERT) {
            params = List.of("playerIndex", "itemId", "slot", "widgetId", "ctrlDown");
        }
        if (def.type == PacketType.OPNPCT) {
            params = List.of("npcIndex", "itemId", "slot", "widgetId", "ctrlDown");
        }
        if (params != null) {
            for (int i = 0; i < def.writeData.length; i++) {
                int index = params.indexOf(def.writeData[i]);
                Object writeValue = objects[index];
                for (String s : def.writeMethods[i]) {
                    //System.out.println("Writing " + s + " " + writeValue);
                    BufferMethods.writeValue(s, (Integer) writeValue, buffer);
                }
            }
            Field PACKETWRITER = getPacketWriterField();
            PACKETWRITER.setAccessible(true);
            try {
                //System.out.println(PACKETWRITER);
                //System.out.println(PACKETWRITER.get(null));
                addNode(PACKETWRITER.get(null), packetBufferNode);
            } catch (Exception e) {
                e.printStackTrace();
            }
            PACKETWRITER.setAccessible(false);
        }
    }

    public static void addNode(Object packetWriter, Object packetBufferNode) {
        if (PacketUtilsPlugin.usingClientAddNode) {
            try {
                Method addNode = null;
                long garbageValue = Math.abs(Long.parseLong(ObfuscatedNames.addNodeGarbageValue));
                if (garbageValue < 256) {
                    addNode = packetWriter.getClass().getDeclaredMethod(ObfuscatedNames.addNodeMethodName, packetBufferNode.getClass(), byte.class);
                    addNode.setAccessible(true);
                    addNode.invoke(packetWriter, packetBufferNode, Byte.parseByte(ObfuscatedNames.addNodeGarbageValue));
                } else if (garbageValue < 32768) {
                    addNode = packetWriter.getClass().getDeclaredMethod(ObfuscatedNames.addNodeMethodName, packetBufferNode.getClass(), short.class);
                    addNode.setAccessible(true);
                    addNode.invoke(packetWriter, packetBufferNode, Short.parseShort(ObfuscatedNames.addNodeGarbageValue));
                } else if (garbageValue < Integer.MAX_VALUE) {
                    addNode = packetWriter.getClass().getDeclaredMethod(ObfuscatedNames.addNodeMethodName, packetBufferNode.getClass(), int.class);
                    //System.out.println("addnode: "+addNode);
                    addNode.setAccessible(true);
                    addNode.invoke(packetWriter, packetBufferNode, Integer.parseInt(ObfuscatedNames.addNodeGarbageValue));
                }
                if (addNode != null) {
                    addNode.setAccessible(false);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            try {
                Method addNode = PacketUtilsPlugin.addNodeMethod;
                addNode.setAccessible(true);
                if (addNode.getParameterCount() == 2) {
                    addNode.invoke(null, packetWriter, packetBufferNode);
                } else {
                    long garbageValue = Math.abs(Long.parseLong(ObfuscatedNames.addNodeGarbageValue));
                    if (garbageValue < 256) {
                        addNode.invoke(null, packetWriter, packetBufferNode, Byte.parseByte(ObfuscatedNames.addNodeGarbageValue));
                    } else if (garbageValue < 32768) {
                        addNode.invoke(null, packetWriter, packetBufferNode, Short.parseShort(ObfuscatedNames.addNodeGarbageValue));
                    } else if (garbageValue < Integer.MAX_VALUE) {
                        addNode.invoke(null, packetWriter, packetBufferNode, Integer.parseInt(ObfuscatedNames.addNodeGarbageValue));
                    }
                }
                addNode.setAccessible(false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    static Field fetchPacketField(String name) {
        try {
            Class ClientPacket = getClientPacketClass();
            return ClientPacket.getDeclaredField(name);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
            return null;
        }
    }


    private static BigInteger modInverse(BigInteger val) {
        BigInteger shift = BigInteger.ONE.shiftLeft(32);
        return val.modInverse(shift);
    }

    private static int modInverse(int val) {
        return modInverse(BigInteger.valueOf(val)).intValue();
    }
}
