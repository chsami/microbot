package net.runelite.client.plugins.questhelper.steps.choice;

import net.runelite.client.plugins.questhelper.MQuestHelperConfig;

import java.util.regex.Pattern;

public class DialogChoiceStep extends WidgetChoiceStep
{

	public DialogChoiceStep(MQuestHelperConfig config, String choice)
	{
		super(config, choice, 219, 1);
		shouldNumber = true;
	}

	public DialogChoiceStep(MQuestHelperConfig config, Pattern pattern)
	{
		super(config, pattern, 219, 1);
		shouldNumber = true;
	}

	public DialogChoiceStep(MQuestHelperConfig config, int choiceId, String choice)
	{
		super(config, choiceId, choice, 219, 1);
		shouldNumber = true;
	}

	public DialogChoiceStep(MQuestHelperConfig config, int choiceId, Pattern pattern)
	{
		super(config, choiceId, pattern, 219, 1);
		shouldNumber = true;
	}

	public DialogChoiceStep(MQuestHelperConfig config, int choice)
	{
		super(config, choice, 219, 1);
		shouldNumber = true;
	}
}
