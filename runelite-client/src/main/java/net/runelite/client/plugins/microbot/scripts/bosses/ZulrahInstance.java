package net.runelite.client.plugins.microbot.scripts.bosses;
import javax.annotation.Nullable;
import net.runelite.api.NPC;
import net.runelite.api.Prayer;
import net.runelite.api.coords.LocalPoint;
import net.runelite.client.plugins.microbot.scripts.bosses.enums.StandLocation;
import net.runelite.client.plugins.microbot.scripts.bosses.enums.ZulrahLocation;
import net.runelite.client.plugins.microbot.scripts.bosses.enums.ZulrahPhase;
import net.runelite.client.plugins.microbot.scripts.bosses.enums.ZulrahType;
import net.runelite.client.plugins.microbot.scripts.bosses.patterns.ZulrahPattern;

public class ZulrahInstance
{
	private static final ZulrahPhase NO_PATTERN_MAGIC_PHASE = new ZulrahPhase(
		ZulrahLocation.NORTH,
		ZulrahType.MAGIC,
		false,
		StandLocation.PILLAR_WEST_OUTSIDE,
		Prayer.PROTECT_FROM_MAGIC
	);
	private static final ZulrahPhase NO_PATTERN_RANGE_PHASE = new ZulrahPhase(
		ZulrahLocation.NORTH,
		ZulrahType.RANGE,
		false,
		StandLocation.TOP_EAST,
		Prayer.PROTECT_FROM_MISSILES
	);
	private static final ZulrahPhase PATTERN_A_OR_B_RANGE_PHASE = new ZulrahPhase(
		ZulrahLocation.NORTH,
		ZulrahType.RANGE,
		false,
		StandLocation.PILLAR_WEST_OUTSIDE,
		Prayer.PROTECT_FROM_MISSILES
	);

	private final LocalPoint startLocation;
	private ZulrahPattern pattern;
	private int stage;
	private ZulrahPhase phase;

	ZulrahInstance(final NPC zulrah)
	{
		this.startLocation = zulrah.getLocalLocation();
	}

	public LocalPoint getStartLocation()
	{
		return startLocation;
	}

	public ZulrahPattern getPattern()
	{
		return pattern;
	}

	public void setPattern(ZulrahPattern pattern)
	{
		this.pattern = pattern;
	}

	int getStage()
	{
		return stage;
	}

	void nextStage()
	{
		++stage;
	}

	public void reset()
	{
		pattern = null;
		stage = 0;
	}

	@Nullable
	public ZulrahPhase getPhase()
	{
		ZulrahPhase patternPhase = null;
		if (pattern != null)
		{
			patternPhase = pattern.get(stage);
		}
		return patternPhase != null ? patternPhase : phase;
	}

	public void setPhase(ZulrahPhase phase)
	{
		this.phase = phase;
	}

	@Nullable
	public ZulrahPhase getNextPhase()
	{
		if (pattern != null)
		{
			return pattern.get(stage + 1);
		}
		else if (phase != null)
		{
			ZulrahType type = phase.getType();
			StandLocation standLocation = phase.getStandLocation();
			if (type == ZulrahType.MELEE)
			{
				return standLocation == StandLocation.TOP_EAST ? NO_PATTERN_MAGIC_PHASE : NO_PATTERN_RANGE_PHASE;
			}
			if (type == ZulrahType.MAGIC)
			{
				return standLocation == StandLocation.TOP_EAST ? NO_PATTERN_RANGE_PHASE : PATTERN_A_OR_B_RANGE_PHASE;
			}
		}
		return null;
	}
}