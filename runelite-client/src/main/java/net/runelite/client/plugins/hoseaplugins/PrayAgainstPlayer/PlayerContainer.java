package net.runelite.client.plugins.hoseaplugins.PrayAgainstPlayer;

import net.runelite.api.Player;

/**
 * Contains a player object
 * When they attacked me
 * And (in milliseconds) when to expire the overlay around them
 */
public class PlayerContainer
{

    private final Player player;
    private final long whenTheyAttackedMe;
    private final int millisToExpireHighlight;

    PlayerContainer(final Player player, final long whenTheyAttackedMe, final int millisToExpireHighlight)
    {
        this.player = player;
        this.whenTheyAttackedMe = whenTheyAttackedMe;
        this.millisToExpireHighlight = millisToExpireHighlight;
    }

    //getters
    public Player getPlayer()
    {
        return player;
    }

    long getWhenTheyAttackedMe()
    {
        return whenTheyAttackedMe;
    }

    int getMillisToExpireHighlight()
    {
        return millisToExpireHighlight;
    }

}
