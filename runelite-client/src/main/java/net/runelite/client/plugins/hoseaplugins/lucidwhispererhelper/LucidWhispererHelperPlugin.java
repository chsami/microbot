package net.runelite.client.plugins.hoseaplugins.lucidwhispererhelper;

import com.google.inject.Provides;
import net.runelite.client.plugins.hoseaplugins.api.item.SlottedItem;
import net.runelite.client.plugins.hoseaplugins.api.utils.*;
import net.runelite.client.plugins.hoseaplugins.lucidwhispererhelper.overlay.WhispererHelperOverlay;
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
import net.runelite.client.ui.overlay.OverlayManager;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@PluginDescriptor(
        name = "<html><font color=\"#32CD32\">Lucid </font>Whisperer Helper</html>",
        description = "Auto-prays against whisperer and more",
        enabledByDefault = false,
        tags = {"whisperer", "dt2"}
)
@Slf4j
public class LucidWhispererHelperPlugin extends Plugin
{

    @Inject
    private Client client;

    @Inject
    private ClientThread clientThread;

    @Inject
    private OverlayManager overlayManager;

    @Inject
    private WhispererHelperOverlay overlay;

    @Inject
    private LucidWhispererHelperConfig config;

    private List<Projectile> attackProjectiles = new ArrayList<>();

    private static final int LEECH_OBJECT_ACTIVE_ID = 47573;
    private static final int LEECH_OBJECT_INACTIVE_ID = 47575;
    private static final int PILLAR_NPC_ID = 12209;
    public static final int TENTACLE_NPC_ID = 12208;


    private boolean negativeVert = false;
    private boolean negativeDiag = false;

    private int enrageStartTick = 0;
    private int pillarSpawnTick = 0;

    @Getter
    private List<LocalPoint> leeches = new ArrayList<>();

    @Getter
    private int bindTicks = 0;

    @Getter
    private List<NPC> vitas = new ArrayList<>();

    @Getter
    private LocalPoint mostHealthPillar = null;

    @Getter
    private LocalPoint nextMostHealthPillar = null;

    @Getter
    private LocalPoint leastHealthPillar = null;


    private boolean inSpecial = false;

    private boolean activateFragment = false;

    private int lastDodgeTick = 0;

    private final int MAGE_PROJ = 2445;
    private final int MELEE_PROJ = 2467;
    private final int RANGED_PROJ = 2444;

    @Getter
    private final Map<LocalPoint, Integer> unsafeTiles = new HashMap<>();

    @Provides
    LucidWhispererHelperConfig getConfig(final ConfigManager configManager)
    {
        return configManager.getConfig(LucidWhispererHelperConfig.class);
    }

    @Subscribe
    private void onGameTick(final GameTick tick)
    {
        vitas = NpcUtils.getAll(npc -> npc != null && npc.getOverheadText() != null && npc.getOverheadText().equals("Vita!"));

        List<NPC> activePillars = NpcUtils.getAll(npc -> npc.getId() == PILLAR_NPC_ID && getHpPercent(npc) > 0);

        if (mostHealthPillar == null && nextMostHealthPillar == null && leastHealthPillar == null)
        {
            if (activePillars != null && activePillars.size() > 0)
            {
                setPillars(activePillars);
            }
        }
        else if (getTicksSincePillarSpawn() > 23)
        {
            mostHealthPillar = null;
            nextMostHealthPillar = null;
            leastHealthPillar = null;
        }

        if (bindTicks > 0)
        {
            bindTicks--;
        }

        NPC whisperer = NpcUtils.getNearestNpc(npc -> npc != null && npc.getName().contains("Whisperer"));
        if (whisperer != null && whisperer.getAnimation() == 10257)
        {
            SlottedItem venator = InventoryUtils.getFirstItemSlotted(ItemID.VENATOR_BOW);
            if (config.autoVenator() && venator != null && venator.getItem() != null)
            {
                InventoryUtils.itemInteract(venator.getItem().getId(), "Wield");
            }
        }

        if (whisperer == null)
        {
            enrageStartTick = 0;
            return;
        }

        if (config.autoAttack() && client.getTickCount() - lastDodgeTick < 3 && client.getLocalPlayer().getInteracting() != whisperer)
        {
            NpcUtils.interact(whisperer, "Attack");
        }

        if (config.autoDodge())
        {
            dodgeUnsafeTiles();
        }

        if (activateFragment)
        {
            if (config.autoFragment())
            {
                Item fragment = InventoryUtils.getFirstItem("Blackstone fragment");
                if (fragment != null)
                {
                    InventoryUtils.itemInteract(fragment.getId(), "Activate");
                }
            }

            activateFragment = false;
        }

        if (config.autoPray())
        {
            if (attackProjectiles == null || attackProjectiles.size() == 0)
            {
                if (client.isPrayerActive(Prayer.PROTECT_FROM_MISSILES))
                {
                    CombatUtils.togglePrayer(Prayer.PROTECT_FROM_MISSILES);
                }
                if (client.isPrayerActive(Prayer.PROTECT_FROM_MAGIC))
                {
                    CombatUtils.togglePrayer(Prayer.PROTECT_FROM_MAGIC);
                }
                if (client.isPrayerActive(Prayer.PROTECT_FROM_MELEE))
                {
                    CombatUtils.togglePrayer(Prayer.PROTECT_FROM_MELEE);
                }
            }
        }
    }

