package net.runelite.client.plugins.microbot.util.misc;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;

public class Predicates {
    public static <T> Predicate<T> distinctByProperty(Function<? super T, ?> propertyExtractor) {
        Set<Object> seen = ConcurrentHashMap.newKeySet();
        return t -> seen.add(propertyExtractor.apply(t));
    }
}
