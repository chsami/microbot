package net.runelite.client.plugins.microbot.scurrius;

import net.runelite.api.GraphicsObject;
import net.runelite.api.Projectile;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.util.npc.Rs2Npc;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;
import net.runelite.client.plugins.microbot.util.prayer.Rs2Prayer;
import net.runelite.client.plugins.microbot.util.prayer.Rs2PrayerEnum;
import net.runelite.client.plugins.microbot.util.walker.Rs2Walker;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;


public class ScurriusScript extends Script {
    public static double version = 1.0;

    final WorldPoint bossLocation = new WorldPoint(3279, 9869, 0);

    private static final int FALLING_ROCKS = 2644;

    public boolean run(ScurriusConfig config) {
        Microbot.enableAutoRunOn = true;
        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            try {
                if (!Microbot.isLoggedIn()) return;
                if (!super.run()) return;
                long startTime = System.currentTimeMillis();


                //  Rs2Walker.walkTo(bossLocation);

                //   Rs2GameObject.interact(ObjectID.BROKEN_BARS, "Climb through (private)");

                Rs2Npc.attack("giant rat");


                // Rs2Npc.attack(7222);

                Rs2Player.eatAt(50);

                long endTime = System.currentTimeMillis();
                long totalTime = endTime - startTime;
                //   System.out.println("Total time for loop " + totalTime);

            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }, 0, 1000, TimeUnit.MILLISECONDS);
        return true;
    }

    @Override
    public void shutdown() {
        super.shutdown();
    }


    /**
     * Pray against mage & range attacks
     *
     * @param projectile
     */
    public void prayAgainstProjectiles(Projectile projectile) {
        if (projectile.getId() == 2642) {
            Rs2Prayer.toggle(Rs2PrayerEnum.PROTECT_RANGE, true);
        } else if (projectile.getId() == 2640) {
            Rs2Prayer.toggle(Rs2PrayerEnum.PROTECT_MAGIC, true);
        }
        Microbot.getClientThread().runOnSeperateThread(() -> {
            sleep(1500);
            Rs2Prayer.toggle(Rs2PrayerEnum.PROTECT_MELEE, true);
            return true;
        });
    }


    /**
     * Dodge if the projectile is on the player
     * This method currently scans around the player x + 1 and y + 1 and x - 1 and y - 1 and
     *
     * @param graphicsObject
     */
    public void dodgeGraphicObject(GraphicsObject graphicsObject) {
        if (graphicsObject.getId() != FALLING_ROCKS)
            return;

        // we use Microbot.getClient().getLocalPlayer().getWorldLocation() because it seems that it does not have to be recalculated for a instanced region
        boolean isProjectileOnPlayer = WorldPoint.fromLocal(Microbot.getClient(), graphicsObject.getLocation()).equals(Microbot.getClient().getLocalPlayer().getWorldLocation());
        if (isProjectileOnPlayer) {
            System.out.println("Projectile on player!!!");
            //try and look around the player first, if no tile around the player is available do a full scan
            // this array can be extended, but let's check if this is okay for now
            //TODO: Check if locationsToCheck is a walkable tile
            List<WorldPoint> locationsToCheck = List.of(new WorldPoint(Rs2Player.getWorldLocation().getX() + 1, Rs2Player.getWorldLocation().getY(), Rs2Player.getWorldLocation().getPlane()),
                    new WorldPoint(Rs2Player.getWorldLocation().getX() - 1, Rs2Player.getWorldLocation().getY(), Rs2Player.getWorldLocation().getPlane()),
                    new WorldPoint(Rs2Player.getWorldLocation().getX(), Rs2Player.getWorldLocation().getY() + 1, Rs2Player.getWorldLocation().getPlane()),
                    new WorldPoint(Rs2Player.getWorldLocation().getX(), Rs2Player.getWorldLocation().getY() - 1, Rs2Player.getWorldLocation().getPlane()));

            Iterator<GraphicsObject> iterator = Microbot.getClient().getTopLevelWorldView().getGraphicsObjects().iterator();
            //if the graphicsObjectLocation is not on a locationToCheck position, walk there beause it is safe
            WorldPoint safeTile = locationsToCheck.stream()
                    .filter(l -> {
                Microbot.getClient().getTopLevelWorldView().getGraphicsObjects().iterator();
                boolean _safeTile = true;
                while (iterator.hasNext()) {
                    WorldPoint graphicsObjectLocation = WorldPoint.fromLocal(Microbot.getClient(), iterator.next().getLocation());
                    System.out.println("checking: " + graphicsObjectLocation);
                    if (l.equals(graphicsObjectLocation)) {
                        _safeTile = false;
                        break;
                    }
                }
                return _safeTile;
            })
                    .findFirst()
                    .orElse(null);
            if (safeTile != null) {
                Rs2Walker.walkFastCanvas(safeTile);
            }
        }
    }
}
