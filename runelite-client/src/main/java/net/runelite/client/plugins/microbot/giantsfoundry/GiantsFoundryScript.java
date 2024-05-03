package net.runelite.client.plugins.microbot.giantsfoundry;

import net.runelite.api.EquipmentInventorySlot;
import net.runelite.api.GameObject;
import net.runelite.api.ObjectComposition;
import net.runelite.api.widgets.Widget;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.giantsfoundry.enums.CommissionType;
import net.runelite.client.plugins.microbot.giantsfoundry.enums.Heat;
import net.runelite.client.plugins.microbot.giantsfoundry.enums.Stage;
import net.runelite.client.plugins.microbot.giantsfoundry.enums.State;
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank;
import net.runelite.client.plugins.microbot.util.equipment.Rs2Equipment;
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Item;
import net.runelite.client.plugins.microbot.util.keyboard.Rs2Keyboard;
import net.runelite.client.plugins.microbot.util.npc.Rs2Npc;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;
import net.runelite.client.plugins.microbot.util.widget.Rs2Widget;

import java.awt.event.KeyEvent;
import java.util.concurrent.TimeUnit;

import static net.runelite.client.plugins.microbot.giantsfoundry.GiantsFoundryState.*;
import static net.runelite.client.plugins.microbot.giantsfoundry.GiantsFoundryState.getHeatAmount;
import static net.runelite.client.plugins.microbot.util.equipment.Rs2Equipment.getEquippedItem;

public class GiantsFoundryScript extends Script {

    static final int CRUCIBLE = 44776;
    static final int MOULD_JIG = 44777;
    static final int LAVA_POOL = 44631;
    static final int WATERFALL = 44632;