    private void setPillars(List<NPC> activePillars)
    {
        NPC mostHealth = null;
        NPC nextMostHealth = null;
        NPC leastHealth = null;

        for (NPC npc : activePillars)
        {
            if (getHpPercent(npc) == 100)
            {
                mostHealth = npc;
                break;
            }
        }

        if (mostHealth != null)
        {
            List<NPC> nextCandidates = NpcUtils.getAll(npc -> npc.getId() == PILLAR_NPC_ID && getHpPercent(npc) == 67);

            float leastDistance = 999;
            NPC closest = null;

            for (NPC npc : nextCandidates)
            {
                float distance = InteractionUtils.distanceTo2DHypotenuse(npc.getWorldLocation(), mostHealth.getWorldLocation());
                if (distance < leastDistance)
                {
                    closest = npc;
                    leastDistance = distance;
                }
            }

            if (closest != null)
            {
                nextMostHealth = closest;
            }
        }

        if (nextMostHealth != null)
        {
            final NPC compareTo = nextMostHealth;
            List<NPC> nextCandidates = NpcUtils.getAll(npc -> npc.getId() == PILLAR_NPC_ID && getHpPercent(npc) <= 67 && npc != compareTo);
            float leastDistance = 999;
            NPC closest = null;

            for (NPC npc : nextCandidates)
            {
                float distance = InteractionUtils.distanceTo2DHypotenuse(npc.getWorldLocation(), mostHealth.getWorldLocation());
                if (distance < leastDistance)
                {
                    closest = npc;
                    leastDistance = distance;
                }
            }

            if (closest != null)
            {
                leastHealth = closest;
            }
        }

        if (mostHealth != null && nextMostHealth != null && leastHealth != null)
        {
            final WorldPoint mostHealthPoint = mostHealth.getWorldLocation().dy(2);
            final WorldPoint nextMostHealthPoint = nextMostHealth.getWorldLocation().dy(2);
            final WorldPoint leastHealthPoint = leastHealth.getWorldLocation().dy(2);

            mostHealthPillar = LocalPoint.fromWorld(client.getTopLevelWorldView(), mostHealthPoint);
            nextMostHealthPillar = LocalPoint.fromWorld(client.getTopLevelWorldView(), nextMostHealthPoint);
            leastHealthPillar = LocalPoint.fromWorld(client.getTopLevelWorldView(), leastHealthPoint);
            activateFragment = true;
            pillarSpawnTick = client.getTickCount();
        }
    }

    public int getTicksSinceEnrageStarted()
    {
        return client.getTickCount() - enrageStartTick;
    }

    public int getTicksSincePillarSpawn()
    {
        return client.getTickCount() - pillarSpawnTick;
    }

    private int getHpPercent(NPC npc)
    {
        if (npc == null)
        {
            return 0;
        }

        int ratio = npc.getHealthRatio();
        int scale = npc.getHealthScale();

        return (int) Math.floor((double) ratio  / (double) scale * 100);
    }

    @Subscribe
    private void onChatMessage(ChatMessage event)
    {
        if (event.getMessage().contains("blackstone fragment pulses"))
        {
            inSpecial = true;
            activateFragment = true;
        }

        if (event.getMessage().contains("blackstone fragment loses"))
        {
            inSpecial = false;
            leeches.clear();
        }

        if (event.getMessage().contains("binds you in place"))
        {
            bindTicks = 8;
        }

        if (event.getMessage().contains("pulls you into the"))
        {
            enrageStartTick = client.getTickCount();
        }
    }

