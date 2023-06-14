package net.runelite.client.plugins.microbot.pestcontrol;

import com.google.common.collect.ImmutableSet;
import lombok.Getter;
import lombok.Setter;
import net.runelite.api.NpcID;
import net.runelite.api.ObjectID;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject;
import net.runelite.client.plugins.microbot.util.npc.Rs2Npc;
import net.runelite.client.plugins.microbot.util.widget.Rs2Widget;

import java.util.Arrays;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static net.runelite.api.Varbits.QUICK_PRAYER;
import static net.runelite.client.plugins.microbot.util.globval.VarbitValues.QUICK_PRAYER_DISABLED;
import static net.runelite.client.plugins.pestcontrol.Portal.*;
import static net.runelite.client.plugins.pestcontrol.Portal.RED;

public class PestControlScript extends Script {
    public static double version = 1.0;

    boolean walkToCenter = false;

    public static int games = 0;

    private static final Set<Integer> SPINNER_IDS = ImmutableSet.of(
            NpcID.SPINNER,
            NpcID.SPINNER_1710,
            NpcID.SPINNER_1711,
            NpcID.SPINNER_1712,
            NpcID.SPINNER_1713
    );

    private static final Set<Integer> BRAWLER_IDS = ImmutableSet.of(
            NpcID.BRAWLER,
            NpcID.BRAWLER_1736,
            NpcID.BRAWLER_1738,
            NpcID.BRAWLER_1737,
            NpcID.BRAWLER_1735
    );

    @Getter
    @Setter
    private static boolean purpleShield = true;
    @Getter
    @Setter
    private static boolean blueShield = true;
    @Getter
    @Setter
    private static boolean redShield = true;
    @Getter
    @Setter
    private static boolean yellowShield = true;

    public boolean run(PestControlConfig config) {
        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            if (!super.run(344)) return;
            try {
                if (Microbot.getClient().getMinimapZoom() != 2.0) {
                    Microbot.getClient().setMinimapZoom(2.0);
                }
                final boolean isInPestControl = Microbot.getClient().getWidget(WidgetInfo.PEST_CONTROL_BLUE_SHIELD) != null;
                final boolean isInBoat = Microbot.getClient().getWidget(WidgetInfo.PEST_CONTROL_BOAT_INFO) != null;
                if (isInPestControl) {
                    if (Microbot.getVarbitValue(QUICK_PRAYER) == QUICK_PRAYER_DISABLED.getValue()) {
                        final Widget prayerOrb = Rs2Widget.getWidget(WidgetInfo.MINIMAP_QUICK_PRAYER_ORB);
                        if (prayerOrb != null) {
                            Microbot.getMouse().click(prayerOrb.getCanvasLocation());
                            sleep(1000, 1500);
                        }
                    }
                    if (!walkToCenter) {
                        WorldPoint worldPoint = Microbot.getWalker().walkFastRegion(32, 17);
                        if (worldPoint.distanceTo(Microbot.getClient().getLocalPlayer().getWorldLocation()) > 4) {
                            return;
                        } else {
                            walkToCenter = true;
                        }
                    }

                    Widget purpleHealth = Rs2Widget.getWidget(PURPLE.getHitpoints());
                    Widget blueHealth = Rs2Widget.getWidget(BLUE.getHitpoints());
                    Widget redHealth = Rs2Widget.getWidget(RED.getHitpoints());
                    Widget yellowHealth = Microbot.getClient().getWidget(YELLOW.getHitpoints());

                    for (int brawler : BRAWLER_IDS) {
                        if (!Microbot.getClient().getLocalPlayer().isInteracting())
                            if (Rs2Npc.interact(brawler, "attack")) {
                                sleepUntil(() -> !Microbot.getClient().getLocalPlayer().isInteracting());
                                return;
                            }
                    }


                    for (int spinner : SPINNER_IDS) {
                        if (Rs2Npc.interact(spinner, "attack")) {
                            sleepUntil(() -> !Microbot.getClient().getLocalPlayer().isInteracting());
                            return;
                        }
                    }

                    if (Microbot.getClient().getLocalPlayer().isInteracting())
                        return;

                    if (!purpleShield && !purpleHealth.getText().trim().equals("0")) {
                        if (!Microbot.getWalker().isCloseToRegion(4, 8, 30)) {
                            WorldPoint worldPoint = Microbot.getWalker().walkFastRegion(8, 30);
                            if (worldPoint == null) {
                                Microbot.getWalker().walkFastRegion(30, 32);
                            }
                        } else {
                            if (!Microbot.getClient().getLocalPlayer().isInteracting())
                                Rs2Npc.attack("portal");
                        }
                        return;
                    }

                    if (!blueShield && !blueHealth.getText().trim().equals("0")) {
                        if (!Microbot.getWalker().isCloseToRegion(4, 55, 29)) {
                            WorldPoint worldPoint = Microbot.getWalker().walkFastRegion(55, 29);
                            if (worldPoint == null) {
                                Microbot.getWalker().walkFastRegion(30, 32);
                            }
                        } else {
                            if (!Microbot.getClient().getLocalPlayer().isInteracting())
                                Rs2Npc.attack("portal");
                        }
                        return;
                    }

                    if (!redShield && !redHealth.getText().trim().equals("0")) {
                        if (!Microbot.getWalker().isCloseToRegion(4, 22, 12)) {
                            WorldPoint worldPoint = Microbot.getWalker().walkFastRegion(22, 12);
                            if (worldPoint == null) {
                                Microbot.getWalker().walkFastRegion(30, 32);
                            }
                        } else {
                            if (!Microbot.getClient().getLocalPlayer().isInteracting())
                                Rs2Npc.attack("portal");
                        }
                        return;
                    }

                    if (!yellowShield && !yellowHealth.getText().trim().equals("0")) {
                        if (!Microbot.getWalker().isCloseToRegion(4, 48, 13)) {
                            WorldPoint worldPoint = Microbot.getWalker().walkFastRegion(48, 13);
                            if (worldPoint == null) {
                                Microbot.getWalker().walkFastRegion(30, 32);
                            }
                        } else {
                            if (!Microbot.getClient().getLocalPlayer().isInteracting())
                                Rs2Npc.attack("portal");
                        }
                        return;
                    }


                    if (!Microbot.getClient().getLocalPlayer().isInteracting()) {
                        net.runelite.api.NPC portal = Arrays.stream(Rs2Npc.getPestControlPortals()).findFirst().orElse(null);
                        if (portal != null) {
                            if (Rs2Npc.attack(portal.getId())) {
                                sleepUntil(() -> !Microbot.getClient().getLocalPlayer().isInteracting());
                            }
                        } else {
                            if (!Microbot.getClient().getLocalPlayer().isInteracting()) {
                                net.runelite.api.NPC[] npcs = Rs2Npc.getAttackableNpcs();
                                Rs2Npc.attack(Arrays.stream(npcs).findFirst().get().getId());
                            }
                        }
                    }

                } else {
                    walkToCenter = false;
                    purpleShield = true;
                    blueShield = true;
                    redShield = true;
                    yellowShield = true;
                    if (!isInBoat) {
                        Rs2GameObject.interact(ObjectID.GANGPLANK_25632);
                    }
                }
            } catch (Exception ex) {
                Rs2Npc.npcInteraction = null;
                Rs2Npc.npcAction = null;
                System.out.println(ex.getMessage());
            }
        }, 0, 1000, TimeUnit.MILLISECONDS);
        return true;
    }

    public void shutDown() {
        super.shutdown();
    }


}
