package net.runelite.client.plugins.microbot.tutorialisland;


public class TutorialIslandScript {
    public boolean run(TutorialIslandConfig config) {
        return true;
    }

     public static void shutdown() {

     }
/*

    public static double version = 1.0;
    LocalPoint position1 = null;

    Status status = Status.NAME;

    public boolean run(TutorialIslandConfig config) {
        Microbot.enableAutoRunOn = false;
        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            if (!super.run()) return;
            try {
                CalculateStatus();

                if (isInDialogue()) {
                    ClickContinue(231);
                    ClickContinue(217);
                    ClickContinue(229);
                    ClickContinue(193);
                    sleep(1000);
                }
                System.out.println(new NameGenerator(random(3, 6)).getName() + new NameGenerator(random(3, 6)).getName());
                System.out.println(Microbot.getVarbitValue(281));
                System.out.println(status);

                switch (status) {
                    case NAME:
                        String name = new NameGenerator(random(3, 6)).getName() + new NameGenerator(random(3, 6)).getName();
                        Rs2Widget.clickWidget("Enter name");
                        VirtualKeyboard.typeString(name);
                        Rs2Widget.clickWidget("Look up name");
                        sleepUntil(() -> Rs2Widget.getWidget(LOOKUPNAME).getText().contains("Set name"));
                        if (!widgets.containingText(LOOKUPNAME, new String[]{"Sorry, the display name <col=ffffff>zezima</col> is <col=ff0000>not available</col>.<br>Try clicking one of our suggestions, instead:"}).isEmpty()) {
                            widgets.interact(LOOKUPNAME, 9, "Enter name");
                            for (int i = 0; i < name.length(); i++) {
                                keyboard.pressKey(8);
                            }
                        }
                        widgets.interact(LOOKUPNAME, 19, "Set name");
                        new ConditionalSleep(5000) {
                            @Override
                            public boolean condition() throws InterruptedException {
                                return !widgets.isVisible(LOOKUPNAME);
                            }
                        }.sleep();
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
                        stop();
                        break;
                }
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }, 0, 1000, TimeUnit.MILLISECONDS);
        return true;
    }

    @Override
    public void shutdown() {
        super.shutdown();
        Rs2Npc.npcInteraction = null;
        Rs2GroundItem.itemInteraction = null;
        Rs2GameObject.objectToInteract = null;
        Rs2Equipment.widgetId = 0;
        Rs2Bank.widgetId = 0;
        reachedEndLine = false;
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

    public void CalculateStatus() {
        if (widgets.isVisible(LOOKUPNAME)) {
            status = Status.NAME;
        } else if (widgets.isVisible(CharacterCreation)) {
            status = Status.CHARACTER;
        } else if (Microbot.getVarbitValue(281) < 10) {
            status = Status.GETTING_STARTED;
        } else if (Microbot.getVarbitValue(281) >= 10 && Microbot.getVarbitValue(281) < 120) {
            status = Status.SURVIVAL_GUIDE;
        } else if (Microbot.getVarbitValue(281) >= 120 && Microbot.getVarbitValue(281) < 200) {
            status = Status.COOKING_GUIDE;
        } else if (Microbot.getVarbitValue(281) >= 200 && Microbot.getVarbitValue(281) <= 250) {
            status = Status.QUEST_GUIDE;
        } else if (Microbot.getVarbitValue(281) >= 260 && Microbot.getVarbitValue(281) <= 360) {
            status = Status.MINING_GUIDE;
        } else if (Microbot.getVarbitValue(281) > 360 && Microbot.getVarbitValue(281) < 510) {
            status = Status.COMBAT_GUIDE;
        } else if (Microbot.getVarbitValue(281) >= 510 && Microbot.getVarbitValue(281) < 540) {
            status = Status.BANKER_GUIDE;
        } else if (Microbot.getVarbitValue(281) >= 540 && Microbot.getVarbitValue(281) < 610) {
            status = Status.PRAYER_GUIDE;
        } else if (Microbot.getVarbitValue(281) >= 610 && Microbot.getVarbitValue(281) < 1000) {
            status = Status.MAGE_GUIDE;
        } else if (Microbot.getVarbitValue(281) == 1000) {
            status = Status.FINISHED;
        }
    }

    public void RandomizeCharacter() {
        if (random(1, 10) == 2) {
            widgets.interact(CharacterCreation, CharacterCreation_Confirm, "Confirm");
            new ConditionalSleep(2000) {
                @Override
                public boolean condition() throws InterruptedException {
                    return !widgets.isVisible(CharacterCreation);
                }
            }.sleep();
        }

        int randomIndex = (int) Math.floor(Math.random() * CharacterCreation_Arrows.length);
        int item = CharacterCreation_Arrows[randomIndex];
        widgets.interact(CharacterCreation, item, "Select");
    }

    private boolean isInDialogue() {
        return !widgets.containingText(231, new String[]{"Click here to continue"}).isEmpty() ||
                !widgets.containingText(229, new String[]{"Click here to continue"}).isEmpty() ||
                !widgets.containingText(193, new String[]{"Click here to continue"}).isEmpty() ||
                !widgets.containingText(217, new String[]{"Click here to continue"}).isEmpty();
    }

    //config 281 needed
    public void GettingStarted() {
        NPC npc = Rs2Npc.getNpc(3308);
        if (!widgets.containingText(231, new String[]{"Click here to continue"}).isEmpty()) return;
        if (!widgets.containingText(229, new String[]{"Click here to continue"}).isEmpty()) return;
        if (widgets.isVisible(219)) {
            getDialogues().selectOption(random(2));
            return;
        }
        if (Microbot.getVarbitValue(281) == 3) {
            Tab.switchToSettingsTab();
            return;
        }

        if (Rs2Npc.interact(npc, "Talk-to")) {
            sleepUntil(() -> Microbot.getClient().getLocalPlayer().getInteracting() == npc);
            sleep(1000);
        }
    }

    public void SurvivalGuide() {
        if (Inventory.contains("Shrimps")) return;
        if (Microbot.getVarbitValue(281) == 10) {
            DoorToSurvivalGuide();
        } else if (Microbot.getVarbitValue(281) == 20) { // SURVIVAL EXPERT
            SurvivalExpert();
        } else if (Microbot.getVarbitValue(281) == 30
                || Microbot.getVarbitValue(281) == 40
                || Microbot.getVarbitValue(281) == 50
                || Microbot.getVarbitValue(281) == 60
                || Microbot.getVarbitValue(281) == 70
                || Microbot.getVarbitValue(281) == 80
                || Microbot.getVarbitValue(281) == 90) { // FISHING + woodcutting + cooking
            if (!Inventory.contains("Raw shrimps")) {
                ClickContinue(193);
                Tab.switchToInventoryTab();
                sleepUntil(() -> Inventory.contains("Raw shrimps"));
                sleep(2000);
                Tab.switchToSkillsTab();
            } else {
                if (Microbot.getVarbitValue(281) < 90) {
                    if (!Inventory.contains("Bronze axe")) {
                        Tab.switchToSkillsTab();
                        if (widgets.containingText(217, new String[]{"Click here to continue"}).isEmpty()) {
                            InteractWithNpc(8503);
                        } else {
                            ClickContinue(217);
                            ClickContinue(193);
                        }
                    } else if (!Inventory.contains("Logs")) {
                        CutTree();
                    } else if (Inventory.contains("Logs")) {
                        LightFire();
                    }
                } else if (Microbot.getVarbitValue(281) == 90 && Inventory.contains("Raw shrimps")) {
                    if (!Inventory.contains("Logs"))
                        CutTree();
                    if (!Rs2GameObject.exists(26185))
                        LightFire();
                    Inventory.interact("Use", "Raw shrimps");
                    Rs2GameObject.interact(26185, "Use");
                    sleepUntil(() -> Inventory.contains("Shrimps"));
                }
            }
        }

    }

    public void MageGuide() {
        if (isInDialogue()) return;
        if (Microbot.getVarbitValue(281) == 610) {
            Microbot.getWalker().walkTo(new WorldPoint(3135, 3088, 0));
        } else if (Microbot.getVarbitValue(281) == 620) {
            Rs2Npc.interact(3309, "Talk-to");
        } else if (Microbot.getVarbitValue(281) == 630) {
            Tab.switchToMagicTab();
        } else if (Microbot.getVarbitValue(281) == 640) {
            Rs2Npc.interact(3309, "Talk-to");
        } else if (Microbot.getVarbitValue(281) == 650) {
            Rs2Magic.cast(MagicAction.WIND_STRIKE);
            Rs2Npc.interact("Chicken", "");
        } else if (Microbot.getVarbitValue(281) == 670) {
            if (widgets.isVisible(788)) {
                if (widgets.isVisible(788, 15, 2)) {
                    widgets.interact(788, 15, 2, "Confirm Old School Main");
                } else {
                    widgets.interact(788, 40, 2, "Select");
                }
                return;
            }
            if (widgets.isVisible(219)) {
                widgets.interact(219, 1, 1, "Continue");
                return;
            }
            Rs2Npc.interact(3309, "Talk-to");
        }
    }

    public void PrayerGuide() {
        if (isInDialogue()) return;
        if (Microbot.getVarbitValue(281) == 640 || Microbot.getVarbitValue(281) == 550 || Microbot.getVarbitValue(281) == 540) {
            Microbot.getWalker().walkTo(new WorldPoint(3124, 3106, 0));
            Rs2Npc.interact(3319, "Talk-to");
        } else if (Microbot.getVarbitValue(281) == 560) {
            Tab.switchToPrayerTab();
        } else if (Microbot.getVarbitValue(281) == 570) {
            Rs2Npc.interact(3319, "Talk-to");
        } else if (Microbot.getVarbitValue(281) == 580) {
            Tab.switchToFriendsTab();
        } else if (Microbot.getVarbitValue(281) == 600) {
            Rs2Npc.interact(3319, "Talk-to");
        }
    }

    public void BankerGuide() {
        if (isInDialogue()) return;
        if (Microbot.getVarbitValue(281) == 510) {
            if (!settings.areRoofsEnabled()) {
                Tab.switchToSettingsTab();
                widgets.interact(116, 75, 9, "All settings");
                new ConditionalSleep(5000) {
                    @Override
                    public boolean condition() throws InterruptedException {
                        return widgets.isVisible(134);
                    }
                }.sleep();
                widgets.interact(134, 18, 53, "Toggle");
            }
            Rs2GameObject.interact("Bank booth", "Use");

            sleepUntil(() -> Microbot.getVarbitValue(281) != 510);
        } else if (Microbot.getVarbitValue(281) == 520) {
            bank.close();
            Rs2GameObject.interact("Poll booth", "Use");
            new ConditionalSleep(5000) {
                @Override
                public boolean condition() throws InterruptedException {
                    return Microbot.getVarbitValue(281) != 520;
                }
            }.sleep();
        } else if (Microbot.getVarbitValue(281) == 525) {
            Microbot.getWalker().walkTo(new WorldPoint(3127, 3123, 0));
            npcs.closest(3310).interact("Talk-to");
        } else if (Microbot.getVarbitValue(281) == 531) {
            tabs.open(Tab.ACCOUNT_MANAGEMENT);
        } else if (Microbot.getVarbitValue(281) == 532) {
            npcs.closest(3310).interact("Talk-to");
        }
    }

    public void CombatGuide() {
        if (Microbot.getVarbitValue(281) >= 370) {
            if (isInDialogue()) return;
            if (Microbot.getVarbitValue(281) == 500) {
                Rs2G.interact("Ladder", "Climb-up");
                new ConditionalSleep(5000) {
                    @Override
                    public boolean condition() throws InterruptedException {
                        return Microbot.getVarbitValue(281) != 500;
                    }
                }.sleep();
                return;
            }
            if (Microbot.getVarbitValue(281) == 480 || Microbot.getVarbitValue(281) == 490) { // killl rat with range
                if (myPlayer().isInteracting(npc)) return;
                Inventory.interact("Wield", "Shortbow");
                Inventory.interact("Wield", "Bronze arrow");
                NPC npc = Rs2Npc.getNpc("Giant rat");
                npc.interact("Attack");
                return;
            }
            if (Microbot.getVarbitValue(281) == 470) {
                Microbot.getWalker().walkTo(new WorldPoint(3109, 9511, 0));
                NPC npc = Rs2Npc.getNpc("Combat Instructor");
                if (npc.interact("Talk-to")) {
                    new ConditionalSleep(1000) {
                        @Override
                        public boolean condition() throws InterruptedException {
                            return false;
                        }
                    }.sleep();
                    new ConditionalSleep(7000) {
                        @Override
                        public boolean condition() throws InterruptedException {
                            return !isInDialogue();
                        }
                    }.sleep();
                }
                return;
            }
            if (Microbot.getVarbitValue(281) >= 420) {
                if (Microbot.getClient().getLocalPlayer().isInteracting() || Rs2Player.isAnimating()) return;
                Tab.switchToCombatOptionsTab();
                Inventory.interact("Wield", "Bronze sword");
                Inventory.interact("Wield", "Wooden shield");
                if (Rs2Equipment.hasEquipped("Bronze sword")) {
                    Microbot.getWalker().walkTo(new WorldPoint(3105, 9517, 0));
                    NPC npc = Rs2Npc.getNpc("Giant rat");
                    Rs2Npc.interact(npc, "Attack");
                    sleepUntil(() -> Microbot.getClient().getLocalPlayer().getInteracting() == npc);
                }
                return;
            }
            if (Microbot.getVarbitValue(281) == 410) {
                NPC npc = Rs2Npc.getNpc("Combat Instructor");
                if (Rs2Npc.interact(npc, "Talk-to")) {
                    sleep(1000);
                    sleepUntil(() -> !isInDialogue());
                }
                return;
            }
            if (Microbot.getVarbitValue(281) == 390 || Microbot.getVarbitValue(281) == 405) {
                if (!widgets.isVisible(84)) {
                    Tab.switchToEquipmentTab();
                    widgets.interact(387, 2, "View equipment stats");
                    new ConditionalSleep(5000) {
                        @Override
                        public boolean condition() throws InterruptedException {
                            return widgets.isVisible(84);
                        }
                    }.sleep();
                    VirtualKeyboard.keyPress(KeyEvent.VK_ESCAPE);
                }
                if (Inventory.contains("Bronze dagger")) {
                    Inventory.interact("Wield", "Bronze dagger");
                    Inventory.interact("Wield", "Bronze dagger");
                }
                return;
            }
            Microbot.getWalker().walkTo(new WorldPoint(random(3106, 3108), random(9508, 9510), 0));
            sleep(500);
            sleepUntil(() -> Rs2Player.isWalking());
            NPC npc = Rs2Npc.getNpc("Combat Instructor");
            if (Rs2Npc.interact(npc, "Talk-to")) {
                sleep(1000);
                sleepUntil(() -> !isInDialogue());
            }
        }
    }

    public void MiningGuide() {
        if (Microbot.getVarbitValue(281) == 260) {
            Microbot.getWalker().walkTo(new WorldPoint(random(3082, 3085), random(9502, 9505), 0));
            if (isInDialogue()) return;
            NPC npc = Rs2Npc.getNpc(3311);
            Rs2Npc.interact(npc, "Talk-to");
            sleepUntil(() -> Microbot.getClient().getLocalPlayer().getInteracting() == npc);
        } else {
            if (Inventory.contains("Bronze dagger")) {
                Rs2GameObject.interact("Gate", "Open");
                sleepUntil(() -> Microbot.getVarbitValue(281) > 360);
                return;
            }
            if (Inventory.contains("Bronze bar") && Inventory.contains("Hammer")) {
                Rs2GameObject.interact("Anvil", "Smith");
                new ConditionalSleep(5000) {
                    @Override
                    public boolean condition() throws InterruptedException {
                        return widgets.isVisible(312);
                    }
                }.sleep();
                widgets.interact(312, 9, "Smith");
                sleepUntil(() -> Inventory.contains("Bronze dagger"));
                return;
            }
            if (Inventory.contains("Bronze bar") && !Inventory.contains("Hammer")) {
                if (isInDialogue()) return;
                NPC npc = Rs2Npc.getNpc(3311);
                Rs2Npc.interact(npc, "Talk-to");
                sleepUntil(() -> Microbot.getClient().getLocalPlayer().getInteracting() == npc);
                return;
            }
            if (Inventory.contains("Bronze pickaxe") && !Inventory.contains("Copper ore") && !Inventory.contains("Tin ore")) {
                if (!Inventory.contains("Copper ore")) {
                    Rs2GameObject.interact(10079, "Mine");
                    sleepUntil(() -> Inventory.contains("Copper ore"));
                }
                if (!Inventory.contains("Tin ore")) {
                    Rs2GameObject.interact(10080, "Mine");
                    sleepUntil(() -> Inventory.contains("Tin ore"));
                }
            } else if (Inventory.contains("Copper ore") && Inventory.contains("Tin ore")) {
                Inventory.interact("Tin ore");
                Rs2GameObject.interact("Furnace");
                sleepUntil(() ->  Inventory.contains("Bronze bar"));
            }
        }
    }

    public void QuestGuide() {
        if (Microbot.getVarbitValue(281) == 200) {
            Microbot.getWalker().walkTo(new WorldPoint(random(3083, 3086), random(3127, 3129), 0));
            Rs2GameObject.interact(9716, "Open");
        } else if (Microbot.getVarbitValue(281) != 250) {
            if (isInDialogue()) return;
            NPC npc = Rs2Npc.getNpc(3312);
            Rs2Npc.interact(npc, "Talk-to");
            sleepUntil(() -> Microbot.getClient().getLocalPlayer().getInteracting() == npc);
            Tab.switchToQuestTab();
        } else {
            Rs2GameObject.interact(9726, "Climb-down");
            sleep(2000);
        }

    }

    public void CookingGuide() {
        if (Microbot.getVarbitValue(281) == 120) {
            Rs2GameObject.interact("Gate", "Open");
            sleepUntil(() -> Microbot.getVarbitValue(281) != 120);
        } else if (Microbot.getVarbitValue(281) == 130) {
            Rs2GameObject.interact("Door", "Open");
            sleepUntil(() ->  Microbot.getVarbitValue(281) != 130);
        } else if (Microbot.getVarbitValue(281) == 140) {
            if (isInDialogue()) return;
            NPC npc = Rs2Npc.getNpc(3305);
            Rs2Npc.interact(npc, "Talk-to");
        } else if (Microbot.getVarbitValue(281) >= 150 && Microbot.getVarbitValue(281) < 200) {
            if (!Inventory.contains("Bread dough") && !Inventory.contains("Bread")) {
                Inventory.interact("Bucket of water");
                Inventory.interact("Pot of flour");
                sleepUntil(() -> Inventory.contains("Dough"));
            } else if (Inventory.contains("Bread dough")) {
                Inventory.interact("Bread dough");
                Rs2GameObject.interact(9736, "Use");
                sleepUntil(() -> Inventory.contains("Bread"));
            } else if (Inventory.contains("Bread")) {
                if (Rs2GameObject.interact(9710, "Open")) {
                    sleep(2000);
                    Tab.switchToMusicTab();
                }
            }
        }
    }

    public void LightFire() {
        if (Rs2GameObject.findObjectById(26185) != null) {
            Inventory.interact("Logs");
            Inventory.interact("Tinderbox");
            sleepUntil(() -> !Inventory.hasItem("Logs"));
        } else if (Rs2GameObject.findObjectByLocation(Microbot.getClient().getLocalPlayer().getWorldLocation()) != null) {
            Microbot.getWalker().walkTo(Rs2Npc.getNpc(8503));
            sleep(1000);
        }
    }

    public void CutTree() {
        Rs2GameObject.interact("Tree", "Chop down");
        sleepUntil(() -> Inventory.hasItem("Logs") && !Rs2Player.isAnimating());
    }

    private void SurvivalExpert() {
        if (InteractWithNpc(8503)) {
            new ConditionalSleep(5000) {
                @Override
                public boolean condition() throws InterruptedException {
                    return !widgets.containingText(231, new String[]{"Click here to continue"}).isEmpty();
                }
            }.sleep();
        } else {
            DoorToSurvivalGuide();
        }
    }

    public void ClickContinue(int rootId) {
        if (!widgets.containingText(rootId, new String[]{"Click here to continue"}).isEmpty()) {
            widgets.containingText(rootId, new String[]{"Click here to continue"}).get(0).interact("Continue");
        }
    }


    public boolean InteractWithNpc(int id) {
        NPC npc = Rs2Npc.getNpc(id);
        if (npc == null) return false;
        if (!Microbot.getWalker().canReach(npc.getWorldLocation())) {
            return false;
        }
        return Rs2Npc.interact(npc, "Talk-to");
    }

    public boolean InteractWithNpc(int id, String interaction) {
        NPC npc = Rs2Npc.getNpc(id);
        if (npc == null) return false;
        if (!Microbot.getWalker().canReach(npc.getWorldLocation())) {
            return false;
        }
        return Rs2Npc.interact(npc, interaction);
    }

    public void DoorToSurvivalGuide() {
        if (Rs2GameObject.interact(9398, "Open")) {
            sleepUntil(() -> Microbot.getClient().getLocalPlayer().getWorldLocation().getX() == 3098);
        }
    }*/
}
