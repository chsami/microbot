package net.runelite.client.plugins.microbot.util.menu;

import lombok.Getter;
import lombok.Setter;
import net.runelite.api.MenuEntry;
import net.runelite.api.Point;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.util.Global;
import net.runelite.client.plugins.microbot.util.math.Random;
import net.runelite.client.ui.FontManager;

import java.applet.Applet;
import java.awt.*;
import java.util.regex.Pattern;

import static net.runelite.client.plugins.microbot.util.Global.sleep;
import static net.runelite.client.plugins.microbot.util.math.Random.random;

/**
 * Context menu related operations.
 */
public class Rs2Menu {
    private static final Pattern HTML_TAG = Pattern
            .compile("(^[^<]+>|<[^>]+>|<[^>]+$)");

    protected static final int TOP_OF_MENU_BAR = 18;
    protected static final int MENU_ENTRY_LENGTH = 15;
    protected static final int MENU_SIDE_BORDER = 7;
    protected static final int MAX_DISPLAYABLE_ENTRIES = 40;

    protected static int lastIndex = -1;

    @Getter
    @Setter
    private static String option = "";
    @Getter
    @Setter
    private static String name = "";


    public static boolean hasAction(String... actions) {
        for (String action : actions) {
            if (getIndex(action) != -1)
                return true;
        }
        return false;
    }

    public static boolean hasAction(Rectangle rect, String... actions) {
        sleep(200, 400);
        Microbot.getMouse().move(new Point((int) rect.getCenterX(), (int) rect.getCenterY()));
        for (String action : actions) {
            if (getIndex(action) != -1)
                return true;
        }
        return false;
    }

    public static boolean hasAction(Point point, String... actions) {
        sleep(200, 400);
        Microbot.getMouse().move(point);
        for (String action : actions) {
            if (getIndex(action) != -1)
                return true;
        }
        return false;
    }

    public static boolean hasAction(Polygon poly, String... actions) {
        sleep(200, 400);
        Microbot.getMouse().move(poly);
        for (String action : actions) {
            if (getIndex(action) != -1)
                return true;
        }
        return false;
    }

    /**
     * Clicks the menu target. Will left-click if the menu item is the first,
     * otherwise open menu and click the target.
     *
     * @param action The action (or action substring) to click.
     * @return <code>true</code> if the menu item was clicked; otherwise
     * <code>false</code>.
     */
    public static boolean doAction(String action, Point point) {
        Microbot.getMouse().move(point);
        return doAction(action, point, (String[]) null);
    }

    public static boolean doAction(String action, Shape shape) {
        Microbot.getMouse().move(shape.getBounds().getCenterX(), shape.getBounds().getCenterY());
        return doAction(action, new Point((int) shape.getBounds().getCenterX(), (int) shape.getBounds().getCenterY()), (String[]) null);
    }

    public static boolean doAction(String action, Rectangle bounds) {
        Microbot.getMouse().move(bounds.getCenterX(), bounds.getCenterY());
        return doAction(action, new Point((int) bounds.getCenterX(), (int) bounds.getCenterY()), (String[]) null);
    }
    public static boolean doActionFast(String action, Rectangle bounds) {
        Microbot.getMouse().move(bounds.getCenterX(), bounds.getCenterY());
        return doActionFast(action, new Point((int) bounds.getCenterX(), (int) bounds.getCenterY()), (String[]) null);
    }

    public static boolean doAction(String action, Polygon poly, String... targets) {
        Microbot.getMouse().move(poly.getBounds().getCenterX(), poly.getBounds().getCenterY());
        return doAction(action, new Point((int) poly.getBounds().getCenterX(), (int) poly.getBounds().getCenterY()), targets);
    }

    public static boolean doAction(String[] actions, Point point) {
        Microbot.getMouse().move(point);
        boolean result = false;
        for (String action : actions) {
            if (hasAction(point, action)) {
                result = doAction(action, point, (String[]) null);
                if (result) break;
            }
        }
        return result;
    }
    public static boolean doAction(String[] actions, Rectangle bounds) {
        Microbot.getMouse().move(bounds.getCenterX(), bounds.getCenterY());
        boolean result = false;
        for (String action : actions) {
            if (hasAction(new Point((int)bounds.getCenterX(), (int)bounds.getCenterY()), action)) {
                result = doAction(action, new Point((int)bounds.getCenterX(), (int)bounds.getCenterY()), (String[]) null);
                if (result) break;
            }
        }
        return result;
    }

    /**
     * Clicks the menu target. Will left-click if the menu item is the first,
     * otherwise open menu and click the target.
     *
     * @param action The action (or action substring) to click.
     * @param target The target (or target substring) of the action to click.
     * @return <code>true</code> if the menu item was clicked; otherwise
     * <code>false</code>.
     */
    public static boolean doAction(final String action, Point point, final String... target) {
        setOption(action);
        if (target != null && target.length > 0)
            setName(target[0]);
        Microbot.getMouse().click(point);
        sleep(300,325);
        setOption("");
        setName("");
        return true;
    }
    public static boolean doActionFast(final String action, Point point, final String... target) {
        setOption(action);
        if (target != null && target.length > 0)
            setName(target[0]);
        Microbot.getMouse().click(point);
        //sleep(300,325);
        setOption("");
        setName("");
        return true;
    }

