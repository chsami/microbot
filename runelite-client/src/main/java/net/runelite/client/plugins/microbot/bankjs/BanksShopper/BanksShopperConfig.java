package net.runelite.client.plugins.microbot.bankjs.BanksShopper;

import net.runelite.client.config.*;

@ConfigGroup(BanksShopperConfig.configGroup)
public interface BanksShopperConfig extends Config {
    
    String configGroup = "banks-shopper";
    String npcName = "npcName";
    String itemNames = "itemNames";
    String minStock = "minStock";
    String action = "action";
    String quantity = "quantity";
    String useBank = "useBank";
    String logout = "logout";

    @ConfigSection(
            name = "Shop Settings",
            description = "Shop Settings",
            position = 0,
            closedByDefault = false
    )
    String shopSection = "shopSection";

    @ConfigSection(
            name = "Item Settings",
            description = "Item Settings",
            position = 0,
            closedByDefault = false
    )
    String itemSection = "itemSection";

    @ConfigSection(
            name = "Bank Settings",
            description = "Bank Settings",
            position = 0,
            closedByDefault = false
    )
    String bankSection = "bankSection";

    @ConfigSection(
            name = "Action Settings",
            description = "Action Settings",
            position = 0,
            closedByDefault = false
    )
    String actionSection = "actionSection";

    // Object or NPC to trade with
    @ConfigItem(
            keyName = npcName,
            name = "NPC Name",
            description = "Sets NPC to trade with",
            position = 0,
            section = shopSection
    )

    default String npcName() {
        return "";
    }


    @ConfigItem(
            keyName = itemNames,
            name = "Item Name(s)",
            description = "Sets Item to Buy or Sell. Supports comma seperated values (item1, item2)",
            position = 0,
            section = itemSection
    )

    default String itemNames() {
        return "item1,item2,item3";
    }

    @ConfigItem(
            keyName = minStock,
            name = "Minimum Stock",
            description = "Sets Minimum Stock level, Will not buy anymore stock past this value.",
            position = 1,
            section = itemSection
    )

    default int minimumStock() {
        return 0;
    }

    @ConfigItem(
            position = 1,
            keyName = action,
            name = "Action",
            description = "Set Buy/Sell Mode",
            section = actionSection
    )
    default Actions action() {
        return Actions.BUY;
    }

    @ConfigItem(
            position = 2,
            keyName = quantity,
            name = "Quantity",
            description = "Set Buy/Sell Quantity",
            section = actionSection
    )
    default Quantities quantity() {
        return Quantities.FIFTY;
    }

    @ConfigItem(
            position = 1,
            keyName = useBank,
            name = "Use Bank",
            description = "Use bank if your inventory is full",
            section = bankSection
    )
    default boolean useBank() {
        return true;
    }

    @ConfigItem(
            position = 3,
            keyName = logout,
            name = "Logout when out of supply",
            description = "Logout",
            section = actionSection
    )
    default boolean logout() {
        return true;
    }

}
