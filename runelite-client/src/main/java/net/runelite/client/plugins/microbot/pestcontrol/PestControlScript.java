package net.runelite.client.plugins.microbot.pestcontrol;

import com.google.common.collect.ImmutableSet;
import lombok.Getter;
import lombok.Setter;
import net.runelite.api.NpcID;
import net.runelite.api.ObjectID;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.util.combat.Rs2Combat;
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Item;
import net.runelite.client.plugins.microbot.util.magic.Rs2Magic;
import net.runelite.client.plugins.microbot.util.npc.Rs2Npc;
import net.runelite.client.plugins.microbot.util.walker.Rs2Walker;
import net.runelite.client.plugins.microbot.util.widget.Rs2Widget;

import java.util.Arrays;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static net.runelite.client.plugins.microbot.util.prayer.Rs2Prayer.isQuickPrayerEnabled;
import static net.runelite.client.plugins.pestcontrol.Portal.*;

public class PestControlScript extends Script {
    public static double version = 1.5;

    boolean walkToCenter = false;
    PestControlConfig config;

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

    final int distanceToPortal = 8;

    public boolean run(PestControlConfig config) {
        this.config = config;
        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            if (!super.run()) return;
            try {
                if (Microbot.getClient().getMinimapZoom() != 2.0) {
                    Microbot.getClient().setMinimapZoom(2.0);
                }
                final boolean isInPestControl = Microbot.getClient().getWidget(WidgetInfo.PEST_CONTROL_BLUE_SHIELD) != null;
                final boolean isInBoat = Microbot.getClient().getWidget(WidgetInfo.PEST_CONTROL_BOAT_INFO) != null;
                if (isInPestControl) {
                    if (!isQuickPrayerEnabled() && Microbot.getClient().getBoostedSkillLevel(Skill.PRAYER) != 0) {
                        final Widget prayerOrb = Rs2Widget.getWidget(WidgetInfo.MINIMAP_QUICK_PRAYER_ORB);
                        if (prayerOrb != null) {
                            Microbot.getMouse().click(prayerOrb.getCanvasLocation());
                            sleep(1000, 1500);
                        }
                    }
                    if (!walkToCenter) {
                        WorldPoint worldPoint = Rs2Walker.walkFastRegion(32, 17);
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

                    Rs2Combat.setSpecState(true, 550);

                    if (handleAttack(PestControlNpc.BRAWLER, purpleHealth, blueHealth, redHealth, yellowHealth, 1)
                            || handleAttack(PestControlNpc.PORTAL, purpleHealth, blueHealth, redHealth, yellowHealth, 1)
                            || handleAttack(PestControlNpc.SPINNER, purpleHealth, blueHealth, redHealth, yellowHealth, 1)) {
                        return;
                    }

                    if (handleAttack(PestControlNpc.BRAWLER, purpleHealth, blueHealth, redHealth, yellowHealth, 2)
                            || handleAttack(PestControlNpc.PORTAL, purpleHealth, blueHealth, redHealth, yellowHealth, 2)
                            || handleAttack(PestControlNpc.SPINNER, purpleHealth, blueHealth, redHealth, yellowHealth, 2)) {
                        return;
                    }
                    if (handleAttack(PestControlNpc.BRAWLER, purpleHealth, blueHealth, redHealth, yellowHealth, 3)
                            || handleAttack(PestControlNpc.PORTAL, purpleHealth, blueHealth, redHealth, yellowHealth, 3)
                            || handleAttack(PestControlNpc.SPINNER, purpleHealth, blueHealth, redHealth, yellowHealth, 3)) {
                        return;
                    }

                    if (Microbot.getClient().getLocalPlayer().isInteracting())
                        return;


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
                        if (Microbot.getClient().getLocalPlayer().getCombatLevel() >= 100) {
                            Rs2GameObject.interact(ObjectID.GANGPLANK_25632);
                        } else if (Microbot.getClient().getLocalPlayer().getCombatLevel() >= 70) {
                            Rs2GameObject.interact(ObjectID.GANGPLANK_25631);
                        } else {
                            Rs2GameObject.interact(ObjectID.GANGPLANK_14315);
                        }
                        if (config.alchInBoat() && !config.alchItem().equalsIgnoreCase("")) {
                            Rs2Magic.alch(config.alchItem());
                        } else {
                            sleep(3000);
                        }
                    }
                }
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }, 0, 1000, TimeUnit.MILLISECONDS);
        return true;
    }

    public void shutDown() {
        super.shutdown();
    }

    private boolean handleAttack(PestControlNpc npcType, Widget purpleHealth, Widget blueHealth, Widget redHealth, Widget yellowHealth, int priority) {
        if (priority == 1) {
            if (config.Priority1() == npcType) {
                if (npcType == PestControlNpc.BRAWLER) {
                    return attackBrawler();
                } else if (npcType == PestControlNpc.PORTAL) {
                    return attackPortals(purpleHealth, blueHealth, redHealth, yellowHealth);
                } else if (npcType == PestControlNpc.SPINNER) {
                    return attackSpinner();
                }
            }
        } else if (priority == 2) {
            if (config.Priority2() == npcType) {
                if (npcType == PestControlNpc.BRAWLER) {
                    return attackBrawler();
                } else if (npcType == PestControlNpc.PORTAL) {
                    return attackPortals(purpleHealth, blueHealth, redHealth, yellowHealth);
                } else if (npcType == PestControlNpc.SPINNER) {
                    return attackSpinner();
                }
            }
        } else {
            if (config.Priority2() == npcType) {
                if (npcType == PestControlNpc.BRAWLER) {
                    return attackBrawler();
                } else if (npcType == PestControlNpc.PORTAL) {
                    return attackPortals(purpleHealth, blueHealth, redHealth, yellowHealth);
                } else if (npcType == PestControlNpc.SPINNER) {
                    return attackSpinner();
                }
            }
        }

        return false;
    }


    private boolean attackPortals(Widget purpleHealth, Widget blueHealth, Widget redHealth, Widget yellowHealth) {
        if (!purpleShield && !purpleHealth.getText().trim().equals("0")) {
            if (!Rs2Walker.isCloseToRegion(distanceToPortal, 8, 30)) {
                WorldPoint worldPoint = Rs2Walker.walkFastRegion(8, 30);
                if (worldPoint == null) {
                    Rs2Walker.walkFastRegion(30, 32);
                    return true;
                }
            } else {
                if (!Microbot.getClient().getLocalPlayer().isInteracting())
                    return Rs2Npc.attack("portal");
            }
            return false;
        }

        if (!blueShield && !blueHealth.getText().trim().equals("0")) {
            if (!Rs2Walker.isCloseToRegion(distanceToPortal, 55, 29)) {
                WorldPoint worldPoint = Rs2Walker.walkFastRegion(55, 29);
                if (worldPoint == null) {
                    Rs2Walker.walkFastRegion(30, 32);
                    return true;
                }
            } else {
                if (!Microbot.getClient().getLocalPlayer().isInteracting())
                    return Rs2Npc.attack("portal");
            }
            return false;
        }

        if (!redShield && !redHealth.getText().trim().equals("0")) {
            if (!Rs2Walker.isCloseToRegion(distanceToPortal, 22, 12)) {
                WorldPoint worldPoint = Rs2Walker.walkFastRegion(22, 12);
                if (worldPoint == null) {
                    Rs2Walker.walkFastRegion(30, 32);
                    return true;
                }
            } else {
                if (!Microbot.getClient().getLocalPlayer().isInteracting())
                    return Rs2Npc.attack("portal");
            }
            return false;
        }

        if (!yellowShield && !yellowHealth.getText().trim().equals("0")) {
            if (!Rs2Walker.isCloseToRegion(distanceToPortal, 48, 13)) {
                WorldPoint worldPoint = Rs2Walker.walkFastRegion(48, 13);
                if (worldPoint == null) {
                    Rs2Walker.walkFastRegion(30, 32);
                    return true;
                }
            } else {
                if (!Microbot.getClient().getLocalPlayer().isInteracting())
                    return Rs2Npc.attack("portal");
            }
            return false;
        }
        return false;
    }

    private boolean attackSpinner() {
        for (int brawler : SPINNER_IDS) {
            if (!Microbot.getClient().getLocalPlayer().isInteracting())
                if (Rs2Npc.interact(brawler, "attack")) {
                    sleepUntil(() -> !Microbot.getClient().getLocalPlayer().isInteracting());
                    return true;
                }
        }
        return false;
    }

    private boolean attackBrawler() {
        for (int brawler : BRAWLER_IDS) {
            if (!Microbot.getClient().getLocalPlayer().isInteracting())
                if (Rs2Npc.interact(brawler, "attack")) {
                    sleepUntil(() -> !Microbot.getClient().getLocalPlayer().isInteracting());
                    return true;
                }
        }
        return false;
    }

}
