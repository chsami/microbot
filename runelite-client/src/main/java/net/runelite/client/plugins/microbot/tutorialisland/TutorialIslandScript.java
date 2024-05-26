package net.runelite.client.plugins.microbot.tutorialisland;


import net.runelite.api.*;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.widgets.Widget;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank;
import net.runelite.client.plugins.microbot.util.dialogues.Rs2Dialogue;
import net.runelite.client.plugins.microbot.util.equipment.Rs2Equipment;
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.keyboard.Rs2Keyboard;
import net.runelite.client.plugins.microbot.util.magic.Rs2Magic;
import net.runelite.client.plugins.microbot.util.npc.Rs2Npc;
import net.runelite.client.plugins.microbot.util.player.NameGenerator;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;
import net.runelite.client.plugins.microbot.util.tabs.Rs2Tab;
import net.runelite.client.plugins.microbot.util.walker.Rs2Walker;
import net.runelite.client.plugins.microbot.util.widget.Rs2Widget;
import net.runelite.client.plugins.skillcalculator.skills.MagicAction;

import java.awt.event.KeyEvent;
import java.util.concurrent.TimeUnit;

import static net.runelite.client.plugins.microbot.util.dialogues.Rs2Dialogue.clickContinue;
import static net.runelite.client.plugins.microbot.util.dialogues.Rs2Dialogue.isInDialogue;
import static net.runelite.client.plugins.microbot.util.math.Random.random;
import static net.runelite.client.plugins.microbot.util.settings.Rs2Settings.hideRoofs;
import static net.runelite.client.plugins.microbot.util.settings.Rs2Settings.turnOffMusic;

public class TutorialIslandScript extends Script {

    public static double version = 1.0;
    public static Status status = Status.NAME;

