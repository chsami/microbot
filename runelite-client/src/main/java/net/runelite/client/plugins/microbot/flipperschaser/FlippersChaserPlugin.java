package net.runelite.client.plugins.microbot.flipperschaser;


import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.api.events.NpcSpawned;
import net.runelite.api.events.PlayerDespawned;
import net.runelite.client.Notifier;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.chat.ChatMessageManager;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.NpcLootReceived;
import net.runelite.client.game.ItemManager;
import net.runelite.client.game.ItemStack;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.npc.Rs2Npc;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;
import net.runelite.client.plugins.microbot.util.prayer.Rs2Prayer;
import net.runelite.client.plugins.microbot.util.prayer.Rs2PrayerEnum;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;


@PluginDescriptor(
    name = PluginDescriptor.Bttqjs + "Flippers Chaser",
    description = "Automates obtaining Flippers from Mogres",
    tags = {"service", "automation", "combat", "microbot"},
    enabledByDefault = false
)
@Slf4j
public class FlippersChaserPlugin extends Plugin {

    @Inject
    private Client client;

    @Inject
    private ClientThread clientThread;

    @Inject
    private OverlayManager overlayManager;

    @Inject
    public FlippersChaserConfig config;

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

        Rs2Player.drinkPrayerPotionAt(5);
        Rs2Player.eatAt(20);

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
        for (ItemStack item : event.getItems()) {
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
        Rs2Npc.interact(npc);
        if (Rs2Player.hasPrayerPoints()) {
            Rs2Prayer.toggle(Rs2PrayerEnum.PROTECT_MELEE);
        }
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
        if (Rs2Inventory.hasItem(ItemID.TELEPORT_TO_HOUSE)) {
            Rs2Inventory.use(ItemID.TELEPORT_TO_HOUSE);
        } else {
            logOut();
        }
    }

    private void logOut() {
        Rs2Player.logout();
    }

    private void sendDiscordNotification(boolean success) {
        String url = config.discordWebhookUrl();
        DiscordWebhook webhook = new DiscordWebhook(url);
        if (success) {
            webhook.addEmbed(new DiscordWebhook.EmbedObject()
                .setTitle("[Success]")
                .setDescription(client.getLocalPlayer().getName() + " has obtained Flippers successfully!")
                .setColor("#B4E380"));
        } else {
            webhook.addEmbed(new DiscordWebhook.EmbedObject()
                .setTitle("[Failure]")
                .setDescription(client.getLocalPlayer().getName() + " has no food in inventory, logging out.")
                .setColor("#FF4C4C"));
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
        if (event.getContainerId() == InventoryID.INVENTORY.getId() && Rs2Inventory.getInventoryFood().isEmpty()) {
            handleFailure();
        }
    }

    // Nested DiscordWebhook class for simplicity
    private static class DiscordWebhook {
        private final String webhookUrl;
        private final StringBuilder json;

        public DiscordWebhook(String webhookUrl) {
            this.webhookUrl = webhookUrl;
            this.json = new StringBuilder();
            this.json.append("{\"embeds\":[");
        }

        public void addEmbed(EmbedObject embed) {
            if (json.length() > 11) {
                json.append(",");
            }
            json.append(embed.toJson());
        }

        public void execute() {
            try {
                json.append("]}");
                byte[] postData = json.toString().getBytes(StandardCharsets.UTF_8);
                URL url = new URL(webhookUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setDoOutput(true);
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("Content-Length", String.valueOf(postData.length));
                try (OutputStream os = conn.getOutputStream()) {
                    os.write(postData);
                }
                conn.getInputStream(); // Ensure the request is sent
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public static class EmbedObject {
            private final StringBuilder json;

            public EmbedObject() {
                this.json = new StringBuilder();
                this.json.append("{");
            }

            public EmbedObject setTitle(String title) {
                appendComma();
                json.append("\"title\":\"").append(title).append("\"");
                return this;
            }

            public EmbedObject setDescription(String description) {
                appendComma();
                json.append("\"description\":\"").append(description).append("\"");
                return this;
            }

            public EmbedObject setColor(String color) {
                appendComma();
                json.append("\"color\":\"").append(Integer.parseInt(color.substring(1), 16)).append("\"");
                return this;
            }

            private void appendComma() {
                if (json.length() > 1 && json.charAt(json.length() - 1) != '{') {
                    json.append(",");
                }
            }

            public String toJson() {
                return json.append("}").toString();
            }
        }
    }
}
