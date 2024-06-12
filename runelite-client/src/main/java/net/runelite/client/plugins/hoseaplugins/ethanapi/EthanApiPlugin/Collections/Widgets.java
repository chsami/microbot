package net.runelite.client.plugins.hoseaplugins.ethanapi.EthanApiPlugin.Collections;

import net.runelite.client.plugins.hoseaplugins.ethanapi.EthanApiPlugin.Collections.query.WidgetQuery;
import net.runelite.api.Client;
import net.runelite.api.widgets.Widget;
import net.runelite.client.RuneLite;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;

public class Widgets {
    static Client client = RuneLite.getInjector().getInstance(Client.class);

    //It is important to note that this method will return all widgets, including hidden ones.
    //Some widgets are not updated while hidden so there is a chance that the widgets returned contain outdated
    // information.
    //for update critical information make sure the widget is not hidden or use the other query types like inventory,
    // equipment ect as they will only return up-to-date information.
    public static WidgetQuery search() {
        HashSet<Widget> returnList = new HashSet<>(Arrays.asList(client.getWidgetRoots()));
        Queue<Widget> queue = new LinkedList<>(Arrays.asList(client.getWidgetRoots()));
        while (!queue.isEmpty()) {
            Widget widget = queue.poll();
            if (widget == null) {
                continue;
            }
            Widget[] dynamicChildren = widget.getDynamicChildren();
            if (dynamicChildren != null) {
                for (Widget dynamicChild : dynamicChildren) {
                    if (dynamicChild == null) {
                        continue;
                    }
                    queue.add(dynamicChild);
                    returnList.add(dynamicChild);
                }
            }
            Widget[] nestedChildren = widget.getNestedChildren();
            if (nestedChildren != null) {
                for (Widget nestedChild : nestedChildren) {
                    if (nestedChild == null) {
                        continue;
                    }
                    queue.add(nestedChild);
                    returnList.add(nestedChild);
                }
            }
            Widget[] staticChildren;
            try {
                staticChildren = widget.getStaticChildren();
            }catch (NullPointerException e){
                continue;
            }
            if (staticChildren != null) {
                for (Widget staticChild : staticChildren) {
                    if (staticChild == null) {
                        continue;
                    }
                    queue.add(staticChild);
                    returnList.add(staticChild);
                }
            }
            queue.addAll(Arrays.asList(widget.getNestedChildren()));
        }
        return new WidgetQuery(returnList);
    }
}
