package net.runelite.client.plugins.hoseaplugins.lucidscurriushelper;

import com.google.inject.Provides;
import net.runelite.client.plugins.hoseaplugins.api.utils.CombatUtils;
import net.runelite.client.plugins.hoseaplugins.api.utils.InteractionUtils;
import net.runelite.client.plugins.hoseaplugins.api.utils.NpcUtils;
import lombok.Getter;
import net.runelite.api.*;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.*;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@PluginDescriptor(name = PluginDescriptor.Lucid + "Scurrius Helper</html>", description = "Dodges Scurrius' falling ceiling attack and re-attacks")
public class LucidScurriusHelperPlugin extends Plugin
{

    @Inject
    private Client client;

    @Inject
    private ClientThread clientThread;

    @Inject
    private LucidScurriusHelperConfig config;

    @Inject
    private ConfigManager configManager;

    @Getter
    private final Map<GraphicsObject, Integer> fallingCeilingToTicks = new HashMap<>();

    private static final int FALLING_CEILING_GRAPHIC = 2644;

    private static final int SCURRIUS = 7222;
    private static final int SCURRIUS_PUBLIC = 7221;

    private static final int DURATION = 9;

    private boolean justDodged = false;

    private int lastDodgeTick = 0;

    private int lastRatTick = 0;

    private int lastActivateTick = 0;

    private List<Projectile> attacks = new ArrayList<>();

    @Provides
    LucidScurriusHelperConfig getConfig(ConfigManager configManager)
    {
        return configManager.getConfig(LucidScurriusHelperConfig.class);
    }

    @Subscribe
    private void onGraphicsObjectCreated(GraphicsObjectCreated event)
    {
        final GraphicsObject graphicsObject = event.getGraphicsObject();
        final int id = graphicsObject.getId();

        if (id == FALLING_CEILING_GRAPHIC)
        {
            fallingCeilingToTicks.put(graphicsObject, DURATION);
        }
    }

    @Subscribe
    private void onAnimationChanged(AnimationChanged event)
    {
        if (!(event.getActor() instanceof NPC))
        {
            return;
        }

        NPC npc = (NPC) event.getActor();
        if (npc.getName() != null && npc.getName().equals("Scurrius") && npc.getAnimation() == 10705 && NpcUtils.getNearestNpc("Giant rat") == null)
        {
            if (config.autoPray())
            {
                CombatUtils.deactivatePrayers(false);
            }
        }
    }

    @Subscribe
    private void onProjectileMoved(ProjectileMoved event)
    {
        final Projectile projectile = event.getProjectile();
        if (projectile.getRemainingCycles() != (projectile.getEndCycle() - projectile.getStartCycle()))
        {
            return;
        }

        if (projectile.getId() != 2642 && projectile.getId() != 2640)
        {
            return;
        }

        NPC scurrius = NpcUtils.getNearestNpc("Scurrius");
        if (scurrius == null || event.getProjectile().getInteracting() != client.getLocalPlayer())
        {
            return;
        }

        if (!attacks.contains(projectile))
        {
            attacks.add(projectile);
            if (config.autoPray())
            {
                CombatUtils.deactivatePrayer(Prayer.PROTECT_FROM_MELEE);
            }
        }
    }


    @Subscribe
    private void onGameTick(GameTick event)
    {
        WorldPoint instancePoint = WorldPoint.fromLocalInstance(client, client.getLocalPlayer().getLocalLocation());

        if (instancePoint.getRegionID() != 13210 || instancePoint.getRegionX() < 23)
        {
            return;
        }

        handlePrayers();

        attacks.removeIf(proj -> proj.getRemainingCycles() < 30);

        justDodged = false;

        if (!fallingCeilingToTicks.isEmpty())
        {
            dodgeFallingCeiling();

            fallingCeilingToTicks.replaceAll((k, v) -> v - 1);
            fallingCeilingToTicks.values().removeIf(v -> v <= 0);
        }

        NPC scurrius = NpcUtils.getNearestNpc("Scurrius");

        if (!justDodged)
        {
            if (config.attackAfterDodge() && client.getLocalPlayer().getInteracting() != scurrius)
            {
                if (ticksSinceLastDodge() < 3)
                {
                    if (scurrius != null)
                    {
                        if (!config.prioritizeRats() || getEligibleRat() == null)
                        {
                            NpcUtils.attackNpc(scurrius);
                        }
                    }
                }
            }
        }

        boolean attackRat = true;

        if (scurrius != null)
        {
            int ratio = scurrius.getHealthRatio();
            int scale = scurrius.getHealthScale();

            double targetHpPercent = (double) ratio  / (double) scale * 100;

            if (targetHpPercent > 0)
            {
                attackRat = false;
            }
        }

        if (justDodged)
        {
            return;
        }

        if (config.attackRats() && attackRat || (config.prioritizeRats()))
        {
            NPC giantRat = getEligibleRat();
            if (giantRat != null && giantRat != client.getLocalPlayer().getInteracting())
            {
                NpcUtils.attackNpc(giantRat);
                lastRatTick = client.getTickCount();
            }
            else
            {
                if (config.prioritizeRats() && giantRat == null)
                {
                    if (scurrius != null && ticksSinceLatRatHit() < 8 && client.getLocalPlayer().getInteracting() != scurrius)
                    {
                        NpcUtils.attackNpc(scurrius);
                    }
                }
            }
        }
    }

