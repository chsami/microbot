package net.runelite.client.plugins.microbot.util.widget;

import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.plugins.microbot.Microbot;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static net.runelite.client.plugins.microbot.util.Global.sleepUntil;

public class Rs2Widget {

    public static boolean clickWidget(String text) {
        Widget widget = findWidget(text, null);
        if (widget != null) {
            Microbot.getMouse().click(widget.getBounds());
            return true;
        }
        return false;
    }
    public static boolean clickWidget(String text, boolean exact) {
        Widget widget = findWidget(text, null, exact);
        if (widget != null) {
            Microbot.getMouse().click(widget.getBounds());
            return true;
        }
        return false;
    }
    public static boolean clickWidget(int parentId, int childId) {
        Widget widget = getWidget(parentId, childId);
        if (widget != null) {
            Microbot.getMouse().click(widget.getBounds());
            return true;
        }
        return false;
    }
    public static Widget getWidget(WidgetInfo wiget) {
        return Microbot.getClientThread().runOnClientThread(() -> Microbot.getClient().getWidget(wiget));
    }
    public static Widget getWidget(int id) {
        return Microbot.getClientThread().runOnClientThread(() -> Microbot.getClient().getWidget(id));
    }

    public static Widget getWidget(int id, int child) {
        return Microbot.getClientThread().runOnClientThread(() -> Microbot.getClient().getWidget(id, child));
    }

    public static boolean getWidgetChildText(int id, String matchingText) {
        return Microbot.getClientThread().runOnClientThread(() -> {
            Widget widget = Microbot.getClient().getWidget(id);
            if (widget == null) return false;
            if (widget.getChildren().length == 0) return false;
            return Arrays.stream(widget.getChildren()).anyMatch(x -> x.getText().contains(matchingText));
        });
    }
    public static int getChildWidgetSpriteID(int id, int childId) {
        return  Microbot.getClientThread().runOnClientThread(() -> Microbot.getClient().getWidget(id).getChild(childId).getSpriteId());
    }

    public static boolean clickWidget(int id) {
        Widget widget = Microbot.getClientThread().runOnClientThread(() -> Microbot.getClient().getWidget(id));
        if (widget == null) return false;
        Microbot.getMouse().click(widget.getBounds());
        return true;
    }

    public static boolean clickChildWidget(int id, int childId) {
        Widget widget = Microbot.getClientThread().runOnClientThread(() -> Microbot.getClient().getWidget(id));
        if (widget == null) return false;
        Microbot.getMouse().click(widget.getChild(childId).getBounds());
        return true;
    }

    public static void changeWidgetText(String textToSearch, String newText) {
        do {
            try {
                Widget widget = findWidget(textToSearch, null);
                if (widget == null) break;
                Microbot.getClientThread().runOnClientThread(() -> widget.setText(newText));
            } catch(Exception ex) {
                System.out.println(ex.getMessage());
            }

        } while (true);
    }

    public static Widget findWidget(String text, List<Widget> children, boolean exact) {
        return Microbot.getClientThread().runOnClientThread(() -> {
            Widget foundWidget = null;
            if (children == null) {
                List<Widget> rootWidgets = Arrays.stream(Microbot.getClient().getWidgetRoots()).filter(x -> x != null && !x.isHidden()).collect(Collectors.toList());
                for (Widget rootWidget : rootWidgets) {
                    if (exact) {
                        if (rootWidget.getText().toLowerCase().contains(text.toLowerCase()) || rootWidget.getName().toLowerCase().contains(">" + text.toLowerCase() + "<")) {
                            return rootWidget;
                        }
                    } else {
                        if (rootWidget.getText().toLowerCase().contains(text.toLowerCase()) || rootWidget.getName().toLowerCase().contains(text.toLowerCase())) {
                            return rootWidget;
                        }
                    }
                    if (rootWidget.getChildren() != null)
                        return findWidget(text, Arrays.stream(rootWidget.getChildren()).filter(x -> x != null && !x.isHidden()).collect(Collectors.toList()), exact);
                    if (rootWidget.getNestedChildren().length > 0)
                        return findWidget(text, Arrays.stream(rootWidget.getNestedChildren()).filter(x -> x != null && !x.isHidden()).collect(Collectors.toList()), exact);
                    if (rootWidget.getDynamicChildren().length > 0)
                        return findWidget(text, Arrays.stream(rootWidget.getDynamicChildren()).filter(x -> x != null && !x.isHidden()).collect(Collectors.toList()), exact);
                    if (rootWidget.getStaticChildren().length > 0)
                        return findWidget(text, Arrays.stream(rootWidget.getStaticChildren()).filter(x -> x != null && !x.isHidden()).collect(Collectors.toList()), exact);
                }
            } else if (children.size() > 0) {
                for (Widget child : children) {
                    foundWidget = searchChildren(text, child, exact);
                    if (foundWidget != null) break;
                }
            }
            return foundWidget;
        });
    }
    public static Widget findWidget(String text) {
        return findWidget(text, null, false);
    }

    public static Widget findWidgetExact(String text) {
        return findWidget(text, null, true);
    }

    public static boolean hasWidget(String text) {
        return findWidget(text, null, false) != null;
    }

    public static boolean sleepUntilHasWidget(String text) {
        sleepUntil(() ->  findWidget(text, null, false) != null);
        return true;
    }

