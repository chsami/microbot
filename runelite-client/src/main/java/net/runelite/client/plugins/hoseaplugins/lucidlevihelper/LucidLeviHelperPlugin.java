package net.runelite.client.plugins.hoseaplugins.lucidlevihelper;

import net.runelite.client.plugins.hoseaplugins.api.utils.CombatUtils;
import net.runelite.api.Client;
import net.runelite.api.Prayer;
import net.runelite.api.Projectile;
import net.runelite.api.events.ClientTick;
import net.runelite.api.events.ProjectileMoved;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;

@PluginDescriptor(
        name = "<html><font color=\"#32CD32\">Lucid </font>Levi Helper</html>",
        description = "Auto-prays against leviathan and more",
        enabledByDefault = false,
        tags = {"leviathan", "dt2", "lucid"}
)
@Singleton
public class LucidLeviHelperPlugin extends Plugin
{

    @Inject
    private Client client;

    private List<Projectile> attackProjectiles = new ArrayList<>();

    private final int MAGE_PROJ = 2489;
    private final int MELEE_PROJ = 2488;
    private final int RANGED_PROJ = 2487;

    @Subscribe
    private void onClientTick(final ClientTick tick)
    {
        attackProjectiles.removeIf(proj -> proj.getRemainingCycles() < 1);

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
            CombatUtils.activatePrayer(prayer);
        }
    }

    @Subscribe
    private void onProjectileMoved(final ProjectileMoved event)
    {
        final Projectile projectile = event.getProjectile();

        if (projectile.getId() != MAGE_PROJ && projectile.getId() != RANGED_PROJ && projectile.getId() != MELEE_PROJ)
        {
            return;
        }

        if (projectile.getRemainingCycles() != (projectile.getEndCycle() - projectile.getStartCycle()))
        {
            return;
        }

        attackProjectiles.add(event.getProjectile());
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
}
