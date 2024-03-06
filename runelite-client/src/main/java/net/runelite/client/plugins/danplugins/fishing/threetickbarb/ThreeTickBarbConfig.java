package net.runelite.client.plugins.danplugins.fishing.threetickbarb;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.plugins.danplugins.fishing.threetickbarb.tickmanipulation.CutEatTickManipulationData;
import net.runelite.client.plugins.danplugins.fishing.threetickbarb.tickmanipulation.TickManipulationData;
import net.runelite.client.plugins.danplugins.fishing.threetickbarb.tickmanipulation.TickManipulationMode;

@ConfigGroup("threeTickBarb")
public interface ThreeTickBarbConfig extends Config {
    @ConfigItem(keyName = "tickManipulationMode", name = "Tick Manipulate Type", description = "Type of tick manip")
    default TickManipulationMode tickManipulateMode()
    {
        return TickManipulationMode.GUAM_TAR;
    }

    @ConfigItem(keyName = "shouldCutEat", name = "Cut Eat", description = "Whether to cut and eat fish")
    default boolean cutEat()
    {
        return false;
    }

    default TickManipulationData tickManipulationData()
    {
        final TickManipulationData normalMethod = this.tickManipulateMode().getTickManipulationData();
        if (this.cutEat())
        {
            return new CutEatTickManipulationData(normalMethod);
        }

        return normalMethod;
    }
}
