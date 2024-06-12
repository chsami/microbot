package net.runelite.client.plugins.microbot.util.misc;

import java.util.Arrays;
import java.util.List;

public class Rs2Potion {
    public static List<String> getPrayerPotionsVariants() {
        return Arrays.asList("prayer potion", "super restore", "moonlight moth mix");
    }

    public static List<String> getRangePotionsVariants() {
        return Arrays.asList("ranging potion", "bastion potion");
    }
}
