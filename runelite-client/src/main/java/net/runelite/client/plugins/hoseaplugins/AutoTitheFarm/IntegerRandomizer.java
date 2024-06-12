package net.runelite.client.plugins.hoseaplugins.AutoTitheFarm;

import lombok.AccessLevel;
import lombok.Getter;
import org.apache.commons.lang3.RandomUtils;

import java.util.HashSet;
import java.util.Set;

public class IntegerRandomizer {

    private final int integerStart;

    private final int integerEnd;

    @Getter(AccessLevel.PACKAGE)
    private final Set<Integer> oldValues = new HashSet<>();

    public IntegerRandomizer(int integerStart, int integerEnd) {
        this.integerStart = integerStart;
        this.integerEnd = integerEnd;
    }

    // get true next random integer (sort of)
    public int getRandomInteger() {
        int newValue;
        final int collectionMaxSize = this.integerEnd - this.integerStart;

        do {
            newValue = RandomUtils.nextInt(this.integerStart, this.integerEnd);
        } while (oldValues.contains(newValue));

        oldValues.add(newValue);

        // last integer from the Set may still be repeated post-clear.
        if (oldValues.size() >= collectionMaxSize) {
            oldValues.clear();
        }

        return newValue;
    }
}
