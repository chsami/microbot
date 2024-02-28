package net.runelite.client.plugins.danplugins.fishing.threetickbarb.tickmanipulation;

import lombok.Getter;

@Getter
public enum TickManipulationMode {
    GUAM_TAR(new HerbTarTickManipulationData()),
    KNIFE_LOG(new KnifeLogTickManipulationData());

    TickManipulationMode(final TickManipulationData tickManipulationData)
    {
        this.tickManipulationData = tickManipulationData;
    }

    private final TickManipulationData tickManipulationData;
}
