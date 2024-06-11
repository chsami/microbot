package net.runelite.client.plugins.hoseaplugins.inferno;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import net.runelite.api.coords.LocalPoint;

class InfernoBlobDeathSpot
{

	static final int BLOB_DEATH_TICKS = 3;
	static final int BLOB_DEATH_ANIMATION = 7584;
	static final int FILL_START_ALPHA = 255;

	@Getter(AccessLevel.PACKAGE)
	private final LocalPoint location;

	@Getter(AccessLevel.PACKAGE)
	@Setter(AccessLevel.PACKAGE)
	private Integer ticksUntilDone;

	@Getter(AccessLevel.PACKAGE)
	private final long deathTime;

	public InfernoBlobDeathSpot(LocalPoint location)
	{
		this.location = location;
		this.ticksUntilDone = BLOB_DEATH_TICKS;
		this.deathTime = System.currentTimeMillis();
	}

	void decrementTick()
	{
		if (ticksUntilDone > 0)
		{
			ticksUntilDone -= 1;
		}
	}

	boolean isDone()
	{
		return ticksUntilDone == 0;
	}

	double fillProgress()
	{
		return (System.currentTimeMillis() - deathTime) / ((BLOB_DEATH_TICKS - 1) * 600.0);
	}

	int fillAlpha()
	{
		return Math.min(Math.max((int) ((1 - fillProgress()) * FILL_START_ALPHA), 0), 255);
	}
}