    public boolean run(TutorialIslandConfig config) {
        Microbot.enableAutoRunOn = false;
        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            try {
                if (!Microbot.isLoggedIn()) return;
                if (!super.run()) return;

                CalculateStatus();

                ClickContinue();

                switch (status) {
                    case NAME:
                        String name = new NameGenerator(random(3, 6)).getName() + new NameGenerator(random(3, 6)).getName();
                        Rs2Widget.clickWidget(36569095);
                        Rs2Keyboard.typeString(name);
                        Rs2Widget.clickWidget("Look up name");
                        sleepUntil(() -> Rs2Widget.hasWidget("Set name"));
                        sleep(4000);
                        if (Rs2Widget.hasWidget("Sorry")) {
                            Rs2Widget.clickWidget(36569095);
                            for (int i = 0; i < name.length(); i++) {
                                Rs2Keyboard.keyPress(KeyEvent.VK_BACK_SPACE);
                                sleep(300, 600);
                            }
                        } else {
                            Rs2Widget.clickWidget("Set name");
                            sleep(3000);
                            Rs2Widget.clickWidget("Set name");
                            sleepUntil(() -> !isLookupNameButtonVisible());
                        }
                        sleep(2000);
                        break;
                    case CHARACTER:
                        RandomizeCharacter();
                        break;
                    case GETTING_STARTED:
                        GettingStarted();
                        break;
                    case SURVIVAL_GUIDE:
                        SurvivalGuide();
                        break;
                    case COOKING_GUIDE:
                        CookingGuide();
                        break;
                    case QUEST_GUIDE:
                        QuestGuide();
                        break;
                    case MINING_GUIDE:
                        MiningGuide();
                        break;
                    case COMBAT_GUIDE:
                        CombatGuide();
                        break;
                    case BANKER_GUIDE:
                        BankerGuide();
                        break;
                    case PRAYER_GUIDE:
                        PrayerGuide();
                        break;
                    case MAGE_GUIDE:
                        MageGuide();
                        break;
                    case FINISHED:
                        shutdown();
                        break;
                }
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }, 0, 600, TimeUnit.MILLISECONDS);
        return true;
    }

    @Override
    public void shutdown() {
        super.shutdown();
    }

    enum Status {
        NAME,
        CHARACTER,
        GETTING_STARTED,
        SURVIVAL_GUIDE,
        COOKING_GUIDE,
        QUEST_GUIDE,
        MINING_GUIDE,
        COMBAT_GUIDE,
        BANKER_GUIDE,
        PRAYER_GUIDE,
        MAGE_GUIDE,
        FINISHED
    }

    int LOOKUPNAME = 558;

    final int CharacterCreation = 679;
    final int CharacterCreation_Confirm = 68;

    final int PROGRESS_BAR = 614;

    final int[] CharacterCreation_Arrows = new int[]{13, 17, 21, 25, 29, 33, 37, 44, 48, 52, 56, 60};

    private boolean isLookupNameButtonVisible() {
        return Rs2Widget.getWidget(LOOKUPNAME, 1) != null;
    }

    private boolean isCharacterCreationVisible() {
        return Rs2Widget.getWidget(CharacterCreation, 1) != null;
    }

    public void CalculateStatus() {
        if (isLookupNameButtonVisible()) {
            status = Status.NAME;
        } else if (isCharacterCreationVisible()) {
            status = Status.CHARACTER;
        } else if (Microbot.getVarbitPlayerValue(281) < 10) {
            status = Status.GETTING_STARTED;
        } else if (Microbot.getVarbitPlayerValue(281) >= 10 && Microbot.getVarbitPlayerValue(281) < 120) {
            status = Status.SURVIVAL_GUIDE;
        } else if (Microbot.getVarbitPlayerValue(281) >= 120 && Microbot.getVarbitPlayerValue(281) < 200) {
            status = Status.COOKING_GUIDE;
        } else if (Microbot.getVarbitPlayerValue(281) >= 200 && Microbot.getVarbitPlayerValue(281) <= 250) {
            status = Status.QUEST_GUIDE;
        } else if (Microbot.getVarbitPlayerValue(281) >= 260 && Microbot.getVarbitPlayerValue(281) <= 360) {
            status = Status.MINING_GUIDE;
        } else if (Microbot.getVarbitPlayerValue(281) > 360 && Microbot.getVarbitPlayerValue(281) < 510) {
            status = Status.COMBAT_GUIDE;
        } else if (Microbot.getVarbitPlayerValue(281) >= 510 && Microbot.getVarbitPlayerValue(281) < 540) {
            status = Status.BANKER_GUIDE;
        } else if (Microbot.getVarbitPlayerValue(281) >= 540 && Microbot.getVarbitPlayerValue(281) < 610) {
            status = Status.PRAYER_GUIDE;
        } else if (Microbot.getVarbitPlayerValue(281) >= 610 && Microbot.getVarbitPlayerValue(281) < 1000) {
            status = Status.MAGE_GUIDE;
        } else if (Microbot.getVarbitPlayerValue(281) == 1000) {
            status = Status.FINISHED;
        }
    }

    public void RandomizeCharacter() {
        if (random(1, 10) == 2) {
            Rs2Widget.clickWidget("Confirm");
            sleepUntil(() -> !isCharacterCreationVisible());
        }

        int randomIndex = (int) Math.floor(Math.random() * CharacterCreation_Arrows.length);
        int item = CharacterCreation_Arrows[randomIndex];
        Widget widget = Rs2Widget.getWidget(CharacterCreation, item);
        Rs2Widget.clickWidget(widget.getId());
        Rs2Widget.clickWidget("Select");
    }

    //config 281 needed
    public void GettingStarted() {
        NPC npc = Rs2Npc.getNpc(3308);
        if (isInDialogue()) return;
        if (Rs2Widget.getWidget(219, 1) != null) {
            Rs2Keyboard.typeString(Integer.toString(random(1, 3)));
            return;
        }
        if (Microbot.getVarbitPlayerValue(281) == 3) {
            Rs2Widget.clickWidget(10747945);
            sleep(1000);
            turnOffMusic();
            Microbot.getMouse().scrollDown(new Point(800, 800));
            Microbot.getClient().setCameraPitchTarget(460);
            return;
        }

        if (Rs2Npc.interact(npc, "Talk-to")) {
            sleepUntil(() -> Microbot.getClient().getLocalPlayer().getInteracting() == npc);
            sleep(1000);
        }
    }

    public void SurvivalGuide() {
        if (Rs2Inventory.contains("Shrimps")) return;
        if (Microbot.getVarbitPlayerValue(281) == 10 || Microbot.getVarbitPlayerValue(281) == 20) {
            SurvivalExpert();
        } else if (Microbot.getVarbitPlayerValue(281) == 30
                || Microbot.getVarbitPlayerValue(281) == 40
                || Microbot.getVarbitPlayerValue(281) == 50
                || Microbot.getVarbitPlayerValue(281) == 60
                || Microbot.getVarbitPlayerValue(281) == 70
                || Microbot.getVarbitPlayerValue(281) == 80
                || Microbot.getVarbitPlayerValue(281) == 90) { // FISHING + woodcutting + cooking
            if (!Rs2Inventory.contains("Raw shrimps")) {
                ClickContinue();
                sleep(1000);
                Rs2Widget.clickWidget(10747959);
                sleep(1000);
                Rs2Npc.interact(3317, "Net");
                sleepUntil(() -> Rs2Inventory.contains("Raw shrimps"));
            } else {
                if (Microbot.getVarbitPlayerValue(281) < 90) {
                    if (!Rs2Inventory.contains("Bronze axe")) {
                        Rs2Widget.clickWidget(10747957);
                        if (!isInDialogue()) {
                            InteractWithNpc(8503);
                        } else {
                            ClickContinue();
                            ClickContinue();
                        }
                    } else if (!Rs2Inventory.contains("Logs") && Microbot.getClient().getSkillExperience(Skill.WOODCUTTING) == 0) {
                        CutTree();
                    } else if (Rs2Inventory.contains("Logs")) {
                        LightFire();
                    }
                } else if (Microbot.getVarbitPlayerValue(281) == 90 && Rs2Inventory.contains("Raw shrimps")) {
                    if (!Rs2Inventory.contains("Logs") && !Rs2GameObject.exists(26185))
                        CutTree();
                    if (!Rs2GameObject.exists(26185))
                        LightFire();
                    Rs2Inventory.use("Raw shrimps");
                    Rs2GameObject.interact(26185, "Use");
                    sleepUntil(() -> Rs2Inventory.contains("Shrimps"));
                }
            }
        }

    }

    public void MageGuide() {
        if (isInDialogue()) return;
        if (Microbot.getVarbitPlayerValue(281) == 610 || Microbot.getVarbitPlayerValue(281) == 620) {
            WorldPoint worldPoint = new WorldPoint(3135, 3088, 0);
            if (Microbot.getClient().getLocalPlayer().getWorldLocation().distanceTo(worldPoint) > 2) {
                Rs2Walker.walkTo(worldPoint);
            } else {
                Rs2Npc.interact(3309, "Talk-to");
                sleepUntil(() -> isInDialogue());
            }
        } else if (Microbot.getVarbitPlayerValue(281) == 630) {
            Rs2Widget.clickWidget(164, 58); //switchToMagicTab
        } else if (Microbot.getVarbitPlayerValue(281) == 640) {
            Rs2Npc.interact(3309, "Talk-to");
        } else if (Microbot.getVarbitPlayerValue(281) == 650) {
            Rs2Magic.castOn(MagicAction.WIND_STRIKE, Rs2Npc.getNpc(3316));
        } else if (Microbot.getVarbitPlayerValue(281) == 670) {
            if (isInDialogue()) {
                clickContinue();
                return;
            }
            if (Rs2Widget.hasWidget("Do you want to go to the mainland?")) {
                Rs2Widget.clickWidget(14352385);
                Rs2Keyboard.typeString("1");
            } else if (Rs2Widget.hasWidget("Select an option")) {
                if (Rs2Widget.hasWidget("No, I'm not planning to do that")) {
                    Rs2Keyboard.typeString("3");
                } else {
                    Rs2Widget.clickWidget("Yes, send me to the mainland");
                }
            } else {
                Rs2Npc.interact(3309, "Talk-to");
            }
        }
    }

    public void PrayerGuide() {
        if (isInDialogue()) return;
        if (Microbot.getVarbitPlayerValue(281) == 640 || Microbot.getVarbitPlayerValue(281) == 550 || Microbot.getVarbitPlayerValue(281) == 540) {
            Rs2Walker.walkTo(new WorldPoint(3124, 3106, 0));
            Rs2Npc.interact(3319, "Talk-to");
        } else if (Microbot.getVarbitPlayerValue(281) == 560) {
            Rs2Widget.clickWidget(10747961); //switchToPrayerTab
        } else if (Microbot.getVarbitPlayerValue(281) == 570) {
            Rs2Npc.interact(3319, "Talk-to");
        } else if (Microbot.getVarbitPlayerValue(281) == 580) {
            Rs2Widget.clickWidget(164, 46); //switchToFriendsTab
        } else if (Microbot.getVarbitPlayerValue(281) == 600) {
            Rs2Npc.interact(3319, "Talk-to");
        }
    }

    public void BankerGuide() {
        if (isInDialogue()) return;
        if (Microbot.getVarbitPlayerValue(281) == 510) {
            Rs2GameObject.interact(ObjectID.BANK_BOOTH_10083);

            sleepUntil(() -> Microbot.getVarbitPlayerValue(281) != 510);
        } else if (Microbot.getVarbitPlayerValue(281) == 520) {
            Rs2Bank.closeBank();
            Rs2GameObject.interact(26815);
            sleepUntil(() -> Microbot.getVarbitPlayerValue(281) != 520);
        } else if (Microbot.getVarbitPlayerValue(281) == 525 || Microbot.getVarbitPlayerValue(281) == 530) {
            Rs2Walker.walkTo(new WorldPoint(3127, 3123, 0), 2);
            Rs2Npc.interact(3310, "Talk-to");
        } else if (Microbot.getVarbitPlayerValue(281) == 531) {
            Rs2Widget.clickWidget(10747943); //switchToAccountManagementTab
        } else if (Microbot.getVarbitPlayerValue(281) == 532) {
            Rs2Npc.interact(3310, "Talk-to");
        }
    }

    public void CombatGuide() {
        if (Microbot.getVarbitPlayerValue(281) >= 370) {
            if (isInDialogue()) return;
            if (Microbot.getVarbitPlayerValue(281) == 500) {
                Rs2Walker.walkTo(new WorldPoint(3111, 9526, Rs2Player.getWorldLocation().getPlane()));
                Rs2GameObject.interact("Ladder","Climb-up");
                sleepUntil(() -> Microbot.getVarbitPlayerValue(281) != 500);
                return;
            }
            if (Microbot.getVarbitPlayerValue(281) == 480 || Microbot.getVarbitPlayerValue(281) == 490) { // killl rat with range
                Actor rat = Microbot.getClient().getLocalPlayer().getInteracting();
                if (rat != null && rat.getName().equalsIgnoreCase("giant rat")) return;
                Rs2Inventory.wield( "Shortbow");
                Rs2Inventory.wield("Bronze arrow");
                Rs2Npc.attack("Giant rat");
                return;
            }
            if (Microbot.getVarbitPlayerValue(281) == 470) {
                Rs2Walker.walkTo(new WorldPoint(3109, 9511, 0));
                NPC npc = Rs2Npc.getNpc("Combat Instructor");
                Rs2Npc.interact(npc, "Talk-to");
                sleep(1000);
                sleepUntil(() -> !isInDialogue());
                return;
            }
            if (Microbot.getVarbitPlayerValue(281) > 420) {
                if (Microbot.getClient().getLocalPlayer().isInteracting() || Rs2Player.isAnimating()) return;
                if (Rs2Equipment.isWearing("Bronze sword")) {
                    Rs2Widget.clickWidget(10747956);
                    WorldPoint worldPoint = new WorldPoint(3105, 9517, 0);
                    Rs2Walker.walkTo(worldPoint);
                    Rs2Npc.attack("Giant rat");
                } else {
                    Rs2Tab.switchToInventoryTab();
                    sleep(500);
                    Rs2Inventory.wield("Bronze sword");
                    sleep(500);
                    Rs2Inventory.wield("Wooden shield");
                }
                return;
            }
            if (Microbot.getVarbitPlayerValue(281) == 420) {
                Rs2Tab.switchToInventoryTab();
                sleep(500);
                Rs2Inventory.wield("Bronze sword");
                sleep(500);
                Rs2Inventory.wield("Wooden shield");
                NPC npc = Rs2Npc.getNpc("Combat Instructor");
                if (Rs2Npc.interact(npc, "Talk-to")) {
                    sleep(1000);
                    sleepUntil(() -> !isInDialogue());
                }
                return;
            }
            if (Microbot.getVarbitPlayerValue(281) == 410) {
                NPC npc = Rs2Npc.getNpc("Combat Instructor");
                if (Rs2Npc.interact(npc, "Talk-to")) {
                    sleep(1000);
                    sleepUntil(() -> !isInDialogue());
                }
                return;
            }
            if (Microbot.getVarbitPlayerValue(281) == 390 || Microbot.getVarbitPlayerValue(281) == 400 || Microbot.getVarbitPlayerValue(281) == 405) {
                if (Rs2Widget.getWidget(84, 1) == null && !Rs2Equipment.isWearing("Bronze dagger")) {
                    Rs2Widget.clickWidget(10747960);
                    sleep(1000);
                    Rs2Widget.clickWidget(25362433);
                    sleepUntil(() ->Rs2Widget.getWidget(84, 1) != null);
                    sleep(1000);
                    Rs2Widget.clickWidget("Bronze dagger");
                    sleep(1000);
                    Rs2Keyboard.keyPress(KeyEvent.VK_ESCAPE);
                    return;
                }
            }
            Rs2Walker.walkTo(new WorldPoint(random(3106, 3108), random(9508, 9510), 0));
            sleep(500);
            sleepUntil(Rs2Player::isWalking);
            NPC npc = Rs2Npc.getNpc("Combat Instructor");
            if (Rs2Npc.interact(npc, "Talk-to")) {
                sleep(1000);
                sleepUntil(() -> !isInDialogue());
            }
        }
    }

    public void MiningGuide() {
        if (Microbot.getVarbitPlayerValue(281) == 260) {
            if (isInDialogue()) return;
            Rs2Walker.walkTo(new WorldPoint(random(3082, 3085), random(9502, 9505), 0));
            NPC npc = Rs2Npc.getNpc(3311);
            Rs2Npc.interact(npc, "Talk-to");
            sleepUntil(() -> Microbot.getClient().getLocalPlayer().getInteracting() == npc);
        } else {
            if (Rs2Inventory.contains("Bronze dagger")) {
                Rs2GameObject.interact(ObjectID.GATE_9718, "Open");
                sleepUntil(() -> Microbot.getVarbitPlayerValue(281) > 360);
                return;
            }
            if (Rs2Inventory.contains("Bronze bar") && Rs2Inventory.contains("Hammer")) {
                Rs2GameObject.interact("Anvil", "Smith");
                sleepUntil(() -> Rs2Widget.getWidget(312, 1) != null);
                Rs2Widget.clickWidget("Bronze dagger");
                sleepUntil(() -> Rs2Inventory.contains("Bronze dagger"));
                return;
            }
            if (Rs2Inventory.contains("Bronze bar") && !Rs2Inventory.contains("Hammer")) {
                if (isInDialogue()) return;
                NPC npc = Rs2Npc.getNpc(3311);
                Rs2Npc.interact(npc, "Talk-to");
                sleepUntil(() -> Microbot.getClient().getLocalPlayer().getInteracting() == npc);
                return;
            }
            if (Rs2Inventory.contains("Bronze pickaxe") && (!Rs2Inventory.contains("Copper ore") || !Rs2Inventory.contains("Tin ore"))) {
                if (!Rs2Inventory.contains("Copper ore")) {
                    Rs2GameObject.interact(10079, "Mine");
                    sleepUntil(() -> Rs2Inventory.contains("Copper ore"));
                }
                if (!Rs2Inventory.contains("Tin ore")) {
                    Rs2GameObject.interact(10080, "Mine");
                    sleepUntil(() -> Rs2Inventory.contains("Tin ore"));
                }
            } else if (Rs2Inventory.contains("Copper ore") && Rs2Inventory.contains("Tin ore")) {
                Rs2Inventory.interact("Tin ore");
                Rs2GameObject.interact("Furnace");
                sleepUntil(() ->  Rs2Inventory.contains("Bronze bar"));
            }
        }
    }

    public void QuestGuide() {
        if (Microbot.getVarbitPlayerValue(281) == 200 || Microbot.getVarbitPlayerValue(281) == 210) {
            Rs2Walker.walkTo(new WorldPoint(random(3083, 3086), random(3127, 3129), 0));
            Rs2GameObject.interact(9716, "Open");
            sleep(600, 1200);
        } else if (Microbot.getVarbitPlayerValue(281) != 250) {
            if (isInDialogue()) return;
            NPC npc = Rs2Npc.getNpc(3312);
            Rs2Npc.interact(npc, "Talk-to");
            sleepUntil(() -> Microbot.getClient().getLocalPlayer().getInteracting() == npc);
            Rs2Tab.switchToQuestTab();
            Rs2Widget.clickWidget(10747958);
        } else {
            Rs2GameObject.interact(9726, "Climb-down");
            sleep(2000);
        }

    }

    public void CookingGuide() {
        if (Microbot.getVarbitPlayerValue(281) == 120) {
            hideRoofs();
            sleep(600);
            Rs2Keyboard.keyPress(KeyEvent.VK_ESCAPE);
            Rs2GameObject.interact(ObjectID.GATE_9470, "Open");
            sleepUntil(() -> Microbot.getVarbitPlayerValue(281) != 120);
        } else if (Microbot.getVarbitPlayerValue(281) == 130) {
            Rs2GameObject.interact(ObjectID.DOOR_9709, "Open");
            sleepUntil(() ->  Microbot.getVarbitPlayerValue(281) != 130);
        } else if (Microbot.getVarbitPlayerValue(281) == 140) {
            if (isInDialogue()) return;
            NPC npc = Rs2Npc.getNpc(3305);
            Rs2Npc.interact(npc, "Talk-to");
        } else if (Microbot.getVarbitPlayerValue(281) >= 150 && Microbot.getVarbitPlayerValue(281) < 200) {
            if (!Rs2Inventory.contains("Bread dough") && !Rs2Inventory.contains("Bread")) {
                Rs2Inventory.combine("Bucket of water", "Pot of flour");
                sleepUntil(() -> Rs2Inventory.contains("Dough"), 2000);
            } else if (Rs2Inventory.contains("Bread dough")) {
                Rs2Inventory.interact("Bread dough");
                Rs2GameObject.interact(9736, "Use");
                sleepUntil(() -> Rs2Inventory.contains("Bread"));
            } else if (Rs2Inventory.contains("Bread")) {
                if (Rs2GameObject.interact(9710, "Open")) {
                    sleep(2000);
                    Rs2Tab.switchToMusicTab();
                }
            }
        }
    }

    public void LightFire() {
        if (Rs2GameObject.findObjectById(26185) == null && Rs2GameObject.findGameObjectByLocation(Microbot.getClient().getLocalPlayer().getWorldLocation()) == null) {
            Rs2Inventory.combine("Logs", "Tinderbox");
            sleepUntil(() -> !Rs2Inventory.hasItem("Logs"));
        } else {
            Rs2Npc.interact(8503);
        }
    }

    public void CutTree() {
        Rs2GameObject.interact("Tree", "Chop down");
        sleepUntil(() -> Rs2Inventory.hasItem("Logs") && !Rs2Player.isAnimating());
    }

    private void SurvivalExpert() {
        if (InteractWithNpc(8503)) {
            sleepUntil(() -> !isInDialogue());
        }
    }

    public void ClickContinue() {
        Rs2Dialogue.clickContinue();
    }


    public boolean InteractWithNpc(int id) {
        NPC npc = Rs2Npc.getNpc(id);
        if (npc == null) return false;
        Rs2Walker.walkTo(npc.getWorldLocation());
        if (!Rs2Npc.hasLineOfSight(npc)) {
            return false;
        }
        return Rs2Npc.interact(npc, "Talk-to");
    }
}
