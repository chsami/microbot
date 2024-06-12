package net.runelite.client.plugins.hoseaplugins.ethanapi.EthanApiPlugin.Collections;

import net.runelite.client.plugins.hoseaplugins.ethanapi.EthanApiPlugin.Collections.query.PlayerQuery;
import net.runelite.api.Client;
import net.runelite.api.Player;
import net.runelite.api.events.GameTick;
import net.runelite.client.RuneLite;
import net.runelite.client.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

public class Players {
    static List<Player> players = new ArrayList<>();
    static Client client = RuneLite.getInjector().getInstance(Client.class);

    public static PlayerQuery search() {
        return new PlayerQuery(players);
    }

    @Subscribe
    public void onGameTick(GameTick e) {
        players.clear();
        for (Player player : client.getPlayers()) {
            if (player == null)
                continue;
            players.add(player);
        }
    }
}
