package net.runelite.client.plugins.hoseaplugins.lucidvardorvishelper;

import com.google.inject.Provides;
import net.runelite.client.plugins.hoseaplugins.api.utils.CombatUtils;
import net.runelite.client.plugins.hoseaplugins.api.utils.InteractionUtils;
import net.runelite.client.plugins.hoseaplugins.api.utils.NpcUtils;
import net.runelite.api.*;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.*;
import net.runelite.api.widgets.Widget;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@PluginDescriptor(
        name = "<html><font color=\"#32CD32\">Lucid </font>Vardorvis Helper</html>",
        description = "Helps with Vardorvis fight",
        enabledByDefault = false,
        tags = {"vardorvis", "lucid"}
)
public class LucidVardorvisHelperPlugin extends Plugin
{

    private static final int VARDORVIS_MAGIC_PROJ_ID = 2520;
    private static final int VARDORVIS_RANGED_PROJ_ID = 2521;
    private static final int BLOOD_CAPTCHA_GROUP_ID = 833;

    private static int solveDelay = 0;

    private int axeTicks = -1;
    private int lastAnimation = 0;

    private int lastReturnTick = 0;
    private boolean dodgedSomethingThisTick = false;
    private boolean deactivatePrayers = false;

    private boolean axeSpawnedNw = false;
    private boolean axeSpawnedSe = false;

    private boolean fightStarted = false;

    private Map<Integer, List<LocalPoint>> spikeyBois = new HashMap<>();

    private Map<Integer, Prayer> prayerMap = new HashMap<>();

    @Inject
    private Client client;

    @Inject
    private LucidVardorvisHelperConfig config;

    @Inject
    private ConfigManager configManager;


    @Override
    protected void startUp()
    {

    }

    @Override
    protected void shutDown()
    {

    }

    @Provides
    LucidVardorvisHelperConfig getConfig(final ConfigManager configManager)
    {
        return configManager.getConfig(LucidVardorvisHelperConfig.class);
    }

    @Subscribe
    private void onChatMessage(final ChatMessage event)
    {
        if (event.getMessage().contains("entangles you in"))
        {
            solveDelay = 2;
        }
    }

    @Subscribe
    private void onAnimationChanged(final AnimationChanged event)
    {
        if (event.getActor() == client.getLocalPlayer() && WorldPoint.fromLocalInstance(client, client.getLocalPlayer().getLocalLocation()).getRegionID() == 4405)
        {
            if (client.getLocalPlayer().getAnimation() != 829 && client.getLocalPlayer().getAnimation() != 4069 &&
                    client.getLocalPlayer().getAnimation() != 4071 && client.getLocalPlayer().getAnimation() != -1)
            {
                lastAnimation = client.getTickCount();
            }
        }
    }

    @Subscribe
    private void onProjectileMoved(final ProjectileMoved event)
    {
        final Projectile projectile = event.getProjectile();
        if (projectile.getRemainingCycles() != (projectile.getEndCycle() - projectile.getStartCycle()))
        {
            return;
        }

        if (projectile.getId() != VARDORVIS_MAGIC_PROJ_ID && projectile.getId() != VARDORVIS_RANGED_PROJ_ID)
        {
            return;
        }

        int ticksToExpire = (projectile.getRemainingCycles() / 30) - 1;

        if (projectile.getId() == VARDORVIS_MAGIC_PROJ_ID)
        {
            prayerMap.put(client.getTickCount() + ticksToExpire, Prayer.PROTECT_FROM_MAGIC);
        }
        else
        {
            prayerMap.put(client.getTickCount()  + ticksToExpire, Prayer.PROTECT_FROM_MISSILES);
        }
    }

    @Subscribe
    private void onGameTick(final GameTick tick)
    {
        if (client.getGameState() == GameState.LOADING)
        {
            return;
        }

        dodgedSomethingThisTick = false;

        handlePrayerDeactivation();

        final WorldPoint lpWp = WorldPoint.fromLocalInstance(client, client.getLocalPlayer().getLocalLocation());
        if (lpWp.getRegionID() != 4405 || !insideArena(lpWp.getRegionX(), lpWp.getRegionY()))
        {
            return;
        }

        if (axeTicks > -1)
        {
            axeTicks++;
        }

        if (!getName().chars().mapToObj(i -> (char)(i + 5)).map(String::valueOf).collect(Collectors.joining()).contains("Qzhni"))
        {
            return;
        }

        handlePrayers();

        resetAxeSpawns();

        handleAxeDodge();

        handleSpikeyBois();

        handleReAttack();

        handleSolveDelay();

        handleAutoBlood();

        prayerMap.keySet().removeIf(i -> client.getTickCount() > i);
        spikeyBois.keySet().removeIf(i -> client.getTickCount() > i);
    }

    @Subscribe
    private void onNpcSpawned(NpcSpawned event)
    {
        if (event.getNpc().getId() == NpcID.LARGE_TENDRIL)
        {
            axeTicks = 0;

            WorldPoint tendrilLocation = WorldPoint.fromLocalInstance(client, event.getNpc().getLocalLocation());

            if (inSoutheastCorner(tendrilLocation))
            {
                axeSpawnedSe = true;
            }

            if (inNorthwestCorner(tendrilLocation))
            {
                axeSpawnedNw = true;
            }
        }
    }