    public static State state;
    static GiantsFoundryConfig config;
    public boolean run(GiantsFoundryConfig config) {
        this.config = config;
        state = State.CRAFTING_WEAPON;
        doAction = true;
        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            if (!super.run()) return;
            if (!Microbot.isLoggedIn()) return;
            try {
                final Rs2Item weapon = getEquippedItem(EquipmentInventorySlot.WEAPON);
                final Rs2Item shield = getEquippedItem(EquipmentInventorySlot.SHIELD);
                if ((weapon != null || shield != null) && !weapon.name.equalsIgnoreCase("preform")) {
                    Microbot.showMessage(("Please start the script without any weapon or shield in your equipment slot."));
                    sleep(5000);
                    return;
                }
                if (!Rs2Equipment.isWearing("ice gloves")) {
                    Microbot.showMessage(("Please start by wearing ice gloves."));
                    sleep(5000);
                    return;
                }
                if (GiantsFoundryState.getProgressAmount() == 1000) {
                    handIn();
                    sleep(600, 1200);
                    Rs2Keyboard.keyPress(KeyEvent.VK_SPACE);
                } else {
                    if (weapon != null) {
                        handleGameLoop();
                    } else {
                        getCommission();
                        selectMould();
                        fillCrucible();
                        pickupMould();
                    }
                }
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }, 0, 100, TimeUnit.MILLISECONDS);
        return true;
    }

    @Override
    public void shutdown() {
        GiantsFoundryState.reset();
        super.shutdown();
    }

    public boolean hasCommission() {
        CommissionType type1 = CommissionType.forVarbit(Microbot.getVarbitValue(MouldHelper.SWORD_TYPE_1_VARBIT));
        CommissionType type2 = CommissionType.forVarbit(Microbot.getVarbitValue(MouldHelper.SWORD_TYPE_2_VARBIT));
        return type1 != CommissionType.NONE && type2 != CommissionType.NONE;
    }

    public void getCommission() {
        if (!hasCommission()) {
            GiantsFoundryState.reset();
            if (Rs2Npc.interact("kovac", "Commission"))
                sleepUntil(this::hasCommission, 5000);
        }
    }

    private boolean hasSelectedMould() {
        return (Microbot.getVarbitValue(GiantsFoundryState.VARBIT_BLADE_SELECTED) > 0
                && Microbot.getVarbitValue(GiantsFoundryState.VARBIT_TIP_SELECTED) > 0
                && Microbot.getVarbitValue(GiantsFoundryState.VARBIT_FORTE_SELECTED) > 0);
    }

    public void selectMould() {
        if (hasSelectedMould())
            return;

        Rs2GameObject.interact(MOULD_JIG);

        sleepUntil(() -> Rs2Widget.findWidget("Forte", null) != null, 5000);

        Widget forte = Rs2Widget.findWidget("Forte", null);
        if (forte != null) {
            Microbot.getMouse().click(forte.getBounds());
            sleep(600, 1200);
            MouldHelper.selectBest();
        }

        Widget blades = Rs2Widget.findWidget("Blades", null);
        if (blades != null) {
            Microbot.getMouse().click(blades.getBounds());
            sleep(600, 1200);
            MouldHelper.selectBest();
        }
        Widget tips = Rs2Widget.findWidget("Tips", null);
        if (tips != null) {
            Microbot.getMouse().click(tips.getBounds());
            sleep(600, 1200);
            MouldHelper.selectBest();
            Microbot.getMouse().click(forte.getBounds());
        }
        Widget setMould = Rs2Widget.getWidget(47054854);
        if (setMould != null) {
            Microbot.getMouse().click(setMould.getBounds());
        }
    }

    public boolean canPour() {
        ObjectComposition objectComposition = Rs2GameObject.findObjectComposition(CRUCIBLE);
        if (objectComposition == null) return false;
        return objectComposition.getImpostor().getName().toLowerCase().contains("(full)");
    }

    public void fillCrucible() {
        if (!hasSelectedMould())
            return;

        if (Microbot.getVarbitValue(MouldHelper.SWORD_TYPE_1_VARBIT) == 0) {
            return;
        }
        if (Microbot.getVarbitValue(GiantsFoundryState.VARBIT_GAME_STAGE) != 1) {
            return;
        }

        if (!Rs2Inventory.hasItemAmount(config.FirstBar().getName(), 14)
                && !Rs2Inventory.hasItemAmount(config.FirstBar().getName(), 14) && !canPour()) {
            Rs2Bank.useBank();
            //check if inv is empty and deposit all inv items
            Rs2Bank.withdrawX(true, config.FirstBar().getName(), 14);
            Rs2Bank.withdrawX(true, config.SecondBar().getName(), 14);
            Rs2Bank.closeBank();
            return;
        }
        Rs2Bank.closeBank();
        if (Rs2Inventory.hasItem(config.FirstBar().getName()) && !canPour()) {
            Rs2GameObject.interact(CRUCIBLE, "Fill");
            sleepUntil(() -> Rs2Widget.findWidget("What metal would you like to add?", null) != null, 5000);
            Rs2Keyboard.keyPress('3');
            sleepUntil(() -> !Rs2Inventory.hasItem(config.FirstBar().getName()), 5000);
        }
        if (Rs2Inventory.hasItem(config.SecondBar().getName()) && !canPour()) {
            Rs2GameObject.interact(CRUCIBLE, "Fill");
            sleepUntil(() -> Rs2Widget.findWidget("What metal would you like to add?", null) != null, 5000);
            sleep(600, 1200);
            Rs2Keyboard.keyPress('4');
            sleepUntil(() -> !Rs2Inventory.hasItem(config.SecondBar().getName()), 5000);
        }
        if (canPour()) {
            Rs2GameObject.interact(CRUCIBLE, "Pour");
            sleep(5000);
            sleepUntil(() -> !canPour(), 10000);
        }
    }

    public boolean canPickupMould() {
        if (canPour()) return false;
        ObjectComposition objectComposition = Rs2GameObject.findObjectComposition(MOULD_JIG);
        if (objectComposition == null) return false;
        return objectComposition.getImpostor().getName().toLowerCase().contains("poured metal");
    }

    public void pickupMould() {
        if (!canPickupMould()) return;
        if (Rs2Inventory.isEmpty() && GiantsFoundryState.getCurrentStage() == null) {
            Rs2GameObject.interact(MOULD_JIG, "Pick-up");
            sleepUntil(() -> !canPickupMould(), 5000);
        }
    }

    boolean doAction = false;

    public void setState(State state) {
        if (this.state == state) return;
        setState(state, true);
    }

    public void setState(State state, boolean doAction) {
        this.state = state;
        this.doAction = doAction;
    }

    public void handleGameLoop() {

        calculateGameState();

        if (!doAction && !BonusWidget.isActive()) return;

        doAction = false;

        switch (state) {
            case HEATING:
                Rs2GameObject.interact(LAVA_POOL, "Heat-preform");
                Rs2Player.waitForAnimation();
                break;
            case COOLING_DOWN:
                Rs2GameObject.interact(WATERFALL, "Cool-preform");
                Rs2Player.waitForAnimation();
                break;
            case CRAFTING_WEAPON:
                craftWeapon();
                break;
        }

    }

    private void calculateGameState() {
        int actionsLeft = GiantsFoundryState.getActionsForHeatLevel();
        Heat currentHeat = GiantsFoundryState.getCurrentHeat();
        Heat requiredHeat = GiantsFoundryState.getCurrentStage().getHeat();

        if (currentHeat == requiredHeat) {
            if (actionsLeft > 8 && state != State.CRAFTING_WEAPON) {
                setState(State.CRAFTING_WEAPON);
                return;
            } else if (state == State.CRAFTING_WEAPON && actionsLeft > 3) {
                return;
            }
        }

        switch (currentHeat) {
            case LOW:
                if (requiredHeat != Heat.LOW) {
                    setState(State.HEATING);
                }
                break;
            case MED:
                if (requiredHeat == Heat.HIGH) {
                    setState(State.HEATING);
                } else {
                    setState(State.COOLING_DOWN);
                }
                break;
            case HIGH:
                if (requiredHeat != Heat.HIGH) {
                    setState(State.COOLING_DOWN);
                }
                break;
            case NONE:
                int currentHeatAmount = getHeatAmount();
                int[] low = getLowHeatRange();
                int[] med = getMedHeatRange();
                int[] high = getHighHeatRange();
                if ((currentHeatAmount <= low[1]) ||
                        ((currentHeatAmount >= low[1] && currentHeatAmount <= med[0]) && (requiredHeat == Heat.HIGH || requiredHeat == Heat.MED || requiredHeat == Heat.NONE)) ||
                        ((currentHeatAmount >= med[1] && currentHeatAmount <= high[0]) && requiredHeat == Heat.HIGH || requiredHeat == Heat.NONE)) {
                    setState(State.HEATING);
                } else {
                    setState(State.COOLING_DOWN);
                }
                break;
        }
    }

    public void craftWeapon() {
        Stage stage = GiantsFoundryState.getCurrentStage();
        if (stage == null) return;
        GameObject obj = GiantsFoundryState.getStageObject(stage);
        if (obj == null) return;
        Rs2GameObject.interact(obj);
        Rs2Player.waitForAnimation();
    }

    private void handIn() {
        Rs2Npc.interact("kovac", "Hand-in");
    }

}
