package net.runelite.client.plugins.hoseaplugins.harpoon2ticker;

import net.runelite.client.plugins.hoseaplugins.ethanapi.EthanApiPlugin.Collections.Inventory;
import net.runelite.client.plugins.hoseaplugins.ethanapi.EthanApiPlugin.Collections.NPCs;
import net.runelite.client.plugins.hoseaplugins.ethanapi.EthanApiPlugin.EthanApiPlugin;
import net.runelite.client.plugins.hoseaplugins.ethanapi.PacketUtils.PacketUtilsPlugin;
import net.runelite.client.plugins.hoseaplugins.ethanapi.Packets.MousePackets;
import net.runelite.client.plugins.hoseaplugins.ethanapi.Packets.MovementPackets;
import net.runelite.client.plugins.hoseaplugins.ethanapi.Packets.NPCPackets;
import net.runelite.client.plugins.hoseaplugins.ethanapi.Packets.WidgetPackets;
import com.google.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.NPC;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.HitsplatApplied;
import net.runelite.api.widgets.Widget;
import net.runelite.client.Notifier;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDependency;
import net.runelite.client.plugins.PluginDescriptor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@PluginDescriptor(
        name = "<html><font color=\"#FF9DF9\">[PP]</font> SwordFish2Tick </html>",
        enabledByDefault = false,
        tags = {"ethan"}
)
public class SwordFish2Tick extends Plugin {
    int lastHitsplat = 0;
    @Inject
    Notifier notifier;
    @Inject
    Client client;
    List<WorldPoint> movements = new ArrayList<>();
    boolean moving = false;

    @Subscribe
    public void onGameTick(GameTick e) {
        if (client.getGameState() != GameState.LOGGED_IN) return;
        if(!NPCs.search().withAction("Dismiss").interactingWithLocal().empty()) return;
        WorldPoint playerLocation = client.getLocalPlayer().getWorldLocation();
        NPC fishingSpot = client.getNpcs().stream().filter(x -> x.getId() == 7470).min(Comparator.comparingInt(x -> x.getWorldLocation().distanceTo(client.getLocalPlayer().getWorldLocation()))).orElse(null);
        if (fishingSpot == null || (fishingSpot.getWorldLocation().getX() == playerLocation.getX() && fishingSpot.getWorldLocation().getY() == playerLocation.getY() + 1)) {
            return;
        }
        if (movements.size() > 0) {
            WorldPoint position = new WorldPoint(movements.get(0).getX(), movements.get(0).getY(),
                    movements.get(0).getPlane());
            //client.addChatMessage(ChatMessageType.GAMEMESSAGE, "2TFisher", "moving", null);
            MousePackets.queueClickPacket();
            MovementPackets.queueMovement(position);
            movements.remove(0);
            if (movements.size() == 0) {
                moving = false;
            }
            return;
        }
        moving = true;
        if (client.getLocalPlayer().getWorldLocation().getX() == 1762) {
            //client.addChatMessage(ChatMessageType.GAMEMESSAGE, "2TFisher","setting movement list 1", null);
            WorldPoint[] array = {playerLocation.dx(1), playerLocation.dx(2), playerLocation.dx(3)};
            movements = new ArrayList<>(Arrays.asList(array));
        } else {
            //client.addChatMessage(ChatMessageType.GAMEMESSAGE, "2TFisher","setting movement list 2", null);
            WorldPoint[] array = {playerLocation.dx(-1).dy(-1), playerLocation.dx(-2).dy(-1), playerLocation.dx(-3).dy(-1),
                    playerLocation.dx(-4).dy(-1), playerLocation.dx(-3)};
            movements = new ArrayList<>(Arrays.asList(array));
        }
        //client.addChatMessage(ChatMessageType.GAMEMESSAGE, "2TFisher", "moving", null);
        MousePackets.queueClickPacket();
        MovementPackets.queueMovement(movements.get(0));
        movements.remove(0);
    }

    @Subscribe
    public void onHitsplatApplied(HitsplatApplied e) {
        if (lastHitsplat == 0) {
            lastHitsplat = client.getTickCount();
        } else {
            if (client.getTickCount() - lastHitsplat != 2) {
                notifier.notify("2 tick fisher failed somehow");
                EthanApiPlugin.stopPlugin(this);
                return;
            }
            lastHitsplat = client.getTickCount();
        }
        if(!NPCs.search().withAction("Dismiss").interactingWithLocal().empty()) return;
        if (!e.getActor().equals(client.getLocalPlayer())) return;
        NPC fishingSpot = client.getNpcs().stream().filter(x -> x.getId() == 7470).min(Comparator.comparingInt(x -> x.getWorldLocation().distanceTo(client.getLocalPlayer().getWorldLocation()))).orElse(null);
        WorldPoint playerLocation = client.getLocalPlayer().getWorldLocation();
        if (fishingSpot == null || !(fishingSpot.getWorldLocation().getX() == playerLocation.getX() && fishingSpot.getWorldLocation().getY() == playerLocation.getY() + 1) || moving) {
            return;
        }
        Optional<Widget> fish = Inventory.search().idInList(List.of(371, 359)).first();
        if (fish.isPresent()) {
            //client.addChatMessage(ChatMessageType.GAMEMESSAGE, "2TFisher","dropping fish", null);
            MousePackets.queueClickPacket();
            WidgetPackets.queueWidgetAction(fish.get(), "Drop");
        }
        //client.addChatMessage(ChatMessageType.GAMEMESSAGE, "2TFisher","fishing", null);
        MousePackets.queueClickPacket();
        NPCPackets.queueNPCAction(fishingSpot, "Harpoon");
    }

    @Override
    public void startUp() {
        lastHitsplat = 0;
        moving = false;
        movements = new ArrayList<>();
    }
}