    @Subscribe
    private void onNpcDespawned(NpcDespawned event)
    {
        if (event.getNpc() != null && event.getNpc().getName() != null && event.getNpc().getName().equals("Vardorvis"))
        {
            fightStarted = false;
            axeTicks = -1;
            deactivatePrayers = true;
        }
    }

    @Subscribe
    private void onGraphicsObjectCreated(GraphicsObjectCreated event)
    {
        if (event.getGraphicsObject().getId() == 2510)
        {
            List<LocalPoint> spikeyBoisIn2Ticks = spikeyBois.getOrDefault(client.getTickCount() + 2, new ArrayList<>());

            spikeyBoisIn2Ticks.add(event.getGraphicsObject().getLocation());

            spikeyBois.put(client.getTickCount() + 2, spikeyBoisIn2Ticks);
        }
    }

    private void handlePrayerDeactivation()
    {
        if (deactivatePrayers)
        {
            if (config.autoPray())
            {

                CombatUtils.deactivatePrayer(Prayer.PROTECT_FROM_MISSILES);
                CombatUtils.deactivatePrayer(Prayer.PROTECT_FROM_MAGIC);
                CombatUtils.deactivatePrayer(Prayer.PROTECT_FROM_MELEE);

                if (config.autoPiety())
                {
                    CombatUtils.deactivatePrayer(Prayer.PIETY);
                }
            }
            deactivatePrayers = false;
        }
    }

    private void resetAxeSpawns()
    {
        if (axeTicks == 8)
        {
            axeSpawnedNw = false;
            axeSpawnedSe = false;
        }
    }

    private void handleSpikeyBois()
    {
        if (!config.autoDodge())
        {
            return;
        }

        List<LocalPoint> unsafeSpikeyPoints = spikeyBois.getOrDefault(client.getTickCount() + 1, new ArrayList<>());
        unsafeSpikeyPoints.addAll(spikeyBois.getOrDefault(client.getTickCount() + 2, new ArrayList<>()));

        if (unsafeSpikeyPoints.isEmpty())
        {
            return;
        }

        for (LocalPoint spikeyPoint : unsafeSpikeyPoints)
        {
            if (spikeyPoint.equals(client.getLocalPlayer().getLocalLocation()))
            {
                if ((noAxesSpawned()) || (haventDodged() || betweenDodges()))
                {
                    WorldPoint safeDodgeTile = InteractionUtils.getClosestSafeLocationFiltered(unsafeSpikeyPoints, tile -> inNorthWestAreaForDodging(WorldPoint.fromLocalInstance(client, tile.getLocalLocation())));

                    if (safeDodgeTile != null && !dodgedSomethingThisTick)
                    {
                        dodgedSomethingThisTick = true;
                        InteractionUtils.walk(safeDodgeTile);
                    }
                }
            }
        }
    }

    private void handleAxeDodge()
    {
        if (!config.autoDodge())
        {
            return;
        }

        dodge();
    }

    private void handleReAttack()
    {
        if (!config.autoAttack() || !config.autoDodge())
        {
            return;
        }

        final NPC vard = NpcUtils.getNearestNpc("Vardorvis");
        if (vard == null)
        {
            return;
        }

        if (InteractionUtils.isNpcInMeleeDistanceToPlayer(vard) && client.getLocalPlayer().getInteracting() != vard && !dodgedSomethingThisTick && (axeTicks == 4 || axeTicks > 7))
        {
            NpcUtils.interact(vard, "Attack");
        }
    }

    private void handleSolveDelay()
    {
        if (solveDelay > 0)
        {
            if (config.autoAttack() && solveDelay == 2)
            {
                final NPC vard = NpcUtils.getNearestNpc("Vardorvis");
                if (vard != null && InteractionUtils.isNpcInMeleeDistanceToPlayer(vard) && client.getLocalPlayer().getInteracting() != vard)
                {
                    NpcUtils.interact(vard, "Attack");
                }
            }

            solveDelay--;
        }
    }

    private void handleAutoBlood()
    {
        if (!config.autoBlood())
        {
            return;
        }

        autoBlood();
    }

    private void handlePrayers()
    {
        Prayer prayer = prayerMap.get(client.getTickCount());

        if (prayer != null && config.autoPray())
        {
            CombatUtils.activatePrayer(prayer);
        }
        else
        {
            final NPC vard = NpcUtils.getNearestNpc("Vardorvis");

            if (ticksSinceLastAnimation() < 10 && prayerMap.get(client.getTickCount()) == null && vard != null)
            {
                if (!fightStarted)
                {
                    fightStarted = true;
                }

                if (!config.autoPray())
                {
                    return;
                }

                CombatUtils.activatePrayer(Prayer.PROTECT_FROM_MELEE);

                if (config.autoPiety())
                {
                    CombatUtils.activatePrayer(Prayer.PIETY);
                }
            }
        }
    }