    public static Widget findWidget(String text, List<Widget> children) {
        return findWidget(text, children, false);
    }


    public static Widget searchChildren(String text, Widget child, boolean exact) {
        Widget found = null;
        if (exact) {
            if (child.getText().toLowerCase().contains(text.toLowerCase()) || child.getName().toLowerCase().contains(">" + text.toLowerCase() + "<")) {
                return child;
            }
        } else {
            if (child.getText().toLowerCase().contains(text.toLowerCase()) || child.getName().toLowerCase().contains(text.toLowerCase())) {
                return child;
            }
        }

        if (child.getChildren() != null) {
            List<Widget> visibleChildWidgets = Arrays.stream(child.getChildren()).filter(x -> x != null && !x.isHidden()).collect(Collectors.toList());
            if (visibleChildWidgets.size() > 0)
                found = findWidget(text, visibleChildWidgets, exact);
        }
        if (found != null) return found;
        if (child.getNestedChildren().length > 0) {
            List<Widget> visibleChildWidgets = Arrays.stream(child.getNestedChildren()).filter(x -> x != null && !x.isHidden()).collect(Collectors.toList());
            if (visibleChildWidgets.size() > 0)
                found = findWidget(text, visibleChildWidgets, exact);
        }
        if (found != null) return found;
        if (child.getDynamicChildren().length > 0) {
            List<Widget> visibleChildWidgets = Arrays.stream(child.getDynamicChildren()).filter(x -> x != null && !x.isHidden()).collect(Collectors.toList());
            if (visibleChildWidgets.size() > 0)
                found = findWidget(text, visibleChildWidgets, exact);
        }
        if (found != null) return found;
        if (child.getStaticChildren().length > 0) {
            List<Widget> visibleChildWidgets = Arrays.stream(child.getStaticChildren()).filter(x -> x != null && !x.isHidden()).collect(Collectors.toList());
            if (visibleChildWidgets.size() > 0)
                found = findWidget(text, visibleChildWidgets, exact);
        }
        return found;
    }

    public static Widget searchChildren(String text, Widget child) {
       return searchChildren(text, child, false);
    }

    public static Widget findWidget(int spriteId, List<Widget> children) {
        return Microbot.getClientThread().runOnClientThread(() -> {
            Widget foundWidget = null;
            if (children == null) {
                List<Widget> rootWidgets = Arrays.stream(Microbot.getClient().getWidgetRoots()).filter(x -> x != null && !x.isHidden()).collect(Collectors.toList());
                for (Widget rootWidget : rootWidgets) {
                    if (rootWidget.getSpriteId() == spriteId) {
                        return rootWidget;
                    }
                    if (rootWidget.getChildren() != null)
                        return findWidget(spriteId, Arrays.stream(rootWidget.getChildren()).filter(x -> x != null && !x.isHidden()).collect(Collectors.toList()));
                    if (rootWidget.getNestedChildren().length > 0)
                        return findWidget(spriteId, Arrays.stream(rootWidget.getNestedChildren()).filter(x -> x != null && !x.isHidden()).collect(Collectors.toList()));
                    if (rootWidget.getDynamicChildren().length > 0)
                        return findWidget(spriteId, Arrays.stream(rootWidget.getDynamicChildren()).filter(x -> x != null && !x.isHidden()).collect(Collectors.toList()));
                    if (rootWidget.getStaticChildren().length > 0)
                        return findWidget(spriteId, Arrays.stream(rootWidget.getStaticChildren()).filter(x -> x != null && !x.isHidden()).collect(Collectors.toList()));
                }
            } else if (children.size() > 0) {
                for (Widget child : children) {
                    foundWidget = searchChildren(spriteId, child);
                    if (foundWidget != null) break;
                }
            }
            return foundWidget;
        });
    }

    public static Widget searchChildren(int spriteId, Widget child) {
        Widget found = null;
        if (child.getSpriteId() == spriteId) {
            return child;
        }
        if (child.getChildren() != null) {
            List<Widget> visibleChildWidgets = Arrays.stream(child.getChildren()).filter(x -> x != null && !x.isHidden()).collect(Collectors.toList());
            if (visibleChildWidgets.size() > 0)
                found = findWidget(spriteId, visibleChildWidgets);
        }
        if (found != null) return found;
        if (child.getNestedChildren().length > 0) {
            List<Widget> visibleChildWidgets = Arrays.stream(child.getNestedChildren()).filter(x -> x != null && !x.isHidden()).collect(Collectors.toList());
            if (visibleChildWidgets.size() > 0)
                found = findWidget(spriteId, visibleChildWidgets);
        }
        if (found != null) return found;
        if (child.getDynamicChildren().length > 0) {
            List<Widget> visibleChildWidgets = Arrays.stream(child.getDynamicChildren()).filter(x -> x != null && !x.isHidden()).collect(Collectors.toList());
            if (visibleChildWidgets.size() > 0)
                found = findWidget(spriteId, visibleChildWidgets);
        }
        if (found != null) return found;
        if (child.getStaticChildren().length > 0) {
            List<Widget> visibleChildWidgets = Arrays.stream(child.getStaticChildren()).filter(x -> x != null && !x.isHidden()).collect(Collectors.toList());
            if (visibleChildWidgets.size() > 0)
                found = findWidget(spriteId, visibleChildWidgets);
        }
        return found;
    }


}
