package net.runelite.client.plugins.hoseaplugins.OneTickSwitcher;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;
import net.runelite.client.config.Keybind;
import net.runelite.client.config.Range;

import java.awt.*;

@ConfigGroup("OneTickSwitcher")
public interface PvpHelperConfig extends Config {

    @ConfigItem(keyName = "copyGear", name = "Copy Gear", description = "", position = -5)
    default boolean copyGear() {
        return false;
    }

    @ConfigItem(keyName = "specToggle", name = "Spec", description = "Toggle to click spec", position = -4)
    default Keybind specToggle() {
        return Keybind.NOT_SET;
    }

    @ConfigSection(name = "Food", description = "Food options", position = -2)
    String foodSection = "Food";

    @ConfigItem(keyName = "tripleEatToggle", name = "Triple Eat", description = "Toggle to triple eat", position = -1, section = foodSection)
    default Keybind tripleEatToggle() {
        return Keybind.NOT_SET;
    }

    @ConfigItem(keyName = "foodName", name = "Food name", description = "Hard food name to eat in triple eat", position = 0, section = foodSection)
    default String foodName() {
        return "Blighted anglerfish";
    }

    @ConfigItem(keyName = "blightedKaram", name = "Blighted Karambwans", description = "If you're using blighted karams over normal", position = 1, section = foodSection)
    default boolean blightedKaram() {
        return false;
    }

    @ConfigItem(keyName = "autoEat", name = "Auto Triple Eat", description = "Auto triple eats when below the threshold", position = 2, section = foodSection)
    default boolean autoEat() {
        return false;
    }

    @Range(min = 1, max = 99)
    @ConfigItem(keyName = "eatThreshold", name = "Eat Threshold", description = "Threshold of HP you should auto eat at", position = 3, section = foodSection)
    default int eatThreshold() {
        return 50;
    }


    @ConfigSection(name = "Gear", description = "Gear to switch", position = 0)
    String gearSection = "Gear";

    @ConfigItem(keyName = "oneToggle", name = "One Toggle", description = "", position = 1, section = gearSection)
    default Keybind oneToggle() {
        return Keybind.NOT_SET;
    }

    @ConfigItem(keyName = "oneGear", name = "One Gear", description = "", position = 2, section = gearSection)
    default String oneGear() {
        return "";
    }

    @ConfigItem(keyName = "twoToggle", name = "Two Toggle", description = "", position = 3, section = gearSection)
    default Keybind twoToggle() {
        return Keybind.NOT_SET;
    }

    @ConfigItem(keyName = "twoGear", name = "Two Gear", description = "", position = 4, section = gearSection)
    default String twoGear() {
        return "";
    }

    @ConfigItem(keyName = "threeToggle", name = "Three Toggle", description = "", position = 5, section = gearSection)
    default Keybind threeToggle() {
        return Keybind.NOT_SET;
    }

    @ConfigItem(keyName = "threeGear", name = "Three Gear", description = "", position = 6, section = gearSection)
    default String threeGear() {
        return "";
    }

    @ConfigItem(keyName = "fourToggle", name = "Four Toggle", description = "", position = 7, section = gearSection)
    default Keybind fourToggle() {
        return Keybind.NOT_SET;
    }

    @ConfigItem(keyName = "fourGear", name = "Four Gear", description = "", position = 8, section = gearSection)
    default String fourGear() {
        return "";
    }

    @ConfigItem(keyName = "fiveToggle", name = "Five Toggle", description = "", position = 9, section = gearSection)
    default Keybind fiveToggle() {
        return Keybind.NOT_SET;
    }

    @ConfigItem(keyName = "fiveGear", name = "Five Gear", description = "", position = 10, section = gearSection)
    default String fiveGear() {
        return "";
    }

    @ConfigItem(keyName = "sixToggle", name = "Six Toggle", description = "", position = 11, section = gearSection)
    default Keybind sixToggle() {
        return Keybind.NOT_SET;
    }

    @ConfigItem(keyName = "sixGear", name = "Six Gear", description = "", position = 12, section = gearSection)
    default String sixGear() {
        return "";
    }