    /**
     * Determines if the item contains the desired action.
     *
     * @param item   The item to check.
     * @param action The item menu action to check.
     * @return <code>true</code> if the item has the action; otherwise
     * <code>false</code>.
     */
    /*public static boolean itemHasAction(final RSItem item, final String action) {
        // Used to determine if an item is droppable/destroyable
        if (item == null) {
            return false;
        }
        ItemDefinition itemDef = item.getDefinition();
        if (itemDef != null) {
            for (String a : itemDef.getInterfaceOptions()) {
                if (a.equalsIgnoreCase(action)) {
                    return true;
                }
            }
        }
        return false;
    }*/

    /**
     * Left clicks at the given index.
     *
     * @param i The index of the item.
     * @return <code>true</code> if the mouse was clicked; otherwise <code>false</code>.
     */
    public static boolean clickIndex(final int i) {
        if (!isOpen()) {
            return false;
        }
        MenuEntry[] entries = getEntries();
        if (entries.length <= i) {
            return false;
        }
        if (!isCollapsed()) {
            return clickMain(i);
        }
        return false;
    }

    private static boolean clickMain(final int i) {
        MenuEntry[] entries = getEntries();
        String item = (entries[i].getOption() + " " + entries[i].getTarget().replaceAll("<.*?>", ""));
        Point menu = getLocation();
        FontMetrics fm = ((Applet) Microbot.getClient()).getGraphics().getFontMetrics(FontManager.getRunescapeBoldFont());
        int xOff = random(1, (fm.stringWidth(item) + MENU_SIDE_BORDER) - 1);
        int yOff = TOP_OF_MENU_BAR + (((MENU_ENTRY_LENGTH * i) + 7));
        sleep(random(100, 200));
        if (isOpen()) {
            Microbot.getMouse().click(new Point(menu.getX() + xOff, menu.getY() + yOff));
            return true;
        }
        return false;
    }

    public static Point getLocation() {
        return new Point(calculateX(), calculateY());
    }

    /**
     * Checks whether the menu is collapsed.
     *
     * @return <code>true</code> if the menu is collapsed; otherwise <code>false</code>.
     */
    public static boolean isCollapsed() {
        return !Microbot.getClient().isMenuOpen();
    }

    /**
     * Checks whether or not the menu is open.
     *
     * @return <code>true</code> if the menu is open; otherwise <code>false</code>.
     */
    public static boolean isOpen() {
        return Microbot.getClient().isMenuOpen();
    }

    /**
     * Strips HTML tags.
     *
     * @param input The string you want to parse.
     * @return The parsed {@code String}.
     */
    public static String stripFormatting(String input) {
        return HTML_TAG.matcher(input).replaceAll("");
    }

    /**
     * Calculates the width of the menu
     *
     * @return the menu width
     */
    protected static int calculateWidth() {
        MenuEntry[] entries = getEntries();
        final int MIN_MENU_WIDTH = 102;
        FontMetrics fm = ((Applet) Microbot.getClient()).getGraphics().getFontMetrics(FontManager.getRunescapeBoldFont());
        int longestEntry = 0;
        for (MenuEntry entry : entries)
            longestEntry = (fm.stringWidth(entry.getOption() + " " +
                    entry.getTarget().replaceAll("<.*?>", ""))
                    > longestEntry) ? fm.stringWidth(entry.getOption() + " " +
                    entry.getTarget().replaceAll("<.*?>", "")) : longestEntry;
        return (longestEntry + MENU_SIDE_BORDER < MIN_MENU_WIDTH) ? MIN_MENU_WIDTH : longestEntry + MENU_SIDE_BORDER;
    }

    /**
     * Calculates the height of the menu
     *
     * @return the menu height
     */
    protected static int calculateHeight() {
        MenuEntry[] entries = getEntries();
        int numberOfEntries = entries.length;
        return MENU_ENTRY_LENGTH * numberOfEntries + TOP_OF_MENU_BAR;
    }

    /**
     * Calculates the top left corner X of the menu
     *
     * @return the menu x
     */
    protected static int calculateX() {
        if (isOpen()) {
            final int MIN_MENU_WIDTH = 102;
            int width = calculateWidth();
            return (width + MENU_SIDE_BORDER < MIN_MENU_WIDTH) ? ((int) Microbot.getMouse().getLastMousePosition().getX()
                    - (MIN_MENU_WIDTH / 2)) : (Microbot.getMouse().getLastMousePosition().getX() - (width / 2));
        }
        return -1;
    }

