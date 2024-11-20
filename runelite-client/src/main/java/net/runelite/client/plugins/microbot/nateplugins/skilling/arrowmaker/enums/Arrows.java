package net.runelite.client.plugins.microbot.nateplugins.skilling.arrowmaker.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Arrows {
    HEADLESS_ARROW("feather", "arrow shaft",1),
    BRONZE_ARROW("headless arrow", "bronze arrowtips",1),
    IRON_ARROW("headless arrow", "iron arrowtips",15),
    STEEL_ARROW("headless arrow", "steel arrowtips",30),
    MITHRIL_ARROW("headless arrow", "mithril arrowtips",45),
    BROAD_ARROW("headless arrow", "broad arrowheads",52),
    ADAMANT_ARROW("headless arrow", "adamant arrowtips",60),
    RUNE_ARROW("headless arrow", "rune arrowtips",75),
    AMETHYST_ARROW("headless arrow", "amethyst arrowtips",82),
    DRAGON_ARROW("headless arrow", "dragon arrowtips",90);

        private final String item1;
        private final String item2;
        private final int lvlreq;
        @Override
        public String toString() {
            return item2;
        }
}
