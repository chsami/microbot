package net.runelite.client.plugins.hoseaplugins.NightmareHelper;

import net.runelite.client.plugins.hoseaplugins.ethanapi.EthanApiPlugin.Collections.query.QuickPrayer;
import net.runelite.client.plugins.hoseaplugins.ethanapi.EthanApiPlugin.EthanApiPlugin;
import net.runelite.client.plugins.hoseaplugins.ethanapi.InteractionApi.InteractionHelper;
import net.runelite.client.plugins.hoseaplugins.ethanapi.PacketUtils.PacketUtilsPlugin;
import net.runelite.client.plugins.hoseaplugins.ethanapi.Packets.MousePackets;
import net.runelite.client.plugins.hoseaplugins.ethanapi.Packets.WidgetPackets;
import com.google.inject.Inject;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.events.AnimationChanged;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.NpcSpawned;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDependency;
import net.runelite.client.plugins.PluginDescriptor;

import java.util.Arrays;

import static net.runelite.client.plugins.hoseaplugins.ethanapi.EthanApiPlugin.Collections.query.QuickPrayer.PROTECT_FROM_MAGIC;
import static net.runelite.client.plugins.hoseaplugins.ethanapi.EthanApiPlugin.Collections.query.QuickPrayer.PROTECT_FROM_MELEE;
import static net.runelite.client.plugins.hoseaplugins.ethanapi.EthanApiPlugin.Collections.query.QuickPrayer.PROTECT_FROM_MISSILES;


@PluginDescriptor(
        name = "<html><font color=\"#FF9DF9\">[PP]</font> NightmareHelper</html>",
        description = "",
        enabledByDefault = false,
        tags = {"ethan"}
)
public class NightmareHelperPlugin extends Plugin {
    @Inject
    Client client;
    boolean forceTab = false;
    QuickPrayer shouldPray;
    boolean cursed = false;
    int i = 10;

    @Subscribe
    public void onGameTick(GameTick event) {
        if (!inFight()) {
            return;
        }
        if (forceTab) {
            System.out.println("forcing tab");
            client.runScript(915, 3);
            forceTab = false;
        }
        if (client.getWidget(5046276) == null) {
            MousePackets.queueClickPacket();
            WidgetPackets.queueWidgetAction(client.getWidget(WidgetInfo.MINIMAP_QUICK_PRAYER_ORB), "Setup");
            forceTab = true;
        }
        handlePrayer();
        if (EthanApiPlugin.isQuickPrayerEnabled()) {
            InteractionHelper.togglePrayer();
        }
        InteractionHelper.togglePrayer();
    }

    @Override
    protected void startUp() {
        forceTab = false;
    }

    @Subscribe
    public void onAnimationChanged(AnimationChanged e) {
        if (e.getActor() != null && e.getActor().getName() != null && (e.getActor().getName().equalsIgnoreCase("the " +
                "nightmare") || e.getActor().getName().equalsIgnoreCase("phosani's nightmare"))) {
            if (e.getActor().getAnimation() == 8595) {
                shouldPray = PROTECT_FROM_MAGIC;
            } else if (e.getActor().getAnimation() == 8596) {
                shouldPray = QuickPrayer.PROTECT_FROM_MISSILES;
            } else if (e.getActor().getAnimation() == 8594) {
                shouldPray = QuickPrayer.PROTECT_FROM_MELEE;
            }
        }
    }

    public boolean inFight() {
        return Arrays.stream(client.getMapRegions()).anyMatch(x -> x == 15258);
    }

    public void handlePrayer() {
        if (shouldPray == null) {
            return;
        }
        if (shouldPray == PROTECT_FROM_MAGIC) {
            if (!cursed) {
                if (!EthanApiPlugin.isQuickPrayerActive(PROTECT_FROM_MAGIC)) {
                    MousePackets.queueClickPacket();
                    WidgetPackets.queueWidgetActionPacket(1, 5046276, -1, 12); //quickPrayer magic
                }
                return;
            }
            if (!EthanApiPlugin.isQuickPrayerActive(PROTECT_FROM_MELEE)) {
                MousePackets.queueClickPacket();
                WidgetPackets.queueWidgetActionPacket(1, 5046276, -1, 14); //quickPrayer melee
            }
            return;
        }
        if (shouldPray == PROTECT_FROM_MELEE) {
            if (!cursed) {
                if (!EthanApiPlugin.isQuickPrayerActive(PROTECT_FROM_MELEE)) {
                    MousePackets.queueClickPacket();
                    WidgetPackets.queueWidgetActionPacket(1, 5046276, -1, 14); //quickPrayer melee
                }
                return;
            }
            if (!EthanApiPlugin.isQuickPrayerActive(PROTECT_FROM_MISSILES)) {
                MousePackets.queueClickPacket();
                WidgetPackets.queueWidgetActionPacket(1, 5046276, -1, 13); //quickPrayer range
            }
            return;
        }
        if (shouldPray == PROTECT_FROM_MISSILES) {
            if (!cursed) {
                if (!EthanApiPlugin.isQuickPrayerActive(PROTECT_FROM_MISSILES)) {
                    MousePackets.queueClickPacket();
                    WidgetPackets.queueWidgetActionPacket(1, 5046276, -1, 13); //quickPrayer range
                }
                return;
            }
            if (!EthanApiPlugin.isQuickPrayerActive(PROTECT_FROM_MAGIC)) {
                MousePackets.queueClickPacket();
                WidgetPackets.queueWidgetActionPacket(1, 5046276, -1, 12); //quickPrayer magic
            }
        }
    }

    @Subscribe
    public void onNpcSpawned(NpcSpawned e) {
        if (!inFight()) {
        }

    }

    @Subscribe
    public void onChatMessage(ChatMessage e) {
        if (e.getType() != ChatMessageType.GAMEMESSAGE) {
            return;
        }
        if (e.getMessage().toLowerCase().contains("the nightmare has cursed you, shuffling your prayers!")) {
            cursed = true;
        }
        if (e.getMessage().toLowerCase().contains("you feel the effects of the nightmare's curse wear off.")) {
            cursed = false;
        }
    }
}
