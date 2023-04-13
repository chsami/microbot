package net.runelite.client.plugins.microbot.util.widget;

import net.runelite.api.widgets.Widget;
import net.runelite.client.plugins.microbot.Microbot;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Rs2Widget {
    public static Widget getWidget(int id) {
        return Microbot.getClientThread().runOnClientThread(() -> Microbot.getClient().getWidget(id));
    }

    public static boolean getWidgetChildText(int id, String matchingText) {
        return Microbot.getClientThread().runOnClientThread(() -> {
            Widget widget = Microbot.getClient().getWidget(id);
            if (widget == null) return false;
            if (widget.getChildren().length == 0) return false;
            return Arrays.stream(widget.getChildren()).anyMatch(x -> x.getText().contains(matchingText));
        });
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

    public static Widget findWidget(String text, List<Widget> children) {
        return Microbot.getClientThread().runOnClientThread(() -> {
            Widget foundWidget = null;
            if (children == null) {
                List<Widget> rootWidgets = Arrays.stream(Microbot.getClient().getWidgetRoots()).filter(x -> !x.isHidden()).collect(Collectors.toList());
                for (Widget rootWidget : rootWidgets) {
                    if (rootWidget.getText().contains(text)) {
                        return rootWidget;
                    }
                    if (rootWidget.getChildren() != null)
                        return findWidget(text, Arrays.stream(rootWidget.getChildren()).filter(x -> !x.isHidden()).collect(Collectors.toList()));
                    if (rootWidget.getNestedChildren().length > 0)
                        return findWidget(text, Arrays.stream(rootWidget.getNestedChildren()).filter(x -> !x.isHidden()).collect(Collectors.toList()));
                    if (rootWidget.getDynamicChildren().length > 0)
                        return findWidget(text, Arrays.stream(rootWidget.getDynamicChildren()).filter(x -> !x.isHidden()).collect(Collectors.toList()));
                    if (rootWidget.getStaticChildren().length > 0)
                        return findWidget(text, Arrays.stream(rootWidget.getStaticChildren()).filter(x -> !x.isHidden()).collect(Collectors.toList()));
                }
            } else if (children.size() > 0) {
                for (Widget child : children) {
                    foundWidget = searchChildren(text, child);
                    if (foundWidget != null) break;
                }
            }
            return foundWidget;
        });
    }

    public static Widget searchChildren(String text, Widget child) {
        Widget found = null;
        if (child.getText().contains(text)) {
            return child;
        }
        if (child.getChildren() != null) {
            List<Widget> visibleChildWidgets = Arrays.stream(child.getChildren()).filter(x -> !x.isHidden()).collect(Collectors.toList());
            if (visibleChildWidgets.size() > 0)
                found = findWidget(text, visibleChildWidgets);
        }
        if (found != null) return found;
        if (child.getNestedChildren().length > 0) {
            List<Widget> visibleChildWidgets = Arrays.stream(child.getNestedChildren()).filter(x -> !x.isHidden()).collect(Collectors.toList());
            if (visibleChildWidgets.size() > 0)
                found = findWidget(text, visibleChildWidgets);
        }
        if (found != null) return found;
        if (child.getDynamicChildren().length > 0) {
            List<Widget> visibleChildWidgets = Arrays.stream(child.getDynamicChildren()).filter(x -> !x.isHidden()).collect(Collectors.toList());
            if (visibleChildWidgets.size() > 0)
                found = findWidget(text, visibleChildWidgets);
        }
        if (found != null) return found;
        if (child.getStaticChildren().length > 0) {
            List<Widget> visibleChildWidgets = Arrays.stream(child.getStaticChildren()).filter(x -> !x.isHidden()).collect(Collectors.toList());
            if (visibleChildWidgets.size() > 0)
                found = findWidget(text, visibleChildWidgets);
        }
        return found;
    }

    public static Widget findWidget(int spriteId, List<Widget> children) {
        return Microbot.getClientThread().runOnClientThread(() -> {
            Widget foundWidget = null;
            if (children == null) {
                List<Widget> rootWidgets = Arrays.stream(Microbot.getClient().getWidgetRoots()).filter(x -> !x.isHidden()).collect(Collectors.toList());
                for (Widget rootWidget : rootWidgets) {
                    if (rootWidget.getSpriteId() == spriteId) {
                        return rootWidget;
                    }
                    if (rootWidget.getChildren() != null)
                        return findWidget(spriteId, Arrays.stream(rootWidget.getChildren()).filter(x -> !x.isHidden()).collect(Collectors.toList()));
                    if (rootWidget.getNestedChildren().length > 0)
                        return findWidget(spriteId, Arrays.stream(rootWidget.getNestedChildren()).filter(x -> !x.isHidden()).collect(Collectors.toList()));
                    if (rootWidget.getDynamicChildren().length > 0)
                        return findWidget(spriteId, Arrays.stream(rootWidget.getDynamicChildren()).filter(x -> !x.isHidden()).collect(Collectors.toList()));
                    if (rootWidget.getStaticChildren().length > 0)
                        return findWidget(spriteId, Arrays.stream(rootWidget.getStaticChildren()).filter(x -> !x.isHidden()).collect(Collectors.toList()));
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
            List<Widget> visibleChildWidgets = Arrays.stream(child.getChildren()).filter(x -> !x.isHidden()).collect(Collectors.toList());
            if (visibleChildWidgets.size() > 0)
                found = findWidget(spriteId, visibleChildWidgets);
        }
        if (found != null) return found;
        if (child.getNestedChildren().length > 0) {
            List<Widget> visibleChildWidgets = Arrays.stream(child.getNestedChildren()).filter(x -> !x.isHidden()).collect(Collectors.toList());
            if (visibleChildWidgets.size() > 0)
                found = findWidget(spriteId, visibleChildWidgets);
        }
        if (found != null) return found;
        if (child.getDynamicChildren().length > 0) {
            List<Widget> visibleChildWidgets = Arrays.stream(child.getDynamicChildren()).filter(x -> !x.isHidden()).collect(Collectors.toList());
            if (visibleChildWidgets.size() > 0)
                found = findWidget(spriteId, visibleChildWidgets);
        }
        if (found != null) return found;
        if (child.getStaticChildren().length > 0) {
            List<Widget> visibleChildWidgets = Arrays.stream(child.getStaticChildren()).filter(x -> !x.isHidden()).collect(Collectors.toList());
            if (visibleChildWidgets.size() > 0)
                found = findWidget(spriteId, visibleChildWidgets);
        }
        return found;
    }
}
