package net.runelite.client.plugins.hoseaplugins.AutoCombatv2.tasks;

import net.runelite.client.plugins.hoseaplugins.AutoCombatv2.AutoCombatv2Config;
import net.runelite.client.plugins.hoseaplugins.AutoCombatv2.AutoCombatv2Plugin;
import net.runelite.client.plugins.hoseaplugins.PiggyUtils.strategy.AbstractTask;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CheckCombatStatus extends AbstractTask<AutoCombatv2Plugin, AutoCombatv2Config> {
    public CheckCombatStatus(AutoCombatv2Plugin plugin, AutoCombatv2Config config) {
        super(plugin, config);
    }

    @Override
    public boolean validate() {
        return true; // Always run to continuously check and update combat status
    }

    @Override
    public void execute() {
        // Check both if the player is interacting and if there is an interaction target
        boolean currentlyInCombat = plugin.getClient().getLocalPlayer().isInteracting() &&
                plugin.getClient().getLocalPlayer().getInteracting() != null;

        if (currentlyInCombat != plugin.inCombat) {
            plugin.inCombat = currentlyInCombat;
            log.info("Combat status updated: Currently in combat = {}", plugin.inCombat);
        }
    }
}