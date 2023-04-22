package net.runelite.client.plugins.microbot.scripts.bosses.enums;

import net.runelite.api.coords.LocalPoint;

public enum ZulrahLocation
{
	NORTH, SOUTH, EAST, WEST;

	public static ZulrahLocation valueOf(LocalPoint start, LocalPoint current)
	{
		int dx = start.getX() - current.getX();
		int dy = start.getY() - current.getY();
		if (dx == -10 * 128 && dy == 2 * 128)
		{
			return ZulrahLocation.EAST;
		}
		else if (dx == 10 * 128 && dy == 2 * 128)
		{
			return ZulrahLocation.WEST;
		}
		else if (dx == 0 && dy == 11 * 128)
		{
			return ZulrahLocation.SOUTH;
		}
		else if (dx == 0 && dy == 0)
		{
			return ZulrahLocation.NORTH;
		}
		else
		{
			return null;
		}
	}
}