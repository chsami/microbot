package net.runelite.client.plugins.hoseaplugins.ethanapi.EthanApiPlugin.Collections.query;

import net.runelite.client.plugins.hoseaplugins.ethanapi.EthanApiPlugin.EthanApiPlugin;
import net.runelite.client.plugins.hoseaplugins.ethanapi.EthanApiPlugin.PathFinding.GlobalCollisionMap;
import net.runelite.client.plugins.hoseaplugins.ethanapi.EthanApiPlugin.Utility.WorldAreaUtility;
import net.runelite.api.Actor;
import net.runelite.api.Client;
import net.runelite.api.NPC;
import net.runelite.api.Player;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.RuneLite;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class PlayerQuery {
    static Client client = RuneLite.getInjector().getInstance(Client.class);
    private List<Player> players;

    public PlayerQuery(List<Player> players) {
        this.players = new ArrayList<>(players);
    }

    public PlayerQuery withName(String name) {
        List<Player> filteredPlayers = new ArrayList<>();
        for (Player player : players) {
            if (player.getName().equals(name))
                filteredPlayers.add(player);
        }
        players = filteredPlayers;
        return this;
    }

    public PlayerQuery withinWorldArea(WorldArea area) {
        players = players.stream().filter(player -> area.contains(player.getWorldLocation())).collect(Collectors.toList());
        return this;
    }

    public PlayerQuery filter(Predicate<? super Player> predicate) {
        players = players.stream().filter(predicate).collect(Collectors.toList());
        return this;
    }

    public PlayerQuery interactingWithLocal() {
        List<Player> filteredPlayers = new ArrayList<>();
        for (Player player : players) {
            if (!player.isInteracting())
                continue;
            if (player.getInteracting() == client.getLocalPlayer())
                filteredPlayers.add(player);
        }
        players = filteredPlayers;
        return this;
    }

    public PlayerQuery interactingWith(Actor actor) {
        List<Player> filteredPlayers = new ArrayList<>();
        for (Player player : players) {
            if (!player.isInteracting())
                continue;
            if (player.getInteracting() == actor)
                filteredPlayers.add(player);
        }
        players = filteredPlayers;
        return this;
    }

    public PlayerQuery nameContains(String name) {
        List<Player> filteredPlayers = new ArrayList<>();
        for (Player player : players) {
            if (player.getName().contains(name))
                filteredPlayers.add(player);
        }
        players = filteredPlayers;
        return this;
    }

    public PlayerQuery notLocalPlayer() {
        players.remove(client.getLocalPlayer());
        return this;
    }

    public PlayerQuery withinLevel(int low, int high) {
        List<Player> filteredPlayers = new ArrayList<>();
        for (Player player : players) {
            if (player.getCombatLevel() >= low && player.getCombatLevel() <= high)
                filteredPlayers.add(player);
        }
        players = filteredPlayers;
        return this;
    }

    public boolean isEmpty() {
        return players.isEmpty();
    }

    public Optional<Player> first() {
        if (players.isEmpty())
            return Optional.empty();
        return Optional.ofNullable(players.get(0));
    }

    public List<Player> result() {
        return players;
    }

    public Optional<Player> nearestByPath() {
        HashMap<WorldPoint, Player> playerMap = new HashMap<>();
        for (Player player : players) {
            for (WorldPoint wp : player.getWorldArea().toWorldPointList()) {
                playerMap.put(wp, player);
            }

            for (WorldPoint wp : WorldAreaUtility.objectInteractableTiles(player)) {
                playerMap.put(wp, player);
            }
        }
        List<WorldPoint> path = EthanApiPlugin.pathToGoalSetFromPlayerNoCustomTiles(new HashSet<>(playerMap.keySet()));
        if (path == null) {
            return Optional.empty();
        }
        if (path.isEmpty()) {
            if (playerMap.containsKey(client.getLocalPlayer().getWorldLocation())) {
                return Optional.ofNullable(playerMap.get(client.getLocalPlayer().getWorldLocation()));
            } else {
                return Optional.empty();
            }
        }
        return Optional.ofNullable(playerMap.get(path.get(path.size() - 1)));
    }
}
