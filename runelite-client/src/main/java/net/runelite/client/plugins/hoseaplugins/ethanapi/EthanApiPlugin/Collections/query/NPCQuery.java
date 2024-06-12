package net.runelite.client.plugins.hoseaplugins.ethanapi.EthanApiPlugin.Collections.query;

import net.runelite.client.plugins.hoseaplugins.ethanapi.EthanApiPlugin.Collections.Players;
import net.runelite.client.plugins.hoseaplugins.ethanapi.EthanApiPlugin.EthanApiPlugin;
import net.runelite.client.plugins.hoseaplugins.ethanapi.EthanApiPlugin.PathFinding.GlobalCollisionMap;
import net.runelite.client.plugins.hoseaplugins.ethanapi.EthanApiPlugin.Utility.WorldAreaUtility;
import net.runelite.api.Actor;
import net.runelite.api.Client;
import net.runelite.api.NPC;
import net.runelite.api.NPCComposition;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.RuneLite;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class NPCQuery {
    static Client client = RuneLite.getInjector().getInstance(Client.class);
    private List<NPC> npcs;

    public NPCQuery(List<NPC> npcs) {
        this.npcs = new ArrayList(npcs.stream().filter(Objects::nonNull).collect(Collectors.toList()));
    }

    public NPCQuery filter(Predicate<? super NPC> predicate) {
        npcs = npcs.stream().filter(predicate).collect(Collectors.toList());
        return this;
    }

    public NPCQuery atLocation(WorldPoint wp) {
        npcs = npcs.stream().filter(npc -> npc.getWorldLocation().equals(wp)).collect(Collectors.toList());
        return this;
    }

    public NPCQuery withAction(String action) {
        npcs = npcs.stream().filter(npc -> {
            NPCComposition npcComposition = getNPCComposition(npc);
            if (npcComposition == null)
                return false;
            String[] actions = getNPCComposition(npc).getActions();
            return Arrays.stream(actions).filter(Objects::nonNull).anyMatch(a -> a.equalsIgnoreCase(action));
        }).collect(Collectors.toList());
        return this;
    }

    public NPCQuery withId(int id) {
        npcs = npcs.stream().filter(npc -> npc.getId() == id).collect(Collectors.toList());
        return this;
    }

    public NPCQuery withName(String name) {
        npcs = npcs.stream().filter(npcs -> npcs.getName() != null && npcs.getName().equals(name)).collect(Collectors.toList());
        return this;
    }

    public NPCQuery nameContains(String name) {
        npcs = npcs.stream().filter(npcs -> npcs.getName() != null && npcs.getName().contains(name)).collect(Collectors.toList());
        return this;
    }

    public NPCQuery interactingWithLocal() {
        List<NPC> filteredNPCs = new ArrayList<>();
        for (NPC npc : npcs) {
            if (!npc.isInteracting())
                continue;
            if (npc.getInteracting() == client.getLocalPlayer())
                filteredNPCs.add(npc);
        }
        npcs = filteredNPCs;
        return this;
    }

    public NPCQuery interactingWith(Actor actor) {
        List<NPC> filteredNPCs = new ArrayList<>();
        for (NPC npc : npcs) {
            if (!npc.isInteracting())
                continue;
            if (npc.getInteracting() == actor)
                filteredNPCs.add(npc);
        }
        npcs = filteredNPCs;
        return this;
    }

    public NPCQuery idInList(List<Integer> ids) {
        npcs = npcs.stream().filter(npcs -> ids.contains(npcs.getId())).collect(Collectors.toList());
        return this;
    }

    public NPCQuery withinWorldArea(WorldArea area) {
        npcs = npcs.stream().filter(npcs -> area.contains(npcs.getWorldLocation())).collect(Collectors.toList());
        return this;
    }

    public NPCQuery withinBounds(WorldPoint min, WorldPoint max) {
        int x1 = min.getX();
        int x2 = max.getX();
        int y1 = min.getY();
        int y2 = max.getY();

        npcs = npcs.stream().filter(npc -> {
            int x3 = npc.getWorldLocation().getX();
            int y3 = npc.getWorldLocation().getY();

            if (x3 > Math.max(x1, x2) || x3 < Math.min(x1, x2)) {
                return false;
            }

            return y3 <= Math.max(y1, y2) && y3 >= Math.min(y1, y2);
        }).collect(Collectors.toList());
        return this;
    }

    public NPCQuery indexIs(int index) {
        npcs = npcs.stream().filter(npcs -> npcs.getIndex() == index).collect(Collectors.toList());
        return this;
    }

    public boolean empty() {
        return npcs.size() == 0;
    }

    public NPCQuery filterUnique() {
        npcs =
                npcs.stream().collect(Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparingInt(NPC::getId))), ArrayList::new));
        return this;
    }

    public List<NPC> result() {
        return npcs;
    }

    public NPCQuery walkable() {
        npcs = npcs.stream().filter(npc -> EthanApiPlugin.canPathToTile(npc.getWorldLocation()).isReachable()).collect(Collectors.toList());
        return this;
    }

    public NPCQuery interacting() {
        npcs = npcs.stream().filter(Actor::isInteracting).collect(Collectors.toList());
        return this;
    }

    public NPCQuery notInteracting() {
        npcs = npcs.stream().filter(npc -> !npc.isInteracting()).collect(Collectors.toList());
        return this;
    }

    public NPCQuery noOneInteractingWith() {
        npcs = npcs.stream().filter(npc -> Players.search().interactingWith(npc).isEmpty()).collect(Collectors.toList());
        return this;
    }

    public NPCQuery playerInteractingWith() {
        npcs = npcs.stream().filter(npc -> client.getLocalPlayer().isInteracting() && client.getLocalPlayer().getInteracting() == npc).collect(Collectors.toList());
        return this;
    }

    public NPCQuery playerNotInteractingWith() {
        npcs = npcs.stream().filter(npc -> !client.getLocalPlayer().isInteracting() || client.getLocalPlayer().getInteracting() != npc).collect(Collectors.toList());
        return this;
    }

    public NPCQuery meleeable() {
        List<WorldPoint> meleeTiles = new ArrayList<>();
        meleeTiles.add(client.getLocalPlayer().getWorldLocation().dx(1));
        meleeTiles.add(client.getLocalPlayer().getWorldLocation().dx(-1));
        meleeTiles.add(client.getLocalPlayer().getWorldLocation().dy(1));
        meleeTiles.add(client.getLocalPlayer().getWorldLocation().dy(-1));
        npcs =
                npcs.stream().filter(npc -> !Collections.disjoint(meleeTiles, npc.getWorldArea().toWorldPointList()) && npc.getWorldArea().distanceTo(client.getLocalPlayer().getWorldLocation()) == 1).collect(Collectors.toList());
        return this;
    }

    public NPCQuery alive() {
        npcs = npcs.stream().filter(npc -> !npc.isDead() && npc.getHealthRatio() != 0).collect(Collectors.toList());
        return this;
    }

    public Optional<NPC> nearestToPlayer() {
        if (npcs.size() == 0) {
            return Optional.ofNullable(null);
        }
        return npcs.stream().min(Comparator.comparingInt(npc -> npc.getWorldLocation().distanceTo(client.getLocalPlayer().getWorldLocation())));
    }

    public Optional<NPC> nearestToPoint(WorldPoint point) {
        if (npcs.size() == 0) {
            return Optional.ofNullable(null);
        }
        return npcs.stream().min(Comparator.comparingInt(npc -> npc.getWorldLocation().distanceTo(point)));
    }

    public Optional<NPC> first() {
        if (npcs.size() == 0) {
            return Optional.ofNullable(null);
        }
        return Optional.ofNullable(npcs.get(0));
    }

    public static NPCComposition getNPCComposition(NPC npc) {
        if (npc.getComposition().getConfigs() == null || npc.getTransformedComposition() == null) {
            return npc.getComposition();
        }
        return npc.getTransformedComposition();
    }

    public Optional<NPC> nearestByPath() {
        HashMap<WorldPoint, NPC> npcMap = new HashMap<>();
        for (NPC npc : npcs) {
            for (WorldPoint wp : npc.getWorldArea().toWorldPointList()) {
                npcMap.put(wp, npc);
            }

            for (WorldPoint wp : WorldAreaUtility.objectInteractableTiles(npc)) {
                npcMap.put(wp, npc);
            }
        }
        List<WorldPoint> path = EthanApiPlugin.pathToGoalSetFromPlayerNoCustomTiles(new HashSet<>(npcMap.keySet()));
        if (path == null) {
            return Optional.empty();
        }
        if (path.isEmpty()) {
            if (npcMap.containsKey(client.getLocalPlayer().getWorldLocation())) {
                return Optional.ofNullable(npcMap.get(client.getLocalPlayer().getWorldLocation()));
            } else {
                return Optional.empty();
            }
        }
        return Optional.ofNullable(npcMap.get(path.get(path.size() - 1)));
    }
}
