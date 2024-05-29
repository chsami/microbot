package net.runelite.client.plugins.microbot.barrows;

import net.runelite.api.NpcID;
import net.runelite.api.Player;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.barrows.enums.STATE;
import net.runelite.client.plugins.microbot.barrows.models.BarrowsBrother;
import net.runelite.client.plugins.microbot.bossassist.models.PRAYSTYLE;
import net.runelite.client.plugins.microbot.example.ExampleConfig;
import net.runelite.client.plugins.microbot.util.npc.Rs2Npc;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class BarrowsScript extends Script {

    private static final int CRYPT_REGION_ID = 14231;

    public static final WorldArea BARROWS_AREA = new WorldArea(
            3545, // x-coordinate of the top-left corner
            3267, // y-coordinate of the top-left corner
            3585 - 3545, // width (x2 - x1)
            3320 - 3267, // height (y1 - y2)
            0 // plane
    );

    public STATE state = STATE.BANKING;

    public static String version = "0,0,1";

    public ArrayList<BarrowsBrother> barrowsBrothers = new ArrayList<BarrowsBrother>();

    public boolean run(ExampleConfig config) {
        Microbot.enableAutoRunOn = false;
        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            try {
                if (!Microbot.isLoggedIn() || Microbot.pauseAllScripts) return;
                if (!super.run()) return;
                long startTime = System.currentTimeMillis();

                if(BARROWS_AREA.contains(Microbot.getClient().getLocalPlayer().getWorldLocation())) {

                } else {
                    // TELEPORT IF BANKING IS DONE
                }

                switch (state) {
                    case BANKING: {
                        break;
                    }
                    case FIGHTHING_AHRIM: {
                    }
                }

                long endTime = System.currentTimeMillis();
                long totalTime = endTime - startTime;
                System.out.println("Total time for loop " + totalTime);

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

    private void handleBanking () {

    }

    private void handleTeleport () {

    }

    private void handleBrotherFight (BarrowsBrother brother) {


    }

    private void constructBrothers () {
        barrowsBrothers.add(new BarrowsBrother(NpcID.AHRIM_THE_BLIGHTED, PRAYSTYLE.MAGE, new WorldPoint(3564, 3289, 0)));
        barrowsBrothers.add(new BarrowsBrother(NpcID.DHAROK_THE_WRETCHED, PRAYSTYLE.MELEE,new WorldPoint(3574, 3299, 0)));
        barrowsBrothers.add(new BarrowsBrother(NpcID.GUTHAN_THE_INFESTED, PRAYSTYLE.MELEE, new WorldPoint(3577, 3281, 0)));
        barrowsBrothers.add(new BarrowsBrother(NpcID.KARIL_THE_TAINTED, PRAYSTYLE.RANGED,new WorldPoint(3565, 3275, 0)));
        barrowsBrothers.add(new BarrowsBrother(NpcID.TORAG_THE_CORRUPTED, PRAYSTYLE.MELEE, new WorldPoint(3555, 3282, 0)));
        barrowsBrothers.add(new BarrowsBrother(NpcID.VERAC_THE_DEFILED, PRAYSTYLE.MELEE, new WorldPoint(3558, 3298, 0)));

    }

    public boolean isInCrypt()
    {
        return getRegionID() == CRYPT_REGION_ID;
    }

    public int getRegionID()
    {
        final Player localPlayer = Microbot.getClient().getLocalPlayer();

        if ( localPlayer == null )
        {
            return 0;
        }

        return localPlayer.getWorldLocation().getRegionID();
    }

}
