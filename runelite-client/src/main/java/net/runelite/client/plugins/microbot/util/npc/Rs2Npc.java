package net.runelite.client.plugins.microbot.util.npc;

import net.runelite.api.Actor;
import net.runelite.api.MenuAction;
import net.runelite.api.NPC;
import net.runelite.api.NPCComposition;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.util.camera.Rs2Camera;
import net.runelite.client.plugins.microbot.util.math.Random;
import net.runelite.client.plugins.microbot.util.menu.NewMenuEntry;
import net.runelite.client.plugins.microbot.util.walker.Rs2Walker;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


public class Rs2Npc {

    public static NPC getNpcByIndex(int index) {
        return Microbot.getClient().getNpcs().stream()
                .filter(x -> x.getIndex() == index)
                .findFirst()
                .orElse(null);
    }

    public static NPC validateInteractable(NPC npc) {
        if (npc != null) {
            Rs2Walker.walkTo(npc.getWorldLocation());
            Rs2Camera.turnTo(npc);
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
                .filter((npc) -> npc.getCombatLevel() > 0 && !npc.isDead() && !npc.isInteracting())
                .sorted(Comparator.comparingInt(value -> value.getLocalLocation().distanceTo(Microbot.getClient().getLocalPlayer().getLocalLocation())))
                .collect(Collectors.toList());

        return npcs.toArray(new NPC[npcs.size()]);
    }

    public static NPC[] getAttackableNpcs(String name) {
        List<NPC> npcs = Microbot.getClient().getNpcs().stream()
                .filter((npc) -> npc.getCombatLevel() > 0 && !npc.isDead() && npc.getName().toLowerCase().equals(name) && !npc.isInteracting())
                .sorted(Comparator.comparingInt(value -> value.getLocalLocation().distanceTo(Microbot.getClient().getLocalPlayer().getLocalLocation())))
                .collect(Collectors.toList());

        return npcs.toArray(new NPC[npcs.size()]);
    }

    public static NPC[] getPestControlPortals() {
        List<NPC> npcs = Microbot.getClient().getNpcs().stream()
                .filter((npc) -> !npc.isDead() && npc.getHealthRatio() > 0 && npc.getName().equalsIgnoreCase("portal"))
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
                        .filter(x -> x != null && x.getName() != null && x.getName().equalsIgnoreCase(name))
                        .min(Comparator.comparingInt(value -> value.getLocalLocation().distanceTo(Microbot.getClient().getLocalPlayer().getLocalLocation())))
                        .orElse(null);

        });
    }

    public static List<NPC> getNpcs(String name) {
        List<NPC> npcs = Microbot.getClientThread().runOnClientThread(() -> Microbot.getClient().getNpcs().stream()
                .filter(x -> x != null && x.getName().equalsIgnoreCase(name))
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
                        .min(Comparator.comparingInt(value -> value.getLocalLocation().distanceTo(Microbot.getClient().getLocalPlayer().getLocalLocation())))
                        .orElse(null);
        });
    }

    public static Optional<NPC> getNpc(int id, List<Integer> excludedIndexes) {
        return Microbot.getClientThread().runOnClientThread(() -> {
            List<NPC> npcs = Arrays.stream(getNpcs()).collect(Collectors.toList());
            if (npcs.isEmpty())
                return Optional.empty();
            else
                return npcs.stream()
                        .filter(x -> x != null && x.getId() == id && !excludedIndexes.contains(x.getIndex()))
                        .min(Comparator.comparingInt(value -> value.getLocalLocation().distanceTo(Microbot.getClient().getLocalPlayer().getLocalLocation())));
        });
    }


    public static boolean interact(NPC npc, String action) {
        if (npc == null) return false;
        Microbot.status = action + " " + npc.getName();
        try {
            NPCComposition npcComposition = Microbot.getClientThread().runOnClientThread(() -> Microbot.getClient().getNpcDefinition(npc.getId()));

            int index = 0;
            for (int i = 0; i < npcComposition.getActions().length; i++) {
                String npcAction = npcComposition.getActions()[i];
                if (npcAction == null || !npcAction.equalsIgnoreCase(action)) continue;
                index = i;
            }

            MenuAction menuAction = getMenuAction(index);

            if (!Rs2Camera.isTileOnScreen(npc.getLocalLocation())) {
                Microbot.getClient().setCameraPitchTarget(Random.random(430, 460));
                Microbot.getMouse().scrollDown(new net.runelite.api.Point(1, 1));
                Microbot.getMouse().scrollDown(new net.runelite.api.Point(1, 1));
                Microbot.getMouse().scrollDown(new net.runelite.api.Point(1, 1));
                Rs2Camera.turnTo(npc);
            }

            if (menuAction != null) {
                Microbot.doInvoke(new NewMenuEntry(0, 0, menuAction.getId(), npc.getIndex(), -1, npc.getName()), new Rectangle(npc.getCanvasTilePoly().getBounds()));
                //Rs2Reflection.invokeMenu(0, 0, menuAction.getId(), npc.getIndex(),-1, action, "", -1, -1);
            }

        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }

        return true;
    }

    @Nullable
    private static MenuAction getMenuAction(int index) {
        MenuAction menuAction = null;

        if (Microbot.getClient().isWidgetSelected()) {
            menuAction = MenuAction.WIDGET_TARGET_ON_NPC;
        } else if (index == 0) {
            menuAction = MenuAction.NPC_FIRST_OPTION;
        } else if (index == 1) {
            menuAction = MenuAction.NPC_SECOND_OPTION;
        } else if (index == 2) {
            menuAction = MenuAction.NPC_THIRD_OPTION;
        } else if (index == 3) {
            menuAction = MenuAction.NPC_FOURTH_OPTION;
        } else if (index == 4) {
            menuAction = MenuAction.NPC_FIFTH_OPTION;
        }
        return menuAction;
    }

    public static boolean interact(NPC npc) {
        return interact(npc, "");
    }

    public static boolean interact(int npcId, String action) {
        NPC npc = getNpc(npcId);

        return interact(npc, action);
    }

    public static boolean attack(NPC npc) {
        return interact(npc, "attack");
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

    public static boolean hasLineOfSight(NPC npc) {
        if (npc == null) return false;
        return new WorldArea(
                npc.getWorldLocation(),
                npc.getComposition().getSize(),
                npc.getComposition().getSize())
                .hasLineOfSightTo(Microbot.getClient(), Microbot.getClient().getLocalPlayer().getWorldLocation().toWorldArea());
    }

    public static WorldPoint getWorldLocation(NPC npc) {
        if (Microbot.getClient().isInInstancedRegion()) {
            LocalPoint l = LocalPoint.fromWorld(Microbot.getClient(), npc.getWorldLocation());
            WorldPoint npcInstancedWorldLocation = WorldPoint.fromLocalInstance(Microbot.getClient(), l);
            return npcInstancedWorldLocation;
        } else {
            return npc.getWorldLocation();
        }
    }
}