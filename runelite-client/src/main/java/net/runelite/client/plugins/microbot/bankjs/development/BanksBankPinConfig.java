package net.runelite.client.plugins.microbot.bankjs.development;

import net.runelite.client.config.*;

@ConfigGroup("example")
public interface BanksBankPinConfig extends Config {

    @ConfigSection(
            name = "Pin Settings",
            description = "Set Bank Pin",
            position = 0,
            closedByDefault = false
    )
    String pinSection = "itemSection";

    // Items
    @ConfigItem(
            keyName = "Bank Pin",
            name = "Bank Pin",
            description = "Sets Bank Pin",
            secret = true,
            position = 0,
            section = pinSection
    )


    default String bankPin() {
        return "";
    }


}
