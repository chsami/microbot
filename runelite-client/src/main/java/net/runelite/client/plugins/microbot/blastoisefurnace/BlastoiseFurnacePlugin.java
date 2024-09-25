package net.runelite.client.plugins.microbot.blastoisefurnace;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.ItemID;
import net.runelite.api.Varbits;
import net.runelite.api.events.*;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

import net.runelite.client.plugins.microbot.blastoisefurnace.enums.State;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;
import java.awt.*;



@PluginDescriptor(
        name = "<html>[<font color=#00ffff>§</font>] " + "BlastoiseFurnace",
        description = "Storm's test plugin",
        tags = {"testing", "microbot", "smithing", "bar", "ore", "blast", "furnace"},
        enabledByDefault = false
)
@Slf4j
public class BlastoiseFurnacePlugin extends Plugin {
    @Inject
    private BlastoiseFurnaceConfig config;
    @Provides
    BlastoiseFurnaceConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(BlastoiseFurnaceConfig.class);
    }

    @Inject
    private OverlayManager overlayManager;
    @Inject
    private BlastoiseFurnaceOverlay BlastoiseFurnaceOverlay;

    @Inject
    BlastoiseFurnaceScript BlastoiseFurnaceScript;


    @Override
    protected void startUp() throws AWTException {
        if (overlayManager != null) {
            overlayManager.add(BlastoiseFurnaceOverlay);
        }
        BlastoiseFurnaceScript.run(config);
    }
    @Subscribe
    public void onVarbitChanged(VarbitChanged event) {
        if (event.getVarbitId() == Varbits.STAMINA_EFFECT) {
            BlastoiseFurnaceScript.staminaTimer = event.getValue();
        }
    }
    @Subscribe
    public void onStatChanged(StatChanged event) {
        if(event.getXp()> BlastoiseFurnaceScript.previousXP){
            if(BlastoiseFurnaceScript.waitingXpDrop) {
                //BlastoiseFurnaceScript.waitingXpDrop = false;
            }
        }
    }
    @Subscribe
    public void onChatMessage(ChatMessage chatMessage) {
        if (chatMessage.getType() == ChatMessageType.GAMEMESSAGE) {
            if(chatMessage.getMessage().contains("The coal bag is now empty.")){
                if(!BlastoiseFurnaceScript.coalBagEmpty) BlastoiseFurnaceScript.coalBagEmpty=true;
            }

            if(chatMessage.getMessage().contains("The coal bag contains")){
                if(BlastoiseFurnaceScript.coalBagEmpty) BlastoiseFurnaceScript.coalBagEmpty=false;
            }
        }
    }
    @Subscribe
    public void onItemContainerChanged(ItemContainerChanged inventory){
        if(inventory.getItemContainer().getId()==93) {
            if (!inventory.getItemContainer().contains(ItemID.COAL) && BlastoiseFurnaceScript.state != State.BANKING) {
                if (!BlastoiseFurnaceScript.coalBagEmpty) BlastoiseFurnaceScript.coalBagEmpty = true;//TODO this sets the bag to empty when we're smithing and coal is added to our inventory.
            }
            if (inventory.getItemContainer().contains(ItemID.COAL) && BlastoiseFurnaceScript.state != State.SMITHING) {
                if (BlastoiseFurnaceScript.coalBagEmpty) BlastoiseFurnaceScript.coalBagEmpty = false;
            }
            if (inventory.getItemContainer().contains(config.getBars().getPrimaryOre()) && BlastoiseFurnaceScript.state != State.SMITHING){
                if(BlastoiseFurnaceScript.primaryOreEmpty){ BlastoiseFurnaceScript.primaryOreEmpty=false; }
            }
            if (!inventory.getItemContainer().contains(config.getBars().getPrimaryOre()) && BlastoiseFurnaceScript.state != State.BANKING){
                if(!BlastoiseFurnaceScript.primaryOreEmpty){ BlastoiseFurnaceScript.primaryOreEmpty=true; }
            }
            if (inventory.getItemContainer().contains(config.getBars().getSecondaryOre()) && BlastoiseFurnaceScript.state != State.SMITHING){
                //TODO ffs for some reason the print fixes it when run from IDE, but compiled still bugs out...
                if(BlastoiseFurnaceScript.secondaryOreEmpty){ System.out.println("secondary set to not empty"); BlastoiseFurnaceScript.secondaryOreEmpty=false; }
            }
            if (!inventory.getItemContainer().contains(config.getBars().getSecondaryOre()) && BlastoiseFurnaceScript.state != State.BANKING){
                if(!BlastoiseFurnaceScript.secondaryOreEmpty){ BlastoiseFurnaceScript.secondaryOreEmpty=true; }
            }
            //TODO added
            if (!inventory.getItemContainer().contains(config.getBars().getSecondaryOre()) && BlastoiseFurnaceScript.state != State.SMITHING){
                if(!BlastoiseFurnaceScript.secondaryOreEmpty){ BlastoiseFurnaceScript.secondaryOreEmpty=true; }
            }
            if (inventory.getItemContainer().contains(config.getBars().getSecondaryOre()) && BlastoiseFurnaceScript.state != State.BANKING){
                if(BlastoiseFurnaceScript.secondaryOreEmpty){ BlastoiseFurnaceScript.secondaryOreEmpty=false; }
            }
        }
    }
    @Subscribe
    public void onClientTick(ClientTick clientTick) {

    }
    protected void shutDown() {
        BlastoiseFurnaceScript.shutdown();
        overlayManager.remove(BlastoiseFurnaceOverlay);
    }
}
