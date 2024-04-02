package net.runelite.client.plugins.nateplugins.combat.nateteleporter;

import net.runelite.api.Skill;
import net.runelite.api.widgets.Widget;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.util.globval.enums.InterfaceTab;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Item;
import net.runelite.client.plugins.microbot.util.magic.Rs2Magic;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;
import net.runelite.client.plugins.microbot.util.tabs.Rs2Tab;

import java.util.concurrent.TimeUnit;


public class TeleportScript extends Script {

    public static double version = 1.2;

    public boolean run(TeleporterConfig config) {
        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            if (!super.run()) return;
            if (!Microbot.isLoggedIn()) return;

            try {
                if (!config.highAlchemy() && (Microbot.isMoving() || Microbot.isAnimating() || Microbot.pauseAllScripts)) return;

                if (config.highAlchemy()) {

                    Rs2Item item = Rs2Inventory.get(config.highAlchemyItem());
                    if (item == null) {
                        Microbot.showMessage("Item: " + config.highAlchemyItem() + " not found in your inventory.");
                        return;
                    }
                    Rs2Magic.highAlch(item);
                    sleepUntil(() -> Rs2Tab.getCurrentTab() == InterfaceTab.MAGIC);
                }

                if (config.SPELL().getTeleport().getLevel() > Microbot.getClient().getBoostedSkillLevel(Skill.MAGIC)) {
                    Microbot.showMessage("Level not high enough!");
                    shutdown();
                } else if (Microbot.getClient().getWidget(config.SPELL().getTeleport().getSpell().getWidgetId()).getSpriteId() != config.SPELL().getTeleport().getSpell().getSprite()) {
                    Microbot.showMessage("Missing required items!");
                    shutdown();
                } else {
                    Rs2Magic.cast(config.SPELL().getTeleport().getSpell());
                }
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }, 0, 1000, TimeUnit.MILLISECONDS);
        return true;
    }
}
