package net.runelite.client.plugins.hoseaplugins.dropparty;

import com.google.inject.Provides;
import lombok.AccessLevel;
import lombok.Getter;
import net.runelite.api.Client;
import net.runelite.api.Player;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameTick;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.Text;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

@PluginDescriptor(
        name = "<html><font color=\"#FF9DF9\">[PP]</font> Drop Party</html>",
        description = "Marks where a user ran, for drop partys",
        tags = {"Drop", "Party", "marker", "player"},
        enabledByDefault = false
)

public class DropPartyPlugin extends Plugin
{
    @Inject
    private DropPartyConfig config;
    @Getter(AccessLevel.PACKAGE)
    private List<WorldPoint> playerPath = new ArrayList<>();
    @Getter(AccessLevel.PACKAGE)
    private final int MAXPATHSIZE = 100;
    private Player runningPlayer;

    @Inject
    private OverlayManager overlayManager;

    @Inject
    private DropPartyOverlay coreOverlay;

    @Inject
    private Client client;

    @Provides
    DropPartyConfig getConfig(ConfigManager configManager)
    {
        return configManager.getConfig(DropPartyConfig.class);
    }

    @Override
    protected void startUp()
    {
        overlayManager.add(coreOverlay);
        reset();
    }

    @Override
    protected void shutDown()
    {
        overlayManager.remove(coreOverlay);
        reset();
    }

    @Subscribe
    private void onGameTick(GameTick event)
    {
        shuffleList();
        if (config.playerName().equalsIgnoreCase(""))
        {
            return;
        }

        runningPlayer = null;

        for (Player player : client.getPlayers())
        {
            if (player.getName() == null)
            {
                continue;
            }
            if (Text.standardize(player.getName()).equalsIgnoreCase(config.playerName()))
            {
                runningPlayer = player;
                break;
            }

        }

        if (runningPlayer == null)
        {
            cordsError();
            return;
        }
        addCords();
    }

    private void cordsError()
    {
        playerPath.add(null);
    }

    private void shuffleList()
    {
        if (playerPath.size() > MAXPATHSIZE - 1)
        {
            playerPath.remove(0);
        }
    }

    private void addCords()
    {
        while (true)
        {
            if (playerPath.size() >= MAXPATHSIZE)
            {
                playerPath.add(runningPlayer.getWorldLocation());
                break;
            }
            playerPath.add(null);
        }
    }

    private void reset()
    {
        playerPath.clear();
    }
}