package net.runelite.client.plugins.microbot.blackjack;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.Player;
import net.runelite.api.Skill;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.ClientTick;
import net.runelite.api.events.HitsplatApplied;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;
import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

@PluginDescriptor(
        //name = PluginDescriptor.Default + "Blackjack",
        name = "<html>[<font color=#ff00ff>§</font>] " + "Blackjack",
        description = "Pollniveach Blackjack script",
        tags = {"Thieving", "StormScript"},
        enabledByDefault = false
)
@Slf4j
public class BlackJackPlugin extends Plugin {
    @Inject
    private BlackJackConfig config;
    @Inject
    private Client client;
    @Provides
    BlackJackConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(BlackJackConfig.class);
    }

    @Inject
    private OverlayManager overlayManager;
    @Inject
    private BlackJackOverlay blackJackOverlay;

    @Inject

    BlackJackScript blackJackScript;

    @Subscribe
    public void onHitsplatApplied(HitsplatApplied event) {
        if (event.getHitsplat().isMine())
        {
            if(BlackJackScript.playerHit==0 || Microbot.getClient().getSkillExperience(Skill.THIEVING)>BlackJackScript.hitsplatXP || BlackJackScript.koPassed){
                BlackJackScript.firstHit=true;
                BlackJackScript.hitsplatXP = Microbot.getClient().getSkillExperience(Skill.THIEVING);
                BlackJackScript.hitsplatStart = System.currentTimeMillis();
                BlackJackScript.playerHit=0;
                BlackJackScript.koPassed=false;
            }
            BlackJackScript.playerHit++;
            if(config.soundHitSplats()) { client.playSoundEffect(3929, 127); }
        }
    }
    @Subscribe
    public void onChatMessage(ChatMessage chatMessage) {
        if (chatMessage.getType() == ChatMessageType.GAMEMESSAGE) {
            if(chatMessage.getMessage().contains("Perhaps I shouldn't do this just yet.")){
                BlackJackScript.npcsCanSeeEachother = true;
                //System.out.println("detected multiple NPCs");
            }
        }
    }
    @Subscribe
    public void onClientTick(ClientTick clientTick) {
        List<Player> dangerousPlayers = getPlayersInRange().stream().filter(this::shouldPlayerCauseConcern).collect(Collectors.toList());
        boolean shouldAlarm = (dangerousPlayers.size() > 0);
        if (shouldAlarm) {
            if (!BlackJackScript.isPlayerNearby){
                BlackJackScript.isPlayerNearby = true;
            }
        }
        if (!shouldAlarm) {
            if (BlackJackScript.isPlayerNearby){
                BlackJackScript.isPlayerNearby = false;
            }
        }
    }
    private List<Player> getPlayersInRange() {
        LocalPoint currentPosition = this.client.getLocalPlayer().getLocalLocation();
        return this.client.getPlayers()
                .stream()
                .filter(player -> (player.getLocalLocation().distanceTo(currentPosition) / 128 <= 8))
                .collect(Collectors.toList());
    }
    private boolean shouldPlayerCauseConcern(Player player) {
        if (player.getId() == this.client.getLocalPlayer().getId())
        { return false; }
        if (player.isClanMember())
        { return false; }

        return true;
    }

    @Override
    protected void startUp() throws AWTException {
        if (overlayManager != null) {
            overlayManager.add(blackJackOverlay);
        }
        blackJackScript.run(config);
    }

    protected void shutDown() {
        blackJackScript.shutdown();
        overlayManager.remove(blackJackOverlay);
    }
}
