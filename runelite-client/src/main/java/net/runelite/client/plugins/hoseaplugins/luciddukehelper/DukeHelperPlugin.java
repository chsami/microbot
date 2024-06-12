package net.runelite.client.plugins.hoseaplugins.luciddukehelper;

import com.google.inject.Provides;
import net.runelite.client.plugins.hoseaplugins.api.utils.*;
import lombok.Getter;
import net.runelite.api.*;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.*;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@PluginDescriptor(name = PluginDescriptor.Lucid + "Duke Helper</html>", description = "Auto-prayers and other helper features and overlays for Duke Succ", enabledByDefault = false, tags = {"succ", "duke", "lucid", "helper"})
public class DukeHelperPlugin extends Plugin
{
    @Inject
    private Client client;

    @Inject
    private ClientThread clientThread;

    @Inject
    private DukeHelperConfig config;

    @Inject
    private DukeHelperOverlay overlay;

    @Inject
    private ConfigManager configManager;

    @Inject
    private OverlayManager overlayManager;

    private int dukeAttackTimer = 0;

    @Getter
    private boolean trackingDuke = false;

    @Getter
    private Map<GraphicsObject, Integer> fallingCeilingTiles = new HashMap<>();

    @Getter
    private Map<WorldPoint, Integer> ventTiles = new HashMap<>();

    private final int DUKE_SLEEPING_POSE = 10174;
    private final int DUKE_SPIKES = 10176;
    private final int DUKE_GAS = 10178;
    private final int DUKE_EYE = 10180;
    private final int VENT_NPC = 12198;
    private final int CEILING_FALLING_GFX = 1447;

    private WorldPoint lastLocationDuringGas = null;

    private int spikeDodgeTick = 0;
    private int eyeDodgeTick = 0;
    private int gasDodgeTick = 0;
    private int gasAttackTick = 0;

    @Provides
    DukeHelperConfig getConfig(ConfigManager configManager)
    {
        return configManager.getConfig(DukeHelperConfig.class);
    }

    @Override
    protected void startUp()
    {
        clientThread.invoke(this::pluginEnabled);
    }

    private void pluginEnabled()
    {
        if (!overlayManager.anyMatch(p -> p == overlay))
        {
            overlayManager.add(overlay);
        }
    }

    @Override
    protected void shutDown()
    {
        if (overlayManager.anyMatch(p -> p == overlay))
        {
            overlayManager.remove(overlay);
        }
    }


    @Subscribe
    private void onGameTick(final GameTick tick)
    {
        fallingCeilingTiles.values().removeIf(i -> client.getTickCount() > i);
        ventTiles.values().removeIf(i -> client.getTickCount() > i + 15);

        if (!trackingDuke)
        {
            return;
        }

        NPC duke = getDukeSuc();
        if (duke == null)
        {
            trackingDuke = false;
            return;
        }

        if (dukeAwake())
        {
            if (config.autoAttackSpikes() && client.getTickCount() == spikeDodgeTick + 1)
            {
                NpcUtils.interact(duke, "Attack");
            }
            else if (config.autoAttackEye() && client.getTickCount() == eyeDodgeTick + 4)
            {
                NpcUtils.interact(duke, "Attack");
            }
            else if (config.autoDodgeGas() && client.getTickCount() == gasDodgeTick + 1)
            {
                dodgeAcross();
            }

            boolean weaponEquipped = EquipmentUtils.contains(config.enrageWeapon());
            boolean weaponInInventory = InventoryUtils.contains(config.enrageWeapon());
            if (!config.enrageWeapon().isEmpty() && dukeHpPercent() <= 25 && !weaponEquipped && weaponInInventory)
            {
                Item enrageWeapon = InventoryUtils.getFirstItem(config.enrageWeapon());
                if (enrageWeapon != null)
                {
                    InventoryUtils.itemInteract(enrageWeapon.getId(), "Wield");
                    return;
                }
            }

            if (config.autoAttackGas())
            {
                boolean attacked = false;
                if (client.getTickCount() > gasDodgeTick + 2 && client.getTickCount() < gasDodgeTick + (in4TickCycle() ? 5 : 6))
                {
                    attacked = attackGas();
                }

                if ( client.getTickCount() < gasDodgeTick + 5 && !attacked)
                {
                    lastLocationDuringGas = client.getLocalPlayer().getWorldLocation();
                }
            }
        }
    }

