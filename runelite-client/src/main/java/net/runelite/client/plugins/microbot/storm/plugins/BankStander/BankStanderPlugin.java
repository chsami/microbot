package net.runelite.client.plugins.microbot.storm.plugins.BankStander;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.api.events.WidgetLoaded;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Item;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;
import java.awt.*;
import java.util.Arrays;

@PluginDescriptor(
        name = PluginDescriptor.eXioStorm + "BankStander",
        description = "Credit to original coder : Bank",
        tags = {"bankstander", "bank.js", "eXioStorm", "storm"},
        enabledByDefault = false
)
@Slf4j
public class BankStanderPlugin extends Plugin {
    @Inject
    private BankStanderConfig config;

    @Provides
    BankStanderConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(BankStanderConfig.class);
    }

    @Inject
    private OverlayManager overlayManager;
    @Inject
    private BankStanderOverlay banksBankStanderOverlay;

    @Inject
    BankStanderScript banksBankStanderScript;


    @Override
    protected void startUp() throws AWTException {
        if (overlayManager != null) {
            overlayManager.add(banksBankStanderOverlay);
        }
        banksBankStanderScript.run(config);
    }
    @Subscribe
    public void onItemContainerChanged(ItemContainerChanged inventory){
        if(inventory.getContainerId()==93){
            if(BankStanderScript.currentStatus== CurrentStatus.COMBINE_ITEMS) {
                if (BankStanderScript.secondItemId != null) {
                    if (Rs2Inventory.items().stream().filter(item -> item.id == BankStanderScript.secondItemId).mapToInt(item -> item.quantity).sum() < config.secondItemQuantity()) {
                        BankStanderScript.itemsProcessed++;
                    }
                } else {
                    if (Rs2Inventory.count(BankStanderScript.secondItemIdentifier) < config.secondItemQuantity()) {
                        BankStanderScript.itemsProcessed++;
                    }
                }
            } else { System.out.println("currentStatus : " + BankStanderScript.currentStatus); }
            if (BankStanderScript.secondItemId != null) { // Use secondItemId if it's available
                if (Arrays.stream(inventory.getItemContainer().getItems())
                        .anyMatch(x -> x.getId() == BankStanderScript.secondItemId)) {
                    // average is 1800, max is 2400~
                    BankStanderScript.previousItemChange = System.currentTimeMillis();
                    //System.out.println("still processing items");
                } else {
                    BankStanderScript.previousItemChange = (System.currentTimeMillis() - 2500);
                }
            } else { // Use secondItemIdentifier if secondItemId is null
                Rs2Item item = Rs2Inventory.get(BankStanderScript.secondItemIdentifier);
                if (item != null) {
                    // average is 1800, max is 2400~
                    BankStanderScript.previousItemChange = System.currentTimeMillis();
                    //System.out.println("still processing items");
                } else {
                    BankStanderScript.previousItemChange = (System.currentTimeMillis() - 2500);
                }
            }

        }
    }
    @Subscribe
    public void onWidgetLoaded(WidgetLoaded widget){
        if (widget.getGroupId()==270) {
            if(BankStanderScript.isWaitingForPrompt) {
                BankStanderScript.isWaitingForPrompt = false;
            }
        }
    }
    protected void shutDown() {
        banksBankStanderScript.shutdown();
        overlayManager.remove(banksBankStanderOverlay);
    }
}
