package net.runelite.client.plugins.microbot.magic.orbcharger.scripts;

import net.runelite.api.ItemID;
import net.runelite.api.Player;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.magic.orbcharger.OrbChargerPlugin;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;
import net.runelite.client.plugins.microbot.util.player.Rs2Pvp;

import javax.inject.Inject;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class PlayerDetectionScript extends Script {

    private final OrbChargerPlugin plugin;

    private final int[] airStaves = {
            ItemID.STAFF_OF_AIR,
            ItemID.AIR_BATTLESTAFF,
            ItemID.MYSTIC_AIR_STAFF,
    };

    @Inject
    public PlayerDetectionScript(OrbChargerPlugin plugin) {
        this.plugin = plugin;
    }

    public boolean run() {
        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            try {
                if (!super.run()) return;
                if (Rs2Pvp.isInWilderness() && Microbot.isLoggedIn()) {
                    List<Player> currentDangerousPlayers = plugin.getDangerousPlayers();
                    
                    List<Player> newDangerousPlayers = Rs2Player.getPlayersInCombatLevelRange().stream()
                            .filter(player -> !Rs2Player.hasPlayerEquippedItem(player, airStaves))
                            .filter(player -> !currentDangerousPlayers.contains(player))
                            .collect(Collectors.toList());

                    if (!newDangerousPlayers.isEmpty()) {
                        currentDangerousPlayers.addAll(newDangerousPlayers);
                        plugin.setDangerousPlayers(currentDangerousPlayers);
                    }
                } else {
                    if (!plugin.getDangerousPlayers().isEmpty()) {
                        plugin.setDangerousPlayers(Collections.emptyList());
                    }
                }
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }, 0, 1000, TimeUnit.MILLISECONDS);
        return true;
    }

    @Override
    public void shutdown() {
        super.shutdown();
    }
}