    /**
     * Calculates the top left corner Y of the menu
     *
     * @return the menu y
     */
    protected static int calculateY() {
        if (isOpen()) {
            final int CANVAS_LENGTH = Microbot.getClient().getCanvasHeight();
            MenuEntry[] entries = getEntries();
            int offset = CANVAS_LENGTH - (Microbot.getMouse().getLastMousePosition().getY() + calculateHeight());
            if (offset < 0 && entries.length >= MAX_DISPLAYABLE_ENTRIES) {
                return Microbot.getMouse().getLastMousePosition().getY() + offset;
            }
            if (offset < 0) {
                return Microbot.getMouse().getLastMousePosition().getY() + offset;
            }
            return Microbot.getMouse().getLastMousePosition().getY();
        }
        return -1;
    }

    public static MenuEntry[] getEntries() {
        MenuEntry[] entries = Microbot.getClient().getMenuEntries();
        MenuEntry[] reversed = new MenuEntry[entries.length];
        for (int i = entries.length - 1, x = 0; i >= 0; i--, x++)
            reversed[i] = entries[x];
        return reversed;
    }

    public static String[] getEntriesString() {
        MenuEntry[] entries = getEntries();
        String[] entryStrings = new String[entries.length];
        for (int i = 0; i < entries.length; i++) {
            entryStrings[i] = stripFormatting(entries[i].getOption()) + " " + ((entries[i].getTarget() != null) ? stripFormatting(entries[i].getTarget()) : "");
        }
        return entryStrings;
    }

    public static String[] getActions() {
        MenuEntry[] entries = getEntries();
        String[] actions = new String[entries.length];
        for (int i = 0; i < entries.length; i++) {
            if (entries[i] != null) {
                actions[i] = entries[i].getOption();
            } else {
                actions[i] = "";
            }
        }
        return actions;
    }

    public static String[] getTargets() {
        MenuEntry[] entries = getEntries();
        String[] targets = new String[entries.length];
        for (int i = 0; i < entries.length; i++) {
            if (entries[i] != null) {
                targets[i] = entries[i].getTarget();
            } else {
                targets[i] = "";
            }
        }
        return targets;
    }

    /**
     * Returns the index in the menu for a given action. Starts at 0.
     *
     * @param action The action that you want the index of.
     * @return The index of the given target in the context menu; otherwise -1.
     */
    public static int getIndex(String action) {
        MenuEntry[] entries = getEntries();
        action = action.toLowerCase();
        for (int i = 0; i < entries.length; i++) {
            if (entries[i].getOption().toLowerCase().equals(action.toLowerCase())) {
                lastIndex = i;
                return i;
            }
        }
        return -1;
    }

    /**
     * Returns the index in the menu for a given action with a given target.
     * Starts at 0.
     *
     * @param action The action of the menu entry of which you want the index.
     * @param target The target of the menu entry of which you want the index.
     *               If target is null, operates like getIndex(String action).
     * @return The index of the given target in the context menu; otherwise -1.
     */
    public static int getIndex(String action, String... target) {
        if (target == null) {
            return getIndex(action);
        }
        action = action.toLowerCase();
        String[] actions = getActions();
        String[] targets = getTargets();
        /* Throw exception if lengths unequal? */
        if (action != null) {
            for (int i = 0; i < Math.min(actions.length, targets.length); i++) {
                if (actions[i].toLowerCase().contains(action)) {
                    lastIndex = checkTargetMatch(target, targets, i);
                    if (lastIndex == -1) continue;
                    return lastIndex;
                }
            }
        } else {
            for (int i = 0; i < targets.length; i++) {
                lastIndex = checkTargetMatch(target, targets, i);
                return lastIndex;
            }
        }
        return -1;
    }

    /**
     * Checks the target list to the menu targets for matches and returns the first index that matches
     *
     * @param target  The list of targets to check
     * @param targets The targets in the menu
     * @param index   The index of the last iteration of the loop acted upon this method
     * @return The index of a matching target or -1
     */
    private static int checkTargetMatch(String[] target, String[] targets, int index) {
        boolean targetMatch = false;
        if (target[0] != null) {
            for (String targetPart : target) {
                if (targets[index].toLowerCase().contains(targetPart.toLowerCase())) {
                    targetMatch = true;
                } else {
                    targetMatch = false;
                }
            }
            if (targetMatch)
                return index;
        } else {
            return index;
        }
        return -1;
    }

    /**
     * Checks whether or not a given action (or action substring) is present in
     * the menu.
     *
     * @param action The action or action substring.
     * @return <code>true</code> if present, otherwise <code>false</code>.
     */
    public static boolean contains(final String action) {
        return getIndex(action) != -1;
    }

    /**
     * Checks whether or not a given action with given target is present
     * in the menu.
     *
     * @param action The action or action substring.
     * @param target The target or target substring.
     * @return <code>true</code> if present, otherwise <code>false</code>.
     */
    public static boolean contains(final String action, final String target) {
        return getIndex(action, target) != -1;
    }

    /*public static boolean isUsingItem() {
        MenuEntry[] entries = getEntries();
        return Arrays.stream(entries).anyMatch(x -> x.getItemId())
    }*/
}