    @Subscribe
    private void onMenuOptionClicked(MenuOptionClicked event)
    {
        if (!trackingDuke)
        {
            return;
        }

        if (event.getMenuAction() == MenuAction.WIDGET_TARGET)
        {
            if (config.oneClickMushroom() && event.getMenuTarget().contains("Arder mushroom") && InventoryUtils.contains("Pestle and mortar"))
            {
                event.consume();
                mushOnPestle(true);

                TileObject o = GameObjectUtils.nearest("Arder mushrooms");
                if (o != null && client.getLocalPlayer().getWorldLocation().getRegionY() == 52)
                {
                    GameObjectUtils.interact(o, "Pick");
                }
            }
            else if (config.oneClickMushroom() && event.getMenuTarget().equals("Musca mushroom") && InventoryUtils.contains("Pestle and mortar"))
            {
                mushOnPestle(false);

                TileObject o = GameObjectUtils.nearest("Musca mushrooms");
                if (o != null && client.getLocalPlayer().getWorldLocation().getRegionY() == 52)
                {
                    GameObjectUtils.interact(o, "Pick");
                }
            }

            else if (config.oneClickDust() && event.getMenuTarget().contains("Arder powder") && InventoryUtils.count("Arder powder") > 0 && dukeSleeping())
            {
                event.consume();
                InteractionUtils.useItemOnNPC(InventoryUtils.getFirstItem("Arder powder").getId(), getDukeSuc());
            }
            else if (config.oneClickDust() && event.getMenuTarget().contains("Musca powder") && InventoryUtils.count("Musca powder") > 0 && dukeSleeping())
            {
                event.consume();
                InteractionUtils.useItemOnNPC(InventoryUtils.getFirstItem("Musca powder").getId(), getDukeSuc());
            }
        }
    }

    private void mushOnPestle(boolean useArder)
    {
        final Item one = InventoryUtils.getFirstItem(useArder ? "Arder mushroom" : "Musca mushroom");
        final Item two = InventoryUtils.getFirstItem("Pestle and mortar");
        InventoryUtils.itemOnItem(two, one);
    }

    @Subscribe
    private void onGraphicsObjectCreated(GraphicsObjectCreated event)
    {
        if (event.getGraphicsObject().getId() == CEILING_FALLING_GFX)
        {
            final WorldPoint worldPoint = WorldPoint.fromLocal(client, event.getGraphicsObject().getLocation());
            int x = worldPoint.getRegionX();
            int y = worldPoint.getRegionY();
            if (y < 39 || ((x == 22 || x == 40) && y % 2 == 1))
            {
                fallingCeilingTiles.put(event.getGraphicsObject(), client.getTickCount() + 4);
            }
        }
    }

    @Subscribe
    private void onNpcSpawned(NpcSpawned event)
    {
        if (event.getActor() instanceof NPC && event.getActor().getName().contains("Duke Suc"))
        {
            toggleDukeTracking();
        }

        if (event.getActor() instanceof NPC && ((NPC)event.getActor()).getId() == VENT_NPC)
        {
            ventTiles.put(event.getActor().getWorldLocation(), client.getTickCount());
        }
    }

    @Subscribe
    private void onNpcDespawned(NpcDespawned event)
    {
        if (event.getActor() instanceof NPC && event.getActor().getName().contains("Duke Suc"))
        {
            toggleDukeTracking();
        }
    }