    @ConfigSection(
            name = "Prayers",
            description = "Prayers to switch",
            position = 2
    )
    String prayerSection = "Prayers";

    @ConfigItem(keyName = "eightToggle", name = "Prayers One Toggle", description = "", position = 16, section = prayerSection)
    default Keybind onePrayerToggle() {
        return Keybind.NOT_SET;
    }

    @ConfigItem(keyName = "onePrayerFirst", name = "Prayers One", description = "", position = 17, section = prayerSection)
    default String onePrayer() {
        return "";
    }

    @ConfigItem(keyName = "nineToggle", name = "Prayers Two Toggle", description = "", position = 18, section = prayerSection)
    default Keybind twoPrayerToggle() {
        return Keybind.NOT_SET;
    }

    @ConfigItem(keyName = "twoPrayer", name = "Prayers Two", description = "", position = 19, section = prayerSection)
    default String twoPrayer() {
        return "";
    }

    @ConfigItem(keyName = "threePrayerToggle", name = "Prayers Three Toggle", description = "", position = 20, section = prayerSection)
    default Keybind threePrayerToggle() {
        return Keybind.NOT_SET;
    }

    @ConfigItem(keyName = "threePrayer", name = "Prayers Three", description = "", position = 21, section = prayerSection)
    default String threePrayer() {
        return "";
    }

    @ConfigSection(
            name = "Spells",
            description = "Spells to cast",
            position = 3
    )
    String spellSection = "Spells";

    @ConfigItem(keyName = "spellOneToggle", name = "Spell One Toggle", description = "", position = 0, section = spellSection)
    default Keybind spellOneToggle() {
        return Keybind.NOT_SET;
    }

    @ConfigItem(keyName = "spellOne", name = "Spell One", description = "", position = 1, section = spellSection)
    default String spellOne() {
        return "Ice barrage";
    }

    @ConfigItem(keyName = "spellTwoToggle", name = "Spell Two Toggle", description = "", position = 2, section = spellSection)
    default Keybind spellTwoToggle() {
        return Keybind.NOT_SET;
    }

    @ConfigItem(keyName = "spellTwo", name = "Spell Two", description = "", position = 3, section = spellSection)
    default String spellTwo() {
        return "Tele block";
    }

    @ConfigItem(keyName = "spellThreeToggle", name = "Spell Three Toggle", description = "", position = 4, section = spellSection)
    default Keybind spellThreeToggle() {
        return Keybind.NOT_SET;
    }

    @ConfigItem(keyName = "spellThree", name = "Spell Three", description = "", position = 5, section = spellSection)
    default String spellThree() {
        return "Tele block";
    }

    @ConfigItem(keyName = "veng", name = "Vengeance", description = "", position = 6, section = spellSection)
    default Keybind veng() {
        return Keybind.NOT_SET;
    }

    @ConfigSection(
            name = "Overlay",
            description = "Overlay settings",
            position = 4
    )
    String overlaySection = "Overlay";

    @ConfigItem(keyName = "targetOverlay", name = "Highlight target", description = "Highlight focused target", position = -1, section = overlaySection)
    default boolean targetOverlay() {
        return true;
    }

    @ConfigItem(keyName = "autoFocusTarget", name = "Auto Focus target", description = "Automatically focuses target player", position = 0, section = overlaySection)
    default boolean autoFocusTarget() {
        return false;
    }

    @Range(min = 1, max = 8)
    @ConfigItem(keyName = "overlayWidth", name = "Highlight width", description = "Width of target highlight", position = 1, section = overlaySection)
    default int overlayWidth() {
        return 2;
    }

    @ConfigItem(keyName = "overlayColor", name = "Highlight color", description = "Color of target highlight", position = 2, section = overlaySection)
    default Color overlayColor() {
        return Color.RED;
    }

    @ConfigItem(keyName = "showOverlay", name = "Show target panel", description = "Enables an on screen panel showing target's expected value", position = 3, section = overlaySection)
    default boolean showOverlay() {
        return false;
    }
}