    @Subscribe
    private void onClientTick(final ClientTick tick)
    {
        attackProjectiles.removeIf(proj -> proj.getRemainingCycles() < 1);
        unsafeTiles.values().removeIf(i -> client.getTickCount() > i);

        WorldPoint fromInstance = WorldPoint.fromLocalInstance(client, client.getLocalPlayer().getLocalLocation());
        leeches.removeIf(leech -> {
            WorldPoint wp = WorldPoint.fromLocalInstance(client, leech);
            return wp.getRegionX() == fromInstance.getRegionX() && wp.getRegionY() == fromInstance.getRegionY();
        });

        Prayer prayer = null;
        int lowestRemaining = 999;

        for (Projectile projectile : attackProjectiles)
        {
            if (projectile.getRemainingCycles() < lowestRemaining)
            {
                prayer = getPrayer(projectile.getId());
                lowestRemaining = projectile.getRemainingCycles();
            }
        }

        if (prayer != null)
        {
            if (config.autoPray())
            {
                CombatUtils.activatePrayer(prayer);
            }
        }
    }

    @Subscribe
    private void onNpcSpawned(final NpcSpawned event)
    {
        if (event.getNpc().getId() == TENTACLE_NPC_ID)
        {
            int dx = 0;
            int dy = 0;

            switch (event.getNpc().getOrientation())
            {
                case 0: // FACING SOUTH
                    dy = -1;
                    break;
                case 256: // SOUTH-WEST
                    dx = -1;
                    dy = -1;
                    break;
                case 512: // WEST
                    dx = -1;
                    break;
                case 768: // NORTH-WEST
                    dx = -1;
                    dy = 1;
                    break;
                case 1024: // NORTH
                    dy = 1;
                    break;
                case 1280: // NORTH-EAST
                    dx = 1;
                    dy = 1;
                    break;
                case 1536: // EAST
                    dx = 1;
                    break;
                case 1792: // SOUTH-EAST
                    dx = 1;
                    dy = -1;
                    break;
            }

            if (dx != 0 || dy != 0)
            {
                for (int i = 0; i < 5; i++)
                {
                    final LocalPoint fromWorld = LocalPoint.fromWorld(client.getTopLevelWorldView(), event.getNpc().getWorldLocation().dx(dx * i).dy(dy * i).dx(1).dy(1));
                    unsafeTiles.put(fromWorld, client.getTickCount() + (getTicksSinceEnrageStarted() < 200 ? 3 : 2));
                }
            }
        }
    }

    private void dodgeUnsafeTiles()
    {
        NPC whisperer = NpcUtils.getNearestNpc(npc -> npc != null && npc.getName() != null && npc.getName().contains("Whisperer"));

        if (!unsafeTiles.isEmpty())
        {
            WorldPoint safeTile = InteractionUtils.getClosestSafeLocationNotInNPCMeleeDistance(new ArrayList<>(unsafeTiles.keySet()), whisperer, config.maxWeaponRange());
            if (safeTile != null && !safeTile.equals(client.getLocalPlayer().getWorldLocation()))
            {
                InteractionUtils.walk(safeTile);
                lastDodgeTick = client.getTickCount();
            }
        }
    }

    @Subscribe
    private void onGameObjectSpawned(GameObjectSpawned event)
    {
        if (event.getGameObject().getId() == LEECH_OBJECT_ACTIVE_ID)
        {
            leeches.add(event.getGameObject().getLocalLocation());
            activateFragment = true;
        }
    }

    @Subscribe
    private void onProjectileMoved(final ProjectileMoved event)
    {
        int id = event.getProjectile().getId();

        if (id != MAGE_PROJ && id != RANGED_PROJ && id != MELEE_PROJ)
        {
            return;
        }

        if (!containsProjectile(event.getProjectile()))
        {
            attackProjectiles.add(event.getProjectile());
        }
    }

    private Prayer getPrayer(int id)
    {
        switch (id)
        {
            case MAGE_PROJ:
                return Prayer.PROTECT_FROM_MAGIC;
            case RANGED_PROJ:
                return Prayer.PROTECT_FROM_MISSILES;
            case MELEE_PROJ:
                return Prayer.PROTECT_FROM_MELEE;
        }
        return null;
    }

    private boolean containsProjectile(Projectile projectile)
    {
        for (Projectile proj : attackProjectiles)
        {
            if (proj.getId() == projectile.getId() && proj.getRemainingCycles() == projectile.getRemainingCycles())
            {
                return true;
            }
        }
        return false;
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
}