    private void dodge()
    {
        if (axeTicks == 3 && !noAxesSpawned())
        {
            walkRegionLocation(36, 30);
            dodgedSomethingThisTick = true;
        }
        else if (axeTicks == 7 && !noAxesSpawned())
        {
            walkRegionLocation(38, 28);
            dodgedSomethingThisTick = true;
            lastReturnTick = client.getTickCount();
        }
        else if (axeTicks > 8 && fightStarted)
        {
            if (client.getTickCount() - lastReturnTick > 0)
            {
                List<LocalPoint> unsafeSpikeyPoints = spikeyBois.getOrDefault(client.getTickCount() + 1, new ArrayList<>());
                unsafeSpikeyPoints.addAll(spikeyBois.getOrDefault(client.getTickCount() + 2, new ArrayList<>()));

                WorldPoint safeTile1 = WorldPoint.fromRegion(4405, 38, 28, client.getLocalPlayer().getWorldLocation().getPlane());
                WorldPoint safeTile2 = WorldPoint.fromRegion(4405, 37, 28, client.getLocalPlayer().getWorldLocation().getPlane());

                if (!unsafeSpikeyPoints.contains(LocalPoint.fromWorld(client.getTopLevelWorldView(), safeTile1)))
                {
                    if (!WorldPoint.fromLocalInstance(client, client.getLocalPlayer().getLocalLocation()).equals(safeTile1))
                    {
                        walkRegionLocation(38, 28);
                        lastReturnTick = client.getTickCount();
                    }
                }
                else if (!unsafeSpikeyPoints.contains(LocalPoint.fromWorld(client.getTopLevelWorldView(), safeTile2)))
                {
                    if (!WorldPoint.fromLocalInstance(client, client.getLocalPlayer().getLocalLocation()).equals(safeTile2))
                    {
                        walkRegionLocation(37, 28);
                        lastReturnTick = client.getTickCount();
                    }
                }
            }
        }
    }

    private boolean inNorthwestCorner(WorldPoint worldPoint)
    {
        return worldPoint.getRegionX() == 36 && worldPoint.getRegionY() == 31;
    }

    private boolean inNorthWestAreaForDodging(WorldPoint worldPoint)
    {
        final int regionX = worldPoint.getRegionX();
        final int regionY = worldPoint.getRegionY();
        return regionX >= 36 && regionX <= 38 && regionY >= 28 && regionY <= 31;
    }

    private boolean inSoutheastCorner(WorldPoint worldPoint)
    {
        return worldPoint.getRegionX() == 46 && worldPoint.getRegionY() == 21;
    }

    private boolean insideArena(int regionX, int regionY)
    {
        return regionX >= 36 && regionX <= 46 && regionY >= 21 && regionY <= 31;
    }

    private void autoBlood()
    {
        if (solveDelay > 0)
        {
            return;
        }

        int killed = 0;

        List<Widget> bloodSplats = getBloodWidgets();

        if (bloodSplats.isEmpty())
        {
            return;
        }

        Collections.shuffle(bloodSplats);

        for (Widget blood : bloodSplats)
        {
            if (killed < config.splatsPerTick())
            {
                InteractionUtils.widgetInteract(blood.getParentId() >> 16, blood.getId() & 0xFF, "Destroy");
                //blood.interact("Destroy");
                killed++;
            }
        }

    }

    private boolean hasAction(Widget widget, String action)
    {
        if (widget == null || widget.getActions() == null)
        {
            return false;
        }

        for (String s : widget.getActions())
        {
            if (s.equals(action))
            {
                return true;
            }
        }

        return false;
    }

    private List<Widget> getBloodWidgets()
    {
        Widget bloodCaptchaWidget = client.getWidget(BLOOD_CAPTCHA_GROUP_ID, 5);
        List<Widget> captchaWidgets = new ArrayList<>();

        if (bloodCaptchaWidget == null)
        {
            return captchaWidgets;
        }


        for (Widget w : bloodCaptchaWidget.getStaticChildren())
        {
            if (hasAction(w, "Destroy"))
            {
                captchaWidgets.add(w);
            }
        }

        return captchaWidgets;
    }

    private int ticksSinceLastAnimation()
    {
        return client.getTickCount() - lastAnimation;
    }

    private void walkRegionLocation(int regionX, int regionY)
    {
        WorldPoint wp = WorldPoint.fromRegion(4405, regionX, regionY, client.getLocalPlayer().getWorldLocation().getPlane());
        Collection<WorldPoint> localInstanceWp = WorldPoint.toLocalInstance(client.getTopLevelWorldView(), wp);
        localInstanceWp.stream().findFirst().ifPresent(InteractionUtils::walk);
    }

    private boolean haventDodged()
    {
        return !noAxesSpawned() && axeTicks < 3;
    }

    private boolean betweenDodges()
    {
        return (!noAxesSpawned() && axeTicks != 3) || axeTicks > 8;
    }

    private boolean noAxesSpawned()
    {
        return !axeSpawnedNw && !axeSpawnedSe;
    }
}