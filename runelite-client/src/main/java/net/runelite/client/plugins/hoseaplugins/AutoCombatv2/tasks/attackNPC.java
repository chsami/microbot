package net.runelite.client.plugins.hoseaplugins.AutoCombatv2.tasks;

import net.runelite.client.plugins.hoseaplugins.ethanapi.PacketUtils.WidgetInfoExtended;
import net.runelite.client.plugins.hoseaplugins.ethanapi.Packets.MousePackets;
import net.runelite.client.plugins.hoseaplugins.ethanapi.Packets.WidgetPackets;
import net.runelite.client.plugins.hoseaplugins.AutoCombatv2.AutoCombatv2Config;
import net.runelite.client.plugins.hoseaplugins.PiggyUtils.strategy.AbstractTask;
import net.runelite.client.plugins.hoseaplugins.AutoCombatv2.AutoCombatv2Plugin;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.plugins.hoseaplugins.ethanapi.InteractionApi.NPCInteraction;
import lombok.Getter;
import net.runelite.api.NPC;

@Getter
@Slf4j
public class attackNPC extends AbstractTask<AutoCombatv2Plugin, AutoCombatv2Config> {
    public attackNPC(AutoCombatv2Plugin plugin, AutoCombatv2Config config) {
        super(plugin, config);
    }

    @Override
    public boolean validate() {
        return !plugin.inCombat && plugin.getLootQueue().isEmpty();
    }

    @Override
    public void execute() {
        if (plugin.getClient() == null) {
            log.error("Client is null");
            return;
        }
        if (plugin.getClient().getNpcs() == null) {
            log.error("NPC list is null");
            return;
        }
        NPC targetNPC = findNPC(config.npcTarget());
        if (targetNPC != null) {
            log.info("Attacking NPC: {}", config.npcTarget());
            NPCInteraction.interact(targetNPC, "Attack");
        } else {
            log.info("NPC not found: {}", config.npcTarget());
        }
    }

    public NPC findNPC(String npcName) {
        if (plugin.getClient() == null || plugin.getClient().getNpcs() == null) {
            log.warn("Client or NPC list not initialized");
            return null;
        }
        return plugin.getClient().getNpcs().stream()
                .filter(npc -> npc.getName() != null && npc.getName().contains(npcName))
                .findFirst()
                .orElse(null);
    }
}