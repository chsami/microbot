package net.runelite.client.plugins.hoseaplugins.ethanapi.EthanApiPlugin.Collections.query;

import net.runelite.client.plugins.hoseaplugins.ethanapi.EthanApiPlugin.Collections.EquipmentItemWidget;
import net.runelite.api.widgets.Widget;
import net.runelite.client.util.Text;
import net.runelite.client.util.WildcardMatcher;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class EquipmentItemQuery {
    private List<EquipmentItemWidget> items;

    public EquipmentItemQuery(List<EquipmentItemWidget> items) {
        this.items = new ArrayList(items);
    }

    public EquipmentItemQuery filter(Predicate<? super Widget> predicate) {
        items = items.stream().filter(predicate).collect(Collectors.toList());
        return this;
    }

    public EquipmentItemQuery withAction(String action) {
        items = items.stream().filter(item -> Arrays.asList(item.getActions()).contains(action)).collect(Collectors.toList());
        return this;
    }

    public EquipmentItemQuery withId(int id) {
        items = items.stream().filter(item -> item.getEquipmentItemId() == id).collect(Collectors.toList());
        return this;
    }

    public EquipmentItemQuery withName(String name) {
        items = items.stream().filter(item -> item.getName().equals(name)).collect(Collectors.toList());
        return this;
    }

    public EquipmentItemQuery nameContains(String name) {
        items = items.stream().filter(item -> item.getName().contains(name)).collect(Collectors.toList());
        return this;
    }

    public EquipmentItemQuery nameContainsNoCase(String name) {
        items =
                items.stream().filter(item -> item.getName().toLowerCase().contains(name.toLowerCase())).collect(Collectors.toList());
        return this;
    }

    public EquipmentItemQuery idInList(List<Integer> ids) {
        items = items.stream().filter(item -> ids.contains(item.getEquipmentItemId())).collect(Collectors.toList());
        return this;
    }

    public EquipmentItemQuery indexIs(int index) {
        items = items.stream().filter(item -> item.getIndex() == index).collect(Collectors.toList());
        return this;
    }

    public EquipmentItemQuery matchesWildCardNoCase(String input) {
        items =
                items.stream().
                        filter(item -> WildcardMatcher.matches(input.toLowerCase(), Text.removeTags(item.getName().toLowerCase()))).
                        collect(Collectors.toList());
        return this;
    }

    public boolean empty() {
        return items.size() == 0;
    }

    public EquipmentItemQuery filterUnique() {
        items = items.stream().collect(Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparingInt(Widget::getItemId))), ArrayList::new));
        return this;
    }

    public List<EquipmentItemWidget> result() {
        return items;
    }

    public Optional<EquipmentItemWidget> first() {
        Widget returnWidget = null;
        if (items.size() == 0) {
            return Optional.ofNullable(null);
        }
        return Optional.ofNullable(items.get(0));
    }
}
