package net.runelite.client.plugins.hoseaplugins.AutoAerial;

import net.runelite.client.plugins.hoseaplugins.ethanapi.EthanApiPlugin.Collections.*;
import net.runelite.client.plugins.hoseaplugins.ethanapi.EthanApiPlugin.Collections.query.NPCQuery;
import net.runelite.client.plugins.hoseaplugins.ethanapi.EthanApiPlugin.EthanApiPlugin;
import net.runelite.client.plugins.hoseaplugins.ethanapi.PacketUtils.PacketUtilsPlugin;
import net.runelite.client.plugins.hoseaplugins.ethanapi.Packets.MousePackets;
import net.runelite.client.plugins.hoseaplugins.ethanapi.Packets.NPCPackets;
import net.runelite.client.plugins.hoseaplugins.ethanapi.Packets.WidgetPackets;
import com.google.inject.Inject;
import com.google.inject.Provides;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.events.GameTick;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.input.KeyManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDependency;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.util.HotkeyListener;
import net.runelite.client.util.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@PluginDescriptor(name = "<html><font color=\"#FF9DF9\">[PP]</font> AutoAerial</html>",
        description = "",
        enabledByDefault = false,
        tags = {"piggy","plugin"})
@Slf4j
public class AutoAerialPlugin extends Plugin {

    public int timeout = 0;
    public int idleTicks = 0;
    public boolean started = false;

    @Inject
    AutoAerialConfig config;
    @Inject
    Client client;
    @Inject
    private KeyManager keyManager;

    @Provides
    public AutoAerialConfig getConfig(ConfigManager configManager) {
        return configManager.getConfig(AutoAerialConfig.class);
    }


    @Override
    public void startUp() {
        timeout = 0;
        keyManager.registerKeyListener(toggle);
    }

    @Override
    public void shutDown() {
        timeout = 0;
        started = false;
        keyManager.unregisterKeyListener(toggle);
    }

    @Subscribe
    public void onGameTick(GameTick event) {
        Player player = client.getLocalPlayer();

        if (!started || EthanApiPlugin.isMoving()) return;


        if (timeout > 0) {
            timeout--;
            return;
        }
        idleTicks = client.getLocalPlayer().getAnimation() == -1 ? idleTicks + 1 : 0;

        doAerial2();
    }

    private void doAerial2() {
        final String[] FISH_NAMES = new String[]{"Bluegill", "Common tench", "Mottled eel", "Greater siren"};
        Deque<Projectile> projectiles = client.getProjectiles();
        ArrayList<Projectile> projectileList = new ArrayList<>();
        projectiles.forEach(projectileList::add);

        Optional<Widget> fish = Inventory.search().nameInList(List.of(FISH_NAMES)).first();
        Optional<Widget> knife = Inventory.search().withName("Knife").first();

        Optional<NPC> validFishingSpots = NPCs.search().withName("Fishing spot").nearestToPlayer().filter(npc -> {
            boolean isSpotInteractedWith = !Players.search()
                    .filter(p -> p.getInteracting() != null && p.getInteracting().equals(npc)).isEmpty();

            boolean isSpotTargetedByProjectile = projectileList.stream().anyMatch(projectile ->
                    projectile.getTarget() != null &&
                            projectile.getTarget().equals(npc.getLocalLocation()));
//
//            log.info("isSpotInteractedWith: " + isSpotInteractedWith);
//            log.info("isSpotTargetedByProjectile: " + isSpotTargetedByProjectile);

            if (isSpotInteractedWith || isSpotTargetedByProjectile) {
                return false;
            }
            return true;
        });
        Optional<NPC> arrowFishSpot = validFishingSpots.filter(npc -> client.getHintArrowNpc() == npc);

        if (knife.isPresent() && fish.isPresent()) {
            log.info("cutting fish");
            MousePackets.queueClickPacket();
            WidgetPackets.queueWidgetOnWidget(knife.get(), fish.get());
            timeout = 2;
        }

        if (arrowFishSpot.isPresent()) {
            log.info("arrow fishing");
            MousePackets.queueClickPacket();
            NPCPackets.queueNPCAction(arrowFishSpot.get(), "Catch");
        }

        if (validFishingSpots.isPresent()) {
            log.info("fishing");
            MousePackets.queueClickPacket();
            NPCPackets.queueNPCAction(validFishingSpots.get(), "Catch");
        }
    }


    private boolean staminaIsActive() {
        return client.getVarbit(Varbits.RUN_SLOWED_DEPLETION_ACTIVE).equals(1);
    }

    private boolean runIsOff() {
        return EthanApiPlugin.getClient().getVarpValue(173) == 0;
    }

    private void checkRunEnergy() {
        if (runIsOff() && client.getEnergy() >= 10 * 100) {
            toggleRunEnergy();
        }
        if (!staminaIsActive()) {
            if (client.getEnergy() >= 1000 && client.getEnergy() < 7000) {
                Inventory.search().nameContains("tamina potion").onlyUnnoted().first().ifPresent(potion -> {
                    MousePackets.queueClickPacket();
                    WidgetPackets.queueWidgetAction(potion, "Drink");
                    timeout = 3;
                });
            }
        }
    }

    private static void toggleRunEnergy() {
        log.info("turning run on");
        MousePackets.queueClickPacket();
        WidgetPackets.queueWidgetActionPacket(1, 10485787, -1, -1);
    }

    private final HotkeyListener toggle = new HotkeyListener(() -> config.toggle()) {
        @Override
        public void hotkeyPressed() {
            toggle();
        }
    };

    public void toggle() {
        if (client.getGameState() != GameState.LOGGED_IN) {
            return;
        }
        started = !started;
    }
}
