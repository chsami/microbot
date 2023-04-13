package net.runelite.client.plugins.microbot.util.bank;

import net.runelite.api.GameObject;
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject;
import net.runelite.client.plugins.microbot.util.menu.Menu;

public class Rs2Bank {

    public static boolean openBank() {
        try {
            GameObject bank = Rs2GameObject.findBank();
            if (bank == null) return false;
            return Menu.doAction("bank", bank.getCanvasTilePoly());
        } catch(Exception ex) {
            System.out.println(ex.getMessage());
        }
        return false;
    }
}
