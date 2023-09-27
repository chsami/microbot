package net.runelite.client.plugins.microbot.farming.tithefarm.farming.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TitheFarmLanes {
    LANE_1_2("Lane one and two"),
    LANE_2_3("Lane two and three"),
    LANE_3_4("Lane three and four"),
    LANE_4_5("Lane four and five");

    private final String name;

    @Override
    public String toString() {
        return name;
    }
}