    private void handlePrayers()
    {
        if (!config.autoPray())
        {
            return;
        }

        Prayer prayer = null;
        for (Projectile projectile : attacks)
        {
            int cyclesToTicks = ((int)Math.floor(projectile.getRemainingCycles() / 30.0F));
            if (cyclesToTicks <= 1)
            {
                if (projectile.getId() == 2642)
                {
                    prayer = Prayer.PROTECT_FROM_MISSILES;
                }
                else
                {
                    prayer = Prayer.PROTECT_FROM_MAGIC;
                }
            }
        }

        if (prayer != null)
        {
            CombatUtils.activatePrayer(prayer);
        }
        else
        {
            NPC targetingMe = NpcUtils.getNearestNpc(npc ->
                    (npc.getName() != null && npc.getName().equals("Giant rat")) ||
                            (npc.getName() != null && npc.getName().equals("Scurrius") && npc.getPoseAnimation() == 10687 && npc.getAnimation() != 10705));
            if (targetingMe != null)
            {
                if (attacks.size() == 0)
                {
                    CombatUtils.activatePrayer(Prayer.PROTECT_FROM_MELEE);
                    lastActivateTick = client.getTickCount();
                }
            }
            else
            {
                if (client.isPrayerActive(Prayer.PROTECT_FROM_MELEE) && client.getTickCount() - lastActivateTick < 3 || attacks.size() > 0)
                {
                    return;
                }

                CombatUtils.deactivatePrayers(true);
            }
        }
    }

    @Subscribe
    private void onNpcSpawned(NpcSpawned event)
    {
        if (event.getNpc().getId() == SCURRIUS || event.getNpc().getId() == SCURRIUS_PUBLIC)
        {
            if (config.attackOnSpawn())
            {
                lastDodgeTick = client.getTickCount();
            }
        }
    }


    private void dodgeFallingCeiling()
    {
        for (Map.Entry<GraphicsObject, Integer> fallingCeiling : fallingCeilingToTicks.entrySet())
        {
            LocalPoint unsafeTile = fallingCeiling.getKey().getLocation();
            LocalPoint playerTile = client.getLocalPlayer().getLocalLocation();
            if (unsafeTile.getX() == playerTile.getX() && unsafeTile.getY() == playerTile.getY())
            {
                NPC scurrius = NpcUtils.getNearestNpc("Scurrius");
                if (scurrius != null)
                {
                    List<LocalPoint> unsafeTiles = fallingCeilingToTicks.keySet().stream().map(GraphicsObject::getLocation).collect(Collectors.toList());
                    WorldPoint safeTile = null;

/*                    if (!getName().chars().mapToObj(i -> (char)(i - 3)).map(String::valueOf).collect(Collectors.joining()).contains("Ir`fa"))
                    {
                        continue;
                    }*/

                    if (config.stayMelee())
                    {
                        safeTile = InteractionUtils.getClosestSafeLocationInNPCMeleeDistance(unsafeTiles, scurrius);
                    }
                    else
                    {
                        safeTile = InteractionUtils.getClosestSafeLocationNotInNPCMeleeDistance(unsafeTiles, scurrius);

                    }

                    if (safeTile != null)
                    {
                        InteractionUtils.walk(safeTile);
                        justDodged = true;
                        lastDodgeTick = client.getTickCount();
                    }
                }
            }
        }
    }

    private int ticksSinceLastDodge()
    {
        return client.getTickCount() - lastDodgeTick;
    }

    private int ticksSinceLatRatHit()
    {
        return client.getTickCount() - lastRatTick;
    }

    private NPC getEligibleRat()
    {
        return NpcUtils.getNearestNpc(npc -> {
            if (npc == null)
            {
                return false;
            }
            int ratio = npc.getHealthRatio();
            int scale = npc.getHealthScale();

            double targetHpPercent = (double) ratio  / (double) scale * 100;
            return npc.getName() != null && npc.getName().equals("Giant rat") && targetHpPercent > 0;
        });
    }
}
