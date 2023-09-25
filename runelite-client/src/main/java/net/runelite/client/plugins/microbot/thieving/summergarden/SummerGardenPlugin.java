package net.runelite.client.plugins.microbot.thieving.summergarden;


import com.google.inject.Provides;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameObjectDespawned;
import net.runelite.api.events.GameObjectSpawned;
import net.runelite.api.events.GameTick;
import net.runelite.client.Notifier;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.ui.overlay.infobox.InfoBox;
import net.runelite.client.ui.overlay.infobox.InfoBoxManager;

import javax.inject.Inject;
import java.awt.*;

@PluginDescriptor(
        name = PluginDescriptor.Mocrosoft + "Summers Garden",
        description = "Microbot Summers Garden - Right click the tree at summers garden and click 'Start', You need a pestle and mortar and empty beer glasses!",
        tags = {"Summers Garden", "minigame", "thieving", "microbot", "skilling"},
        enabledByDefault = false
)
@Slf4j
public class SummerGardenPlugin extends Plugin
{
    @Inject
    private Client client;

    @Inject
    private OverlayManager overlayManager;

    @Inject
    private InfoBoxManager infoBoxManager;

    @Inject
    private ItemManager itemManager;

    @Inject
    private Notifier notifier;

    @Inject
    private SummerGardenOverlay overlay;

    @Inject
    private SummerGardenConfig config;

    public static final String CONFIG_GROUP = "oneclicksummergarden";
    public static final String CONFIG_KEY_GATE_START = "useGateStartPoint";
    public static final String CONFIG_KEY_COUNTDOWN_TIMER_INFOBOX = "showCountdownTimer";
    public static final String CONFIG_KEY_RACE_STYLE_COUNTDOWN = "raceStyleCountdown";
    public static final String CONFIG_KEY_RACE_STYLE_VOLUME = "raceStyleVolume";
    private static final WorldPoint GARDEN = new WorldPoint(2915, 5490, 0);
    private static final String STAMINA_MESSAGE = "[One Click Summer Garden] Low Stamina Warning";
    private static final String CYCLE_MESSAGE = "[One Click Summer Garden] Cycle Ready";
    private static final int SUMMER_SQUIRK_ITEM_ID = 10845;
    private static final int RACE_STYLE_SOUND_LOW = 3817;
    private static final int RACE_STYLE_SOUND_HIGH = 3818;
    private static final int OBJECT_ID_TREE = 12943;

    private InfoBox countdownTimerInfoBox;
    private boolean sentStaminaNotification = false;

    @Getter
    private GameObject treeObject;

    @Override
    protected void startUp()
    {
        enableOverlay();
        if (config.showCountdownTimer())
        {
            enableCountdownTimerInfoBox();
        }
        ElementalCollisionDetector.setGateStart(config.useGateStartPoint());
    }

    @Override
    protected void shutDown()
    {
        disableOverlay();
        disableCountdownTimerInfoBox();
    }

    private boolean overlayEnabled = false;

    private void enableOverlay()
    {
        if (overlayEnabled)
        {
            return;
        }

        overlayEnabled = true;
        overlayManager.add(overlay);
    }

    private void disableOverlay()
    {
        if (overlayEnabled)
        {
            overlayManager.remove(overlay);
        }
        overlayEnabled = false;
    }

    private void enableCountdownTimerInfoBox()
    {
        if (countdownTimerInfoBox == null)
        {
            countdownTimerInfoBox = new InfoBox(itemManager.getImage(SUMMER_SQUIRK_ITEM_ID), this)
            {
                @Override
                public String getText()
                {
                    return String.valueOf(ElementalCollisionDetector.getTicksUntilStart());
                }

                @Override
                public Color getTextColor()
                {
                    return null;
                }
            };
            infoBoxManager.addInfoBox(countdownTimerInfoBox);
        }
    }

    private void disableCountdownTimerInfoBox()
    {
        infoBoxManager.removeInfoBox(countdownTimerInfoBox);
        countdownTimerInfoBox = null;
    }

