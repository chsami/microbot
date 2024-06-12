package net.runelite.client.plugins.hoseaplugins.PiggyUtils.strategy;

import net.runelite.api.NPC;
import net.runelite.api.TileObject;
import net.runelite.api.widgets.Widget;

import java.util.function.Predicate;

public interface TaskInterface {
    /**
     * Whether the task should run
     *
     * @return True if it can run, false if it should not
     */
    boolean validate();

    /**
     * The action(s) to perform when {@link #validate() validate} returns true
     */
    void execute();

    /**
     * Interact with a tile object
     * <p>{@link #interact(TileObject, String, boolean)}</p>
     * <p>{@link #interact(TileObject, String, Predicate)}</p>
     *
     * @param object The object to interact with
     * @param action The action to perform
     * @return True if the TileObject, false if was null
     */
    boolean interactObject(TileObject object, String action);

    /**
     * Interact with a tile object, with an option to specify interaction with the nearest object.
     * <p>{@link #interactObject(TileObject, String)}</p>
     * <p>{@link #interact(TileObject, String, Predicate)}</p>
     *
     * @param object  The object to interact with
     * @param action  The action to perform
     * @param nearest Whether to interact with the nearest object
     * @return True if the TileObject, false if was null
     */
    boolean interactObject(String objectName, String action, boolean nearest);

    /**
     * Interact with a tile object, with an option to specify a condition to interact with the object.
     * <p>{@link #interactObject(TileObject, String)}</p>
     * <p>{@link #interactObject(String, String, boolean)}</p>
     *
     * @param object    The object to interact with
     * @param action    The action to perform
     * @param condition Filter used to find the object
     * @return True if the TileObject, false if was null
     */
    boolean interactObject(String objectName, String action, Predicate<TileObject> condition);

    boolean interactNpc(NPC npc, String action);

    boolean interactNpc(String npcName, String action, boolean nearest);

    boolean interactNpc(String npcName, String action, Predicate<NPC> condition);

//    boolean interactWidget(Widget widget, String action);
//
//    boolean interactItem(String itemName, String action, Predicate<Widget> condition);

//    boolean interactWidgets(Widget widget1, Widget widget2);
//    Add this method but follow the principles of widgetOnWidget, widgetOnNpc, widgetOnObject

}
