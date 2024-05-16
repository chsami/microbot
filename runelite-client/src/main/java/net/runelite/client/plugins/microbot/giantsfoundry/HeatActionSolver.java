package net.runelite.client.plugins.microbot.giantsfoundry;

public class HeatActionSolver {

    /**
     * @param goal       the desired heat destination
     * @param init_dx1   initial speed of heating/cooling. currently 7 for heat/cool, 27 for dunk/quench.
     * @param dx2_offset bonus acceleration. currently, 0 for heat/cool, 2 for dunk/quench.
     * @return Index here refers to tick. So an index of 10 means the goal can be reached in 10 ticks.
     */
    public static int findDx0Index(int goal, int init_dx1, int dx2_offset) {
        int dx0 = 0;
        int dx1 = init_dx1;
        int count_index = 0;
        for (int dx2 = 1; dx0 <= goal; dx2++) {  // Start from 1 up to the count inclusive
            int repetitions;
            if (dx2 == 1) {
                repetitions = 2;  // The first number appears twice
            } else if (dx2 % 2 == 0) {
                repetitions = 6;  // Even numbers appear six times
            } else {
                repetitions = 4;  // Odd numbers (after 1) appear four times
            }
            for (int j = 0; j < repetitions && dx0 <= goal; j++) {
                dx0 += dx1;
                dx1 += dx2 + dx2_offset;  // Sum the current number 'repetitions' times
                count_index += 1;
            }
        }
        return count_index;
    }


    /**
     * We can use the pattern to get the dx2 at a specific index numerically
     *
     * @param index the index/tick we want to calculate dx2 at
     * @return the acceleration of heating/cooling at index/tick
     */
    public static int getDx2AtIndex(int index) {
        if (index <= 1) return 1;

        index -= 2;
        // 0 1 2 3 4 5 6 7 8 9
        // e,e,e,e,e,e,o,o,o,o

        int block = index / 10;
        int block_idx = index % 10;
        int number = block * 2;
        if (block_idx <= 5) {
            return number + 2;
        } else {
            return number + 3;
        }
    }


    /**
     * We can use the pattern to get the dx1 at a specific index numerically
     *
     * @param index    the index/tick we want to calculate the speed of heating/cooling
     * @param constant the initial speed of heating/cooling.
     * @return the speed of heating at index/tick
     */
    public static int getDx1AtIndex(int index, int constant) {
        int _dx1 = constant;
        for (int i = 0; i < index; ++i) {
            _dx1 += getDx2AtIndex(i);
        }

        return _dx1;
    }
}