    @Subscribe
    public void onGameTick(GameTick e)
    {
        Player p = client.getLocalPlayer();
        if (p == null)
        {
            return;
        }

        if (p.getWorldLocation().distanceTo2D(GARDEN) >= 50)
        {
            disableCountdownTimerInfoBox();
            disableOverlay();
            return;
        }

        if (config.showCountdownTimer())
        {
            enableCountdownTimerInfoBox();
        }
        enableOverlay();
        client.getNpcs()
                .stream()
                .filter(ElementalCollisionDetector::isSummerElemental)
                .forEach(npc -> ElementalCollisionDetector.updatePosition(npc, client.getTickCount()));
        ElementalCollisionDetector.updateCountdownTimer(client.getTickCount());

        // cycle notification
        if (config.cycleNotification() && ElementalCollisionDetector.getTicksUntilStart() == config.notifyTicksBeforeStart())
        {
            notifier.notify(CYCLE_MESSAGE, TrayIcon.MessageType.INFO);
        }

        playCountdownSounds();

        checkStamina();
    }

    private void playCountdownSounds()
    {
        // Race-style countdown  -Green Donut
        if (config.raceStyleCountdown() && ElementalCollisionDetector.getTicksUntilStart() <= 3 && config.raceStyleVolume() > 0)
        {
            // As playSoundEffect only uses the volume argument when the in-game volume isn't muted, sound effect volume
            // needs to be set to the value desired for race sounds and afterwards reset to the previous value.
            Preferences preferences = client.getPreferences();
            int previousVolume = preferences.getSoundEffectVolume();
            preferences.setSoundEffectVolume(config.raceStyleVolume());

            if (ElementalCollisionDetector.getTicksUntilStart() == 0)
            {
                // high sound for countdown 0
                client.playSoundEffect(RACE_STYLE_SOUND_HIGH, config.raceStyleVolume());
            }
            else
            {
                // low sound for countdown 3,2,1
                client.playSoundEffect(RACE_STYLE_SOUND_LOW, config.raceStyleVolume());
            }
            preferences.setSoundEffectVolume(previousVolume);
        }
    }

    private void checkStamina()
    {
        // check for stamina usage
        int stamThreshold = config.staminaThreshold() * 100;
        if (stamThreshold != 0)
        {
            boolean stamActive = client.getVarbitValue(Varbits.RUN_SLOWED_DEPLETION_ACTIVE) != 0;
            if (client.getEnergy() <= stamThreshold && !stamActive && !sentStaminaNotification)
            {
                notifier.notify(STAMINA_MESSAGE, TrayIcon.MessageType.INFO);
                sentStaminaNotification = true;
            }
            else if (client.getEnergy() > stamThreshold)
            {
                sentStaminaNotification = false;
            }
        }
    }

    @Subscribe
    public void onConfigChanged(ConfigChanged configChanged)
    {
        if (!configChanged.getGroup().equals(CONFIG_GROUP))
        {
            return;
        }

        if (configChanged.getKey().equals(CONFIG_KEY_GATE_START))
        {
            ElementalCollisionDetector.setGateStart(config.useGateStartPoint());
        }
        else if (configChanged.getKey().equals(CONFIG_KEY_COUNTDOWN_TIMER_INFOBOX))
        {
            if (config.showCountdownTimer())
            {
                enableCountdownTimerInfoBox();
            }
            else
            {
                disableCountdownTimerInfoBox();
            }
        }
    }

    @Subscribe
    public void onGameObjectSpawned(GameObjectSpawned e)
    {
        if (e.getGameObject().getId() == OBJECT_ID_TREE)
        {
            this.treeObject = e.getGameObject();
        }
    }

    @Subscribe
    public void onGameObjectDespawned(GameObjectDespawned e)
    {
        if (e.getGameObject() == this.treeObject)
        {
            this.treeObject = null;
        }
    }

    @Provides
    SummerGardenConfig provideConfig(ConfigManager configManager)
    {
        return configManager.getConfig(SummerGardenConfig.class);
    }
}
