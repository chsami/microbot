package net.runelite.client.plugins.microbot.example;

import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;

import java.util.concurrent.TimeUnit;


public class ExampleScript extends Script {

    public static boolean test = false;

    public static boolean isDropping = false;
    public static boolean crafting = false;

    public boolean run(ExampleConfig config) {
        Microbot.enableAutoRunOn = false;
        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            try {
                if (!Microbot.isLoggedIn()) return;
                if (!super.run()) return;
                long startTime = System.currentTimeMillis();

                //CODE HERE


                //volcanic ash

                if (Rs2Player.isAnimating()) return;

                if (Rs2GameObject.interact(11365, "mine", 1)) {
                    sleepUntil(() -> Rs2Player.isAnimating());
                }

                if (Rs2Inventory.isFull()) {
                    Rs2Inventory.dropAllExcept("scroll box", "coins", "pickaxe", "clue", "spade");
                }


              //  Rs2Inventory.useItemOnObject(995, 13197);

/*                if (Rs2GameObject.interact(15345, "build")) {
                    sleepUntil(() -> Rs2Widget.findWidget("Furniture") != null, 2000);
                    Rs2Keyboard.typeString("3");
                    sleepUntil(() -> Rs2GameObject.findObjectById(13397) != null, 2000);
                } else {
                    Rs2GameObject.interact(13397, "remove");
                    sleepUntil(() -> Rs2Widget.findWidget("Yes") != null, 2000);
                    Rs2Keyboard.typeString("1");
                    sleepUntil(() -> Rs2GameObject.findObjectById(15345) != null, 2000);
                }*/

              //  Rs2Shop.buyItem("air rune pack", "50");
               // Rs2Shop.buyItem("fire rune pack", "50");
              //  Rs2Shop.buyItem("water rune pack", "50");
                //Rs2Shop.buyItem("uncut ruby", "50");




          /*      if (Rs2Player.isAnimating(4000)) return;

                if (isDropping && Rs2Inventory.count() != 4) {
                    Rs2Inventory.dropAllExcept("chisel", "chaos rune", "coins", "clue compass");
                    return;
                } else {
                    isDropping = false;
                }

                if (!Rs2Inventory.hasItem("uncut") && crafting) {
                    isDropping = true;
                    return;
                }

                if (Rs2Inventory.isFull() && Rs2Inventory.hasItem("uncut")) {
                    Rs2Inventory.interact("chisel", "use");
                    sleepGaussian(600, 150);
                    Rs2Inventory.interact("uncut", "use");
                    sleep(1000);
                    Rs2Keyboard.keyPress(KeyEvent.VK_SPACE);
                    sleepUntil(() -> Microbot.isGainingExp);
                } else if (Rs2Inventory.isFull()){
                    isDropping = true;
                } else {
                    Rs2GameObject.interact(51935, "steal-from");
                }*/


                long endTime = System.currentTimeMillis();
                long totalTime = endTime - startTime;
                System.out.println("Total time for loop " + totalTime);

            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }, 0, 100, TimeUnit.MILLISECONDS);
        return true;
    }

    @Override
    public void shutdown() {
        super.shutdown();
    }
}