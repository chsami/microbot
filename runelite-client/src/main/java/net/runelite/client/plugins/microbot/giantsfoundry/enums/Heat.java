package net.runelite.client.plugins.microbot.giantsfoundry.enums;

import java.awt.Color;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.runelite.client.ui.ColorScheme;

@Getter
@AllArgsConstructor
public enum Heat
{
    LOW("Low", ColorScheme.PROGRESS_COMPLETE_COLOR),
    MED("Medium", ColorScheme.PROGRESS_INPROGRESS_COLOR),
    HIGH("High", ColorScheme.PROGRESS_ERROR_COLOR),
    NONE("Not in range", ColorScheme.LIGHT_GRAY_COLOR);

    private final String name;
    private final Color color;
}
