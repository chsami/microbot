package net.runelite.client.plugins.microbot.bankjs.development.BanksSlayer;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;
import net.runelite.client.plugins.microbot.bankjs.development.BanksShopper.Actions;
import net.runelite.client.plugins.microbot.bankjs.development.BanksShopper.Quantities;
import net.runelite.client.plugins.microbot.bankjs.development.BanksSlayer.enums.SlayerMasters;

@ConfigGroup("banksSlayer")
public interface BanksSlayerConfig extends Config {

    @ConfigSection(
            name = "Slayer Master Settings",
            description = "Slayer Master Settings",
            position = 0,
            closedByDefault = false
    )
    String slayerMasterSection = "slayerMasterSection";

    // Object or NPC to trade with
    @ConfigItem(
            keyName = "Slayer Master Name",
            name = "Slayer Master Name",
            description = "Sets Slayer Master to Use.",
            position = 0,
            section = slayerMasterSection
    )

    default SlayerMasters slayerMaster() {
        return SlayerMasters.TURAEL;
    }
}
