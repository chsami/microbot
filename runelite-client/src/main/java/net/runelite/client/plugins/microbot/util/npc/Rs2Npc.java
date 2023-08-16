package net.runelite.client.plugins.microbot.util.npc;

import net.runelite.api.*;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.util.camera.Camera;
import net.runelite.client.plugins.microbot.util.math.Random;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static net.runelite.client.plugins.microbot.util.Global.sleep;


public class Rs2Npc {

    public static NPC npcInteraction = null;
    public static String npcAction = null;


    public static NPC getNpcByIndex(int index) {
        return Microbot.getClient().getNpcs().stream()
                .filter(x -> x.getIndex() == index)
                .findFirst()
                .orElse(null);
    }

    public static NPC validateInteractable(NPC npc) {
        if(npc != null) {
            Microbot.getWalker().walkTo(npc.getWorldLocation());
            Camera.turnTo(npc);
            return npc;
        }
        return null;
    }

    public static List<NPC> getNpcsForPlayer() {
        List<NPC> npcs = Microbot.getClient().getNpcs().stream()
                .filter(x -> x.getInteracting() == Microbot.getClient().getLocalPlayer())
                .sorted(Comparator
                        .comparingInt(value -> value
                                .getLocalLocation()
                                .distanceTo(Microbot.getClient().getLocalPlayer().getLocalLocation())))
                .collect(Collectors.toList());

        return npcs;
    }

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

    public static NPC[] getAttackableNpcs() {
        List<NPC> npcs = Microbot.getClient().getNpcs().stream()
                .filter((npc) -> npc.getCombatLevel() > 0 && !npc.isDead())
                .sorted(Comparator.comparingInt(value -> value.getLocalLocation().distanceTo(Microbot.getClient().getLocalPlayer().getLocalLocation())))
                .collect(Collectors.toList());

        return npcs.toArray(new NPC[npcs.size()]);
    }

    public static NPC[] getAttackableNpcs(String name) {
        List<NPC> npcs = Microbot.getClient().getNpcs().stream()
                .filter((npc) -> npc.getCombatLevel() > 0 && !npc.isDead() && npc.getName().toLowerCase().equals(name))
                .sorted(Comparator.comparingInt(value -> value.getLocalLocation().distanceTo(Microbot.getClient().getLocalPlayer().getLocalLocation())))
                .collect(Collectors.toList());

        return npcs.toArray(new NPC[npcs.size()]);
    }

    public static NPC[] getPestControlPortals() {
        List<NPC> npcs = Microbot.getClient().getNpcs().stream()
                .filter((npc) -> !npc.isDead() && npc.getHealthRatio() > 0 && npc.getName().toLowerCase().equals("portal"))
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

    public static NPC getNpc(int id, List<Integer> excludedIndexes) {
        return Microbot.getClientThread().runOnClientThread(() -> {
            List<NPC> npcs = Arrays.stream(getNpcs()).collect(Collectors.toList());
            if (npcs.isEmpty())
                return null;
            else
                return npcs.stream()
                        .filter(x -> x != null && x.getId() == id && !excludedIndexes.contains(x.getIndex()))
                        .sorted(Comparator.comparingInt(value -> value.getLocalLocation().distanceTo(Microbot.getClient().getLocalPlayer().getLocalLocation())))
                        .findFirst().orElse(null);
        });
    }


    public static boolean interact(NPC npc, String action) {
        if (npc == null) return false;
        try {
            npcInteraction = npc;
            npcAction = action;
            Microbot.getMouse().clickFast(Random.random(0, Microbot.getClient().getCanvasWidth()), Random.random(0, Microbot.getClient().getCanvasHeight()));
            sleep(100);
            npcInteraction = null;
            npcAction = null;
        } catch(Exception ex) {
            System.out.println(ex.getMessage());
        }

        return true;
    }

    public static boolean interact(int npcId, String action) {
        NPC npc = Microbot.getClient().getNpcs().stream().filter(x -> x.getId() == npcId).findFirst().orElse(null);

        return interact(npc, action);
    }

    public static boolean attack(int npcId) {
        NPC npc = getNpc(npcId);

        return interact(npc, "attack");
    }

    public static boolean attack(String npcName) {
        NPC npc = getNpc(npcName);

        return interact(npc, "attack");
    }

    public static boolean interact(String npcName, String action) {
        NPC npc = getNpc(npcName);

        return interact(npc, action);
    }

    public static boolean pickpocket(String npcName) {
        NPC npc = getNpc(npcName);

        return interact(npc, "pickpocket");
    }

    public static void handleMenuSwapper(MenuEntry menuEntry) {
        if (npcInteraction == null) return;
        menuEntry.setIdentifier(npcInteraction.getIndex());
        menuEntry.setParam0(0);
        menuEntry.setTarget("<col=ffff00>" + npcInteraction.getName() + "<col=ff00>  (level-" + npcInteraction.getCombatLevel() + ")");
        menuEntry.setParam1(0);
        menuEntry.setOption(Rs2Npc.npcAction);
        if (npcAction.toLowerCase().equals("talk-to")) {
            menuEntry.setType(MenuAction.NPC_FIRST_OPTION);
        } else if (npcAction.toLowerCase().equals("attack")) {
            menuEntry.setType(MenuAction.NPC_SECOND_OPTION);
        } else if (npcAction.toLowerCase().equals("pickpocket") || npcAction.toLowerCase().equals("bank") || npcAction.toLowerCase().equals("dream")) {
            menuEntry.setType(MenuAction.NPC_THIRD_OPTION);
        } else if (npcAction.toLowerCase().equals("collect")) {
            menuEntry.setType(MenuAction.NPC_FOURTH_OPTION);
        } else {
            menuEntry.setType(MenuAction.NPC_FIRST_OPTION);
        }
    }
}