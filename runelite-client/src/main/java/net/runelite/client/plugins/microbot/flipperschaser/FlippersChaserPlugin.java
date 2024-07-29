package net.runelite.client.plugins.microbot.flipperschaser;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.*;
import net.runelite.client.Notifier;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.chat.ChatMessageManager;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.discord.DiscordWebhook;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.microbot.util.keyboard.Rs2Keyboard;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.mouse.Mouse;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;
import javax.swing.*;
import java.awt.event.KeyEvent;

@PluginDescriptor(
    name = PluginDescriptor.Bttqjs + "Flippers Chaser",
    description = "Automates obtaining Flippers from Mogres",
    tags = {"service", "automation", "combat", "microbot"}
)
@Slf4j
public class FlippersChaserPlugin extends Plugin {

    @Inject
    private Client client;

    @Inject
    private ClientThread clientThread;

    @Inject
    private Notifier notifier;

    @Inject
    private ItemManager itemManager;

    @Inject
    private ChatMessageManager chatMessageManager;

    @Inject
    private OverlayManager overlayManager;

    @Inject
    private FlippersChaserConfig config;

    private boolean inCombat;
    private boolean flippersObtained;

    @Provides
    FlippersChaserConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(FlippersChaserConfig.class);
    }

    @Override
    protected void startUp() throws Exception {
        log.info("Flippers Chaser started!");
        overlayManager.add(new FlippersChaserOverlay(this));
        inCombat = false;
        flippersObtained = false;
    }

    @Override
    protected void shutDown() throws Exception {
        log.info("Flippers Chaser stopped!");
        overlayManager.remove(new FlippersChaserOverlay(this));
    }

    @Subscribe
    public void onGameTick(GameTick event) {
        if (flippersObtained) {
            return;
        }

        if (config.usePrayer() && client.getBoostedSkillLevel(Skill.PRAYER) <= 5) {
            Rs2Inventory.useItem(ItemID.PRAYER_POTION4);
        }

        if (config.useFood() && client.getBoostedSkillLevel(Skill.HITPOINTS) <= 20) {
            useFood(config.foodType());
        }

        if (!inCombat) {
            useFishingExplosive();
        }
    }

    @Subscribe
    public void onNpcSpawned(NpcSpawned event) {
        NPC npc = event.getNpc();
        if (npc.getName().equals("Mogre")) {
            inCombat = true;
            clientThread.invoke(() -> attackNpc(npc));
        }
    }

    @Subscribe
    public void onNpcLootReceived(NpcLootReceived event) {
        for (Item item : event.getItems()) {
            if (item.getId() == ItemID.FLIPPERS) {
                flippersObtained = true;
                handleSuccess();
                break;
            }
        }
    }

    private void useFishingExplosive() {
        NPC fishingSpot = findFishingSpot();
        if (fishingSpot != null) {
            Rs2Inventory.useItemOnNpc(ItemID.FISHING_EXPLOSIVE, fishingSpot);
        }
    }

    private NPC findFishingSpot() {
        return client.getNpcs().stream()
            .filter(npc -> npc.getName().equals("Ominous Fishing Spot"))
            .findFirst()
            .orElse(null);
    }

    private void attackNpc(NPC npc) {
        Mouse.click(npc.getCanvasTilePoly().getBounds());
        if (config.usePrayer()) {
            Rs2Player.togglePrayer(Prayer.PROTECT_FROM_MELEE);
        }
    }

    private void useFood(String foodType) {
        int foodId;
        switch (foodType) {
            case "Cooked karambwan":
                foodId = ItemID.COOKED_KARAMBWAN;
                break;
            case "Monkfish":
                foodId = ItemID.MONKFISH;
                break;
            case "Shark":
            default:
                foodId = ItemID.SHARK;
                break;
        }
        Rs2Inventory.useItem(foodId);
    }

    private void handleSuccess() {
        if (config.useDiscordWebhook()) {
            sendDiscordNotification(true);
        }
        teleportOrLogout();
    }

    private void handleFailure() {
        if (config.useDiscordWebhook()) {
            sendDiscordNotification(false);
        }
        logOut();
    }

    private void teleportOrLogout() {
        if (Rs2Inventory.containsItem(ItemID.TELEPORT_TO_HOUSE)) {
            Rs2Inventory.useItem(ItemID.TELEPORT_TO_HOUSE);
        } else {
            logOut();
        }
    }

    private void logOut() {
        Rs2Keyboard.keyPress(KeyEvent.VK_ESCAPE);
        clientThread.invoke(() -> client.runScript(ScriptID.LOG_OUT, 1));
    }

    private void sendDiscordNotification(boolean success) {
        String url = config.discordWebhookUrl();
        DiscordWebhook webhook = new DiscordWebhook(url);
        if (success) {
            webhook.addEmbed(new DiscordWebhook.EmbedObject()
                .setTitle("[Success]")
                .setDescription(client.getLocalPlayer().getName() + " has obtained Flippers successfully!")
                .setColor("#B4E380")
                .setImage(client.getScreenshot()));
        } else {
            webhook.addEmbed(new DiscordWebhook.EmbedObject()
                .setTitle("[Failure]")
                .setDescription(client.getLocalPlayer().getName() + " has no food in inventory, logging out.")
                .setColor("#FF4C4C")
                .setImage(client.getScreenshot()));
        }
        webhook.execute();
    }

    @Subscribe
    public void onPlayerDespawned(PlayerDespawned event) {
        if (event.getPlayer().getName().equals(client.getLocalPlayer().getName())) {
            handleFailure();
        }
    }

    @Subscribe
    public void onItemContainerChanged(ItemContainerChanged event) {
        if (event.getContainerId() == InventoryID.INVENTORY.getId() && !Rs2Inventory.containsFood()) {
            handleFailure();
        }
    }
}