    @Subscribe
    private void onAnimationChanged(AnimationChanged event)
    {
        if (!trackingDuke)
        {
            return;
        }

        if (event.getActor() == getDukeSuc())
        {
            if (config.autoDodgeSpikes() && event.getActor().getAnimation() == DUKE_SPIKES)
            {
                dodgeBehindPillar();
                spikeDodgeTick = client.getTickCount();
            }
            if (config.autoDodgeEye() && event.getActor().getAnimation() == DUKE_EYE)
            {
                if (dodgeBehindPillar())
                {
                    eyeDodgeTick = client.getTickCount();
                }
            }
            if (config.autoDodgeGas())
            {
                if (event.getActor().getAnimation() == DUKE_GAS && (client.getTickCount() - gasDodgeTick) > 5)
                {
                    if (dodgeBehindPillar())
                    {
                        gasDodgeTick = client.getTickCount();

                    }
                    if (lastLocationDuringGas == null)
                    {
                        lastLocationDuringGas = client.getLocalPlayer().getWorldLocation();
                    }
                }
            }
        }
    }

    private boolean dodgeBehindPillar()
    {
        int regX = client.getLocalPlayer().getWorldLocation().getRegionX();
        int dx = regX == 33 ? 2 : regX == 34 ? 1 : regX == 29 ? -2 : regX == 28 ? -1 : 0;
        if (dx != 0)
        {
            InteractionUtils.walk(client.getLocalPlayer().getWorldLocation().dx(dx));
            return true;
        }
        return false;
    }

    private void dodgeAcross()
    {
        int regX = client.getLocalPlayer().getWorldLocation().getRegionX();
        int dx = regX == 35 ? -8 : regX == 27 ? 8 : 0;
        if (dx != 0)
        {
            InteractionUtils.walk(client.getLocalPlayer().getWorldLocation().dx(dx));
        }
    }

    private boolean attackGas()
    {
        int regX = client.getLocalPlayer().getWorldLocation().getRegionX();
        int fiveTickDirectionX = regX == 29 ? -2 : (regX == 33 ? 2 : 0);
        int fourTickDirectionX = lastLocationDuringGas.getRegionX() == 29 ? 4 : lastLocationDuringGas.getRegionX() == 33 ? -4 : 0;

        if (!in4TickCycle() && fiveTickDirectionX != 0)
        {
            if (client.getTickCount() != gasAttackTick + 1)
            {
                NpcUtils.interact(getDukeSuc(), "Attack");
                gasAttackTick = client.getTickCount();
                return true;
            }

            if (client.getTickCount() == gasAttackTick + 1)
            {
                InteractionUtils.walk(client.getLocalPlayer().getWorldLocation().dx(fiveTickDirectionX));
                return false;
            }
        }
        if (in4TickCycle() && fourTickDirectionX != 0)
        {
            if (client.getTickCount() != gasAttackTick + 1)
            {
                NpcUtils.interact(getDukeSuc(), "Attack");
                gasAttackTick = client.getTickCount();
                return true;
            }

            if (client.getTickCount() == gasAttackTick + 1)
            {
                InteractionUtils.walk(client.getLocalPlayer().getWorldLocation().dx(fourTickDirectionX));
                return false;
            }
        }
        return false;
    }

    private boolean in4TickCycle()
    {

        if (dukeHpPercent() <= 25)
        {
            return config.fourTickCycleEnrage();
        }
        else
        {
            return config.fourTickCycle();
        }
    }

    private void toggleDukeTracking()
    {
        trackingDuke = !trackingDuke;

        if (trackingDuke)
        {
            // Start tracking
            fallingCeilingTiles.clear();
        }
    }

    public double dukeHpPercent()
    {
        final NPC duke = getDukeSuc();
        if (duke == null)
        {
            return 999999;
        }

        final int ratio = duke.getHealthRatio();
        final int scale = duke.getHealthScale();

        return Math.floor((double) ratio  / (double) scale * 100);
    }

    public boolean dukeAwake()
    {
       NPC duke = getDukeSuc();
       return  duke.getAnimation() != -1 || duke.getPoseAnimation() != DUKE_SLEEPING_POSE;
    }

    public boolean dukeSleeping()
    {
        NPC duke = getDukeSuc();
        return  duke.getAnimation() == -1 && duke.getPoseAnimation() == DUKE_SLEEPING_POSE;
    }

    private NPC getDukeSuc()
    {
        return NpcUtils.getNearestNpc(npc -> npc.getName() != null && npc.getName().contains("Duke Suc"));
    }
}
