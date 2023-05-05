package net.runelite.client.plugins.microbot.util.npc;

import net.runelite.api.Actor;
import net.runelite.api.NPC;
import net.runelite.api.NPCComposition;
import net.runelite.api.Point;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.util.menu.Rs2Menu;

import java.awt.*;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;


public class Rs2Npc {

    public static int getHealth(Actor npc) {
        int lastRatio = 0;
        int lastHealthScale = 0;
        int lastMaxHealth = 0;
        int health = 0;
        if (npc == null) {
            return 0;
        }

        if (npc.getName() != null && npc.getHealthScale() > 0) {
            lastRatio = npc.getHealthRatio();
            lastHealthScale = npc.getHealthScale();

            NPCComposition composition = ((NPC) npc).getTransformedComposition();
            lastMaxHealth = Microbot.getNpcManager().getHealth(((NPC) npc).getId());
        }

        // Health bar
        if (lastRatio >= 0 && lastHealthScale > 0) {
            if (lastMaxHealth != 0) {
                // This is the reverse of the calculation of healthRatio done by the server
                // which is: healthRatio = 1 + (healthScale - 1) * health / maxHealth (if health > 0, 0 otherwise)
                // It's able to recover the exact health if maxHealth <= healthScale.
                if (lastRatio > 0) {
                    int minHealth = 1;
                    int maxHealth;
                    if (lastHealthScale > 1) {
                        if (lastRatio > 1) {
                            // This doesn't apply if healthRatio = 1, because of the special case in the server calculation that
                            // health = 0 forces healthRatio = 0 instead of the expected healthRatio = 1
                            minHealth = (lastMaxHealth * (lastRatio - 1) + lastHealthScale - 2) / (lastHealthScale - 1);
                        }
                        maxHealth = (lastMaxHealth * lastRatio - 1) / (lastHealthScale - 1);
                        if (maxHealth > lastMaxHealth) {
                            maxHealth = lastMaxHealth;
                        }
                    } else {
                        // If healthScale is 1, healthRatio will always be 1 unless health = 0
                        // so we know nothing about the upper limit except that it can't be higher than maxHealth
                        maxHealth = lastMaxHealth;
                    }
                    // Take the average of min and max possible healths
                    health = (minHealth + maxHealth + 1) / 2;
                }
            }
        }
        return health;
    }

    public static NPC[] getNpcs() {
        List<NPC> npcs = Microbot.getClient().getNpcs().stream()
                .sorted(Comparator.comparingInt(value -> value.getLocalLocation().distanceTo(Microbot.getClient().getLocalPlayer().getLocalLocation())))
                .collect(Collectors.toList());

        return npcs.toArray(new NPC[npcs.size()]);
    }

    public static NPC getNpc(String name) {
        return Microbot.getClientThread().runOnClientThread(() -> {
            List<NPC> npcs = Arrays.stream(getNpcs()).collect(Collectors.toList());
            if (npcs.isEmpty())
                return null;
            else
                return npcs.stream()
                        .filter(x -> x != null && x.getName().toLowerCase().equalsIgnoreCase(name.toLowerCase()))
                        .sorted(Comparator.comparingInt(value -> value.getLocalLocation().distanceTo(Microbot.getClient().getLocalPlayer().getLocalLocation())))
                        .findAny().orElse(null);
        });
    }

    public static List<NPC> getNpcs(String name) {
        List<NPC> npcs = Microbot.getClientThread().runOnClientThread(() -> Microbot.getClient().getNpcs().stream()
                .filter(x -> x != null && x.getName().toLowerCase().equalsIgnoreCase(name.toLowerCase()))
                .sorted(Comparator.comparingInt(value -> value.getLocalLocation().distanceTo(Microbot.getClient().getLocalPlayer().getLocalLocation())))
                .collect(Collectors.toList()));

        return npcs;
    }

    public static NPC getNpc(int id) {
        return Microbot.getClientThread().runOnClientThread(() -> {
            List<NPC> npcs = Arrays.stream(getNpcs()).collect(Collectors.toList());
            if (npcs.isEmpty())
                return null;
            else
                return npcs.stream()
                        .filter(x -> x != null && x.getId() == id)
                        .sorted(Comparator.comparingInt(value -> value.getLocalLocation().distanceTo(Microbot.getClient().getLocalPlayer().getLocalLocation())))
                        .findFirst().orElse(null);
        });
    }


    private static boolean interact(NPC npc, String action) {
        if (npc == null) return false;

        Polygon screenLoc = npc.getCanvasTilePoly();

        if (screenLoc == null || screenLoc.getBounds().getCenterY() == 0 && screenLoc.getBounds().getCenterY() == 0)
            return false;

        Point point = new Point((int) screenLoc.getBounds().getCenterX(), (int) screenLoc.getBounds().getCenterY());
        Microbot.getMouse().move(point);

        return Rs2Menu.doAction(action, point, npc.getName());
    }

    public static boolean interact(int npcId) {
        return interact(npcId, "");
    }

    public static boolean interact(String npcName) {
        return interact(npcName, "");
    }

    public static boolean interact(int npcId, String action) {
        NPC npc = Microbot.getClient().getNpcs().stream().filter(x -> x.getId() == npcId).findFirst().get();

        return interact(npc, action);
    }

    public static boolean interact(String npcName, String action) {
        NPC npc = getNpc(npcName);

        return interact(npc, action);
    }
}