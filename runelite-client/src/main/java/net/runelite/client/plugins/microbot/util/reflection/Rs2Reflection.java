package net.runelite.client.plugins.microbot.util.reflection;

import lombok.SneakyThrows;
import net.runelite.api.NPC;

import java.lang.reflect.Field;

public class Rs2Reflection {
    @SneakyThrows
    public static int getAnimation(NPC npc) {
        Field animation = npc.getClass().getSuperclass().getDeclaredField("ck");
        animation.setAccessible(true);
        int anim = animation.getInt(npc) * -1553687919;
        animation.setAccessible(false);
        return anim;
    }

}
